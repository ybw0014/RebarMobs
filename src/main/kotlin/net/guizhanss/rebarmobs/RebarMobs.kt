package net.guizhanss.rebarmobs

import net.guizhanss.guizhanlib.rebar.addon.AbstractAddon
import net.guizhanss.rebarmobs.commands.RebarMobsCommands
import net.guizhanss.rebarmobs.config.RebarMobsConfig
import net.guizhanss.rebarmobs.guide.RebarMobsPages
import net.guizhanss.rebarmobs.items.RebarMobsItems
import net.guizhanss.rebarmobs.recipes.RebarMobsRecipes
import org.bstats.bukkit.Metrics
import org.bukkit.Material
import java.util.Locale

class RebarMobs : AbstractAddon(GITHUB_USER, GITHUB_REPO, GITHUB_BRANCH, AUTO_UPDATE_KEY) {
    override val languages = setOf(Locale.ENGLISH)

    override val material = Material.CREEPER_HEAD

    override fun enable() {
        setupMetrics()

        configs = RebarMobsConfig(this)

        RebarMobsPages
        RebarMobsItems
        RebarMobsRecipes

        RebarMobsCommands.register(this)
    }

    override fun disable() {
    }

    protected override fun autoUpdate() {
        // TODO: impl auto update logic
    }

    private fun setupMetrics() {
        val metrics = Metrics(this, 30082)
    }

    companion object {
        private const val GITHUB_USER = "ybw0014"
        private const val GITHUB_REPO = "RebarMobs"
        private const val GITHUB_BRANCH = "master"
        private const val AUTO_UPDATE_KEY = "auto-update"

        lateinit var configs: RebarMobsConfig
            private set

        fun instance() = getInstance<RebarMobs>()
    }
}
