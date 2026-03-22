# AGENTS.md

## 1. Overview

RebarMobs is a Minecraft Paper plugin (Addon) that built on the Rebar framework with Kotlin DSL extensions from GuizhanLib-KT.

## 2. Folder Structure

- `src/main/java/net/guizhanss/rebarmobs/RebarMobsLoader.java`: Paper PluginLoader that loads Kotlin runtime into classpath.
- `src/main/kotlin/net/guizhanss/rebarmobs/`:
    - `RebarMobsBootstrap.kt`: Paper PluginBootstrap for early registration (enchantments).
    - `RebarMobs.kt`: Main plugin class extending AbstractAddon, manages lifecycle and component registration.
    - `commands/`: Command registration using BaseKommand DSL; handlers in subdirectory.
    - `config/`: Configuration management using yamlConfig DSL.
    - `datatypes/`: Custom PersistentDataType implementations (e.g., EntityType serialization).
    - `guide/`: Guide page definitions for in-game documentation.
    - `items/`: Custom items and blocks; `resources/` for items, `multiblocks/` for structures.
    - `recipes/`: Custom recipe types and recipe loading.
    - `utils/`: NamespacedKey management and translation utilities.
- `src/main/resources/`:
    - `config.yml`: Plugin configuration.
    - `lang/en.yml`: English translations using `%placeholder%` format.
    - `recipes/`: Recipe YAML definitions.
- `build.gradle.kts`: Gradle build with Paper plugin metadata; dependencies on Rebar, Pylon, GuizhanLib.

## 3. Core Behaviors & Patterns

### Item Registration Pattern

Use `RebarItemRegistry` DSL to register items. Items are defined as delegated properties:

```kotlin
object RebarMobsItems : RebarItemRegistry(RebarMobs.instance()) {
    val ITEM_NAME by item<CustomItem> { key = ...; material = ... }
}
```

### Data Persistence (PDC)

Use `persistentItemData` delegated property for ItemStack data storage:

```kotlin
var data: Type by persistentItemData(KEY, DATATYPE) { defaultValue }
```

### Event Listeners

Place Bukkit Listener in companion object of RebarItem subclass. Framework auto-registers:

```kotlin
class MyItem(item: ItemStack) : RebarItem(item) {
    companion object : Listener {
        @EventHandler fun onEvent(e: SomeEvent) { ... }
    }
}
```

### Translation System

Use `RebarArgument.of("name", value)` for named placeholders. Translation files use `%name%` format, NOT `{0}`:

```kotlin
Component.translatable(key).arguments(
    RebarArgument.of("enchantment", name),
    RebarArgument.of("level", level)
)
```

### Command DSL

Use `baseCommand(plugin, name)` from GuizhanLib-KT. Handlers implement `KommandExecutor`:

```kotlin
baseCommand(plugin, "cmd") {
    subCommand("sub") { execute(MyHandler) }
}
```

## 4. Conventions

### Naming

- Kotlin files use PascalCase for classes, camelCase for functions/properties
- NamespacedKey constants in `RebarMobsKeys` object
- Translation keys follow pattern: `rebarmobs.category.item_name.field`

### Code Style

- Run `./gradlew spotlessApply` before committing
- Kotlin uses ktlint, Java uses Google Java Format (AOSP)

### Paper Plugin Structure

- `RebarMobsLoader`: Loads Kotlin stdlib/reflect
- `RebarMobsBootstrap`: Early-stage registration (enchantments, registry entries)
- `RebarMobs`: Main plugin lifecycle

## 5. Working Agreements

- Respond in user's preferred language; if unspecified, infer from codebase (keep tech terms in English, never translate code blocks)
- Create tests/lint only when explicitly requested
- Build context by reviewing related usages and patterns before editing
- Prefer simple solutions; avoid unnecessary abstraction
- Ask for clarification when requirements are ambiguous
- Minimal changes; preserve public APIs
- Run `./gradlew clean shadowJar` after code changes to verify type safety
- New functions: single-purpose, colocated with related code
- External dependencies: only when necessary, explain why

## 6. Resources

Rebar repository: https://github.com/pylonmc/rebar
Pylon repository: https://github.com/pylonmc/pylon
