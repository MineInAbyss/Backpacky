package com.mineinabyss.backpacky.listeners

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.mineinabyss.backpacky.backpacky
import com.mineinabyss.backpacky.components.Backpack
import com.mineinabyss.backpacky.components.BackpackContent
import com.mineinabyss.backpacky.isBackpack
import com.mineinabyss.backpacky.toGearyOrNull
import com.mineinabyss.geary.papermc.datastore.decode
import com.mineinabyss.geary.papermc.datastore.encode
import com.mineinabyss.geary.papermc.datastore.encodeComponentsTo
import com.mineinabyss.geary.papermc.datastore.remove
import com.mineinabyss.geary.papermc.tracking.items.inventory.toGeary
import com.mineinabyss.geary.serialization.getOrSetPersisting
import com.mineinabyss.geary.serialization.setPersisting
import com.mineinabyss.idofront.items.editItemMeta
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.ShapedRecipe

class BackpackListener : Listener {

    @EventHandler
    fun PrepareItemCraftEvent.onUpgradeBackpack() {
        if (recipe != null || inventory.matrix.none { it?.isBackpack() == true }) return

        val matrix = inventory.matrix.map { it?.clone()?.editItemMeta { persistentDataContainer.remove<BackpackContent>() } }.toTypedArray()
        val recipesWithBackpack = Bukkit.recipeIterator().asSequence()
            .filterIsInstance<ShapedRecipe>().filter { it.result.isBackpack() }
        val customRecipeResult = recipesWithBackpack.filter { recipe ->
            val ingredients = recipe.shape.flatMap { it.mapNotNull { c -> recipe.choiceMap[c] } }
            ingredients.zip(matrix).all { it.first.test(it.second ?: ItemStack.empty()) }
        }.firstOrNull()

        val backpack = inventory.matrix.first { it?.isBackpack() == true }?.itemMeta?.persistentDataContainer?.decode<BackpackContent>() ?: return
        inventory.result = customRecipeResult?.result?.editItemMeta {
            persistentDataContainer.encode(backpack)
        }

    }

    @EventHandler
    fun PlayerInteractEvent.onOpenBackpack() {
        // Disable from placing blocks etc with offhand if a backpack is being opened
        if (hand == EquipmentSlot.OFF_HAND && player.inventory.itemInMainHand.isBackpack())
            setUseItemInHand(Event.Result.DENY)

        val (item, gearyItem) = (item ?: return) to (item?.toGearyOrNull() ?: return)
        val backpack = gearyItem.get<Backpack>() ?: return
        val backpackContent = gearyItem.getOrSetPersisting { BackpackContent() }
        val title = backpack.title ?: item.itemMeta?.displayName().takeIf { item.itemMeta.hasDisplayName() }
        ?: item.itemMeta.itemName()

        val backpackGui = Gui.gui(backpack.guiType).title(title).let {
            if (backpack.guiType == GuiType.CHEST) it.rows(backpack.rows.coerceIn(1, 6)) else it
        }.create()

        backpackGui.setOpenGuiAction {
            it.inventory.clear()
            it.inventory.contents = backpackContent.content.toTypedArray()
        }

        backpackGui.setCloseGuiAction {
            backpackContent.content.clear()
            backpackContent.content.addAll(it.inventory.contents)
            gearyItem.setPersisting(backpackContent)
            gearyItem.encodeComponentsTo(item)
        }

        backpackGui.setDefaultClickAction {
            if (it.click == ClickType.SWAP_OFFHAND && player.inventory.itemInOffHand.isBackpack()) it.isCancelled = true
            if (it.clickedInventory is PlayerInventory && player.inventory.toGeary()?.get(it.slot)?.has<Backpack>() == true)
                it.isCancelled = true
            if (it.cursor.isBackpack() || it.currentItem?.isBackpack() == true) it.isCancelled = true

            backpacky.launch {
                delay(1)
                gearyItem.setPersisting(BackpackContent(it.inventory.contents.toMutableList()))
                gearyItem.encodeComponentsTo(item)
            }
        }

        backpackGui.setDragAction {
            backpacky.launch {
                delay(1.ticks)
                gearyItem.setPersisting(BackpackContent(it.inventory.contents.toMutableList()))
                gearyItem.encodeComponentsTo(item)
            }
        }

        backpackGui.open(player)
        setUseItemInHand(Event.Result.DENY)
    }
}