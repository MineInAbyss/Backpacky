package com.mineinabyss.backpacky.listeners

import com.mineinabyss.backpacky.components.Backpack
import com.mineinabyss.backpacky.components.BackpackContent
import com.mineinabyss.backpacky.toGearyOrNull
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.geary.papermc.datastore.decode
import com.mineinabyss.geary.papermc.datastore.decodeComponents
import com.mineinabyss.geary.papermc.datastore.encodeComponentsTo
import com.mineinabyss.geary.papermc.datastore.has
import com.mineinabyss.geary.papermc.tracking.items.inventory.toGeary
import com.mineinabyss.geary.serialization.getOrSetPersisting
import com.mineinabyss.geary.serialization.setPersisting
import com.mineinabyss.idofront.messaging.broadcast
import com.mineinabyss.idofront.serialization.ItemStackSerializer
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.components.ScrollType
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.ScrollingGui
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class BackpackListener : Listener {

    @EventHandler
    fun PlayerInteractEvent.onOpenBackpack() {
        val (item, gearyItem) = (item ?: return) to (item?.toGearyOrNull() ?: return)
        val backpack = gearyItem.get<Backpack>() ?: return
        val backpackContent = gearyItem.getOrSetPersisting { BackpackContent() }
        val title = backpack.title ?: item.itemMeta?.displayName().takeIf { item.itemMeta.hasDisplayName() } ?: item.itemMeta.itemName()
        val guiType = backpack.guiType

        val backpackGui = Gui.gui(guiType).rows(backpack.rows).title(title).create()
        backpackGui.setOpenGuiAction {
            it.inventory.clear()
            it.inventory.contents = backpackContent.content.toTypedArray()
        }

        backpackGui.setCloseGuiAction {
            backpackContent.content.clear()
            backpackContent.content.addAll(it.inventory.contents)
            gearyItem.encodeComponentsTo(item)
        }

        backpackGui.setDefaultClickAction {
            if (player.inventory.toGeary()?.get(it.slot)?.has<Backpack>() == true) isCancelled = true
            if (it.cursor.itemMeta?.persistentDataContainer?.has<Backpack>() == true) isCancelled = true
            if (it.currentItem?.itemMeta?.persistentDataContainer?.has<Backpack>() == true) isCancelled = true
        }

        backpackGui.open(player)
    }
}