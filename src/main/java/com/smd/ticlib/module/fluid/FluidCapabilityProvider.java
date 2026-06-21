package com.smd.ticlib.module.fluid;

import com.smd.ticlib.core.state.TicStackState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class FluidCapabilityProvider implements ICapabilityProvider {

    private final TicStackState state;
    private final ItemStack stack;
    private final IFluidHandlerItem handler;
    private int cachedDirtyVersion = Integer.MIN_VALUE;
    private boolean cachedHasTank;

    public FluidCapabilityProvider(ItemStack stack) {
        this.state = TicStackState.of(stack);
        this.stack = stack;
        this.handler = new FluidHandler(state);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && hasTankCached();
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing) ? CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(handler) : null;
    }

    private boolean hasTankCached() {
        int dirtyVersion = state == null ? 0 : state.dirtyVersion();
        if (dirtyVersion != cachedDirtyVersion) {
            cachedDirtyVersion = dirtyVersion;
            cachedHasTank = state != null && stack.getCount() == 1 && FluidModule.INSTANCE.hasTank(state);
        }
        return cachedHasTank;
    }
}
