package com.mineinabyss.backpacky.components

import com.mineinabyss.idofront.serialization.ItemStackSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack

@Serializable
@SerialName("backpacky:backpack_content")
class BackpackContent(val content: MutableList<@Serializable(ItemStackSerializer::class) ItemStack?> = mutableListOf())