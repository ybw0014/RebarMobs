package net.guizhanss.rebarmobs.items.resources

import io.github.pylonmc.rebar.config.Settings
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter
import io.github.pylonmc.rebar.datatypes.RebarSerializers
import io.github.pylonmc.rebar.i18n.RebarArgument
import io.github.pylonmc.rebar.item.RebarItem
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.guizhanss.guizhanlib.kt.minecraft.extensions.isAir
import net.guizhanss.guizhanlib.kt.rebar.utils.persistentItemData
import net.guizhanss.rebarmobs.config.adapters.RebarMobsConfigAdapters
import net.guizhanss.rebarmobs.datatypes.RebarMobsDataTypes
import net.guizhanss.rebarmobs.utils.RebarMobsKeys
import net.guizhanss.rebarmobs.utils.refreshLore
import net.guizhanss.rebarmobs.utils.rmKey
import net.guizhanss.rebarmobs.utils.translatableKey
import net.kyori.adventure.text.Component
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

class SoulShard(
    item: ItemStack,
) : RebarItem(item) {
    var mobType: EntityType? by persistentItemData(MOB_TYPE_KEY, RebarMobsDataTypes.ENTITY_TYPE, null)
    var soulAmount: Int by persistentItemData(SOUL_AMOUNT_KEY, RebarSerializers.INTEGER, 0)

    override fun getPlaceholders() = listOf(
        RebarArgument.of(
            "mob-type",
            Component.translatable(
                mobType?.translationKey() ?: translatableKey("no_mob_type"),
            ),
        ),
        RebarArgument.of(
            "tier",
            getTier(soulAmount),
        ),
        RebarArgument.of("souls", soulAmount),
    )

    data class SoulShardTierConfig(
        val requirement: Int,

    )

    companion object : Listener {
        val MOB_TYPE_KEY = rmKey("mob_type")
        val SOUL_AMOUNT_KEY = rmKey("soul_amount")

        private val settings = Settings.get(RebarMobsKeys.SOUL_SHARD)

        private val TIER_ADAPTER = ConfigAdapter<SoulShardTierConfig> {
            val section = ConfigAdapter.CONFIG_SECTION.convert(it)
            return@ConfigAdapter SoulShardTierConfig(section.getOrThrow("requirement", ConfigAdapter.INTEGER))
        }

        val DISABLED_ENTITY_TYPES = setOf(
            EntityType.PLAYER,
            EntityType.UNKNOWN,
            EntityType.ARMOR_STAND,
        ) + settings.getOrThrow("disabled_entities", ConfigAdapter.SET.from(RebarMobsConfigAdapters.ENTITY_TYPE))
        val TIERS = settings.getOrThrow("tiers", ConfigAdapter.LIST.from(TIER_ADAPTER))

        fun getTier(amount: Int) = TIERS.indexOfFirst { amount < it.requirement }.takeIf { it != -1 } ?: TIERS.size

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
                val shard = fromStack(offHandItem) as? SoulShard
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

                val shard = fromStack(item) as? SoulShard ?: continue

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

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        fun onEntityDeath(e: EntityDeathEvent) {
            val p = e.damageSource.causingEntity as? Player ?: return
            val entity = e.entity
            if (entity.type in DISABLED_ENTITY_TYPES) return

            val extraSouls = p.inventory.itemInMainHand.getEnchantmentLevel(soulStealerEnchant)
            val applicableShard = findApplicableShard(p, e.entity.type) ?: return
            val shard = applicableShard.first
            shard.soulAmount += 1 + extraSouls
            shard.refreshLore(p.locale())
        }
    }
}
