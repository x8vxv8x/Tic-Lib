package com.smd.ticlib.api;

import com.smd.ticlib.core.nbt.TicNbt;
import com.smd.ticlib.core.state.TicStackState;
import com.smd.ticlib.core.tconstruct.TicNativeAccess;
import com.smd.ticlib.module.stats.PersistentStatsModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.utils.Tags;

public final class TicStats {

    private TicStats() {
    }

    public static boolean setBroken(ItemStack stack, boolean broken) {
        TicStackState state = TicStackState.of(stack);
        if (state == null) {
            return false;
        }
        NBTTagCompound stats = state.nativeAccess().stats().copy();
        stats.setBoolean(Tags.BROKEN, broken);
        state.nativeAccess().setStats(stats);
        state.commit();
        return true;
    }

    public static String[] getStats(ItemStack stack) {
        return PersistentStatsModule.INSTANCE.getStats(stack);
    }

    public static String[] getStats(NBTTagCompound root) {
        TicNativeAccess access = TicNativeAccess.of(root);
        return access == null ? TicNbt.EMPTY_STRINGS : access.getStatNames();
    }

    public static boolean hasStat(ItemStack stack, String statName) {
        return PersistentStatsModule.INSTANCE.hasStat(stack, statName);
    }

    public static boolean hasStat(NBTTagCompound root, String statName) {
        TicNativeAccess access = TicNativeAccess.of(root);
        return access != null && access.hasNumericStat(statName);
    }

    public static float getFloat(ItemStack stack, String statName) {
        return PersistentStatsModule.INSTANCE.getFloat(stack, statName);
    }

    public static int getInt(ItemStack stack, String statName) {
        return PersistentStatsModule.INSTANCE.getInt(stack, statName);
    }

    public static float getFloat(NBTTagCompound root, String statName) {
        TicNativeAccess access = TicNativeAccess.of(root);
        return access != null && access.hasNumericStat(statName) ? access.stats().getFloat(statName) : 0.0F;
    }

    public static int getInt(NBTTagCompound root, String statName) {
        TicNativeAccess access = TicNativeAccess.of(root);
        return access != null && access.hasNumericStat(statName) ? access.stats().getInteger(statName) : 0;
    }

    public static boolean add(ItemStack stack, String statName, double amount, String token) {
        return PersistentStatsModule.INSTANCE.add(stack, statName, amount, token);
    }

    public static boolean addNow(NBTTagCompound root, String statName, double amount) {
        TicNativeAccess access = TicNativeAccess.of(root);
        return access != null && access.addNumericStat(statName, amount);
    }
}
