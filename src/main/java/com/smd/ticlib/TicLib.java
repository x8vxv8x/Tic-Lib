package com.smd.ticlib;

import com.smd.ticlib.core.lifecycle.TicLifecycleBus;
import com.smd.ticlib.core.lifecycle.TicLifecycleEvents;
import com.smd.ticlib.module.armor.ArmorTraitCacheModule;
import com.smd.ticlib.module.crafttweaker.CraftTweakerBuildModule;
import com.smd.ticlib.module.fluid.FluidEvents;
import com.smd.ticlib.module.fluid.FluidModule;
import com.smd.ticlib.module.stats.PersistentStatsModule;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(   modid = Tags.MOD_ID,
        name = Tags.MOD_NAME,
        version = Tags.VERSION,
        dependencies = "required-after:tconstruct;required-after:conarm")
public class TicLib {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        TicLifecycleBus.register(PersistentStatsModule.INSTANCE);
        TicLifecycleBus.register(FluidModule.INSTANCE);
        TicLifecycleBus.register(CraftTweakerBuildModule.INSTANCE);
        MinecraftForge.EVENT_BUS.register(TicLifecycleEvents.INSTANCE);
        MinecraftForge.EVENT_BUS.register(FluidEvents.INSTANCE);
        MinecraftForge.EVENT_BUS.register(CraftTweakerBuildModule.INSTANCE);
        MinecraftForge.EVENT_BUS.register(ArmorTraitCacheModule.INSTANCE);
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);
    }

}
