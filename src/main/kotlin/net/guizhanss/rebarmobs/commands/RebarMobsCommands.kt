package net.guizhanss.rebarmobs.commands

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.guizhanss.guizhanlib.kt.minecraft.command.BaseKommand
import net.guizhanss.guizhanlib.kt.minecraft.command.baseCommand
import net.guizhanss.rebarmobs.RebarMobs
import net.guizhanss.rebarmobs.commands.handlers.EnchantmentBookHandler
import net.guizhanss.rebarmobs.utils.rmTranslatableKey

object RebarMobsCommands {
    private lateinit var mainCommand: BaseKommand

    fun register(plugin: RebarMobs) {
        mainCommand =
            baseCommand(plugin, "rebarmobs") {
                descriptionTranslatable(rmTranslatableKey("command.description"))
                permission = "rebarmobs.command"
                aliases = listOf("rm", "rebarm", "rmobs")

                subCommand("enchantment_book") {
                    descriptionTranslatable(rmTranslatableKey("command.enchantment_book.description"))
                    usage = "<enchantment> [level]"
                    permission = "rebarmobs.command.enchantment_book"
                    playerOnly()
                    execute(EnchantmentBookHandler)
                    tabComplete { _, args ->
                        when (args.size) {
                            1 -> {
                                RegistryAccess
                                    .registryAccess()
                                    .getRegistry(RegistryKey.ENCHANTMENT)
                                    .map { it.key.toString() }
                            }

                            2 -> {
                                (1..5).map { it.toString() }
                            }

                            else -> {
                                listOf()
                            }
                        }
                    }
                }
            }
    }
}
