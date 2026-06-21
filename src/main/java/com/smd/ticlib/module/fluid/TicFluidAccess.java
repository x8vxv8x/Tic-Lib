package com.smd.ticlib.module.fluid;

import com.smd.ticlib.core.state.TicStackState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.Arrays;

public final class TicFluidAccess {

    private final TicStackState state;
    private final FluidHandler handler;

    public TicFluidAccess(TicStackState state) {
        this.state = state;
        this.handler = new FluidHandler(state);
    }

    public boolean isValid() {
        return state != null && !state.stack().isEmpty();
    }

    public ItemStack stack() {
        return state == null ? ItemStack.EMPTY : state.stack();
    }

    public boolean hasAnyTank() {
        return isValid() && FluidModule.INSTANCE.hasTank(state);
    }

    public int primaryCapacity() {
        return isValid() ? FluidModule.INSTANCE.getCapacity(state) : 0;
    }

    @Nullable
    public FluidStack primaryFluid() {
        return isValid() ? FluidModule.INSTANCE.getFluid(state) : null;
    }

    public boolean setPrimaryCapacity(int capacity) {
        if (!isValid()) {
            return false;
        }
        return FluidModule.INSTANCE.setCapacity(state.stack(), capacity);
    }

    @Nullable
    public FluidStack setPrimaryFluid(@Nullable FluidStack fluid) {
        if (!isValid()) {
            return null;
        }
        return FluidModule.INSTANCE.setFluid(state.stack(), fluid);
    }

    public boolean clearPrimaryFluid() {
        return setPrimaryFluid(null) == null;
    }

    public TicFluidOperationResult fill(FluidStack resource, boolean commit) {
        if (!isValid() || resource == null || resource.amount <= 0) {
            return TicFluidOperationResult.empty();
        }
        int amount = handler.fill(resource, commit);
        return amount <= 0 ? TicFluidOperationResult.empty() : TicFluidOperationResult.of(amount, new FluidStack(resource, amount), commit);
    }

    public TicFluidOperationResult drain(FluidStack resource, boolean commit) {
        if (!isValid() || resource == null || resource.amount <= 0) {
            return TicFluidOperationResult.empty();
        }
        FluidStack drained = handler.drain(resource, commit);
        return drained == null || drained.amount <= 0 ? TicFluidOperationResult.empty() : TicFluidOperationResult.of(drained.amount, drained, commit);
    }

    public TicFluidOperationResult drain(int maxDrain, boolean commit) {
        if (!isValid() || maxDrain <= 0) {
            return TicFluidOperationResult.empty();
        }
        FluidStack drained = handler.drain(maxDrain, commit);
        return drained == null || drained.amount <= 0 ? TicFluidOperationResult.empty() : TicFluidOperationResult.of(drained.amount, drained, commit);
    }

    public TicFluidTankView[] tanks() {
        return isValid() ? handler.describeTanks() : new TicFluidTankView[0];
    }

    public TicFluidTankView primaryTank() {
        if (!isValid()) {
            return new TicFluidTank(TicFluidTankKind.PRIMARY, "primary", 0, 0, null, true, true);
        }
        return new TicFluidTank(TicFluidTankKind.PRIMARY, "primary", 0, primaryCapacity(), primaryFluid(), true, true);
    }

    public TicFluidTankView[] modifierTanks() {
        if (!isValid()) {
            return new TicFluidTankView[0];
        }
        return Arrays.stream(handler.describeTanks())
                .filter(tank -> tank.kind() == TicFluidTankKind.MODIFIER)
                .toArray(TicFluidTankView[]::new);
    }

    public IFluidHandlerItem handler() {
        return handler;
    }

    public IFluidTankProperties[] tankProperties() {
        return handler.getTankProperties();
    }
}
