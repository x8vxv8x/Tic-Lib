package com.smd.ticlib.core.data;

import com.smd.ticlib.core.nbt.TicNbt;
import com.smd.ticlib.core.target.TicTarget;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

public final class TicDataAccess {

    public static final String ROOT = "ticlib";
    public static final String DATA_VERSION = "DataVersion";
    public static final String DIRTY_VERSION = "DirtyVersion";
    public static final String COMPONENTS = "Components";

    private static final int CURRENT_VERSION = 1;

    private final ItemStack stack;
    private final NBTTagCompound root;

    private TicDataAccess(ItemStack stack, NBTTagCompound root) {
        this.stack = stack;
        this.root = root;
    }

    public static TicDataAccess of(TicTarget target) {
        return target == null || !target.isValid() ? null : of(target.stack());
    }

    public static TicDataAccess of(ItemStack stack) {
        return stack == null || stack.isEmpty() ? null : new TicDataAccess(stack, TicNbt.getOrCreateRoot(stack));
    }

    public static TicDataAccess ofRoot(NBTTagCompound root) {
        return root == null ? null : new TicDataAccess(null, root);
    }

    public NBTTagCompound root() {
        return root;
    }

    public NBTTagCompound data() {
        if (!root.hasKey(ROOT, Constants.NBT.TAG_COMPOUND)) {
            return null;
        }
        return root.getCompoundTag(ROOT);
    }

    public NBTTagCompound writableData() {
        NBTTagCompound data = root.getCompoundTag(ROOT);
        if (!data.hasKey(DATA_VERSION, Constants.NBT.TAG_INT)) {
            data.setInteger(DATA_VERSION, CURRENT_VERSION);
        }
        root.setTag(ROOT, data);
        return data;
    }

    public int dataVersion() {
        NBTTagCompound data = data();
        return data == null ? 0 : data.getInteger(DATA_VERSION);
    }

    public int dirtyVersion() {
        NBTTagCompound data = data();
        return data == null ? 0 : data.getInteger(DIRTY_VERSION);
    }

    public NBTTagCompound component(String id) {
        NBTTagCompound data = data();
        if (data == null || id == null || id.trim().isEmpty() || !data.hasKey(COMPONENTS, Constants.NBT.TAG_COMPOUND)) {
            return null;
        }
        NBTTagCompound components = data.getCompoundTag(COMPONENTS);
        return components.hasKey(id, Constants.NBT.TAG_COMPOUND) ? components.getCompoundTag(id) : null;
    }

    public NBTTagCompound writableComponent(String id) {
        NBTTagCompound data = writableData();
        NBTTagCompound components = data.getCompoundTag(COMPONENTS);
        NBTTagCompound component = components.getCompoundTag(id);
        components.setTag(id, component);
        data.setTag(COMPONENTS, components);
        root.setTag(ROOT, data);
        return component;
    }

    public void removeComponent(String id) {
        NBTTagCompound data = data();
        if (data == null || !data.hasKey(COMPONENTS, Constants.NBT.TAG_COMPOUND)) {
            return;
        }
        NBTTagCompound components = data.getCompoundTag(COMPONENTS);
        components.removeTag(id);
        data.setTag(COMPONENTS, components);
        root.setTag(ROOT, data);
        markDirty();
    }

    public void markDirty() {
        NBTTagCompound data = writableData();
        data.setInteger(DIRTY_VERSION, data.getInteger(DIRTY_VERSION) + 1);
        root.setTag(ROOT, data);
    }

    public void commit() {
        if (stack != null && !stack.isEmpty()) {
            stack.setTagCompound(root);
        }
    }

    public static void copyData(ItemStack from, ItemStack to) {
        if (from == null || to == null || from.isEmpty() || to.isEmpty()) {
            return;
        }
        copyData(TicNbt.getRoot(from), TicNbt.getOrCreateRoot(to));
        to.setTagCompound(TicNbt.getOrCreateRoot(to));
    }

    public static void copyData(NBTTagCompound fromRoot, NBTTagCompound toRoot) {
        if (fromRoot == null || toRoot == null || !fromRoot.hasKey(ROOT, Constants.NBT.TAG_COMPOUND)) {
            return;
        }
        toRoot.setTag(ROOT, fromRoot.getCompoundTag(ROOT).copy());
        TicDataAccess access = ofRoot(toRoot);
        if (access != null) {
            access.markDirty();
        }
    }
}
