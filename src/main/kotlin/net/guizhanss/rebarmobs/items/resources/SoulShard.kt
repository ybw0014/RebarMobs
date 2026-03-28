package net.guizhanss.rebarmobs.items.resources

import io.github.pylonmc.rebar.config.Settings
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter
import io.github.pylonmc.rebar.datatypes.RebarSerializers
import io.github.pylonmc.rebar.event.api.annotation.MultiHandler
import io.github.pylonmc.rebar.i18n.RebarArgument
import io.github.pylonmc.rebar.item.RebarItem
import io.github.pylonmc.rebar.item.base.RebarInteractor
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.guizhanss.guizhanlib.kt.minecraft.extensions.isAir
import net.guizhanss.guizhanlib.kt.rebar.utils.persistentItemData
import net.guizhanss.rebarmobs.config.adapters.RebarMobsConfigAdapters
import net.guizhanss.rebarmobs.datatypes.persistent.RebarMobsPersistentDataTypes
import net.guizhanss.rebarmobs.utils.RebarMobsKeys
import net.guizhanss.rebarmobs.utils.refreshLore
import net.guizhanss.rebarmobs.utils.rmKey
import net.guizhanss.rebarmobs.utils.translatableKey
import net.kyori.adventure.text.Component
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class SoulShard(
    item: ItemStack,
) : RebarItem(item),
    RebarInteractor {
    var mobType: EntityType? by persistentItemData(MOB_TYPE_KEY, RebarMobsPersistentDataTypes.ENTITY_TYPE, null)
    var soulAmount: Int by persistentItemData(SOUL_AMOUNT_KEY, RebarSerializers.INTEGER, 0)

    fun getTier() = getTier(soulAmount)

    override fun getPlaceholders() = listOf(
        RebarArgument.of(
            "mob-type",
            Component.translatable(
                mobType?.translationKey() ?: translatableKey("no-mob-type"),
            ),
        ),
        RebarArgument.of(
            "tier",
            getTier(soulAmount).first,
        ),
        RebarArgument.of("souls", soulAmount),
    )

    @MultiHandler([EventPriority.MONITOR])
    override fun onUsedToClick(
        event: PlayerInteractEvent,
        priority: EventPriority,
    ) {
        if (event.action != Action.RIGHT_CLICK_AIR || event.useItemInHand() == Event.Result.DENY) return
        val shard = from<SoulShard>(event.item) ?: return
        shard.soulAmount = 0
        shard.mobType = null
        shard.refreshLore(event.player.locale())
        // TODO: maybe some animations of souls get released
    }

    override fun toString() = "SoulShard{validItem=${!stack.isAir()}, mobType=$mobType, soulAmount=$soulAmount}"

    data class SoulShardTierConfig(
        val requirement: Int,
        val ignoreLight: Boolean,
        val requirePlayer: Boolean,
        val spawnInterval: Int,
        val spawnCount: Int,
    )

    companion object : Listener {
        val MOB_TYPE_KEY = rmKey("mob_type")
        val SOUL_AMOUNT_KEY = rmKey("soul_amount")

        private val settings = Settings.get(RebarMobsKeys.SOUL_SHARD)

        private val TIER_ADAPTER = ConfigAdapter<SoulShardTierConfig> {
            val section = ConfigAdapter.CONFIG_SECTION.convert(it)
            return@ConfigAdapter SoulShardTierConfig(
                section.getOrThrow("requirement", ConfigAdapter.INTEGER),
                section.getOrThrow("ignore-light", ConfigAdapter.BOOLEAN),
                section.getOrThrow("require-player", ConfigAdapter.BOOLEAN),
                section.getOrThrow("spawn-interval", ConfigAdapter.INTEGER),
                section.getOrThrow("spawn-count", ConfigAdapter.INTEGER),
            )
        }

        val DISABLED_ENTITY_TYPES = setOf(
            EntityType.PLAYER,
            EntityType.UNKNOWN,
            EntityType.ARMOR_STAND,
        ) + settings.getOrThrow("disabled-entities", ConfigAdapter.SET.from(RebarMobsConfigAdapters.ENTITY_TYPE))
        val TIERS = settings.getOrThrow("tiers", ConfigAdapter.LIST.from(TIER_ADAPTER))

        /**
         * Get the tier number and the corresponding tier config
         */
        fun getTier(amount: Int): Pair<Int, SoulShardTierConfig?> {
            val tier = TIERS.indexOfFirst { amount < it.requirement }.takeIf { it != -1 } ?: TIERS.size
            return tier to tier.takeIf { it > 0 }?.let { TIERS[it - 1] }
        }

        private val soulStealerEnchant: Enchantment by lazy {
            RegistryAccess
                .registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT)
                .get(RebarMobsKeys.SOUL_STEALER) ?: error("Soul Stealer enchantment is not initialized!")
        }

        private fun findApplicableShard(
            player: Player,
            targetType: EntityType,
        ): Pair<SoulShard, Int>? {
            val mainContents = player.inventory.contents
            var emptyShard: Pair<SoulShard, Int>? = null

            // check offhand item
            val offHandItem = player.inventory.itemInOffHand
            if (!offHandItem.isAir()) {
                val shard = from<SoulShard>(offHandItem)
                if (shard != null) {
                    when (shard.mobType) {
                        targetType -> return shard to -1
                        null -> emptyShard = shard to -1
                        else -> Unit
                    }
                }
            }

            // check inventory
            for (slot in mainContents.indices) {
                val item = mainContents[slot]
                if (item.isAir()) continue

                val shard = from<SoulShard>(item) ?: continue

                when (shard.mobType) {
                    targetType -> return shard to slot
                    null if emptyShard == null -> emptyShard = shard to slot
                    else -> Unit
                }
            }

            // now the empty shard will be used, set up its type now
            emptyShard?.first?.mobType = targetType
            return emptyShard
        }

        @EventHandler(priority = EventPriority.MONITOR)
        fun onEntityDeath(e: EntityDeathEvent) {
            val p = e.damageSource.causingEntity as? Player ?: return

            // check entity
            val entity = e.entity
            if (entity.type in DISABLED_ENTITY_TYPES) return
            if (entity.persistentDataContainer.has(RebarMobsKeys.SOUL_CAGE_SPAWNED)) return

            // check shard
            val applicableShard = findApplicableShard(p, e.entity.type) ?: return
            val shard = applicableShard.first

            // calculate extra souls from soul stealer enchantment and vile sword
            var souls = 1
            if (entity.persistentDataContainer.has(RebarMobsKeys.VILE_SWORD_KILLED)) {
                souls++
            }
            souls += p.inventory.itemInMainHand.getEnchantmentLevel(soulStealerEnchant)
            shard.soulAmount += souls
            shard.refreshLore(p.locale())
        }
    }
}
