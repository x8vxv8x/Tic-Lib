# TicLib CraftTweaker / Java 用法

`TicLib` 是一个面向 TiC 1.12.2 / ConArm 的底层库。当前版本已经破坏性重构：

- Java 公开入口收敛到 `com.smd.ticlib.api.*`。
- CraftTweaker 入口仍为 `mods.ticlib.TicTool`、`mods.ticlib.TicEvents` 和对应 expansion。
- 内部实现分为 `core`、`module`、`api`，脚本和外部 Java 代码不应依赖内部包。
- 旧 `util/state/stats/fluid` 兼容层已删除，旧 NBT 数据不迁移。

## NBT 写入语义

TicLib 私有数据统一保存到：

```text
root.ticlib.DataVersion
root.ticlib.DirtyVersion
root.ticlib.Components.<moduleId>
```

当前内置组件：

- `ticlib:stats`：保存 token 化属性增量。
- `ticlib:fluid`：保存工具/盔甲流体容量和当前流体。

规则：

- 不写隐藏 trait/modifier。
- 不污染 `Traits`、`Modifiers`、`Base.Modifiers` 来持久化库数据。
- 工具/盔甲 rebuild 后由 lifecycle 模块复制并 replay `ticlib` 私有组件。
- trait 和 base modifier API 仍然操作 TiC 原生 NBT，因为它们本身就是 TiC 语义。

## Java API

```java
import com.smd.ticlib.api.TicArmor;
import com.smd.ticlib.api.TicFluids;
import com.smd.ticlib.api.TicItems;
import com.smd.ticlib.api.TicStats;
import com.smd.ticlib.api.TicTraits;

if (TicItems.isTool(stack)) {
    String[] materials = TicItems.getMaterials(stack);
    String[] traits = TicTraits.getTraits(stack);
    String[] stats = TicStats.getStats(stack);
}

TicStats.add(stack, "MiningSpeed", 2.0D, "bonus_speed");
TicStats.add(stack, "FreeModifiers", 1.0D, "bonus_slot");
TicTraits.addRegisteredTrait(stack, "sharp", 0xffffff, 1);
TicFluids.of(stack).setPrimaryCapacity(1000);
TicFluids.of(stack).setPrimaryFluid(new FluidStack(FluidRegistry.WATER, 500));

String[] armorTraits = TicArmor.getTraits(player);
```

流体 modifier 扩展点：

```java
import com.smd.ticlib.module.fluid.TicFluidTankProvider;

public class MyModifier extends SomeModifier implements TicFluidTankProvider {
    // 实现 getTankCapacity / fill / drain 等方法。
}
```

## 查询

```zenscript
mods.ticlib.TicTool.isTool(stack as IItemStack) as bool
mods.ticlib.TicTool.isArmor(stack as IItemStack) as bool
mods.ticlib.TicTool.getAllItems() as IItemStack[]
mods.ticlib.TicTool.getArmorType(stack as IItemStack) as string
mods.ticlib.TicTool.getArmorSlot(stack as IItemStack) as IEntityEquipmentSlot
mods.ticlib.TicTool.getMaterials(stack as IItemStack) as string[]
```

## 词条

```zenscript
mods.ticlib.TicTool.getTraits(stack as IItemStack) as string[]
mods.ticlib.TicTool.hasTrait(stack as IItemStack, traitId as string) as bool
mods.ticlib.TicTool.getTraitColor(stack as IItemStack, traitId as string) as int
mods.ticlib.TicTool.getTraitLevel(stack as IItemStack, traitId as string) as int
mods.ticlib.TicTool.applyRegisteredTrait(stack as IItemStack, traitId as string, color as int, level as int) as bool
mods.ticlib.TicTool.removeRegisteredTrait(stack as IItemStack, traitId as string) as bool
mods.ticlib.TicTool.withRegisteredTrait(stack as IItemStack, traitId as string, color as int, level as int) as IItemStack
mods.ticlib.TicTool.withoutRegisteredTrait(stack as IItemStack, traitId as string) as IItemStack
```

`addTrait/removeTrait/withTrait/withoutTrait` 仍作为脚本侧短别名存在。

## Stats 数值字段

