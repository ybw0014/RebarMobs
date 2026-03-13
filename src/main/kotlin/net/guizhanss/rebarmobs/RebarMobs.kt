package net.guizhanss.rebarmobs

import net.byteflux.libby.Library
import net.guizhanss.guizhanlib.libraries.BukkitLibraryManager
import net.guizhanss.guizhanlib.rebar.addon.AbstractAddon
import net.guizhanss.rebarmobs.config.RebarMobsConfig
import net.guizhanss.rebarmobs.guide.RebarMobsPages
import net.guizhanss.rebarmobs.items.RebarMobsItems
import net.guizhanss.rebarmobs.recipes.RebarMobsRecipes
import org.bstats.bukkit.Metrics
import org.bukkit.Material
import java.util.Locale

class RebarMobs : AbstractAddon(GITHUB_USER, GITHUB_REPO, GITHUB_BRANCH, AUTO_UPDATE_KEY) {
    override val languages = setOf(Locale.ENGLISH)

    override val material = Material.DRAGON_HEAD

    override fun load() {
        // check if there is central repo prop defined
        val centralRepo =
            System.getProperty("centralRepository") ?: "https://maven-central.storage-download.googleapis.com/maven2/"

        logger.info("Loading libraries, please wait...")
        logger.info("If you stuck here for a long time, try to specify a mirror repository.")
        logger.info("Add -DcentralRepository=<url> to the JVM arguments.")

        // download libs
        val manager = BukkitLibraryManager(this)
        manager.addRepository(centralRepo)
        manager.loadLibrary(
            Library
                .builder()
                .groupId("org.jetbrains.kotlin")
                .artifactId("kotlin-stdlib")
                .version("2.3.10")
                .build(),
        )
        manager.loadLibrary(
            Library
                .builder()
                .groupId("org.jetbrains.kotlin")
                .artifactId("kotlin-reflect")
                .version("2.3.10")
                .build(),
        )

        logger.info("Loaded all required libraries.")
    }

    override fun enable() {
        setupMetrics()

        configs = RebarMobsConfig(this)

        RebarMobsPages
        RebarMobsItems
        RebarMobsRecipes

        registerListeners()
    }

    override fun disable() {
    }

    protected override fun autoUpdate() {
        // TODO: impl auto update logic
    }

    private fun setupMetrics() {
        val metrics = Metrics(this, 30082)
    }

    private fun registerListeners() {
        // nothing yet
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
