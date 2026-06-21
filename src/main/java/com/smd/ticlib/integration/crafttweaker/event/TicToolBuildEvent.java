package com.smd.ticlib.integration.crafttweaker.event;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.materials.Material;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

import java.util.List;

@ZenRegister
@ZenClass("mods.ticlib.event.ToolBuildEvent")
public final class TicToolBuildEvent extends TicItemBuildEvent {

    public TicToolBuildEvent(NBTTagCompound root, List<Material> materials, String toolId) {
        super(root, materials, toolId);
    }

    @ZenGetter("toolId")
    public String getToolId() {
        return itemId;
    }
}
