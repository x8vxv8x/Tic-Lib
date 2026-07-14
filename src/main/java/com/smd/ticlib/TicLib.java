package com.smd.ticlib;

import com.smd.ticlib.core.lifecycle.TicLifecycleBus;
import com.smd.ticlib.core.lifecycle.TicLifecycleEvents;
import com.smd.ticlib.module.armor.ArmorTraitCacheModule;
import com.smd.ticlib.module.crafttweaker.CraftTweakerBuildModule;
import com.smd.ticlib.module.fluid.FluidEvents;
import com.smd.ticlib.module.fluid.FluidModule;
import com.smd.ticlib.module.fluid.FluidTooltipEvents;
import com.smd.ticlib.module.stats.PersistentStatsModule;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(   modid = Tags.MOD_ID,
        name = Tags.MOD_NAME,
        version = Tags.VERSION,
        dependencies = "required-after:tconstruct;required-after:conarm;after:crafttweaker")
public class TicLib {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        TicLifecycleBus.register(PersistentStatsModule.INSTANCE);
        TicLifecycleBus.register(FluidModule.INSTANCE);
        
        MinecraftForge.EVENT_BUS.register(TicLifecycleEvents.INSTANCE);
        MinecraftForge.EVENT_BUS.register(FluidEvents.INSTANCE);
        
        MinecraftForge.EVENT_BUS.register(ArmorTraitCacheModule.INSTANCE);
        
        if (event.getSide() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(FluidTooltipEvents.INSTANCE);
        }
        
        if (Loader.isModLoaded("crafttweaker")) {
            TicLifecycleBus.register(CraftTweakerBuildModule.INSTANCE);
            MinecraftForge.EVENT_BUS.register(CraftTweakerBuildModule.INSTANCE);
        }
        
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);
    }

}
