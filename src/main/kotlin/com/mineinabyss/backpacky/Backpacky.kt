package com.mineinabyss.backpacky

import com.mineinabyss.backpacky.listeners.BackpackListener
import com.mineinabyss.geary.autoscan.autoscan
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.idofront.plugin.listeners
import org.bukkit.plugin.java.JavaPlugin

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
        listeners(BackpackListener())
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
