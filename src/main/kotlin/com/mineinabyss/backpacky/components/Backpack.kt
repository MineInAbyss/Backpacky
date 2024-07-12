package com.mineinabyss.backpacky.components

import com.mineinabyss.idofront.textcomponents.miniMsg
import dev.triumphteam.gui.components.GuiType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
@SerialName("backpacky:backpack")
data class Backpack(
    @SerialName("title") private val _title: String? = null,
    val guiType: GuiType = GuiType.CHEST,
    val rows: Int = 6
) {
    val title: Component? get() = _title?.miniMsg()
}