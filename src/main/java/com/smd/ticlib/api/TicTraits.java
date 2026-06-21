package com.smd.ticlib.api;

import com.smd.ticlib.core.nbt.TicNbt;
import com.smd.ticlib.core.target.TicTarget;
import com.smd.ticlib.core.target.TicTargets;
import com.smd.ticlib.core.tconstruct.TicNativeAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class TicTraits {

    private TicTraits() {
    }

    public static String[] getTraits(ItemStack stack) {
        TicTarget target = TicTargets.resolve(stack);
        return target.isValid() ? target.nativeAccess().getTraits() : TicNbt.EMPTY_STRINGS;
    }

    public static String[] getTraits(NBTTagCompound root) {
        TicNativeAccess access = TicNativeAccess.of(root);
        return access == null ? TicNbt.EMPTY_STRINGS : access.getTraits();
    }

    public static boolean hasTrait(ItemStack stack, String traitId) {
        TicTarget target = TicTargets.resolve(stack);
        return target.isValid() && target.nativeAccess().hasTrait(traitId);
    }

    public static boolean hasTrait(NBTTagCompound root, String traitId) {
        TicNativeAccess access = TicNativeAccess.of(root);
        return access != null && access.hasTrait(traitId);
    }

    public static int getTraitColor(ItemStack stack, String traitId) {
        TicTarget target = TicTargets.resolve(stack);
        return target.isValid() ? target.nativeAccess().getTraitColor(traitId) : 0xffffff;
    }

    public static int getTraitLevel(ItemStack stack, String traitId) {
        TicTarget target = TicTargets.resolve(stack);
        return target.isValid() ? target.nativeAccess().getTraitLevel(traitId) : 1;
    }

    public static boolean addRegisteredTrait(ItemStack stack, String traitId, int color, int level) {
        TicTarget target = TicTargets.resolve(stack);
        if (!target.isValid()) {
            return false;
        }
        boolean changed = target.nativeAccess().addRegisteredTrait(traitId, color, level, true);
        if (changed) {
            stack.setTagCompound(target.writableRoot());
        }
        return changed;
    }

    public static boolean addBuildTrait(NBTTagCompound root, String traitId, int color, int level) {
        TicNativeAccess access = TicNativeAccess.of(root);
        return access != null && access.addRegisteredTrait(traitId, color, level, false);
    }

    public static boolean removeRegisteredTrait(ItemStack stack, String traitId) {
        TicTarget target = TicTargets.resolve(stack);
        if (!target.isValid()) {
            return false;
        }
        boolean changed = target.nativeAccess().removeRegisteredTrait(traitId, true);
        if (changed) {
            stack.setTagCompound(target.writableRoot());
        }
        return changed;
    }

    public static boolean removeBuildTrait(NBTTagCompound root, String traitId) {
        TicNativeAccess access = TicNativeAccess.of(root);
        return access != null && access.removeRegisteredTrait(traitId, false);
    }

    public static ItemStack withRegisteredTrait(ItemStack stack, String traitId, int color, int level) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = stack.copy();
        addRegisteredTrait(copy, traitId, color, level);
        return copy;
    }

    public static ItemStack withoutRegisteredTrait(ItemStack stack, String traitId) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = stack.copy();
        removeRegisteredTrait(copy, traitId);
        return copy;
    }

    public static String[] getBaseModifiers(NBTTagCompound root) {
        TicNativeAccess access = TicNativeAccess.of(root);
        return access == null ? TicNbt.EMPTY_STRINGS : access.getBaseModifiers();
    }

    public static boolean hasBaseModifier(NBTTagCompound root, String traitOrModifierId) {
        TicNativeAccess access = TicNativeAccess.of(root);
        return access != null && access.hasBaseModifier(traitOrModifierId);
    }

    public static boolean addBaseModifier(NBTTagCompound root, String traitOrModifierId) {
        TicNativeAccess access = TicNativeAccess.of(root);
        return access != null && access.addBaseModifier(traitOrModifierId);
    }

    public static boolean removeBaseModifier(NBTTagCompound root, String traitOrModifierId) {
        TicNativeAccess access = TicNativeAccess.of(root);
        return access != null && access.removeBaseModifier(traitOrModifierId);
    }
}
