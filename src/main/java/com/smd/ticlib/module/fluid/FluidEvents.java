package com.smd.ticlib.module.fluid;

import com.smd.ticlib.Tags;
import com.smd.ticlib.core.target.TicTargets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class FluidEvents {

    public static final FluidEvents INSTANCE = new FluidEvents();

    private static final ResourceLocation CAPABILITY_ID = new ResourceLocation(Tags.MOD_ID, "fluid");

    private FluidEvents() {
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (TicTargets.isTarget(stack)) {
            event.addCapability(CAPABILITY_ID, new FluidCapabilityProvider(stack));
        }
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
