package com.smd.ticlib.module.stats;

import com.smd.ticlib.core.data.TicDataAccess;
import com.smd.ticlib.core.lifecycle.TicLifecycleContext;
import com.smd.ticlib.core.lifecycle.TicModule;
import com.smd.ticlib.core.nbt.TicNbt;
import com.smd.ticlib.core.target.TicTargetKind;
import com.smd.ticlib.core.state.TicStackState;
import com.smd.ticlib.core.tconstruct.TicNativeAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

public final class PersistentStatsModule implements TicModule {

    public static final PersistentStatsModule INSTANCE = new PersistentStatsModule();
    public static final String ID = "ticlib:stats";

    private static final String BONUSES = "Bonuses";
    private static final String APPLIED_TOKENS = "AppliedTokens";

    private PersistentStatsModule() {
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public boolean supports(TicTargetKind kind) {
        return kind == TicTargetKind.TOOL || kind == TicTargetKind.ARMOR;
    }

    @Override
    public void onBuild(TicLifecycleContext context) {
        apply(context);
    }

    @Override
    public void onReplay(TicLifecycleContext context) {
        apply(context);
    }

    public String[] getStats(ItemStack stack) {
        TicStackState state = TicStackState.of(stack);
        if (state == null) {
            return TicNbt.EMPTY_STRINGS;
        }
        return state.nativeAccess().getStatNames();
    }

    public boolean hasStat(ItemStack stack, String statName) {
        TicStackState state = TicStackState.of(stack);
        return state != null && state.nativeAccess().hasNumericStat(statName);
    }

    public float getFloat(ItemStack stack, String statName) {
        TicStackState state = TicStackState.of(stack);
        return state != null && state.nativeAccess().hasNumericStat(statName) ? state.nativeAccess().stats().getFloat(statName) : 0.0F;
    }

    public int getInt(ItemStack stack, String statName) {
        TicStackState state = TicStackState.of(stack);
        return state != null && state.nativeAccess().hasNumericStat(statName) ? state.nativeAccess().stats().getInteger(statName) : 0;
    }

    public boolean add(ItemStack stack, String statName, double amount, String token) {
        TicStackState state = TicStackState.of(stack);
        if (state == null || token == null || token.trim().isEmpty()) {
            return false;
        }
        TicNativeAccess nativeAccess = state.nativeAccess();
        if (!nativeAccess.hasNumericStat(statName)) {
            return false;
        }
        NBTTagCompound component = state.writableComponent(ID);
        if (hasToken(component, token)) {
            return true;
        }

        NBTTagCompound bonuses = component.getCompoundTag(BONUSES);
        bonuses.setDouble(statName, bonuses.getDouble(statName) + amount);
        component.setTag(BONUSES, bonuses);
        addToken(component, token);

        nativeAccess.addNumericStat(statName, amount);
        state.markDirty();
        state.commit();
        return true;
    }

    public boolean apply(TicLifecycleContext context) {
        if (context == null) {
            return false;
        }
        NBTTagCompound component = context.data().component(ID);
        if (component == null || !component.hasKey(BONUSES, Constants.NBT.TAG_COMPOUND)) {
            return false;
        }
        TicNativeAccess nativeAccess = context.nativeAccess();
        NBTTagCompound stats = nativeAccess.stats().copy();
        NBTTagCompound bonuses = component.getCompoundTag(BONUSES);
        boolean changed = false;
        for (String statName : bonuses.getKeySet()) {
            changed |= TicNbt.addNumeric(stats, statName, bonuses.getDouble(statName));
        }
        if (changed) {
            nativeAccess.setStats(stats);
        }
        return changed;
    }

    private static boolean hasToken(NBTTagCompound component, String token) {
        return component.hasKey(APPLIED_TOKENS, Constants.NBT.TAG_COMPOUND)
                && component.getCompoundTag(APPLIED_TOKENS).hasKey(token);
    }

    private static void addToken(NBTTagCompound component, String token) {
        NBTTagCompound tokens = component.getCompoundTag(APPLIED_TOKENS);
        tokens.setDouble(token, 1.0D);
        component.setTag(APPLIED_TOKENS, tokens);
    }
}
