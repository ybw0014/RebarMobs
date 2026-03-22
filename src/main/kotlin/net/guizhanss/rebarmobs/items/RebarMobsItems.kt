package net.guizhanss.rebarmobs.items

import io.papermc.paper.datacomponent.DataComponentTypes
import net.guizhanss.guizhanlib.kt.rebar.items.register.RebarItemRegistry
import net.guizhanss.guizhanlib.kt.rebar.items.register.block
import net.guizhanss.guizhanlib.kt.rebar.items.register.item
import net.guizhanss.rebarmobs.RebarMobs
import net.guizhanss.rebarmobs.guide.RebarMobsPages
import net.guizhanss.rebarmobs.items.multiblocks.SoulAltar
import net.guizhanss.rebarmobs.items.resources.SoulShard
import net.guizhanss.rebarmobs.utils.RebarMobsKeys
import org.bukkit.Material

@Suppress("unstableApiUsage", "unused")
object RebarMobsItems : RebarItemRegistry(RebarMobs.instance()) {
    // <editor-fold desc="Magic resources" defaultstate="collapsed">
    val SOUL_SHARD by item<SoulShard> {
        key = RebarMobsKeys.SOUL_SHARD
        material = Material.FLINT
        builder {
            set(DataComponentTypes.MAX_STACK_SIZE, 1)
        }
        postRegister {
            RebarMobsPages.RESOURCES_MAGIC.addItem(it)
        }
    }
    // </editor-fold>

    // <editor-fold desc="Multiblocks" defaultstate="collapsed">
    val SOUL_ALTAR by block<SoulAltar> {
        key = RebarMobsKeys.SOUL_ALTAR
        material = Material.GLOWSTONE
        postRegister {
            RebarMobsPages.BLOCKS.addItem(it)
        }
    }
    // </editor-fold>
}
