package com.smd.ticlib.integration.crafttweaker;

import com.smd.ticlib.api.TicArmor;
import com.smd.ticlib.api.TicFluids;
import com.smd.ticlib.api.TicItems;
import com.smd.ticlib.api.TicStats;
import com.smd.ticlib.api.TicTraits;
import com.smd.ticlib.module.fluid.TicFluidAccess;
import com.smd.ticlib.module.fluid.TicFluidOperationResult;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityEquipmentSlot;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.ticlib.TicTool")
@ZenRegister
public final class TicTool {

    private TicTool() {
    }

    @ZenMethod
    public static boolean isTool(IItemStack stack) {
        return TicItems.isTool(toStackCopy(stack));
    }

    @ZenMethod
    public static IItemStack[] getAllItems() {
        return CraftTweakerMC.getIItemStacks(TicItems.getAllKnownItems());
    }

    @ZenMethod
    public static boolean isArmor(IItemStack stack) {
        return TicItems.isArmor(toStackCopy(stack));
    }

    @ZenMethod
    public static String getArmorType(IItemStack stack) {
        return TicItems.getArmorType(toStackCopy(stack));
    }

    @ZenMethod
    public static IEntityEquipmentSlot getArmorSlot(IItemStack stack) {
        EntityEquipmentSlot slot = TicItems.getArmorSlot(toStackCopy(stack));
        return slot == null ? null : CraftTweakerMC.getIEntityEquipmentSlot(slot);
    }

    @ZenMethod
    public static String[] getMaterials(IItemStack stack) {
        return TicItems.getMaterials(toStackCopy(stack));
    }

    @ZenMethod
    public static String[] getTraits(IItemStack stack) {
        return TicTraits.getTraits(toStackCopy(stack));
    }

    @ZenMethod
    public static boolean hasTrait(IItemStack stack, String traitId) {
        return TicTraits.hasTrait(toStackCopy(stack), traitId);
    }

    @ZenMethod
    public static int getTraitColor(IItemStack stack, String traitId) {
        return TicTraits.getTraitColor(toStackCopy(stack), traitId);
    }

    @ZenMethod
    public static int getTraitLevel(IItemStack stack, String traitId) {
        return TicTraits.getTraitLevel(toStackCopy(stack), traitId);
    }

    @ZenMethod
    public static boolean addTrait(IItemStack stack, String traitId, int color, int level) {
        return applyRegisteredTrait(stack, traitId, color, level);
    }

    @ZenMethod
    public static boolean applyRegisteredTrait(IItemStack stack, String traitId, int color, int level) {
        return TicTraits.addRegisteredTrait(toMutableStack(stack), traitId, color, level);
    }

    @ZenMethod
    public static boolean removeTrait(IItemStack stack, String traitId) {
        return removeRegisteredTrait(stack, traitId);
    }

    @ZenMethod
    public static boolean removeRegisteredTrait(IItemStack stack, String traitId) {
        return TicTraits.removeRegisteredTrait(toMutableStack(stack), traitId);
    }

    @ZenMethod
    public static IItemStack withTrait(IItemStack stack, String traitId, int color, int level) {
        return withRegisteredTrait(stack, traitId, color, level);
    }

    @ZenMethod
    public static IItemStack withRegisteredTrait(IItemStack stack, String traitId, int color, int level) {
        return CraftTweakerMC.getIItemStack(TicTraits.withRegisteredTrait(toStackCopy(stack), traitId, color, level));
    }

    @ZenMethod
    public static IItemStack withoutTrait(IItemStack stack, String traitId) {
        return withoutRegisteredTrait(stack, traitId);
    }

    @ZenMethod
    public static IItemStack withoutRegisteredTrait(IItemStack stack, String traitId) {
        return CraftTweakerMC.getIItemStack(TicTraits.withoutRegisteredTrait(toStackCopy(stack), traitId));
    }

    @ZenMethod
    public static boolean setBroken(IItemStack stack, boolean broken) {
        return TicStats.setBroken(toMutableStack(stack), broken);
    }

    @ZenMethod
    public static String[] getStats(IItemStack stack) {
        return TicStats.getStats(toStackCopy(stack));
    }

    @ZenMethod
    public static boolean hasStat(IItemStack stack, String statName) {
        return TicStats.hasStat(toStackCopy(stack), statName);
    }

    @ZenMethod
    public static float getFloatStat(IItemStack stack, String statName) {
        return TicStats.getFloat(toStackCopy(stack), statName);
    }

    @ZenMethod
    public static int getIntStat(IItemStack stack, String statName) {
        return TicStats.getInt(toStackCopy(stack), statName);
    }

    @ZenMethod
    public static boolean addStat(IItemStack stack, String statName, float amount, String token) {
        return TicStats.add(toMutableStack(stack), statName, amount, token);
    }

    @ZenMethod
    public static int getFluidCapacity(IItemStack stack) {
        return TicFluids.of(toStackCopy(stack)).primaryCapacity();
    }

    @ZenMethod
    public static boolean setFluidCapacity(IItemStack stack, int capacity) {
        return TicFluids.of(toMutableStack(stack)).setPrimaryCapacity(capacity);
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
    public static boolean clearFluid(IItemStack stack) {
        return TicFluids.of(toMutableStack(stack)).clearPrimaryFluid();
    }

    @ZenMethod
    public static boolean hasAnyFluidTank(IItemStack stack) {
        return TicFluids.of(toStackCopy(stack)).hasAnyTank();
    }

    @ZenMethod
    public static int fillFluid(IItemStack stack, String fluidName, int amount, boolean doFill) {
        if (fluidName == null || fluidName.trim().isEmpty() || amount <= 0) {
            return 0;
        }
        net.minecraftforge.fluids.Fluid fluid = net.minecraftforge.fluids.FluidRegistry.getFluid(fluidName);
        if (fluid == null) {
            return 0;
        }
        TicFluidOperationResult result = TicFluids.of(toMutableStack(stack)).fill(new FluidStack(fluid, amount), doFill);
        return result.amount();
    }

    @ZenMethod
    public static String[] getArmorTraits(IPlayer player) {
        return TicArmor.getTraits(toPlayer(player));
    }

    @ZenMethod
    public static String[] getArmorSlotTraits(IPlayer player, String slotName) {
        return TicArmor.getSlotTraits(toPlayer(player), slotName);
    }

    @ZenMethod
    public static boolean hasArmorTrait(IPlayer player, String traitId) {
        return TicArmor.hasTrait(toPlayer(player), traitId);
    }

    @ZenMethod
    public static boolean hasArmorSlotTrait(IPlayer player, String slotName, String traitId) {
        return TicArmor.hasSlotTrait(toPlayer(player), slotName, traitId);
    }

    @ZenMethod
    public static boolean refreshArmorCache(IPlayer player) {
        return TicArmor.refreshCache(toPlayer(player));
    }

    private static ItemStack toStackCopy(IItemStack stack) {
        return stack == null ? ItemStack.EMPTY : CraftTweakerMC.getItemStack(stack);
    }

    private static ItemStack toMutableStack(IItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return CraftTweakerMC.getItemStack(stack.mutable());
    }

    private static EntityPlayer toPlayer(IPlayer player) {
        return CraftTweakerMC.getPlayer(player);
    }
}
