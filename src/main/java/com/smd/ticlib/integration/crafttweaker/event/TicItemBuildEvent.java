package com.smd.ticlib.integration.crafttweaker.event;

import com.smd.ticlib.api.TicStats;
import com.smd.ticlib.api.TicTraits;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.materials.Material;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

@ZenRegister
@ZenClass("mods.ticlib.event.ItemBuildEvent")
public class TicItemBuildEvent {

    protected final NBTTagCompound root;
    protected final List<Material> materials;
    protected final String itemId;

    TicItemBuildEvent(NBTTagCompound root, List<Material> materials, String itemId) {
        this.root = root;
        this.materials = materials;
        this.itemId = itemId == null ? "" : itemId;
    }

    @ZenGetter("itemId")
    public String getItemId() {
        return itemId;
    }

    @ZenGetter("materials")
    public String[] getMaterials() {
        String[] result = new String[materials.size()];
        for (int i = 0; i < materials.size(); i++) {
            Material material = materials.get(i);
            result[i] = material == null ? "" : material.getIdentifier();
        }
        return result;
    }

    @ZenMethod
    public boolean hasMaterial(String materialId) {
        if (materialId == null || materialId.trim().isEmpty()) {
            return false;
        }
        for (Material material : materials) {
            if (material != null && materialId.equals(material.getIdentifier())) {
                return true;
            }
        }
        return false;
    }

    @ZenMethod
    public String[] getTraits() {
        return TicTraits.getTraits(root);
    }

    @ZenMethod
    public boolean hasTrait(String traitId) {
        return TicTraits.hasTrait(root, traitId);
    }

    @ZenMethod
    public boolean addTrait(String traitId, int color, int level) {
        return applyRegisteredTrait(traitId, color, level);
    }

    @ZenMethod
    public boolean applyRegisteredTrait(String traitId, int color, int level) {
        return TicTraits.addBuildTrait(root, traitId, color, level);
    }

    @ZenMethod
    public boolean removeTrait(String traitId) {
        return TicTraits.removeBuildTrait(root, traitId);
    }

    @ZenMethod
    public String[] getBaseModifiers() {
        return TicTraits.getBaseModifiers(root);
    }

    @ZenMethod
    public boolean hasBaseModifier(String traitOrModifierId) {
        return TicTraits.hasBaseModifier(root, traitOrModifierId);
    }

    @ZenMethod
    public boolean addBaseModifier(String traitOrModifierId) {
        return TicTraits.addBaseModifier(root, traitOrModifierId);
    }

    @ZenMethod
    public boolean removeBaseModifier(String traitOrModifierId) {
        return TicTraits.removeBaseModifier(root, traitOrModifierId);
    }

    @ZenMethod
    public String[] getStats() {
        return TicStats.getStats(root);
    }

    @ZenMethod
    public boolean hasStat(String statName) {
        return TicStats.hasStat(root, statName);
    }

    @ZenMethod
    public float getFloatStat(String statName) {
        return TicStats.getFloat(root, statName);
    }

    @ZenMethod
    public int getIntStat(String statName) {
        return TicStats.getInt(root, statName);
    }

    @ZenMethod
    public boolean addStat(String statName, float amount) {
        return TicStats.addNow(root, statName, amount);
    }
}
