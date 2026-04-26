package net.guizhanss.rebarmobs.items.blocks

import io.github.pylonmc.rebar.block.BlockStorage
import io.github.pylonmc.rebar.block.RebarBlock
import io.github.pylonmc.rebar.block.base.RebarBreakHandler
import io.github.pylonmc.rebar.block.context.BlockBreakContext
import io.github.pylonmc.rebar.block.context.BlockCreateContext
import io.github.pylonmc.rebar.config.Settings
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter
import io.github.pylonmc.rebar.datatypes.RebarSerializers
import io.github.pylonmc.rebar.i18n.RebarArgument
import io.github.pylonmc.rebar.item.RebarItem
import io.github.pylonmc.rebar.waila.WailaDisplay
import net.guizhanss.guizhanlib.kt.rebar.items.RebarMainHandInteractBlock
import net.guizhanss.guizhanlib.minecraft.utils.InventoryUtil
import net.guizhanss.rebarmobs.items.resources.SoulShard
import net.guizhanss.rebarmobs.utils.RebarMobsKeys
import net.guizhanss.rebarmobs.utils.rmKey
import net.guizhanss.rebarmobs.utils.translatableKey
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.block.Block
import org.bukkit.block.CreatureSpawner
import org.bukkit.block.spawner.SpawnRule
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.SpawnerSpawnEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class SoulCage :
    RebarBlock,
    RebarMainHandInteractBlock,
    RebarBreakHandler {
    constructor(block: Block, context: BlockCreateContext) : super(block, context)

    constructor(block: Block, pdc: PersistentDataContainer) : super(block, pdc) {
        val shardItem = pdc.get(STORED_SHARD_KEY, RebarSerializers.ITEM_STACK)
        storedShard = RebarItem.from<SoulShard>(shardItem)
        configureSpawner()
    }

    var storedShard: SoulShard? = null
        private set

    override fun getBlockTextureProperties(): MutableMap<String, Pair<String, Int>> {
        val properties = super.getBlockTextureProperties()
        properties["state"] = (if (storedShard != null) "filled" else "empty") to 2
        return properties
    }

    override fun write(pdc: PersistentDataContainer) {
        if (storedShard != null) {
            pdc.set(STORED_SHARD_KEY, RebarSerializers.ITEM_STACK, storedShard!!.stack)
        } else {
            pdc.remove(STORED_SHARD_KEY)
        }
    }

    override fun onMainHandInteract(event: PlayerInteractEvent) {
        if (event.player.isSneaking) {
            if (storedShard == null) return
            InventoryUtil.push(event.player, storedShard!!.stack)
            storedShard = null
            clearSpawner()
            scheduleBlockTextureItemRefresh()
        } else {
            if (storedShard != null) return
            val shard = RebarItem.from<SoulShard>(event.item!!.clone()) ?: return
            if (shard.mobType == null) return

            val (tier, _) = shard.getTier()
            if (tier == 0) return

            storedShard = shard
            if (event.player.gameMode != GameMode.CREATIVE && event.player.gameMode != GameMode.SPECTATOR) {
                event.item!!.amount--
            }
            configureSpawner()
            scheduleBlockTextureItemRefresh()
        }
    }

    override fun onBreak(
        drops: MutableList<ItemStack>,
        context: BlockBreakContext,
    ) {
        storedShard?.also {
            drops.add(it.stack)
        }
    }

    override fun getWaila(player: Player): WailaDisplay {
        if (storedShard == null || storedShard?.mobType == null) {
            return WailaDisplay(Component.translatable(translatableKey("waila.empty")))
        }

        val (tier, _) = SoulShard.getTier(storedShard!!.soulAmount)
        return WailaDisplay(
            Component.translatable(
                translatableKey("waila.filled"),
                RebarArgument.of(
                    "mob-type",
                    Component.translatable(
                        storedShard!!.mobType!!.translationKey(),
                    ),
                ),
                RebarArgument.of(
                    "tier",
                    tier,
                ),
            ),
        )
    }

    private fun configureSpawner() {
        val spawner = block.state as? CreatureSpawner ?: return
        val shard = storedShard ?: return
        val mobType = shard.mobType ?: return
        val (_, tierConfig) = shard.getTier()
        val config = tierConfig ?: return

        spawner.spawnedType = mobType
        val delayTicks = config.spawnInterval * 20
        spawner.minSpawnDelay = delayTicks
        spawner.maxSpawnDelay = delayTicks
        spawner.delay = delayTicks
        spawner.spawnCount = config.spawnCount
        spawner.maxNearbyEntities = MAX_NEARBY_ENTITIES
        spawner.requiredPlayerRange = if (config.requirePlayer) PLAYER_ACTIVATION_RANGE else -1
        spawner.spawnRange = SPAWN_RANGE

        if (config.ignoreLight) {
            val snapshot = Bukkit.getEntityFactory().createEntitySnapshot("{id:\"${mobType.key}\"}")
            spawner.addPotentialSpawn(snapshot, 1, SpawnRule(0, 15, 0, 15))
        }

        spawner.update()
    }

    private fun clearSpawner() {
        val spawner = block.state as? CreatureSpawner ?: return
        spawner.spawnedType = null
        spawner.potentialSpawns.clear()
        spawner.update()
    }

    class Item(item: ItemStack) : RebarItem(item)

    companion object : Listener {
        val STORED_SHARD_KEY = rmKey("stored_shard")

        private val settings = Settings.get(RebarMobsKeys.SOUL_CAGE)

        val MAX_NEARBY_ENTITIES = settings.getOrThrow("max-nearby-entities", ConfigAdapter.INTEGER)
        val PLAYER_ACTIVATION_RANGE = settings.getOrThrow("player-activation-range", ConfigAdapter.INTEGER)
        val SPAWN_RANGE = settings.getOrThrow("spawn-range", ConfigAdapter.INTEGER)

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        fun onSpawnerSpawn(event: SpawnerSpawnEvent) {
            val spawner = event.spawner ?: return
            BlockStorage.getAs<SoulCage>(spawner.block) ?: return
            event.entity.persistentDataContainer.set(
                RebarMobsKeys.SOUL_CAGE_SPAWNED,
                PersistentDataType.BOOLEAN,
                true,
            )
        }
    }
}
