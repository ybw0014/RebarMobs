package net.guizhanss.rebarmobs.recipes

import io.github.pylonmc.rebar.config.ConfigSection
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter
import io.github.pylonmc.rebar.guide.button.ItemButton
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder
import io.github.pylonmc.rebar.recipe.ConfigurableRecipeType
import io.github.pylonmc.rebar.recipe.FluidOrItem
import io.github.pylonmc.rebar.recipe.RebarRecipe
import io.github.pylonmc.rebar.recipe.RecipeInput
import io.github.pylonmc.rebar.util.gui.GuiItems
import net.guizhanss.guizhanlib.kt.minecraft.extensions.toItem
import net.guizhanss.rebarmobs.items.RebarMobsItems
import net.guizhanss.rebarmobs.utils.RebarMobsKeys
import net.guizhanss.rebarmobs.utils.rmTranslatableKey
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.invui.gui.Gui

@JvmRecord
data class CursingRecipe(
    @get:JvmName("key")
    val key: NamespacedKey,
    val input: ItemStack,
    val result: ItemStack,
) : RebarRecipe {
    override fun getKey() = key

    override val inputs get() = listOf(RecipeInput.of(input))
    override val results get() = listOf(FluidOrItem.of(result))

    override fun display() = Gui
        .builder()
        .setStructure(
            "# # # # # # # # #",
            "# # # # q # # # #",
            "# i # # f # # o #",
            "# # # # s # # # #",
            "# # # # # # # # #",
        ).addIngredient('#', GuiItems.backgroundBlack())
        .addIngredient(
            's',
            ItemButton(Material.SOUL_SAND.toItem(), Material.SOUL_SOIL.toItem()),
        )
        .addIngredient(
            'f',
            ItemButton.from(
                ItemStackBuilder.gui(Material.SOUL_CAMPFIRE, RebarMobsKeys.CURSED_FIRE).name(
                    Component.translatable(
                        rmTranslatableKey("item.${RebarMobsKeys.CURSED_FIRE.key}.name"),
                    ),
                ).build(),
            ),
        )
        .addIngredient('q', ItemButton.from(RebarMobsItems.QUARTZ_AND_STEEL))
        .addIngredient('i', ItemButton.from(input))
        .addIngredient('o', ItemButton.from(result))
        .build()

    companion object {
        val RECIPE_TYPE =
            object : ConfigurableRecipeType<CursingRecipe>(RebarMobsKeys.CURSING) {
                override fun loadRecipe(
                    key: NamespacedKey,
                    section: ConfigSection,
                ): CursingRecipe = CursingRecipe(
                    key,
                    section.getOrThrow("input", ConfigAdapter.ITEM_STACK),
                    section.getOrThrow("result", ConfigAdapter.ITEM_STACK),
                )
            }
    }
}
