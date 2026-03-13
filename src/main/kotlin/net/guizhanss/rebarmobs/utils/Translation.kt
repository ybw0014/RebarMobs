package net.guizhanss.rebarmobs.utils

import io.github.pylonmc.rebar.item.RebarItem

fun RebarItem.translatableKey(path: String) = rmTranslatableKey("item.${key.key}.$path")