```zenscript
mods.ticlib.TicTool.getStats(stack as IItemStack) as string[]
mods.ticlib.TicTool.hasStat(stack as IItemStack, statName as string) as bool
mods.ticlib.TicTool.getFloatStat(stack as IItemStack, statName as string) as float
mods.ticlib.TicTool.getIntStat(stack as IItemStack, statName as string) as int
mods.ticlib.TicTool.addStat(stack as IItemStack, statName as string, amount as float, token as string) as bool
```

说明：

- `addStat` 会保存到 `ticlib.Components["ticlib:stats"]`。
- 整数、浮点和其它 NBT 数值字段共用 `addStat`，内部会按原字段类型写回。
- 相同 token 只应用一次。
- 只允许修改当前 `Stats` 中已经存在的数值字段。
- rebuild 后由 `PersistentStatsModule` replay。

## 流体容器

```zenscript
mods.ticlib.TicTool.hasAnyFluidTank(stack as IItemStack) as bool
mods.ticlib.TicTool.getFluidCapacity(stack as IItemStack) as int
mods.ticlib.TicTool.setFluidCapacity(stack as IItemStack, capacity as int) as bool
mods.ticlib.TicTool.getFluidAmount(stack as IItemStack) as int
mods.ticlib.TicTool.getFluidName(stack as IItemStack) as string
mods.ticlib.TicTool.clearFluid(stack as IItemStack) as bool
mods.ticlib.TicTool.fillFluid(stack as IItemStack, fluidName as string, amount as int, doFill as bool) as int
```

说明：

- 支持 TiC 工具和 ConArm 盔甲。
- 容量大于 0 的物品会暴露 Forge `FLUID_HANDLER_ITEM_CAPABILITY`。
- Java 侧推荐入口改为 `TicFluids.of(stack)`，它会返回 `TicFluidAccess`。
- `TicFluidAccess` 提供 `primaryCapacity()`、`primaryFluid()`、`setPrimaryCapacity()`、`setPrimaryFluid()`、`tanks()`、`modifierTanks()`、`fill()`、`drain()`。
- 不默认拦截工具右键方块；世界交互由 Java 侧显式调用 `TicFluids.interactWithFluidHandler`。

## 构建事件

```zenscript
import mods.ticlib.TicEvents;

TicEvents.onToolBuild(function(event as mods.ticlib.event.ToolBuildEvent) {
    if (event.toolId == "tconstruct:pickaxe" && event.hasMaterial("manyullyn")) {
        event.addTrait("sharp", 0xffffff, 1);
    }

    if (event.hasMaterial("cobalt")) {
        event.addBaseModifier("haste");
    }

    if (event.hasStat("MiningSpeed")) {
        event.addStat("MiningSpeed", 1.0);
    }
});
```

事件对象：

```zenscript
event.itemId as string
event.toolId as string
event.armorId as string
event.materials as string[]
event.hasMaterial(materialId as string) as bool

event.getTraits() as string[]
event.hasTrait(traitId as string) as bool
event.addTrait(traitId as string, color as int, level as int) as bool
event.removeTrait(traitId as string) as bool
event.getBaseModifiers() as string[]
event.addBaseModifier(traitOrModifierId as string) as bool
event.removeBaseModifier(traitOrModifierId as string) as bool

event.getStats() as string[]
event.hasStat(statName as string) as bool
event.getFloatStat(statName as string) as float
event.getIntStat(statName as string) as int
event.addStat(statName as string, amount as float) as bool
```

## 护甲缓存

```zenscript
mods.ticlib.TicTool.getArmorTraits(player as IPlayer) as string[]
mods.ticlib.TicTool.getArmorSlotTraits(player as IPlayer, slotName as string) as string[]
mods.ticlib.TicTool.hasArmorTrait(player as IPlayer, traitId as string) as bool
mods.ticlib.TicTool.hasArmorSlotTrait(player as IPlayer, slotName as string, traitId as string) as bool
mods.ticlib.TicTool.refreshArmorCache(player as IPlayer) as bool
```

槽位名支持 `head/helmet`、`chest/chestplate`、`legs/leggings`、`feet/boots`。
