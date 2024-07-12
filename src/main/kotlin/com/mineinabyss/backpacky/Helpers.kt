package com.mineinabyss.backpacky

import com.mineinabyss.geary.datatypes.Component
import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.papermc.tracking.items.gearyItems
import com.mineinabyss.idofront.nms.nbt.fastPDC
import org.bukkit.inventory.ItemStack

internal fun ItemStack.toGearyOrNull(): GearyEntity? = gearyItems.itemProvider.deserializeItemStackToEntity(this.fastPDC)
internal inline fun <reified T : Component> ItemStack.decode(): T? = gearyItems.itemProvider.deserializeItemStackToEntity(this.fastPDC)?.get<T>()