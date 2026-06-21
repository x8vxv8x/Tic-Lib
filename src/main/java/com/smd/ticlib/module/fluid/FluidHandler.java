package com.smd.ticlib.module.fluid;

import com.smd.ticlib.core.state.TicStackState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.utils.TagUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class FluidHandler implements IFluidHandlerItem {

    private final TicStackState state;
    private final ItemStack container;
    private int cachedDirtyVersion = Integer.MIN_VALUE;
    private List<ModifierProvider> cachedProviders = new ArrayList<>();
    private List<ModifierTank> cachedTanks = new ArrayList<>();

    public FluidHandler(ItemStack container) {
        this(TicStackState.of(container));
    }

    public FluidHandler(TicStackState state) {
        this.state = state;
        this.container = state == null ? ItemStack.EMPTY : state.stack();
    }

    @Nonnull
    @Override
    public ItemStack getContainer() {
        return container;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        if (state == null) {
            return new IFluidTankProperties[0];
        }
        refreshCaches();
        List<IFluidTankProperties> properties = new ArrayList<>();
        int capacity = FluidModule.INSTANCE.getCapacity(state);
        if (capacity > 0) {
            properties.add(new TicFluidTankProperties(FluidModule.INSTANCE.getFluid(state), capacity, true, true));
        }
        for (ModifierTank tank : cachedTanks) {
            properties.add(new TicFluidTankProperties(
                    tank.provider.getFluidInTank(container, tank.modifierTag, tank.tank),
                    tank.provider.getTankCapacity(container, tank.modifierTag, tank.tank),
                    true,
                    true
            ));
        }
        return properties.toArray(new IFluidTankProperties[0]);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (state == null || resource == null || resource.amount <= 0) {
            return 0;
        }
        refreshCaches();
        int filled = FluidModule.INSTANCE.fillInternal(state, resource, doFill);
        if (filled >= resource.amount) {
            return filled;
        }
        FluidStack remaining = new FluidStack(resource, resource.amount - filled);
        for (ModifierProvider provider : cachedProviders) {
            int tankFilled = provider.provider.fill(container, provider.modifierTag, remaining, doFill);
            if (tankFilled <= 0) {
                continue;
            }
            filled += tankFilled;
            if (filled >= resource.amount) {
                return filled;
            }
            remaining.amount = resource.amount - filled;
        }
        return filled;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (state == null || resource == null || resource.amount <= 0) {
            return null;
        }
        refreshCaches();
        FluidStack drained = FluidModule.INSTANCE.drainInternal(state, resource, doDrain);
        int drainedAmount = drained == null ? 0 : drained.amount;
        if (drainedAmount >= resource.amount) {
            return drained;
        }
        FluidStack remaining = new FluidStack(resource, resource.amount - drainedAmount);
        for (ModifierProvider provider : cachedProviders) {
            FluidStack tankDrained = provider.provider.drain(container, provider.modifierTag, remaining, doDrain);
            if (tankDrained != null && tankDrained.amount > 0) {
                if (drained == null) {
                    drained = tankDrained;
                } else if (drained.isFluidEqual(tankDrained)) {
                    drained.amount += tankDrained.amount;
                }
                drainedAmount += tankDrained.amount;
                if (drainedAmount >= resource.amount) {
                    break;
                }
                remaining.amount = resource.amount - drainedAmount;
            }
        }
        return drained;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (state == null || maxDrain <= 0) {
            return null;
        }
        refreshCaches();
        FluidStack drained = FluidModule.INSTANCE.drainInternal(state, maxDrain, doDrain);
        int drainedAmount = drained == null ? 0 : drained.amount;
        if (drainedAmount >= maxDrain) {
            return drained;
        }
        FluidStack filter = drained == null ? null : new FluidStack(drained, maxDrain - drainedAmount);
        for (ModifierProvider provider : cachedProviders) {
            FluidStack tankDrained = filter == null
                    ? provider.provider.drain(container, provider.modifierTag, maxDrain - drainedAmount, doDrain)
                    : provider.provider.drain(container, provider.modifierTag, filter, doDrain);
            if (tankDrained != null && tankDrained.amount > 0) {
                if (drained == null) {
                    drained = tankDrained;
                    filter = new FluidStack(drained, maxDrain - tankDrained.amount);
                } else if (drained.isFluidEqual(tankDrained)) {
                    drained.amount += tankDrained.amount;
                    filter.amount = maxDrain - drained.amount;
                }
                drainedAmount += tankDrained.amount;
                if (drainedAmount >= maxDrain) {
                    break;
                }
            }
        }
        return drained;
    }

    public boolean hasModifierTank() {
        if (state == null) {
            return false;
        }
        refreshCaches();
        return !cachedTanks.isEmpty();
    }

    public TicFluidTankView[] describeTanks() {
        if (state == null) {
            return new TicFluidTankView[0];
        }
        refreshCaches();
        List<TicFluidTankView> tanks = new ArrayList<>();
        int primaryCapacity = FluidModule.INSTANCE.getCapacity(state);
        if (primaryCapacity > 0) {
            tanks.add(new TicFluidTank(
                    TicFluidTankKind.PRIMARY,
                    "primary",
                    0,
                    primaryCapacity,
                    FluidModule.INSTANCE.getFluid(state),
                    true,
                    true
            ));
        }
        for (ModifierTank tank : cachedTanks) {
            tanks.add(new TicFluidTank(
                    TicFluidTankKind.MODIFIER,
                    tank.id(),
                    tank.tank,
                    tank.provider.getTankCapacity(container, tank.modifierTag, tank.tank),
                    tank.provider.getFluidInTank(container, tank.modifierTag, tank.tank),
                    true,
                    true
            ));
        }
        return tanks.toArray(new TicFluidTankView[0]);
    }

    private void refreshCaches() {
        int dirtyVersion = state == null ? 0 : state.dirtyVersion();
        if (dirtyVersion == cachedDirtyVersion) {
            return;
        }
        cachedDirtyVersion = dirtyVersion;
        cachedProviders = buildModifierProviders();
        cachedTanks = buildModifierTanks(container, cachedProviders);
    }

    private static List<ModifierTank> buildModifierTanks(ItemStack container, List<ModifierProvider> providers) {
        List<ModifierTank> tanks = new ArrayList<>();
        for (ModifierProvider modifierProvider : providers) {
            int count = Math.max(0, modifierProvider.provider.getTanks(container, modifierProvider.modifierTag));
            for (int tank = 0; tank < count; tank++) {
                if (modifierProvider.provider.getTankCapacity(container, modifierProvider.modifierTag, tank) > 0) {
                    tanks.add(new ModifierTank(modifierProvider.provider, modifierProvider.modifierTag, tank));
                }
            }
        }
        return tanks;
    }

    private List<ModifierProvider> buildModifierProviders() {
        List<ModifierProvider> providers = new ArrayList<>();
        for (NBTTagCompound modifierTag : getModifierTags(container)) {
            IModifier modifier = TinkerRegistry.getModifier(modifierTag.getString("identifier"));
            if (modifier instanceof TicFluidTankProvider) {
                providers.add(new ModifierProvider((TicFluidTankProvider) modifier, modifierTag));
            }
        }
        return providers;
    }

    private static List<NBTTagCompound> getModifierTags(ItemStack stack) {
        List<NBTTagCompound> tags = new ArrayList<>();
        for (int i = 0; i < TagUtil.getModifiersTagList(stack).tagCount(); i++) {
            tags.add(TagUtil.getModifiersTagList(stack).getCompoundTagAt(i));
        }
        return tags;
    }

    private static final class ModifierTank {
        private final TicFluidTankProvider provider;
        private final NBTTagCompound modifierTag;
        private final int tank;

        private ModifierTank(TicFluidTankProvider provider, NBTTagCompound modifierTag, int tank) {
            this.provider = provider;
            this.modifierTag = modifierTag;
            this.tank = tank;
        }

        private String id() {
            return modifierTag.getString("identifier") + "#" + tank;
        }
    }

    private static final class ModifierProvider {
        private final TicFluidTankProvider provider;
        private final NBTTagCompound modifierTag;

        private ModifierProvider(TicFluidTankProvider provider, NBTTagCompound modifierTag) {
            this.provider = provider;
            this.modifierTag = modifierTag;
        }
    }
}
