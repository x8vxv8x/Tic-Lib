package com.smd.ticlib.api;

import com.smd.ticlib.core.target.TicTargets;
import crafttweaker.api.entity.IEntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public final class TicItems {

    private TicItems() {
    }

    public static boolean isTool(ItemStack stack) {
        return TicTargets.isTool(stack);
    }

    public static boolean isArmor(ItemStack stack) {
        return TicTargets.isArmor(stack);
    }

    public static boolean isTarget(ItemStack stack) {
        return TicTargets.isTarget(stack);
    }

    public static ItemStack[] getAllKnownItems() {
        return TicTargets.getAllKnownItems();
    }

    public static String getArmorType(ItemStack stack) {
        return TicTargets.getArmorType(stack);
    }

    public static EntityEquipmentSlot getArmorSlot(ItemStack stack) {
        return TicTargets.getArmorSlot(stack);
    }

    public static String[] getMaterials(ItemStack stack) {
        return TicTargets.getMaterials(stack);
    }
}
