package net.guizhanss.rebarmobs.utils

import io.github.pylonmc.rebar.i18n.RebarTranslator.Companion.translate
import io.github.pylonmc.rebar.item.RebarItem
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder.Companion.loreKey
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import java.util.Locale

/**
 * Force update the lore of the bound [ItemStack].
 */
fun RebarItem.refreshLore(locale: Locale) {
    stack.lore(listOf(Component.translatable(loreKey(key))))
    stack.translate(locale, getPlaceholders())
}
