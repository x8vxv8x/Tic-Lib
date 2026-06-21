package com.smd.ticlib.module.fluid;

import com.smd.ticlib.core.lifecycle.TicLifecycleContext;
import com.smd.ticlib.core.lifecycle.TicModule;
import com.smd.ticlib.core.state.TicStackState;
import com.smd.ticlib.core.target.TicTargetKind;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public final class FluidModule implements TicModule {

    public static final FluidModule INSTANCE = new FluidModule();
    public static final String ID = "ticlib:fluid";

    private static final String CAPACITY = "Capacity";
    private static final String FLUID = "Fluid";

    private FluidModule() {
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
    public void onValidate(TicLifecycleContext context) {
        ItemStack stack = context.stack();
        if (stack == null || stack.isEmpty()) {
            return;
        }
        TicStackState state = TicStackState.of(stack);
        if (state != null) {
            normalize(state);
        }
    }

    public boolean hasTank(ItemStack stack) {
        TicStackState state = TicStackState.of(stack);
        return state != null && stack.getCount() == 1 && hasTank(state);
    }

    public int getCapacity(ItemStack stack) {
        TicStackState state = TicStackState.of(stack);
        return state == null ? 0 : getCapacity(state);
    }

    public boolean setCapacity(ItemStack stack, int capacity) {
        TicStackState state = TicStackState.of(stack);
        if (state == null) {
            return false;
        }
        NBTTagCompound component = state.writableComponent(ID);
        component.setInteger(CAPACITY, Math.max(0, capacity));
        normalize(state);
        state.markDirty();
        state.commit();
        return true;
    }

    @Nullable
    public FluidStack getFluid(ItemStack stack) {
        TicStackState state = TicStackState.of(stack);
        return state == null ? null : getFluid(state);
    }

    @Nullable
    public FluidStack setFluid(ItemStack stack, @Nullable FluidStack fluid) {
        TicStackState state = TicStackState.of(stack);
        if (state == null) {
            return null;
        }
        FluidStack stored = setFluid(state, fluid);
        state.markDirty();
        state.commit();
        return stored;
    }

    int getCapacity(TicStackState state) {
        NBTTagCompound component = state.component(ID);
        return component == null ? 0 : Math.max(0, component.getInteger(CAPACITY));
    }

    @Nullable
    FluidStack getFluid(TicStackState state) {
        NBTTagCompound component = state.component(ID);
        if (component == null || !component.hasKey(FLUID, Constants.NBT.TAG_COMPOUND)) {
            return null;
        }
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(component.getCompoundTag(FLUID));
        return fluid == null || fluid.amount <= 0 ? null : fluid;
    }

    @Nullable
    FluidStack setFluid(TicStackState state, @Nullable FluidStack fluid) {
        int capacity = getCapacity(state);
        NBTTagCompound component = state.writableComponent(ID);
        if (fluid == null || fluid.amount <= 0 || capacity <= 0) {
            component.removeTag(FLUID);
            return null;
        }
        FluidStack stored = fluid.copy();
        if (stored.amount > capacity) {
            stored.amount = capacity;
        }
        component.setTag(FLUID, stored.writeToNBT(new NBTTagCompound()));
        return stored;
    }

    public int fill(ItemStack stack, FluidStack resource, boolean doFill) {
        TicStackState state = TicStackState.of(stack);
        if (resource == null || resource.amount <= 0 || state == null || stack.getCount() != 1) {
            return 0;
        }
        return new FluidHandler(state).fill(resource, doFill);
    }

    @Nullable
    public FluidStack drain(ItemStack stack, FluidStack resource, boolean doDrain) {
        TicStackState state = TicStackState.of(stack);
        if (resource == null || resource.amount <= 0 || state == null || stack.getCount() != 1) {
            return null;
        }
        return new FluidHandler(state).drain(resource, doDrain);
    }

    @Nullable
    public FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain) {
        TicStackState state = TicStackState.of(stack);
        if (maxDrain <= 0 || state == null || stack.getCount() != 1) {
            return null;
        }
        return new FluidHandler(state).drain(maxDrain, doDrain);
    }

    int fillInternal(TicStackState state, FluidStack resource, boolean doFill) {
        int capacity = getCapacity(state);
        if (capacity <= 0 || resource == null || resource.amount <= 0) {
            return 0;
        }
        FluidStack current = getFluid(state);
        if (current == null) {
            int filled = Math.min(capacity, resource.amount);
            if (doFill && filled > 0) {
                setFluid(state, new FluidStack(resource, filled));
                state.markDirty();
                state.commit();
            }
            return filled;
        }
        if (!current.isFluidEqual(resource) || current.amount >= capacity) {
            return 0;
        }
        int filled = Math.min(resource.amount, capacity - current.amount);
        if (doFill && filled > 0) {
            current.amount += filled;
            setFluid(state, current);
            state.markDirty();
            state.commit();
        }
        return filled;
    }

    @Nullable
    FluidStack drainInternal(TicStackState state, FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount <= 0) {
            return null;
        }
        FluidStack current = getFluid(state);
        if (current == null || !current.isFluidEqual(resource)) {
            return null;
        }
        return drainCurrent(state, current, resource.amount, doDrain);
    }

    @Nullable
    FluidStack drainInternal(TicStackState state, int maxDrain, boolean doDrain) {
        if (maxDrain <= 0) {
            return null;
        }
        FluidStack current = getFluid(state);
        return current == null ? null : drainCurrent(state, current, maxDrain, doDrain);
    }

    @Nullable
    private FluidStack drainCurrent(TicStackState state, FluidStack current, int maxDrain, boolean doDrain) {
        int drained = Math.min(maxDrain, current.amount);
        if (drained <= 0) {
            return null;
        }
        FluidStack result = new FluidStack(current, drained);
        if (doDrain) {
            current.amount -= drained;
            setFluid(state, current.amount <= 0 ? null : current);
            state.markDirty();
            state.commit();
        }
        return result;
    }

    boolean hasTank(TicStackState state) {
        return getCapacity(state) > 0 || new FluidHandler(state).hasModifierTank();
    }

    private void normalize(TicStackState state) {
        int capacity = getCapacity(state);
        FluidStack fluid = getFluid(state);
        if (capacity <= 0 || fluid == null) {
            setFluid(state, null);
            return;
        }
        if (fluid.amount > capacity) {
            fluid.amount = capacity;
            setFluid(state, fluid);
        }
    }
}
