package com.eyeofender.massapi.database;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.persistence.PersistenceException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.eyeofender.massapi.MassAPI;
import com.eyeofender.massapi.chat.Messenger;
import com.eyeofender.massapi.database.table.Membership;

public class MassDatabase {
    private static MassAPI api;
    private static int taskId;

    private MassDatabase() {

    }

    public static void initialize(MassAPI api) {
        MassDatabase.api = api;

        try {
            api.getDatabase().find(Membership.class).findRowCount();
        } catch (PersistenceException ex) {
            api.getMessenger().log(Level.INFO, "Installing database due to first time usage.");
            api.installDDL();
        }

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(api, new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    validateMembership(player, false);
                }
            }
        }, 0L, 20L * 60L);
    }

    public static void terminate() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public static Membership getMembership(Player player) {
        return api.getDatabase().find(Membership.class).where().ieq("name", player.getName()).findUnique();
    }

    public static void saveMembership(Membership membership) {
        api.getDatabase().update(membership);
    }

    public static void validateMembership(Player player, boolean warn) {
        Messenger messenger = api.getMessenger();
        Membership membership = getMembership(player);
        if (membership == null) return;

        Date date = new Date(new java.util.Date().getTime());
        Date expiry = membership.getExpiry();

        refreshName(player, membership);

        if (expiry == null) return;

        if (expiry.after(date)) {
            if (warn) {
                long days = TimeUnit.MILLISECONDS.toDays(expiry.getTime() - date.getTime()) + 1;
                if (days <= 7) {
                    messenger.tellPlayer(player, "Your $1 membership will expire in $2 day$3!", membership, days, days == 1 ? "" : "s");
                    messenger.tellPlayer(player, "Visit $1 to renew your membership.", Messenger.STORE_URL);
                }
            }
            return;
        }

        messenger.tellPlayer(player, "Your $1 membership has expired!", membership);
        messenger.tellPlayer(player, "Visit $1 to renew your membership.", "http://themassmc.com/shop");

        api.getDatabase().delete(membership);
        refreshName(player, null);
    }

    private static void refreshName(Player player, Membership membership) {
        String prefix = membership != null ? membership.getPrefix() : "&f";
        player.setDisplayName(ChatColor.translateAlternateColorCodes('&', prefix) + player.getName() + ChatColor.RESET);
    }

    public static void setupPlayer(Player player) {
        validateMembership(player, true);
    }

    public static List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Membership.class);
        return list;
    }

}
