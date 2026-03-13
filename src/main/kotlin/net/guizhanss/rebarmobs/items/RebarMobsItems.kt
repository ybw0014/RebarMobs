package net.guizhanss.rebarmobs.items

import io.github.pylonmc.rebar.block.RebarBlock
import io.github.pylonmc.rebar.item.RebarItem
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import net.guizhanss.rebarmobs.guide.RebarMobsPages
import net.guizhanss.rebarmobs.items.multiblocks.SoulAltar
import net.guizhanss.rebarmobs.items.resources.SoulShard
import net.guizhanss.rebarmobs.utils.RebarMobsKeys
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Suppress("unstableApiUsage")
object RebarMobsItems {
    // <editor-fold desc="Magic resources" defaultstate="collapsed">
    val SOUL_SHARD: ItemStack =
        ItemStackBuilder
            .rebar(Material.FLINT, RebarMobsKeys.SOUL_SHARD)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build()

    init {
        RebarItem.register<SoulShard>(SOUL_SHARD)
        RebarMobsPages.RESOURCES_MAGIC.addItem(SOUL_SHARD)
    }
    // </editor-fold>

    // <editor-fold desc="Multiblocks" defaultstate="collapsed">
    val SOUL_ALTAR: ItemStack =
        ItemStackBuilder
            .rebar(Material.GLOWSTONE, RebarMobsKeys.SOUL_ALTAR)
            .build()

    init {
        RebarItem.register<SoulAltar.Item>(SOUL_ALTAR, RebarMobsKeys.SOUL_ALTAR)
        RebarBlock.register<SoulAltar>(RebarMobsKeys.SOUL_ALTAR, Material.GLOWSTONE)
        RebarMobsPages.RESOURCES_MAGIC.addItem(SOUL_ALTAR)
    }
    // </editor-fold>
}
