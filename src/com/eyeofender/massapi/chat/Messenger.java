package com.eyeofender.massapi.chat;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.eyeofender.massapi.MassAPI;
import com.eyeofender.massapi.database.table.Membership;

public class Messenger {

    private static MassAPI api;
    public static final String STORE_URL = "http://themassmc.com/shop";

    private Messenger() {

    }

    public static void initialize(MassAPI api) {
        Messenger.api = api;
    }

    public static void log(Level level, String message) {
        api.getLogger().log(level, message);
    }

    public static void log(Level level, String message, Object... args) {
        log(level, format(message, args));
    }

    public static void tellPlayer(Player player, String message) {
        player.sendMessage(message);
    }

    public static void tellPlayer(Player player, String message, Object... args) {
        tellPlayer(player, format(message, args));
    }

    private static String format(String message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            message = message.replace("$" + (i + 1), describeObject(args[i]));
        }

        return message;
    }

    private static String describeObject(Object obj) {
        if (obj instanceof ComplexEntityPart) return describeObject(((ComplexEntityPart) obj).getParent());
        else if (obj instanceof Item) return describeMaterial(((Item) obj).getItemStack().getType());
        else if (obj instanceof ItemStack) return describeMaterial(((ItemStack) obj).getType());
        else if (obj instanceof Entity) return describeEntity((Entity) obj);
        else if (obj instanceof Block) return describeMaterial(((Block) obj).getType());
        else if (obj instanceof Material) return describeMaterial((Material) obj);
        else if (obj instanceof Location) return describeLocation((Location) obj);
        else if (obj instanceof World) return ((World) obj).getName();
        else if (obj instanceof Membership) return ((Membership) obj).getType();
        return obj.toString();
    }

    private static String describeEntity(Entity entity) {
        if (entity instanceof Player) return ((Player) entity).getName();
        return entity.getType().toString().toLowerCase().replace("_", " ");
    }

    private static String describeLocation(Location loc) {
        return loc.getX() + ", " + loc.getY() + ", " + loc.getZ();
    }

    private static String describeMaterial(Material material) {
        if (material == Material.INK_SACK) return "dye";
        return material.toString().toLowerCase().replace("_", " ");
    }

}
