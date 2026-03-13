package net.guizhanss.rebarmobs.items.multiblocks

import io.github.pylonmc.rebar.block.RebarBlock
import io.github.pylonmc.rebar.block.base.RebarBreakHandler
import io.github.pylonmc.rebar.block.base.RebarInteractBlock
import io.github.pylonmc.rebar.block.base.RebarRecipeProcessor
import io.github.pylonmc.rebar.block.base.RebarSimpleMultiblock
import io.github.pylonmc.rebar.block.base.RebarSimpleMultiblock.MultiblockComponent
import io.github.pylonmc.rebar.block.base.RebarSimpleMultiblock.VanillaMultiblockComponent
import io.github.pylonmc.rebar.block.context.BlockCreateContext
import io.github.pylonmc.rebar.event.api.annotation.MultiHandler
import io.github.pylonmc.rebar.item.RebarItem
import io.github.pylonmc.rebar.registry.RebarRegistry
import io.papermc.paper.event.block.BlockBreakBlockEvent
import net.guizhanss.rebarmobs.recipes.SoulAltarRecipe
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.joml.Vector3i

class SoulAltar :
    RebarBlock,
    RebarSimpleMultiblock,
    RebarInteractBlock,
    RebarRecipeProcessor<SoulAltarRecipe> {
    constructor(block: Block, context: BlockCreateContext) : super(block, context) {
        setMultiblockDirection(context.facing)
        setRecipeType(SoulAltarRecipe.RECIPE_TYPE)
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
        priority: EventPriority
    ) {
        if (event.action != Action.RIGHT_CLICK_BLOCK || event.useInteractedBlock() == Event.Result.DENY || event.hand != EquipmentSlot.HAND) return

        if (priority == EventPriority.NORMAL) {
            event.setUseItemInHand(Event.Result.DENY)
            return
        } else {
            event.setUseInteractedBlock(Event.Result.DENY)
        }

        val item = event.item ?: return

        for (recipe in SoulAltarRecipe.RECIPE_TYPE) {
            if (!recipe.input.isSimilar(item)) continue

            for ((vec, _) in components) {
                val b = block.getRelative(vec.x, vec.y, vec.z)
                if (!BlockBreakBlockEvent(b, block, listOf()).callEvent()) continue
                b.type = Material.AIR
            }
            if (!BlockBreakBlockEvent(block, block, listOf()).callEvent()) break
            block.type = Material.AIR
            break
        }
    }

    override fun onRecipeFinished(recipe: SoulAltarRecipe) {
        TODO("Not yet implemented")
    }

    class Item(
        stack: ItemStack,
    ) : RebarItem(stack)
}
