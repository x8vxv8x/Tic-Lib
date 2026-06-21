package com.smd.ticlib.core.lifecycle;

import com.smd.ticlib.core.data.TicDataAccess;
import com.smd.ticlib.core.target.TicTarget;
import com.smd.ticlib.core.target.TicTargetKind;
import com.smd.ticlib.core.tconstruct.TicNativeAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.materials.Material;

import java.util.Collections;
import java.util.List;

public final class TicLifecycleContext {

    private final ItemStack stack;
    private final NBTTagCompound root;
    private final TicTargetKind kind;
    private final String itemId;
    private final List<Material> materials;

    private TicLifecycleContext(ItemStack stack, NBTTagCompound root, TicTargetKind kind, String itemId, List<Material> materials) {
        this.stack = stack;
        this.root = root;
        this.kind = kind;
        this.itemId = itemId == null ? "" : itemId;
        this.materials = materials == null ? Collections.<Material>emptyList() : materials;
    }

    public static TicLifecycleContext forStack(TicTarget target) {
        return target == null || !target.isValid()
                ? null
                : new TicLifecycleContext(target.stack(), target.writableRoot(), target.kind(), "", Collections.<Material>emptyList());
    }

    public static TicLifecycleContext forBuild(NBTTagCompound root, TicTargetKind kind, String itemId, List<Material> materials) {
        return root == null ? null : new TicLifecycleContext(null, root, kind, itemId, materials);
    }

    public ItemStack stack() {
        return stack;
    }

    public NBTTagCompound root() {
        return root;
    }

    public TicTargetKind kind() {
        return kind;
    }

    public String itemId() {
        return itemId;
    }

    public List<Material> materials() {
        return materials;
    }

    public TicDataAccess data() {
        return TicDataAccess.ofRoot(root);
    }

    public TicNativeAccess nativeAccess() {
        return TicNativeAccess.of(root);
    }

    public void commit() {
        if (stack != null && !stack.isEmpty()) {
            stack.setTagCompound(root);
        }
    }
}
