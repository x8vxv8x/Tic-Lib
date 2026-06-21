package com.smd.ticlib.module.fluid;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public interface TicFluidTankProvider {

    default int getTanks(ItemStack stack, NBTTagCompound modifierTag) {
        return 1;
    }

    default int getTankCapacity(ItemStack stack, NBTTagCompound modifierTag, int tank) {
        return 0;
    }

    @Nullable
    default FluidStack getFluidInTank(ItemStack stack, NBTTagCompound modifierTag, int tank) {
        return null;
    }

    default int fill(ItemStack stack, NBTTagCompound modifierTag, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Nullable
    default FluidStack drain(ItemStack stack, NBTTagCompound modifierTag, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Nullable
    default FluidStack drain(ItemStack stack, NBTTagCompound modifierTag, int maxDrain, boolean doDrain) {
        return null;
    }
}
