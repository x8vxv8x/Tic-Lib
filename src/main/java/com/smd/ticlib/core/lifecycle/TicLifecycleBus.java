package com.smd.ticlib.core.lifecycle;

import com.smd.ticlib.core.data.TicDataAccess;
import com.smd.ticlib.core.target.TicTarget;
import com.smd.ticlib.core.target.TicTargetKind;
import com.smd.ticlib.core.target.TicTargets;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.materials.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TicLifecycleBus {

    private static final List<TicModule> MODULES = new ArrayList<>();

    private TicLifecycleBus() {
    }

    public static void register(TicModule module) {
        if (module != null && !MODULES.contains(module)) {
            MODULES.add(module);
        }
    }

    public static List<TicModule> modules() {
        return Collections.unmodifiableList(MODULES);
    }

    public static void build(NBTTagCompound root, TicTargetKind kind, String itemId, List<Material> materials) {
        TicLifecycleContext context = TicLifecycleContext.forBuild(root, kind, itemId, materials);
        if (context == null) {
            return;
        }
        for (TicModule module : MODULES) {
            if (module.supports(kind)) {
                module.onBuild(context);
            }
        }
    }

    public static void copyAndReplay(ItemStack oldStack, ItemStack newStack) {
        TicTarget newTarget = TicTargets.resolve(newStack);
        if (!newTarget.isValid()) {
            return;
        }
        TicDataAccess.copyData(oldStack, newStack);
        TicLifecycleContext oldContext = TicLifecycleContext.forStack(TicTargets.resolve(oldStack));
        TicLifecycleContext newContext = TicLifecycleContext.forStack(newTarget);
        for (TicModule module : MODULES) {
            if (module.supports(newTarget.kind())) {
                module.onCopy(oldContext, newContext);
            }
        }
        replay(newStack);
    }

    public static boolean copyPersistentData(ItemStack oldStack, ItemStack newStack) {
        TicTarget oldTarget = TicTargets.resolve(oldStack);
        TicTarget newTarget = TicTargets.resolve(newStack);
        if (!oldTarget.isValid() || !newTarget.isValid() || oldTarget.kind() != newTarget.kind()) {
            return false;
        }
        TicDataAccess.copyData(oldStack, newStack);
        TicLifecycleContext oldContext = TicLifecycleContext.forStack(oldTarget);
        TicLifecycleContext newContext = TicLifecycleContext.forStack(newTarget);
        for (TicModule module : MODULES) {
            if (module.supports(newTarget.kind())) {
                module.onCopy(oldContext, newContext);
            }
        }
        newContext.commit();
        return true;
    }

    public static void replay(ItemStack stack) {
        TicTarget target = TicTargets.resolve(stack);
        if (!target.isValid()) {
            return;
        }
        TicLifecycleContext context = TicLifecycleContext.forStack(target);
        for (TicModule module : MODULES) {
            if (module.supports(target.kind())) {
                module.onReplay(context);
            }
        }
        for (TicModule module : MODULES) {
            if (module.supports(target.kind())) {
                module.onValidate(context);
            }
        }
        context.commit();
    }
}
