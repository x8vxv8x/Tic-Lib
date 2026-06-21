package com.smd.ticlib.module.fluid;

import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public interface TicFluidTankView {

    TicFluidTankKind kind();

    String id();

    int tankIndex();

    int capacity();

    @Nullable
    FluidStack fluid();

    boolean canFill();

    boolean canDrain();
}
