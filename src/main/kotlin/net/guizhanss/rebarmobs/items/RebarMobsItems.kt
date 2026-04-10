package net.guizhanss.rebarmobs.items

import io.github.pylonmc.rebar.block.RebarBlock
import io.github.pylonmc.rebar.item.RebarItem
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import net.guizhanss.guizhanlib.kt.rebar.items.register.RebarItemRegistry
import net.guizhanss.guizhanlib.kt.rebar.items.register.block
import net.guizhanss.guizhanlib.kt.rebar.items.register.blockOnly
import net.guizhanss.guizhanlib.kt.rebar.items.register.item
import net.guizhanss.guizhanlib.kt.rebar.items.register.weapon
import net.guizhanss.rebarmobs.RebarMobs
import net.guizhanss.rebarmobs.guide.RebarMobsPages
import net.guizhanss.rebarmobs.items.blocks.CursedFire
import net.guizhanss.rebarmobs.items.blocks.MobHead
import net.guizhanss.rebarmobs.items.blocks.SoulCage
import net.guizhanss.rebarmobs.items.multiblocks.SoulAltar
import net.guizhanss.rebarmobs.items.resources.SoulShard
import net.guizhanss.rebarmobs.items.tools.QuartzAndSteel
import net.guizhanss.rebarmobs.items.weapons.VileSword
import net.guizhanss.rebarmobs.utils.PlayerHead
import net.guizhanss.rebarmobs.utils.RebarMobsKeys
import net.guizhanss.rebarmobs.utils.rmKey
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.EnumMap

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
            RebarMobsPages.MAIN.addItem(it)
        }
    }

    val CORRUPTED_ESSENCE by item<RebarItem> {
        key = RebarMobsKeys.CORRUPTED_ESSENCE
        material = Material.PURPLE_DYE
        postRegister {
            RebarMobsPages.MAIN.addItem(it)
        }
    }

    val VILE_DUST by item<RebarItem> {
        key = RebarMobsKeys.VILE_DUST
        material = Material.GLOWSTONE_DUST
        postRegister {
            RebarMobsPages.MAIN.addItem(it)
        }
    }

    val CORRUPTED_INGOT by item<RebarItem> {
        key = RebarMobsKeys.CORRUPTED_INGOT
        material = Material.IRON_INGOT
        postRegister {
            RebarMobsPages.MAIN.addItem(it)
        }
    }

    val VILE_SWORD_FRAME by item<RebarItem> {
        key = RebarMobsKeys.VILE_SWORD_FRAME
        material = Material.STICK
        postRegister {
            RebarMobsPages.MAIN.addItem(it)
        }
    }
    // </editor-fold>

    // <editor-fold desc="Multiblocks" defaultstate="collapsed">
    val SOUL_ALTAR by block<SoulAltar> {
        key = RebarMobsKeys.SOUL_ALTAR
        material = Material.GLOWSTONE
        postRegister {
            RebarMobsPages.MAIN.addItem(it)
        }
    }
    // </editor-fold>

    // <editor-fold desc="Blocks" defaultstate="collapsed">
    val SOUL_CAGE by block<SoulCage> {
        key = RebarMobsKeys.SOUL_CAGE
        material = Material.SPAWNER
        builder {
            editMeta { meta ->
                meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            }
        }
        postRegister {
            RebarMobsPages.MAIN.addItem(it)
        }
    }

    val CURSED_FIRE by blockOnly<CursedFire> {
        key = RebarMobsKeys.CURSED_FIRE
        material = Material.SOUL_FIRE
    }
    // </editor-fold>

    // <editor-fold desc="Tools" defaultstate="collapsed">
    val QUARTZ_AND_STEEL by item<QuartzAndSteel> {
        key = RebarMobsKeys.QUARTZ_AND_STEEL
        material = Material.FLINT_AND_STEEL
        postRegister {
            RebarMobsPages.MAIN.addItem(it)
        }
    }
    // </editor-fold>

    // <editor-fold desc="Weapons" defaultstate="collapsed">
    val VILE_SWORD by weapon<VileSword> {
        key = RebarMobsKeys.VILE_SWORD
        material = Material.IRON_SWORD
        postRegister {
            RebarMobsPages.MAIN.addItem(it)
        }
    }
    // </editor-fold>

    // <editor-fold desc="Mob Heads" defaultstate="collapsed">
    val MOB_HEADS: Map<EntityType, ItemStack>
        field = EnumMap(EntityType::class.java)

    private fun registerMobHead(entityType: EntityType, head: PlayerHead) {
        if (MOB_HEADS.containsKey(entityType)) error("Entity type $entityType is already registered as mob head!")

        val nsKey = rmKey("mob_head_$entityType")
        val stack = ItemStackBuilder.rebar(head.createHeadItem(), nsKey).build()
        RebarItem.register<MobHead.Item>(stack)
        RebarBlock.register<MobHead>(nsKey, Material.PLAYER_HEAD)
        RebarMobsPages.MAIN.addItem(stack)
    }

    init {
        // Hostile mobs
        registerMobHead(EntityType.BLAZE, PlayerHead.ENTITY_BLAZE)
        registerMobHead(EntityType.CAVE_SPIDER, PlayerHead.ENTITY_CAVE_SPIDER)
        registerMobHead(EntityType.DROWNED, PlayerHead.ENTITY_DROWNED)
        registerMobHead(EntityType.ELDER_GUARDIAN, PlayerHead.ENTITY_ELDER_GUARDIAN)
        registerMobHead(EntityType.ENDERMAN, PlayerHead.ENTITY_ENDERMAN)
        registerMobHead(EntityType.EVOKER, PlayerHead.ENTITY_EVOKER)
        registerMobHead(EntityType.GHAST, PlayerHead.ENTITY_GHAST)
        registerMobHead(EntityType.GUARDIAN, PlayerHead.ENTITY_GUARDIAN)
        registerMobHead(EntityType.HUSK, PlayerHead.ENTITY_HUSK)
        registerMobHead(EntityType.ILLUSIONER, PlayerHead.ENTITY_ILLUSIONER)
        registerMobHead(EntityType.MAGMA_CUBE, PlayerHead.ENTITY_MAGMA_CUBE)
        registerMobHead(EntityType.SHULKER, PlayerHead.ENTITY_SHULKER)
        registerMobHead(EntityType.SLIME, PlayerHead.ENTITY_SLIME)
        registerMobHead(EntityType.SPIDER, PlayerHead.ENTITY_SPIDER)
        registerMobHead(EntityType.STRAY, PlayerHead.ENTITY_STRAY)
        registerMobHead(EntityType.VEX, PlayerHead.ENTITY_VEX)
        registerMobHead(EntityType.VINDICATOR, PlayerHead.ENTITY_VINDICATOR)
        registerMobHead(EntityType.WITCH, PlayerHead.ENTITY_WITCH)
        registerMobHead(EntityType.WITHER, PlayerHead.ENTITY_WITHER)
        registerMobHead(EntityType.ZOMBIE_VILLAGER, PlayerHead.ENTITY_ZOMBIE_VILLAGER)
        registerMobHead(EntityType.RAVAGER, PlayerHead.ENTITY_RAVAGER)
        registerMobHead(EntityType.PILLAGER, PlayerHead.ENTITY_PILLAGER)
        registerMobHead(EntityType.PIGLIN, PlayerHead.ENTITY_PIGLIN)
        registerMobHead(EntityType.ZOMBIFIED_PIGLIN, PlayerHead.ENTITY_ZOMBIFIED_PIGLIN)
        registerMobHead(EntityType.BOGGED, PlayerHead.ENTITY_BOGGED)
        registerMobHead(EntityType.BREEZE, PlayerHead.ENTITY_BREEZE)
        registerMobHead(EntityType.CREAKING, PlayerHead.ENTITY_CREAKING)
        registerMobHead(EntityType.ZOMBIE_NAUTILUS, PlayerHead.ENTITY_ZOMBIE_NAUTILUS)
        registerMobHead(EntityType.CAMEL_HUSK, PlayerHead.ENTITY_CAMEL_HUSK)
        registerMobHead(EntityType.PARCHED, PlayerHead.ENTITY_PARCHED)

        // Passive mobs
        registerMobHead(EntityType.BAT, PlayerHead.ENTITY_BAT)
        registerMobHead(EntityType.CHICKEN, PlayerHead.ENTITY_CHICKEN)
        registerMobHead(EntityType.COW, PlayerHead.ENTITY_COW)
        registerMobHead(EntityType.DOLPHIN, PlayerHead.ENTITY_DOLPHIN)
        registerMobHead(EntityType.HORSE, PlayerHead.ENTITY_HORSE)
        registerMobHead(EntityType.IRON_GOLEM, PlayerHead.ENTITY_IRON_GOLEM)
        registerMobHead(EntityType.LLAMA, PlayerHead.ENTITY_LLAMA)
        registerMobHead(EntityType.MOOSHROOM, PlayerHead.ENTITY_MOOSHROOM)
        registerMobHead(EntityType.OCELOT, PlayerHead.ENTITY_OCELOT)
        registerMobHead(EntityType.PARROT, PlayerHead.ENTITY_PARROT)
        registerMobHead(EntityType.PIG, PlayerHead.ENTITY_PIG)
        registerMobHead(EntityType.POLAR_BEAR, PlayerHead.ENTITY_POLAR_BEAR)
        registerMobHead(EntityType.RABBIT, PlayerHead.ENTITY_RABBIT)
        registerMobHead(EntityType.SHEEP, PlayerHead.ENTITY_SHEEP)
        registerMobHead(EntityType.SQUID, PlayerHead.ENTITY_SQUID)
        registerMobHead(EntityType.TURTLE, PlayerHead.ENTITY_TURTLE)
        registerMobHead(EntityType.VILLAGER, PlayerHead.ENTITY_VILLAGER)
        registerMobHead(EntityType.WANDERING_TRADER, PlayerHead.ENTITY_WANDERING_TRADER)
        registerMobHead(EntityType.FOX, PlayerHead.ENTITY_FOX)
        registerMobHead(EntityType.PANDA, PlayerHead.ENTITY_PANDA)
        registerMobHead(EntityType.STRIDER, PlayerHead.ENTITY_STRIDER)
        registerMobHead(EntityType.AXOLOTL, PlayerHead.ENTITY_AXOLOTL)
        registerMobHead(EntityType.GLOW_SQUID, PlayerHead.ENTITY_GLOW_SQUID)
        registerMobHead(EntityType.GOAT, PlayerHead.ENTITY_GOAT)
        registerMobHead(EntityType.ALLAY, PlayerHead.ENTITY_ALLAY)
        registerMobHead(EntityType.FROG, PlayerHead.ENTITY_FROG)
        registerMobHead(EntityType.TADPOLE, PlayerHead.ENTITY_TADPOLE)
        registerMobHead(EntityType.CAMEL, PlayerHead.ENTITY_CAMEL)
        registerMobHead(EntityType.SNIFFER, PlayerHead.ENTITY_SNIFFER)
        registerMobHead(EntityType.ARMADILLO, PlayerHead.ENTITY_ARMADILLO)
        registerMobHead(EntityType.HAPPY_GHAST, PlayerHead.ENTITY_HAPPY_GHAST)
        registerMobHead(EntityType.COPPER_GOLEM, PlayerHead.ENTITY_COPPER_GOLEM)
        registerMobHead(EntityType.NAUTILUS, PlayerHead.ENTITY_NAUTILUS)
    }
    // </editor-fold>
}
