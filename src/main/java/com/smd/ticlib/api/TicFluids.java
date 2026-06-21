package com.smd.ticlib.api;

import com.smd.ticlib.core.state.TicStackState;
import com.smd.ticlib.module.fluid.TicFluidAccess;
import com.smd.ticlib.module.fluid.TicFluidOperationResult;
import com.smd.ticlib.module.fluid.TicFluidTankView;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public final class TicFluids {

    private TicFluids() {
    }

    public static TicFluidAccess of(ItemStack stack) {
        return new TicFluidAccess(TicStackState.of(stack));
    }

    public static boolean hasTank(ItemStack stack) {
        return of(stack).hasAnyTank();
    }

    public static IFluidTankProperties[] getTankProperties(ItemStack stack) {
        return of(stack).tankProperties();
    }

    public static TicFluidTankView[] getTanks(ItemStack stack) {
        return of(stack).tanks();
    }

    public static int getCapacity(ItemStack stack) {
        return of(stack).primaryCapacity();
    }

    public static boolean setCapacity(ItemStack stack, int capacity) {
        return of(stack).setPrimaryCapacity(capacity);
    }

    @Nullable
    public static FluidStack getFluid(ItemStack stack) {
        return of(stack).primaryFluid();
    }

    @Nullable
    public static FluidStack setFluid(ItemStack stack, @Nullable FluidStack fluid) {
        return of(stack).setPrimaryFluid(fluid);
    }

    public static boolean clearFluid(ItemStack stack) {
        return of(stack).clearPrimaryFluid();
    }

    public static int fill(ItemStack stack, FluidStack resource, boolean doFill) {
        return of(stack).fill(resource, doFill).amount();
    }

    public static TicFluidOperationResult fillDetailed(ItemStack stack, FluidStack resource, boolean doFill) {
        return of(stack).fill(resource, doFill);
    }

    @Nullable
    public static FluidStack drain(ItemStack stack, FluidStack resource, boolean doDrain) {
        return of(stack).drain(resource, doDrain).fluid();
    }

    public static TicFluidOperationResult drainDetailed(ItemStack stack, FluidStack resource, boolean doDrain) {
        return of(stack).drain(resource, doDrain);
    }

    @Nullable
    public static FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain) {
        return of(stack).drain(maxDrain, doDrain).fluid();
    }

    public static TicFluidOperationResult drainDetailed(ItemStack stack, int maxDrain, boolean doDrain) {
        return of(stack).drain(maxDrain, doDrain);
    }

    public static boolean interactWithFluidHandler(EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing side) {
        if (player == null || hand == null || world == null || pos == null) {
            return false;
        }
        return hasTank(player.getHeldItem(hand)) && FluidUtil.interactWithFluidHandler(player, hand, world, pos, side);
    }
}
