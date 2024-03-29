package com.eyeofender.massapi;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.eyeofender.massapi.chat.Messenger;
import com.eyeofender.massapi.database.MassDatabase;
import com.eyeofender.massapi.listener.PlayerListener;

public class MassAPI extends JavaPlugin {

    private Messenger messenger;

    @Override
    public void onEnable() {
        this.messenger = new Messenger(this);
        MassDatabase.initialize(this);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        messenger.log(Level.INFO, "Version $1 enabled succesfully.", getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        MassDatabase.terminate();
        messenger.log(Level.INFO, "Version $1 disabled.", getDescription().getVersion());
    }

    @Override
    public void installDDL() {
        super.installDDL();
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        return MassDatabase.getDatabaseClasses();
    }

    public Messenger getMessenger() {
        return messenger;
    }

}
