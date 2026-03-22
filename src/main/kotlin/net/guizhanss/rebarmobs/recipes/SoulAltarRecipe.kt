package net.guizhanss.rebarmobs.recipes

import io.github.pylonmc.rebar.config.ConfigSection
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter
import io.github.pylonmc.rebar.guide.button.ItemButton
import io.github.pylonmc.rebar.recipe.ConfigurableRecipeType
import io.github.pylonmc.rebar.recipe.FluidOrItem
import io.github.pylonmc.rebar.recipe.IngredientCalculator
import io.github.pylonmc.rebar.recipe.RebarRecipe
import io.github.pylonmc.rebar.recipe.RecipeInput
import io.github.pylonmc.rebar.util.gui.GuiItems
import net.guizhanss.rebarmobs.items.RebarMobsItems
import net.guizhanss.rebarmobs.utils.RebarMobsKeys
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.invui.gui.Gui

@JvmRecord
data class SoulAltarRecipe(
    @get:JvmName("getKey_")
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
            "# # # # # # # # #",
            "# i # # a # # o #",
            "# # # # # # # # #",
            "# # # # # # # # #",
        ).addIngredient('#', GuiItems.backgroundBlack())
        .addIngredient('a', ItemButton.from(RebarMobsItems.SOUL_ALTAR))
        .addIngredient('i', ItemButton.from(input))
        .addIngredient('o', ItemButton.from(result))
        .build()

    companion object {
        val RECIPE_TYPE =
            object : ConfigurableRecipeType<SoulAltarRecipe>(RebarMobsKeys.SOUL_ALTAR) {
                override fun loadRecipe(
                    key: NamespacedKey,
                    section: ConfigSection,
                ): SoulAltarRecipe = SoulAltarRecipe(
                    key,
                    section.getOrThrow("input", ConfigAdapter.ITEM_STACK),
                    section.getOrThrow("result", ConfigAdapter.ITEM_STACK),
                )

                override fun addRecipe(recipe: SoulAltarRecipe) {
                    super.addRecipe(recipe)
                    if (recipe.results.size > 1) {
                        IngredientCalculator.addBaseRecipe(recipe)
                    }
                }
            }
    }
}
