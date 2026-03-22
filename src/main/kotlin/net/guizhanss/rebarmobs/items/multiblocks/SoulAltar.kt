package net.guizhanss.rebarmobs.items.multiblocks

import io.github.pylonmc.rebar.block.BlockStorage
import io.github.pylonmc.rebar.block.RebarBlock
import io.github.pylonmc.rebar.block.base.RebarInteractBlock
import io.github.pylonmc.rebar.block.base.RebarSimpleMultiblock
import io.github.pylonmc.rebar.block.base.RebarSimpleMultiblock.MultiblockComponent
import io.github.pylonmc.rebar.block.base.RebarSimpleMultiblock.VanillaMultiblockComponent
import io.github.pylonmc.rebar.block.context.BlockBreakContext
import io.github.pylonmc.rebar.block.context.BlockCreateContext
import io.github.pylonmc.rebar.event.api.annotation.MultiHandler
import io.github.pylonmc.rebar.item.RebarItem
import io.papermc.paper.event.block.BlockBreakBlockEvent
import net.guizhanss.guizhanlib.minecraft.utils.InventoryUtil
import net.guizhanss.rebarmobs.recipes.SoulAltarRecipe
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.joml.Vector3i

class SoulAltar :
    RebarBlock,
    RebarSimpleMultiblock,
    RebarInteractBlock {
    constructor(block: Block, context: BlockCreateContext) : super(block, context) {
        setMultiblockDirection(context.facing)
    }

    constructor(block: Block, pdc: PersistentDataContainer) : super(block, pdc)

    override val components: Map<Vector3i, MultiblockComponent>
        get() =
            mapOf(
                Vector3i(1, 0, 0) to VanillaMultiblockComponent(Material.QUARTZ_BLOCK),
                Vector3i(-1, 0, 0) to VanillaMultiblockComponent(Material.QUARTZ_BLOCK),
                Vector3i(0, 0, 1) to VanillaMultiblockComponent(Material.QUARTZ_BLOCK),
                Vector3i(0, 0, -1) to VanillaMultiblockComponent(Material.QUARTZ_BLOCK),
                Vector3i(1, 0, 1) to VanillaMultiblockComponent(Material.OBSIDIAN),
                Vector3i(1, 0, -1) to VanillaMultiblockComponent(Material.OBSIDIAN),
                Vector3i(-1, 0, 1) to VanillaMultiblockComponent(Material.OBSIDIAN),
                Vector3i(-1, 0, -1) to VanillaMultiblockComponent(Material.OBSIDIAN),
            )

    @MultiHandler(priorities = [EventPriority.NORMAL, EventPriority.MONITOR])
    override fun onInteract(
        event: PlayerInteractEvent,
        priority: EventPriority,
    ) {
        if (event.action != Action.RIGHT_CLICK_BLOCK || event.useInteractedBlock() == Event.Result.DENY ||
            event.hand != EquipmentSlot.HAND
        ) {
            return
        }

        if (priority == EventPriority.NORMAL) {
            event.setUseItemInHand(Event.Result.DENY)
            return
        } else {
            event.setUseInteractedBlock(Event.Result.DENY)
        }

        val item = event.item ?: return

        for (recipe in SoulAltarRecipe.RECIPE_TYPE) {
            if (!recipe.input.isSimilar(item)) continue

            // consume ingredient
            if (event.player.gameMode != GameMode.CREATIVE || event.player.gameMode != GameMode.SPECTATOR) {
                item.amount -= recipe.input.amount
            }

            // remove surrounding blocks
            for ((vec, _) in components) {
                val b = block.getRelative(vec.x, vec.y, vec.z)
                if (!BlockBreakBlockEvent(b, block, listOf()).callEvent()) continue
                b.world.playSound(b.location, Sound.BLOCK_STONE_BREAK, 1.0f, 1.0f)
                b.world.spawnParticle(Particle.BLOCK, b.location.add(0.5, 0.5, 0.5), 30, b.blockData)
                b.type = Material.AIR
            }

            // remove self
            block.world.playSound(block.location, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f)
            block.world.spawnParticle(Particle.BLOCK, block.location.add(0.5, 0.5, 0.5), 50, block.blockData)
            val context = BlockBreakContext.PluginBreak(block, false)
            BlockStorage.breakBlock(block, context)

            // spawn result
            InventoryUtil.push(event.player, recipe.result)
            return
        }
    }

    class Item(
        stack: ItemStack,
    ) : RebarItem(stack)
}
