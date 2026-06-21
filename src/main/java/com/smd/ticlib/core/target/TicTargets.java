package com.smd.ticlib.core.target;

import c4.conarm.lib.ArmoryRegistry;
import c4.conarm.lib.armor.ArmorCore;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tinkering.ITinkerable;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class TicTargets {

    private static final List<EntityEquipmentSlot> ARMOR_SLOTS = Arrays.asList(
            EntityEquipmentSlot.HEAD,
            EntityEquipmentSlot.CHEST,
            EntityEquipmentSlot.LEGS,
            EntityEquipmentSlot.FEET
    );

    private TicTargets() {
    }

    public static TicTarget resolve(ItemStack stack) {
        return new TicTarget(stack, kindOf(stack));
    }

    public static TicTargetKind kindOf(ItemStack stack) {
        if (isArmor(stack)) {
            return TicTargetKind.ARMOR;
        }
        if (isTool(stack)) {
            return TicTargetKind.TOOL;
        }
        return TicTargetKind.UNKNOWN;
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.isEmpty();
    }

    public static boolean isTool(ItemStack stack) {
        if (isEmpty(stack) || isArmor(stack)) {
            return false;
        }
        if (stack.getItem() instanceof ToolCore) {
            return true;
        }
        return stack.getItem() instanceof ITinkerable && hasTicData(stack);
    }

    public static boolean isArmor(ItemStack stack) {
        return !isEmpty(stack) && stack.getItem() instanceof ArmorCore;
    }

    public static boolean isTarget(ItemStack stack) {
        return isTool(stack) || isArmor(stack);
    }

    public static EntityEquipmentSlot getArmorSlot(ItemStack stack) {
        return isArmor(stack) ? ((ArmorCore) stack.getItem()).armorType : null;
    }

    public static String getArmorType(ItemStack stack) {
        EntityEquipmentSlot slot = getArmorSlot(stack);
        if (slot == null) {
            return "";
        }
        switch (slot) {
            case HEAD:
                return "helmet";
            case CHEST:
                return "chestplate";
            case LEGS:
                return "leggings";
            case FEET:
                return "boots";
            default:
                return "";
        }
    }

    public static EntityEquipmentSlot parseArmorSlot(String slotName) {
        if (slotName == null) {
            return null;
        }
        switch (slotName.trim().toLowerCase(Locale.ROOT)) {
            case "head":
            case "helmet":
                return EntityEquipmentSlot.HEAD;
            case "chest":
            case "chestplate":
                return EntityEquipmentSlot.CHEST;
            case "legs":
            case "leggings":
                return EntityEquipmentSlot.LEGS;
            case "feet":
            case "boots":
                return EntityEquipmentSlot.FEET;
            default:
                return null;
        }
    }

    public static int armorSlotIndex(EntityEquipmentSlot slot) {
        if (slot == null) {
            return -1;
        }
        switch (slot) {
            case HEAD:
                return 0;
            case CHEST:
                return 1;
            case LEGS:
                return 2;
            case FEET:
                return 3;
            default:
                return -1;
        }
    }

    public static List<EntityEquipmentSlot> armorSlots() {
        return ARMOR_SLOTS;
    }

    public static ItemStack[] getAllKnownItems() {
        Map<String, ItemStack> items = new LinkedHashMap<>();
        for (ToolCore tool : TinkerRegistry.getTools()) {
            if (tool.getRegistryName() != null) {
                items.put(tool.getRegistryName().toString(), new ItemStack(tool));
            }
        }
        for (ArmorCore armor : ArmoryRegistry.getArmor()) {
            if (armor.getRegistryName() != null) {
                items.put(armor.getRegistryName().toString(), new ItemStack(armor));
            }
        }
        return items.values().toArray(new ItemStack[0]);
    }

    public static String[] getMaterials(ItemStack stack) {
        if (!isTarget(stack)) {
            return com.smd.ticlib.core.nbt.TicNbt.EMPTY_STRINGS;
        }
        return com.smd.ticlib.core.nbt.TicNbt.readStringList(TagUtil.getBaseMaterialsTagList(stack));
    }

    public static List<String> getMaterialList(ItemStack stack) {
        return new ArrayList<>(Arrays.asList(getMaterials(stack)));
    }

    private static boolean hasTicData(ItemStack stack) {
        if (isEmpty(stack)) {
            return false;
        }
        return TagUtil.getTagSafe(stack).hasKey(Tags.BASE_DATA, 10)
                || TagUtil.getTagSafe(stack).hasKey(Tags.TOOL_DATA, 10);
    }
}
