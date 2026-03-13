package net.guizhanss.rebarmobs.items.resources

import io.github.pylonmc.rebar.datatypes.RebarSerializers
import io.github.pylonmc.rebar.i18n.RebarArgument
import io.github.pylonmc.rebar.item.RebarItem
import net.guizhanss.rebarmobs.datatypes.RebarMobsDataTypes
import net.guizhanss.rebarmobs.utils.rmKey
import net.guizhanss.rebarmobs.utils.translatableKey
import net.kyori.adventure.text.Component
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

class SoulShard(
    item: ItemStack,
) : RebarItem(item) {
    override fun getPlaceholders() =
        super.getPlaceholders().toMutableList().apply {
            add(
                RebarArgument.of(
                    "mob-type",
                    Component.translatable(
                        mobType?.translationKey() ?: translatableKey("no_mob_type"),
                    ),
                ),
            )
            add(RebarArgument.of("tier", 0)) // TODO: tier display
            add(RebarArgument.of("souls", soulAmount))
        }

    val mobType: EntityType?
        get() = stack.persistentDataContainer.get(MOB_TYPE_KEY, RebarMobsDataTypes.ENTITY_TYPE)
    val soulAmount: Int
        get() = stack.persistentDataContainer.getOrDefault(SOUL_AMOUNT_KEY, RebarSerializers.INTEGER, 0)

    companion object {
        val MOB_TYPE_KEY = rmKey("mob_type")
        val SOUL_AMOUNT_KEY = rmKey("soul_amount")
    }
}
