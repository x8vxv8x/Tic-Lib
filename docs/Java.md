# TicLib Java 使用说明

本文面向其它 Mod 或 TicLib 内部扩展的 Java 调用者，说明公开 facade、NBT 数据语义、build root 方法和流体扩展点。

CraftTweaker 脚本用法见 [Crt.md](Crt.md)。

## 入口总览

外部 Java 代码推荐只依赖：

```text
com.smd.ticlib.api.TicItems
com.smd.ticlib.api.TicTraits
com.smd.ticlib.api.TicStats
com.smd.ticlib.api.TicFluids
com.smd.ticlib.api.TicArmor
```

内部包 `core`、`module`、`integration` 不是外部稳定 API。除非是在 TicLib 内部开发，或使用本文明确说明的扩展点 `TicFluidTankProvider`，否则不要直接依赖这些包。

## 数据写入语义

TicLib 私有数据统一保存到：

```text
root.ticlib.DataVersion
root.ticlib.DirtyVersion
root.ticlib.Components.<moduleId>
```

当前内置组件：

```text
root.ticlib.Components["ticlib:stats"]
root.ticlib.Components["ticlib:fluid"]
```

约定：

- trait、modifier、base modifier 仍然写 TiC 原生 NBT，因为它们本身就是 TiC 语义。
- stat 增量、token、流体容量和流体内容写入 TicLib 私有组件。
- 不把隐藏 trait/modifier 当作库数据存储手段。
- 工具或盔甲 rebuild 后，lifecycle 模块会复制 `root.ticlib` 并重放组件状态。

## 快速示例

```java
import com.smd.ticlib.api.TicArmor;
import com.smd.ticlib.api.TicFluids;
import com.smd.ticlib.api.TicItems;
import com.smd.ticlib.api.TicStats;
import com.smd.ticlib.api.TicTraits;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

if (TicItems.isTool(stack) || TicItems.isArmor(stack)) {
    String[] materials = TicItems.getMaterials(stack);
    String[] traits = TicTraits.getTraits(stack);
    String[] stats = TicStats.getStats(stack);
}

TicStats.add(stack, "MiningSpeed", 2.0D, "my_mod:bonus_speed");
TicStats.add(stack, "FreeModifiers", 1.0D, "my_mod:bonus_slot");
TicTraits.addRegisteredTrait(stack, "sharp", 0xffffff, 1);

TicFluids.of(stack).setPrimaryCapacity(1000);
TicFluids.of(stack).setPrimaryFluid(new FluidStack(FluidRegistry.WATER, 500));

String[] armorTraits = TicArmor.getTraits(player);
```

## TicItems

```java
TicItems.isTool(stack);
TicItems.isArmor(stack);
TicItems.isTarget(stack);
TicItems.getAllKnownItems();
TicItems.getArmorType(stack);       // helmet/chestplate/leggings/boots 或空字符串
TicItems.getArmorSlot(stack);       // EntityEquipmentSlot 或 null
TicItems.getMaterials(stack);
```

识别范围：

- `ToolCore` 工具。
- 带 TiC root data 的 `ITinkerable` 工具。
- ConArm `ArmorCore` 盔甲。

## TicTraits

```java
TicTraits.getTraits(stack);
TicTraits.hasTrait(stack, traitId);
TicTraits.getTraitColor(stack, traitId);
TicTraits.getTraitLevel(stack, traitId);
TicTraits.addRegisteredTrait(stack, traitId, color, level);
TicTraits.removeRegisteredTrait(stack, traitId);
TicTraits.withRegisteredTrait(stack, traitId, color, level);
TicTraits.withoutRegisteredTrait(stack, traitId);
```

Build root 方法：

```java
TicTraits.getTraits(root);
TicTraits.hasTrait(root, traitId);
TicTraits.addBuildTrait(root, traitId, color, level);
TicTraits.removeBuildTrait(root, traitId);
TicTraits.getBaseModifiers(root);
TicTraits.hasBaseModifier(root, traitOrModifierId);
TicTraits.addBaseModifier(root, traitOrModifierId);
TicTraits.removeBaseModifier(root, traitOrModifierId);
```

`addBuildTrait/removeBuildTrait` 和 base modifier 方法接收 `NBTTagCompound root`，主要用于 TiC/ConArm build event 内部。普通物品修改优先使用 `ItemStack` 方法。

## TicStats

```java
TicStats.setBroken(stack, broken);
TicStats.getStats(stack);
TicStats.hasStat(stack, statName);
TicStats.getFloat(stack, statName);
TicStats.getInt(stack, statName);
TicStats.add(stack, statName, amount, token);
```

Build root 方法：

