package com.smd.ticlib.module.fluid;

import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

final class TicFluidTank implements TicFluidTankView {

    private final TicFluidTankKind kind;
    private final String id;
    private final int tankIndex;
    private final int capacity;
    private final FluidStack fluid;
    private final boolean canFill;
    private final boolean canDrain;

    TicFluidTank(TicFluidTankKind kind, String id, int tankIndex, int capacity, @Nullable FluidStack fluid, boolean canFill, boolean canDrain) {
        this.kind = kind;
        this.id = id == null ? "" : id;
        this.tankIndex = tankIndex;
        this.capacity = Math.max(0, capacity);
        this.fluid = fluid == null ? null : fluid.copy();
        this.canFill = canFill;
        this.canDrain = canDrain;
    }

    @Override
    public TicFluidTankKind kind() {
        return kind;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public int tankIndex() {
        return tankIndex;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Nullable
    @Override
    public FluidStack fluid() {
        return fluid == null ? null : fluid.copy();
    }

    @Override
    public boolean canFill() {
        return canFill;
    }

    @Override
    public boolean canDrain() {
        return canDrain;
    }
}
