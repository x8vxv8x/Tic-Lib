package com.smd.ticlib.module.fluid;

import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public final class TicFluidOperationResult {

    private final int amount;
    private final FluidStack fluid;
    private final boolean changed;

    private TicFluidOperationResult(int amount, @Nullable FluidStack fluid, boolean changed) {
        this.amount = Math.max(0, amount);
        this.fluid = fluid == null ? null : fluid.copy();
        this.changed = changed;
    }

    public static TicFluidOperationResult empty() {
        return new TicFluidOperationResult(0, null, false);
    }

    public static TicFluidOperationResult of(int amount, @Nullable FluidStack fluid, boolean changed) {
        return new TicFluidOperationResult(amount, fluid, changed);
    }

    public int amount() {
        return amount;
    }

    @Nullable
    public FluidStack fluid() {
        return fluid == null ? null : fluid.copy();
    }

    public boolean changed() {
        return changed;
    }
}
