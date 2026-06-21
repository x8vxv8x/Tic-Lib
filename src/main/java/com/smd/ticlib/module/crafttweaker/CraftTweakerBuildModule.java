package com.smd.ticlib.module.crafttweaker;

import com.smd.ticlib.core.lifecycle.TicLifecycleContext;
import com.smd.ticlib.core.lifecycle.TicModule;
import com.smd.ticlib.core.target.TicTargetKind;
import com.smd.ticlib.integration.crafttweaker.event.TicArmorBuildEvent;
import com.smd.ticlib.integration.crafttweaker.event.TicEvents;
import com.smd.ticlib.integration.crafttweaker.event.TicToolBuildEvent;
import crafttweaker.mc1120.events.ScriptRunEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class CraftTweakerBuildModule implements TicModule {

    public static final CraftTweakerBuildModule INSTANCE = new CraftTweakerBuildModule();

    private CraftTweakerBuildModule() {
    }

    @Override
    public String id() {
        return "ticlib:crafttweaker";
    }

    @Override
    public boolean supports(TicTargetKind kind) {
        return kind == TicTargetKind.TOOL || kind == TicTargetKind.ARMOR;
    }

    @Override
    public void onBuild(TicLifecycleContext context) {
        if (context.kind() == TicTargetKind.TOOL && TicEvents.INSTANCE.hasToolBuildHandlers()) {
            TicEvents.INSTANCE.publishToolBuild(new TicToolBuildEvent(context.root(), context.materials(), context.itemId()));
        } else if (context.kind() == TicTargetKind.ARMOR && TicEvents.INSTANCE.hasArmorBuildHandlers()) {
            TicEvents.INSTANCE.publishArmorBuild(new TicArmorBuildEvent(context.root(), context.materials(), context.itemId()));
        }
    }

    @SubscribeEvent
    public void onScriptsReload(ScriptRunEvent.Pre event) {
        TicEvents.clear();
    }
}
