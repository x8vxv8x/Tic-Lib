package com.smd.ticlib.api;

import com.smd.ticlib.module.armor.ArmorTraitCacheModule;
import net.minecraft.entity.player.EntityPlayer;

public final class TicArmor {

    private TicArmor() {
    }

    public static String[] getTraits(EntityPlayer player) {
        return ArmorTraitCacheModule.INSTANCE.getTraits(player);
    }

    public static String[] getSlotTraits(EntityPlayer player, String slotName) {
        return ArmorTraitCacheModule.INSTANCE.getSlotTraits(player, slotName);
    }

    public static boolean hasTrait(EntityPlayer player, String traitId) {
        return ArmorTraitCacheModule.INSTANCE.hasTrait(player, traitId);
    }

    public static boolean hasSlotTrait(EntityPlayer player, String slotName, String traitId) {
        return ArmorTraitCacheModule.INSTANCE.hasSlotTrait(player, slotName, traitId);
    }

    public static boolean refreshCache(EntityPlayer player) {
        return ArmorTraitCacheModule.INSTANCE.refresh(player);
    }
}
