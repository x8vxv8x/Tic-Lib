package com.smd.ticlib.core.state;

import com.smd.ticlib.core.data.TicDataAccess;
import com.smd.ticlib.core.target.TicTarget;
import com.smd.ticlib.core.target.TicTargetKind;
import com.smd.ticlib.core.target.TicTargets;
import com.smd.ticlib.core.tconstruct.TicNativeAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class TicStackState {

    private final TicTarget target;
    private final TicDataAccess data;
    private final TicNativeAccess nativeAccess;

    private TicStackState(TicTarget target) {
        this.target = target;
        this.data = target.data();
        this.nativeAccess = target.nativeAccess();
    }

    public static TicStackState of(ItemStack stack) {
        return of(TicTargets.resolve(stack));
    }

    public static TicStackState of(TicTarget target) {
        return target == null || !target.isValid() ? null : new TicStackState(target);
    }

    public ItemStack stack() {
        return target.stack();
    }

    public TicTargetKind kind() {
        return target.kind();
    }

    public NBTTagCompound root() {
        return target.writableRoot();
    }

    public TicDataAccess data() {
        return data;
    }

    public TicNativeAccess nativeAccess() {
        return nativeAccess;
    }

    public int dataVersion() {
        return data.dataVersion();
    }

    public int dirtyVersion() {
        return data.dirtyVersion();
    }

    public NBTTagCompound component(String id) {
        return data.component(id);
    }

    public NBTTagCompound writableComponent(String id) {
        return data.writableComponent(id);
    }

    public void removeComponent(String id) {
        data.removeComponent(id);
    }

    public void markDirty() {
        data.markDirty();
    }

    public void commit() {
        data.commit();
    }
}
