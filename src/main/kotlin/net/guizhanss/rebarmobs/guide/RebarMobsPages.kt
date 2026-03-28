package net.guizhanss.rebarmobs.guide

import io.github.pylonmc.rebar.content.guide.RebarGuide
import io.github.pylonmc.rebar.guide.pages.base.SimpleStaticGuidePage
import net.guizhanss.rebarmobs.RebarMobs
import net.guizhanss.rebarmobs.utils.RebarMobsKeys
import org.bukkit.Material

/**
 * Use Pylon's guide pages when available, otherwise use our own pages.
 */
object RebarMobsPages {
    private val isPylonEnabled =
        RebarMobs
            .instance()
            .server.pluginManager
            .isPluginEnabled("Pylon")

    val MAIN = SimpleStaticGuidePage(RebarMobsKeys.REBAR_MOBS)

//    val RESOURCES_MAGIC: SimpleStaticGuidePage =
//        if (isPylonEnabled) PylonPages.MAGIC else SimpleStaticGuidePage(RebarMobsKeys.RESOURCES_MAGIC)
//    val MULTIBLOCKS: SimpleStaticGuidePage =
//        if (isPylonEnabled) PylonPages.SIMPLE_MACHINES else SimpleStaticGuidePage(RebarMobsKeys.BLOCKS)
//    val BLOCKS: SimpleStaticGuidePage =
//        if (isPylonEnabled) PylonPages.SIMPLE_MACHINES else SimpleStaticGuidePage(RebarMobsKeys.MULTIBLOCKS)

    init {
        RebarGuide.rootPage.addPage(Material.CREEPER_HEAD, MAIN)

//        if (!isPylonEnabled) {
//            MAIN.addPage(Material.ECHO_SHARD, RESOURCES_MAGIC)
//            MAIN.addPage(Material.BRICKS, MULTIBLOCKS)
//            MAIN
//        }
    }
}
