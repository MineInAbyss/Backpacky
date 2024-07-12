package com.mineinabyss.backpacky

import com.mineinabyss.backpacky.listeners.BackpackListener
import com.mineinabyss.geary.autoscan.autoscan
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.idofront.plugin.listeners
import org.bukkit.plugin.java.JavaPlugin

lateinit var backpacky: Backpacky
class Backpacky : JavaPlugin() {

    override fun onLoad() {
        geary {
            autoscan(classLoader, "com.mineinabyss.backpacky.components") {
                components()
            }
        }
    }

    override fun onEnable() {
        // Plugin startup logic
        backpacky = this
        listeners(BackpackListener())
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
