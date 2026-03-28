package net.guizhanss.rebarmobs.items.tools

import io.github.pylonmc.rebar.block.BlockStorage
import io.github.pylonmc.rebar.event.api.annotation.MultiHandler
import io.github.pylonmc.rebar.item.RebarItem
import io.github.pylonmc.rebar.item.base.RebarInteractor
import net.guizhanss.rebarmobs.utils.RebarMobsKeys
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class QuartzAndSteel(item: ItemStack) :
    RebarItem(item),
    RebarInteractor {

    @MultiHandler([EventPriority.HIGHEST])
    override fun onUsedToClick(
        event: PlayerInteractEvent,
        priority: EventPriority,
    ) {
        if (event.action != Action.RIGHT_CLICK_BLOCK || event.useItemInHand() == Event.Result.DENY) return

        val clickedBlock = event.clickedBlock ?: return
        if (clickedBlock.type != Material.SOUL_SAND || event.blockFace != BlockFace.UP) {
            event.isCancelled = true
            return
        }

        event.isCancelled = true

        val fireBlock = clickedBlock.getRelative(event.blockFace)
        if (fireBlock.type.isAir) {
            BlockStorage.placeBlock(fireBlock, RebarMobsKeys.CURSED_FIRE)
            stack.damage(1, event.player)
        }
    }
}
