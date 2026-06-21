package com.smd.ticlib.integration.crafttweaker.event;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.materials.Material;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

import java.util.List;

@ZenRegister
@ZenClass("mods.ticlib.event.ArmorBuildEvent")
public final class TicArmorBuildEvent extends TicItemBuildEvent {

    public TicArmorBuildEvent(NBTTagCompound root, List<Material> materials, String armorId) {
        super(root, materials, armorId);
    }

    @ZenGetter("armorId")
    public String getArmorId() {
        return itemId;
    }
}
