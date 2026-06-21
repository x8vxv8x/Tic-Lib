package com.smd.ticlib.core.tconstruct;

import com.smd.ticlib.core.nbt.TicNbt;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolBuilder;

import java.util.ArrayList;
import java.util.List;

public final class TicNativeAccess {

    private static final int DEFAULT_COLOR = 0xffffff;
    private static final int DEFAULT_LEVEL = 1;

    private final NBTTagCompound root;

    private TicNativeAccess(NBTTagCompound root) {
        this.root = root;
    }

    public static TicNativeAccess of(NBTTagCompound root) {
        return root == null ? null : new TicNativeAccess(root);
    }

    public static TicNativeAccess of(ItemStack stack) {
        return stack == null || stack.isEmpty() ? null : of(TicNbt.getOrCreateRoot(stack));
    }

    public NBTTagCompound root() {
        return root;
    }

    public NBTTagCompound stats() {
        return TagUtil.getToolTag(root);
    }

    public void setStats(NBTTagCompound stats) {
        TagUtil.setToolTag(root, stats);
    }

    public String[] getStatNames() {
        NBTTagCompound stats = stats();
        if (stats == null || stats.isEmpty()) {
            return TicNbt.EMPTY_STRINGS;
        }
        List<String> names = new ArrayList<>();
        for (String key : stats.getKeySet()) {
            if (TicNbt.isNumeric(stats.getTag(key))) {
                names.add(key);
            }
        }
        return names.toArray(new String[0]);
    }

    public boolean hasNumericStat(String statName) {
        return statName != null && !statName.trim().isEmpty() && TicNbt.isNumeric(stats().getTag(statName));
    }

    public boolean addNumericStat(String statName, double amount) {
        NBTTagCompound copy = stats().copy();
        if (!TicNbt.addNumeric(copy, statName, amount)) {
            return false;
        }
        setStats(copy);
        return true;
    }

    public String[] getTraits() {
        return TicNbt.readStringList(TagUtil.getTraitsTagList(root));
    }

    public boolean hasTrait(String traitId) {
        if (!isValidId(traitId)) {
            return false;
        }
        for (String trait : getTraits()) {
            if (traitId.equals(trait)) {
                return true;
            }
        }
        return false;
    }

    public int getTraitColor(String traitId) {
        NBTTagCompound modifier = getModifierTag(traitId);
        return modifier == null ? DEFAULT_COLOR : (modifier.hasKey("color") ? modifier.getInteger("color") : DEFAULT_COLOR);
    }

    public int getTraitLevel(String traitId) {
        NBTTagCompound modifier = getModifierTag(traitId);
        return modifier == null ? DEFAULT_LEVEL : Math.max(DEFAULT_LEVEL, modifier.hasKey("level") ? modifier.getInteger("level") : DEFAULT_LEVEL);
    }

    public boolean addRegisteredTrait(String traitId, int color, int level, boolean syncBaseModifier) {
        TraitHandle handle = resolveTrait(traitId);
        if (handle == null || hasTrait(traitId)) {
            return false;
        }
        if (!root.hasKey(Tags.TOOL_TRAITS, 9)) {
            root.setTag(Tags.TOOL_TRAITS, new NBTTagList());
        }
        if (!root.hasKey(Tags.TOOL_MODIFIERS, 9)) {
            root.setTag(Tags.TOOL_MODIFIERS, new NBTTagList());
        }

        ToolBuilder.addTrait(root, handle.trait, color);
        if (level > DEFAULT_LEVEL) {
            rebuildModifierEntry(handle.modifierIdentifier, collectTraitsByModifier(handle.modifierIdentifier), color);
        }
        if (syncBaseModifier) {
            syncBaseModifier(handle.modifierIdentifier, true);
        }
        return true;
    }

    public boolean removeRegisteredTrait(String traitId, boolean syncBaseModifier) {
        TraitHandle handle = resolveTrait(traitId);
        if (handle == null || !hasTrait(traitId)) {
            return false;
        }

        TagUtil.setTraitsTagList(root, TicNbt.copyStringListWithout(TagUtil.getTraitsTagList(root), traitId));
        List<String> remainingTraits = collectTraitsByModifier(handle.modifierIdentifier);
        if (remainingTraits.isEmpty()) {
            TagUtil.setModifiersTagList(root, TicNbt.copyModifierListWithout(TagUtil.getModifiersTagList(root), handle.modifierIdentifier));
            if (syncBaseModifier) {
                syncBaseModifier(handle.modifierIdentifier, false);
            }
        } else {
            rebuildModifierEntry(handle.modifierIdentifier, remainingTraits, DEFAULT_COLOR);
        }
        return true;
    }

    public String[] getBaseModifiers() {
        return TicNbt.readStringList(TagUtil.getBaseModifiersTagList(root));
    }

    public boolean hasBaseModifier(String traitOrModifierId) {
        String modifierIdentifier = resolveModifierIdentifier(traitOrModifierId);
        return modifierIdentifier != null && TicNbt.stringListContains(TagUtil.getBaseModifiersTagList(root), modifierIdentifier);
    }

