package com.smd.ticlib.module.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public final class TicFluidTankProperties implements IFluidTankProperties {

    private final FluidStack contents;
    private final int capacity;
    private final boolean canFill;
    private final boolean canDrain;

    public TicFluidTankProperties(@Nullable FluidStack contents, int capacity, boolean canFill, boolean canDrain) {
        this.contents = contents == null ? null : contents.copy();
        this.capacity = Math.max(0, capacity);
        this.canFill = canFill;
        this.canDrain = canDrain;
    }

    @Nullable
    @Override
    public FluidStack getContents() {
        return contents == null ? null : contents.copy();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean canFill() {
        return canFill;
    }

    @Override
    public boolean canDrain() {
        return canDrain;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluidStack) {
        return canFill;
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluidStack) {
        return canDrain;
    }
}
