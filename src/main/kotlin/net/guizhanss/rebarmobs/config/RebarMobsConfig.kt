package net.guizhanss.rebarmobs.config

import net.guizhanss.guizhanlib.kt.minecraft.config.ConfigField
import net.guizhanss.guizhanlib.kt.minecraft.config.yamlConfig
import net.guizhanss.rebarmobs.RebarMobs

class RebarMobsConfig(
    plugin: RebarMobs,
) {
    lateinit var autoUpdateEnabled: ConfigField<Boolean>
    lateinit var autoUpdateIntervalDays: ConfigField<Int>
    lateinit var autoUpdateDownload: ConfigField<Boolean>

    private val config =
        yamlConfig(plugin, "config.yml") {
            autoUpdateEnabled = boolean("auto-update.enabled", true)
            autoUpdateIntervalDays = int("auto-update.interval-days", 1)
            autoUpdateDownload = boolean("auto-update.download", true)
        }

    init {
        reload()
    }

    fun reload() {
        config.reload()
    }
}
