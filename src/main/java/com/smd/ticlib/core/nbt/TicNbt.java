package com.smd.ticlib.core.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.library.utils.TagUtil;

import java.util.ArrayList;
import java.util.List;

public final class TicNbt {

    public static final String[] EMPTY_STRINGS = new String[0];

    private TicNbt() {
    }

    public static NBTTagCompound getRoot(ItemStack stack) {
        return stack == null || stack.isEmpty() || !stack.hasTagCompound() ? null : stack.getTagCompound();
    }

    public static NBTTagCompound getOrCreateRoot(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return new NBTTagCompound();
        }
        NBTTagCompound root = stack.getTagCompound();
        if (root == null) {
            root = new NBTTagCompound();
            stack.setTagCompound(root);
        }
        return root;
    }

    public static NBTTagCompound copyRoot(ItemStack stack) {
        NBTTagCompound root = TagUtil.getTagSafe(stack);
        return root == null ? new NBTTagCompound() : root.copy();
    }

    public static String[] readStringList(NBTTagList list) {
        if (list == null || list.tagCount() == 0) {
            return EMPTY_STRINGS;
        }
        String[] values = new String[list.tagCount()];
        for (int i = 0; i < list.tagCount(); i++) {
            values[i] = list.getStringTagAt(i);
        }
        return values;
    }

    public static List<String> toJavaList(NBTTagList list) {
        List<String> values = new ArrayList<>();
        if (list == null) {
            return values;
        }
        for (int i = 0; i < list.tagCount(); i++) {
            values.add(list.getStringTagAt(i));
        }
        return values;
    }

    public static boolean stringListContains(NBTTagList list, String value) {
        if (list == null || value == null) {
            return false;
        }
        for (int i = 0; i < list.tagCount(); i++) {
            if (value.equals(list.getStringTagAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static NBTTagList copyStringListAppending(NBTTagList source, String value) {
        NBTTagList result = new NBTTagList();
        if (source != null) {
            for (int i = 0; i < source.tagCount(); i++) {
                result.appendTag(new NBTTagString(source.getStringTagAt(i)));
            }
        }
        result.appendTag(new NBTTagString(value));
        return result;
    }

    public static NBTTagList copyStringListWithout(NBTTagList source, String value) {
        NBTTagList result = new NBTTagList();
        if (source == null) {
            return result;
        }
        for (int i = 0; i < source.tagCount(); i++) {
            String current = source.getStringTagAt(i);
            if (!value.equals(current)) {
                result.appendTag(new NBTTagString(current));
            }
        }
        return result;
    }

    public static NBTTagList copyModifierListWithout(NBTTagList source, String identifier) {
        NBTTagList result = new NBTTagList();
        if (source == null) {
            return result;
        }
        for (int i = 0; i < source.tagCount(); i++) {
            NBTTagCompound entry = source.getCompoundTagAt(i);
            if (!identifier.equals(entry.getString("identifier"))) {
                result.appendTag(entry.copy());
            }
        }
        return result;
    }

    public static NBTTagList copyModifierListAppending(NBTTagList source, NBTTagCompound modifier) {
        NBTTagList result = new NBTTagList();
        if (source != null) {
            for (int i = 0; i < source.tagCount(); i++) {
                result.appendTag(source.getCompoundTagAt(i).copy());
            }
        }
        result.appendTag(modifier.copy());
        return result;
    }

    public static boolean isNumeric(NBTBase value) {
        if (value == null) {
            return false;
        }
        switch (value.getId()) {
            case Constants.NBT.TAG_BYTE:
            case Constants.NBT.TAG_SHORT:
            case Constants.NBT.TAG_INT:
            case Constants.NBT.TAG_LONG:
            case Constants.NBT.TAG_FLOAT:
            case Constants.NBT.TAG_DOUBLE:
                return true;
            default:
                return false;
        }
    }

    public static boolean addNumeric(NBTTagCompound tag, String key, double amount) {
        if (tag == null || key == null || key.trim().isEmpty() || !isNumeric(tag.getTag(key))) {
            return false;
        }
        switch (tag.getTag(key).getId()) {
            case Constants.NBT.TAG_BYTE:
                tag.setByte(key, (byte) (tag.getByte(key) + (int) amount));
                return true;
            case Constants.NBT.TAG_SHORT:
                tag.setShort(key, (short) (tag.getShort(key) + (int) amount));
                return true;
            case Constants.NBT.TAG_INT:
                tag.setInteger(key, tag.getInteger(key) + (int) amount);
                return true;
            case Constants.NBT.TAG_LONG:
                tag.setLong(key, tag.getLong(key) + (long) amount);
                return true;
            case Constants.NBT.TAG_FLOAT:
                tag.setFloat(key, tag.getFloat(key) + (float) amount);
                return true;
            case Constants.NBT.TAG_DOUBLE:
                tag.setDouble(key, tag.getDouble(key) + amount);
                return true;
            default:
                return false;
        }
    }
}
