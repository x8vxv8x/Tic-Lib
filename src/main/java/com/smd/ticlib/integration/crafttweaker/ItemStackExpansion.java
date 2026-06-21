package com.smd.ticlib.integration.crafttweaker;

import com.smd.ticlib.api.TicFluids;
import com.smd.ticlib.api.TicItems;
import com.smd.ticlib.api.TicStats;
import com.smd.ticlib.api.TicTraits;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityEquipmentSlot;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * 将 TicTool 中适合作为物品实例方法的功能，以 ZenExpansion 的形式挂在 IItemStack 上。
 * 原 TicTool 的所有静态方法保持不变，本类提供更符合链式调用习惯的 API。
 */
@ZenExpansion("crafttweaker.item.IItemStack")
@ZenRegister
public final class ItemStackExpansion {

    private ItemStackExpansion() {
    }

    // ===================== 只读查询 =====================

    /**
     * 判断此物品是否为匠魂工具。
     */
    @ZenMethod
    public static boolean isTool(IItemStack stack) {
        return TicItems.isTool(toStackCopy(stack));
    }

    /**
     * 判断此物品是否为匠魂盔甲。
     */
    @ZenMethod
    public static boolean isArmor(IItemStack stack) {
        return TicItems.isArmor(toStackCopy(stack));
    }

    /**
     * 获取盔甲类型（如果不是盔甲，返回空字符串或 null，视底层实现）。
     */
    @ZenMethod
    public static String getArmorType(IItemStack stack) {
        return TicItems.getArmorType(toStackCopy(stack));
    }

    /**
     * 获取盔甲对应的装备槽（如果不是盔甲，返回 null）。
     */
    @ZenMethod
    public static IEntityEquipmentSlot getArmorSlot(IItemStack stack) {
        EntityEquipmentSlot slot = TicItems.getArmorSlot(toStackCopy(stack));
        return slot == null ? null : CraftTweakerMC.getIEntityEquipmentSlot(slot);
    }

    /**
     * 获取构成工具/盔甲的部件材料名称数组。
     */
    @ZenMethod
    public static String[] getMaterials(IItemStack stack) {
        return TicItems.getMaterials(toStackCopy(stack));
    }

    /**
     * 获取该物品上所有特性的 ID 数组。
     */
    @ZenMethod
    public static String[] getTraits(IItemStack stack) {
        return TicTraits.getTraits(toStackCopy(stack));
    }

    /**
     * 是否拥有指定 ID 的特性。
     */
    @ZenMethod
    public static boolean hasTrait(IItemStack stack, String traitId) {
        return TicTraits.hasTrait(toStackCopy(stack), traitId);
    }

    /**
     * 获取指定特性的颜色值。
     */
    @ZenMethod
    public static int getTraitColor(IItemStack stack, String traitId) {
        return TicTraits.getTraitColor(toStackCopy(stack), traitId);
    }

    /**
     * 获取指定特性的等级。
     */
    @ZenMethod
    public static int getTraitLevel(IItemStack stack, String traitId) {
        return TicTraits.getTraitLevel(toStackCopy(stack), traitId);
    }

    /**
     * 获取该物品的所有属性名称数组。
     */
    @ZenMethod
    public static String[] getStats(IItemStack stack) {
        return TicStats.getStats(toStackCopy(stack));
    }

    /**
     * 是否拥有指定名称的属性。
     */
    @ZenMethod
    public static boolean hasStat(IItemStack stack, String statName) {
        return TicStats.hasStat(toStackCopy(stack), statName);
    }

    /**
     * 获取指定浮点属性值。
     */
    @ZenMethod
    public static float getFloatStat(IItemStack stack, String statName) {
        return TicStats.getFloat(toStackCopy(stack), statName);
    }

    /**
     * 获取指定整数属性值。
     */
    @ZenMethod
    public static int getIntStat(IItemStack stack, String statName) {
        return TicStats.getInt(toStackCopy(stack), statName);
    }

    @ZenMethod
    public static int getFluidCapacity(IItemStack stack) {
        return TicFluids.of(toStackCopy(stack)).primaryCapacity();
    }

    @ZenMethod
    public static int getFluidAmount(IItemStack stack) {
        FluidStack fluid = TicFluids.of(toStackCopy(stack)).primaryFluid();
        return fluid == null ? 0 : fluid.amount;
    }

    @ZenMethod
    public static String getFluidName(IItemStack stack) {
        FluidStack fluid = TicFluids.of(toStackCopy(stack)).primaryFluid();
        return fluid == null || fluid.getFluid() == null ? "" : fluid.getFluid().getName();
    }

    @ZenMethod
    public static boolean hasAnyFluidTank(IItemStack stack) {
        return TicFluids.of(toStackCopy(stack)).hasAnyTank();
    }

    // ===================== 链式修改（返回新物品，不影响原物品） =====================

    /**
     * 返回一个添加了指定特性的新物品（原物品不变）。
     * 若添加失败，返回原物品。
     */
    @ZenMethod
    public static IItemStack withTrait(IItemStack stack, String traitId, int color, int level) {
        ItemStack copy = toStackCopy(stack).copy();
        ItemStack result = TicTraits.withRegisteredTrait(copy, traitId, color, level);
        return result.isEmpty() ? stack : CraftTweakerMC.getIItemStack(result);
    }

    /**
     * 返回一个移除了指定特性的新物品（原物品不变）。
     * 若移除失败，返回原物品。
     */
    @ZenMethod
    public static IItemStack withoutTrait(IItemStack stack, String traitId) {
        ItemStack copy = toStackCopy(stack).copy();
        ItemStack result = TicTraits.withoutRegisteredTrait(copy, traitId);
        return result.isEmpty() ? stack : CraftTweakerMC.getIItemStack(result);
    }

    /**
     * 返回一个设置了“破损”状态的新物品。
     * 若操作失败（如不是工具/盔甲），返回原物品。
     */
    @ZenMethod
    public static IItemStack withBroken(IItemStack stack, boolean broken) {
        ItemStack copy = toStackCopy(stack).copy();
        boolean success = TicStats.setBroken(copy, broken);
        return success ? CraftTweakerMC.getIItemStack(copy) : stack;
    }

    /**
     * 返回一个添加了浮点属性的新物品。
     */
    @ZenMethod
    public static IItemStack withStat(IItemStack stack, String statName, float amount, String token) {
        ItemStack copy = toStackCopy(stack).copy();
        boolean success = TicStats.add(copy, statName, amount, token);
        return success ? CraftTweakerMC.getIItemStack(copy) : stack;
    }

    @ZenMethod
    public static IItemStack withFluidCapacity(IItemStack stack, int capacity) {
        ItemStack copy = toStackCopy(stack).copy();
        boolean success = TicFluids.of(copy).setPrimaryCapacity(capacity);
        return success ? CraftTweakerMC.getIItemStack(copy) : stack;
    }

    @ZenMethod
    public static IItemStack withoutFluid(IItemStack stack) {
        ItemStack copy = toStackCopy(stack).copy();
        boolean success = TicFluids.of(copy).clearPrimaryFluid();
        return success ? CraftTweakerMC.getIItemStack(copy) : stack;
    }

    // ===================== 工具方法 =====================

    private static ItemStack toStackCopy(IItemStack stack) {
        return stack == null ? ItemStack.EMPTY : CraftTweakerMC.getItemStack(stack);
    }
}
