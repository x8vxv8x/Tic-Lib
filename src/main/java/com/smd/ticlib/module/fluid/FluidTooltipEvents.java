package com.smd.ticlib.module.fluid;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class FluidTooltipEvents {

    public static final FluidTooltipEvents INSTANCE = new FluidTooltipEvents();

    private FluidTooltipEvents() {
    }

    @SubscribeEvent
    public void addTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!FluidModule.INSTANCE.hasTank(stack)) {
            return;
        }
        int capacity = FluidModule.INSTANCE.getCapacity(stack);
        FluidStack fluid = FluidModule.INSTANCE.getFluid(stack);
        if (fluid == null) {
            event.getToolTip().add("Tank: 0 / " + capacity + " mB");
        } else {
            event.getToolTip().add(fluid.getLocalizedName() + ": " + fluid.amount + " / " + capacity + " mB");
        }
    }
}