```java
TicStats.getStats(root);
TicStats.hasStat(root, statName);
TicStats.getFloat(root, statName);
TicStats.getInt(root, statName);
TicStats.addNow(root, statName, amount);
```

`TicStats.add` 规则：

- 只允许修改当前 `Stats` 中已经存在的数值字段。
- `token` 不能为空，相同 token 只应用一次。
- 增量保存到 `ticlib:stats`，rebuild 后会自动重放。
- 整数、浮点和其它 NBT 数值字段共用同一套逻辑，写回时按原字段类型处理。

`TicStats.addNow` 是即时 root 修改，不保存 token，适合 build event 内部。

## TicFluids

```java
TicFluids.of(stack);
TicFluids.hasTank(stack);
TicFluids.getTankProperties(stack);
TicFluids.getTanks(stack);
TicFluids.getCapacity(stack);
TicFluids.setCapacity(stack, capacity);
TicFluids.getFluid(stack);
TicFluids.setFluid(stack, fluid);
TicFluids.clearFluid(stack);
TicFluids.fill(stack, fluid, doFill);
TicFluids.fillDetailed(stack, fluid, doFill);
TicFluids.drain(stack, fluid, doDrain);
TicFluids.drain(stack, maxDrain, doDrain);
TicFluids.drainDetailed(stack, fluid, doDrain);
TicFluids.drainDetailed(stack, maxDrain, doDrain);
TicFluids.interactWithFluidHandler(player, hand, world, pos, side);
```

`TicFluids.of(stack)` 返回 `TicFluidAccess`，可继续调用：

```java
access.isValid();
access.stack();
access.hasAnyTank();
access.primaryCapacity();
access.primaryFluid();
access.setPrimaryCapacity(capacity);
access.setPrimaryFluid(fluid);
access.clearPrimaryFluid();
access.fill(resource, commit);
access.drain(resource, commit);
access.drain(maxDrain, commit);
access.tanks();
access.primaryTank();
access.modifierTanks();
access.handler();
access.tankProperties();
```

流体规则：

- 主 tank 容量大于 0，或 modifier 提供 tank 时，物品会暴露 Forge `FLUID_HANDLER_ITEM_CAPABILITY`。
- `doFill/doDrain` 或 `commit` 为 `false` 时只模拟，不写入。
- 容量降低后，超过容量的流体会被裁剪；容量为 0 时主 tank 流体会被清空。
- TicLib 默认不拦截工具右键方块；世界交互由调用方显式使用 `TicFluids.interactWithFluidHandler`。

## TicArmor

```java
TicArmor.getTraits(player);
TicArmor.getSlotTraits(player, slotName);
TicArmor.hasTrait(player, traitId);
TicArmor.hasSlotTrait(player, slotName, traitId);
TicArmor.refreshCache(player);
```

槽位名支持：

```text
head / helmet
chest / chestplate
legs / leggings
feet / boots
```

护甲 trait 缓存只存在内存中，不写入 ItemStack NBT。玩家换装、登录、登出、clone 时会刷新；特殊场景可以主动调用 `refreshCache`。

## Modifier 额外流体槽

Java modifier 可以实现 `TicFluidTankProvider`，为工具或盔甲上的 modifier 提供额外 tank。

```java
import com.smd.ticlib.module.fluid.TicFluidTankProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class MyModifier extends SomeModifier implements TicFluidTankProvider {
    @Override
    public int getTanks(ItemStack stack, NBTTagCompound modifierTag) {
        return 1;
    }

    @Override
    public int getTankCapacity(ItemStack stack, NBTTagCompound modifierTag, int tank) {
        return 1000;
    }

    @Nullable
    @Override
    public FluidStack getFluidInTank(ItemStack stack, NBTTagCompound modifierTag, int tank) {
        return null;
    }

    @Override
    public int fill(ItemStack stack, NBTTagCompound modifierTag, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(ItemStack stack, NBTTagCompound modifierTag, int maxDrain, boolean doDrain) {
        return null;
    }
}
```

主 tank 的持久化由 TicLib 管理；modifier tank 的具体存储和读写语义由 modifier 自己决定。

## 常见注意事项

- 外部 Java 代码优先调用 `com.smd.ticlib.api.*`，不要直接操作 `core` 或 `module`。
- 对非 TiC/ConArm 物品调用 API 时通常返回空数组、空字符串、0、null 或 false。
- `TicStats.add` 需要稳定 token，推荐使用 `modid:用途`。
- `TicTraits.withRegisteredTrait` 和 `withoutRegisteredTrait` 返回副本；`addRegisteredTrait` 和 `removeRegisteredTrait` 修改传入 stack。
- build root 方法适合构建事件内部，不适合替代普通 `ItemStack` facade。
