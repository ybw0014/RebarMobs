package net.guizhanss.rebarmobs.listeners

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.guizhanss.guizhanlib.kt.minecraft.extensions.isAir
import net.guizhanss.rebarmobs.RebarMobs
import net.guizhanss.rebarmobs.items.RebarMobsItems
import net.guizhanss.rebarmobs.utils.RebarMobsKeys
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import java.util.logging.Level
import kotlin.random.Random

class MobHeadListener : Listener {

    private val decapitatorEnchantment: Enchantment by lazy {
        RegistryAccess
            .registryAccess()
            .getRegistry(RegistryKey.ENCHANTMENT)
            .get(RebarMobsKeys.DECAPITATOR) ?: error("Decapitator enchantment is not initialized!")
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onEntityDeath(e: EntityDeathEvent) {
        val p = e.damageSource.causingEntity as? Player ?: return

        val entity = e.entity
        val headItem = RebarMobsItems.MOB_HEADS[entity.type] ?: return
        val headConfig = RebarMobs.configs.mobHeadsConfig.value.getEntityConfig(entity.type)

        var chance = headConfig.baseChance
        // check if player is holding Decapitator enchanted item in main hand
        val mainHandItem = p.inventory.itemInMainHand
        if (!mainHandItem.isAir()) {
            chance += mainHandItem.getEnchantmentLevel(decapitatorEnchantment) * headConfig.decapitatorChance
        }

        if (Random.nextDouble() < chance) {
            entity.world.dropItemNaturally(entity.location, headItem.clone())
        }
    }
}