    public boolean addBaseModifier(String traitOrModifierId) {
        String modifierIdentifier = resolveModifierIdentifier(traitOrModifierId);
        if (modifierIdentifier == null) {
            return false;
        }
        NBTTagList modifiers = TagUtil.getBaseModifiersTagList(root);
        if (TicNbt.stringListContains(modifiers, modifierIdentifier)) {
            return true;
        }
        TagUtil.setBaseModifiersTagList(root, TicNbt.copyStringListAppending(modifiers, modifierIdentifier));
        return true;
    }

    public boolean removeBaseModifier(String traitOrModifierId) {
        String modifierIdentifier = resolveModifierIdentifier(traitOrModifierId);
        if (modifierIdentifier == null) {
            return false;
        }
        NBTTagList modifiers = TagUtil.getBaseModifiersTagList(root);
        if (!TicNbt.stringListContains(modifiers, modifierIdentifier)) {
            return false;
        }
        TagUtil.setBaseModifiersTagList(root, TicNbt.copyStringListWithout(modifiers, modifierIdentifier));
        return true;
    }

    public IModifier getModifier(String identifier) {
        return isValidId(identifier) ? TinkerRegistry.getModifier(identifier) : null;
    }

    public NBTTagCompound getModifierTag(String traitId) {
        TraitHandle handle = resolveTrait(traitId);
        if (handle == null) {
            return null;
        }
        NBTTagList modifiers = TagUtil.getModifiersTagList(root);
        for (int i = 0; i < modifiers.tagCount(); i++) {
            NBTTagCompound modifier = modifiers.getCompoundTagAt(i);
            if (handle.modifierIdentifier.equals(modifier.getString("identifier"))) {
                return modifier;
            }
        }
        return null;
    }

    private List<String> collectTraitsByModifier(String modifierIdentifier) {
        List<String> traits = new ArrayList<>();
        NBTTagList traitList = TagUtil.getTraitsTagList(root);
        for (int i = 0; i < traitList.tagCount(); i++) {
            String traitId = traitList.getStringTagAt(i);
            TraitHandle handle = resolveTrait(traitId);
            if (handle != null && modifierIdentifier.equals(handle.modifierIdentifier)) {
                traits.add(traitId);
            }
        }
        return traits;
    }

    private void rebuildModifierEntry(String modifierIdentifier, List<String> traitIds, int color) {
        NBTTagCompound rebuiltRoot = new NBTTagCompound();
        rebuiltRoot.setTag(Tags.TOOL_MODIFIERS, new NBTTagList());
        rebuiltRoot.setTag(Tags.TOOL_TRAITS, new NBTTagList());

        for (String traitId : traitIds) {
            TraitHandle handle = resolveTrait(traitId);
            if (handle != null) {
                ToolBuilder.addTrait(rebuiltRoot, handle.trait, color);
            }
        }

        NBTTagCompound rebuiltTag = TinkerUtil.getModifierTag(rebuiltRoot, modifierIdentifier);
        NBTTagList modifiers = TagUtil.getModifiersTagList(root);
        int index = TinkerUtil.getIndexInCompoundList(modifiers, modifierIdentifier);
        if (index >= 0) {
            modifiers.set(index, rebuiltTag);
        } else if (!rebuiltTag.isEmpty()) {
            modifiers.appendTag(rebuiltTag);
        }
        TagUtil.setModifiersTagList(root, modifiers);
    }

    private void syncBaseModifier(String modifierIdentifier, boolean add) {
        if (!root.hasKey(Tags.BASE_DATA, 10)) {
            return;
        }
        NBTTagCompound baseData = root.getCompoundTag(Tags.BASE_DATA);
        if (!baseData.hasKey(Tags.BASE_MODIFIERS, 9)) {
            return;
        }
        NBTTagList modifiers = baseData.getTagList(Tags.BASE_MODIFIERS, 8);
        if (add) {
            if (!TicNbt.stringListContains(modifiers, modifierIdentifier)) {
                baseData.setTag(Tags.BASE_MODIFIERS, TicNbt.copyStringListAppending(modifiers, modifierIdentifier));
            }
            return;
        }
        if (TicNbt.stringListContains(modifiers, modifierIdentifier)) {
            baseData.setTag(Tags.BASE_MODIFIERS, TicNbt.copyStringListWithout(modifiers, modifierIdentifier));
        }
    }

    private static TraitHandle resolveTrait(String traitId) {
        if (!isValidId(traitId)) {
            return null;
        }
        ITrait trait = TinkerRegistry.getTrait(traitId);
        IModifier modifier = TinkerRegistry.getModifier(traitId);
        if (trait == null || !(modifier instanceof AbstractTrait)) {
            return null;
        }
        return new TraitHandle(trait, ((AbstractTrait) modifier).getModifierIdentifier());
    }

    private static String resolveModifierIdentifier(String traitOrModifierId) {
        TraitHandle handle = resolveTrait(traitOrModifierId);
        if (handle != null) {
            return handle.modifierIdentifier;
        }
        return isValidId(traitOrModifierId) && TinkerRegistry.getModifier(traitOrModifierId) != null ? traitOrModifierId : null;
    }

    private static boolean isValidId(String id) {
        return id != null && !id.trim().isEmpty();
    }

    private static final class TraitHandle {
        private final ITrait trait;
        private final String modifierIdentifier;

        private TraitHandle(ITrait trait, String modifierIdentifier) {
            this.trait = trait;
            this.modifierIdentifier = modifierIdentifier;
        }
    }
}
