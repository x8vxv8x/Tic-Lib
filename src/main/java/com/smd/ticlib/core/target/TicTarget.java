package com.smd.ticlib.core.target;

import com.smd.ticlib.core.data.TicDataAccess;
import com.smd.ticlib.core.nbt.TicNbt;
import com.smd.ticlib.core.tconstruct.TicNativeAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class TicTarget {

    private final ItemStack stack;
    private final TicTargetKind kind;

    TicTarget(ItemStack stack, TicTargetKind kind) {
        this.stack = stack;
        this.kind = kind;
    }

    public ItemStack stack() {
        return stack;
    }

    public TicTargetKind kind() {
        return kind;
    }

    public boolean isValid() {
        return kind != TicTargetKind.UNKNOWN && stack != null && !stack.isEmpty();
    }

    public NBTTagCompound root() {
        return TicNbt.getRoot(stack);
    }

    public NBTTagCompound writableRoot() {
        return TicNbt.getOrCreateRoot(stack);
    }

    public TicDataAccess data() {
        return TicDataAccess.of(this);
    }

    public TicNativeAccess nativeAccess() {
        return TicNativeAccess.of(writableRoot());
    }
}
