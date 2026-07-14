package com.smd.ticlib.module.fluid;

import com.smd.ticlib.Tags;
import com.smd.ticlib.core.target.TicTargets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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
}
