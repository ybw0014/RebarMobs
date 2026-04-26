package net.guizhanss.rebarmobs.items.blocks

import io.github.pylonmc.rebar.block.BlockStorage
import io.github.pylonmc.rebar.block.RebarBlock
import io.github.pylonmc.rebar.block.context.BlockCreateContext
import io.github.pylonmc.rebar.datatypes.RebarSerializers
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder
import net.guizhanss.guizhanlib.kt.minecraft.items.edit
import net.guizhanss.rebarmobs.RebarMobs
import net.guizhanss.rebarmobs.recipes.CursingRecipe
import net.guizhanss.rebarmobs.utils.RebarMobsKeys
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class CursedFire : RebarBlock {
    constructor(block: Block, context: BlockCreateContext) : super(block, context)

    constructor(block: Block, pdc: PersistentDataContainer) : super(block, pdc)

    override fun getBlockTextureItem(): ItemStack {
        val builder = ItemStackBuilder.of(Material.BARRIER)
        builder.editPdc { it.set(rebarBlockTextureEntityKey, RebarSerializers.BOOLEAN, true) }
        builder.addCustomModelDataString(schema.key.toString())
        return builder.build()
    }

    companion object : Listener {
        const val PROCESS_COOLDOWN_MS = 2000L

        fun isCursedFire(block: Block): Boolean = BlockStorage.get(block) is CursedFire

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        fun onEntityDamage(event: EntityDamageEvent) {
            val entity = event.entity
            if (entity !is Item) return

            val pdc = entity.persistentDataContainer
            val immuneUntil = pdc.getOrDefault(
                RebarMobsKeys.CURSED_FIRE_IMMUNE_UNTIL,
                RebarSerializers.LONG,
                0L,
            )

            if (System.currentTimeMillis() < immuneUntil) {
                event.isCancelled = true
                entity.fireTicks = 0
                return
            }

            if (event.cause != EntityDamageEvent.DamageCause.FIRE) return

            val loc = entity.location
            val currentBlock = loc.block
            val blockBelow = loc.clone().subtract(0.0, 0.1, 0.0).block

            val isCursed = isCursedFire(currentBlock) || isCursedFire(blockBelow)
            if (!isCursed) return

            val itemStack = entity.itemStack
            val result = findCursingRecipeResult(itemStack)

            if (result != null) {
                event.isCancelled = true
                entity.itemStack = result
                entity.fireTicks = 0
                pdc.set(
                    RebarMobsKeys.CURSED_FIRE_IMMUNE_UNTIL,
                    RebarSerializers.LONG,
                    System.currentTimeMillis() + PROCESS_COOLDOWN_MS,
                )

                val currentVel = entity.velocity
                val horizontalSpeed = sqrt(currentVel.x * currentVel.x + currentVel.z * currentVel.z)
                val targetSpeed = 0.35
                val maxSpeed = 0.6

                val newVelocity = when {
                    horizontalSpeed < 0.001 -> {
                        val angle = Random.nextDouble() * 2 * kotlin.math.PI
                        Vector(cos(angle) * targetSpeed, 0.4, sin(angle) * targetSpeed)
                    }

                    horizontalSpeed > maxSpeed -> {
                        val scale = maxSpeed / horizontalSpeed
                        Vector(currentVel.x * scale, 0.4, currentVel.z * scale)
                    }

                    else -> {
                        val scale = targetSpeed / horizontalSpeed
                        Vector(currentVel.x * scale, 0.4, currentVel.z * scale)
                    }
                }

                entity.velocity = newVelocity

                listOf(5, 10, 15, 20, 30).forEach { delay ->
                    RebarMobs.instance().pluginScheduler.run(delay) {
                        if (!entity.isValid) return@run
                        entity.fireTicks = 0

                        val currentLoc = entity.location
                        val currentBlock = currentLoc.block
                        val blockBelow = currentLoc.clone().subtract(0.0, 0.1, 0.0).block
                        val stillInCursedFire = isCursedFire(currentBlock) || isCursedFire(blockBelow)

                        if (stillInCursedFire) {
                            val vel = entity.velocity
                            val horizontalSpeed = sqrt(vel.x * vel.x + vel.z * vel.z)
                            if (horizontalSpeed < 0.2) {
                                val angle = Random.nextDouble() * 2 * kotlin.math.PI
                                entity.velocity = Vector(cos(angle) * 0.35, 0.4, sin(angle) * 0.35)
                            }
                        }
                    }
                }
            }
        }

        private fun findCursingRecipeResult(item: ItemStack): ItemStack? {
            for (recipe in CursingRecipe.RECIPE_TYPE.recipes) {
                if (recipe.input.isSimilar(item)) {
                    val multiplier = item.amount / recipe.input.amount
                    return recipe.result.clone().edit { amount(recipe.result.amount * multiplier) }
                }
            }
            return null
        }
    }
}
