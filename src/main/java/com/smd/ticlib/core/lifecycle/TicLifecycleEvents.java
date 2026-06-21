package com.smd.ticlib.core.lifecycle;

import c4.conarm.lib.events.ArmoryEvent;
import com.smd.ticlib.core.target.TicTargetKind;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent;
import slimeknights.tconstruct.library.events.TinkerEvent;

public final class TicLifecycleEvents {

    public static final TicLifecycleEvents INSTANCE = new TicLifecycleEvents();

    private TicLifecycleEvents() {
    }

    @SubscribeEvent
    public void onToolBuilding(TinkerEvent.OnItemBuilding event) {
        String id = event.tool == null || event.tool.getRegistryName() == null ? "" : event.tool.getRegistryName().toString();
        TicLifecycleBus.build(event.tag, TicTargetKind.TOOL, id, event.materials);
    }

    @SubscribeEvent
    public void onArmorBuilding(ArmoryEvent.OnItemBuilding event) {
        String id = event.armor == null || event.armor.getRegistryName() == null ? "" : event.armor.getRegistryName().toString();
        TicLifecycleBus.build(event.tag, TicTargetKind.ARMOR, id, event.materials);
    }

    @SubscribeEvent
    public void onArmorRepaired(ArmoryEvent.OnRepair event) {
        TicLifecycleBus.replay(event.itemStack);
    }

    @SubscribeEvent
    public void onToolCrafted(TinkerCraftingEvent.ToolCraftingEvent event) {
        TicLifecycleBus.replay(event.getItemStack());
    }

    @SubscribeEvent
    public void onToolModified(TinkerCraftingEvent.ToolModifyEvent event) {
        TicLifecycleBus.copyAndReplay(event.getToolBeforeModification(), event.getItemStack());
    }

    @SubscribeEvent
    public void onToolPartReplaced(TinkerCraftingEvent.ToolPartReplaceEvent event) {
        TicLifecycleBus.replay(event.getItemStack());
    }
}
