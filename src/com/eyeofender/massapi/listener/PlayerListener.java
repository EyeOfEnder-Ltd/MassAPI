package com.eyeofender.massapi.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.eyeofender.massapi.database.MassDatabase;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        MassDatabase.setupPlayer(event.getPlayer());
    }

}
