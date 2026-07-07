# TicLib CraftTweaker 使用说明

本文面向整合包脚本作者，说明 TicLib 在 CraftTweaker 中暴露的入口、扩展方法、构建事件和常见注意事项。

TicLib 支持 Minecraft 1.12.2、Tinkers' Construct 和 Construct's Armory，脚本侧可以统一查询和修改 TiC 工具、ConArm 盔甲、trait、stats、流体容量和玩家盔甲 trait 缓存。

Java 调用方式见 [Java.md](Java.md)。

## 入口总览

静态入口：

```zenscript
mods.ticlib.TicTool
mods.ticlib.TicEvents
```

同时提供扩展方法：

```text
crafttweaker.item.IItemStack
crafttweaker.player.IPlayer
```

## 数据语义

TicLib 私有数据保存到：

```text
root.ticlib.Components["ticlib:stats"]
root.ticlib.Components["ticlib:fluid"]
```

脚本使用时只需要记住：

- trait、base modifier 是 TiC 原生语义，会写入 TiC 原生 NBT。
- `addStat(..., token)` 会写入 `ticlib:stats`，rebuild 后会自动重放。
- 流体容量和主 tank 流体写入 `ticlib:fluid`。
- 相同 stat token 只会生效一次。

## 静态入口

```zenscript
import mods.ticlib.TicTool;

TicTool.isTool(stack as IItemStack) as bool
TicTool.isArmor(stack as IItemStack) as bool
TicTool.getAllItems() as IItemStack[]
TicTool.getArmorType(stack as IItemStack) as string
TicTool.getArmorSlot(stack as IItemStack) as IEntityEquipmentSlot
TicTool.getMaterials(stack as IItemStack) as string[]
```

Trait：

```zenscript
TicTool.getTraits(stack as IItemStack) as string[]
TicTool.hasTrait(stack as IItemStack, traitId as string) as bool
TicTool.getTraitColor(stack as IItemStack, traitId as string) as int
TicTool.getTraitLevel(stack as IItemStack, traitId as string) as int
TicTool.applyRegisteredTrait(stack as IItemStack, traitId as string, color as int, level as int) as bool
TicTool.removeRegisteredTrait(stack as IItemStack, traitId as string) as bool
TicTool.withRegisteredTrait(stack as IItemStack, traitId as string, color as int, level as int) as IItemStack
TicTool.withoutRegisteredTrait(stack as IItemStack, traitId as string) as IItemStack
```

短别名：

```zenscript
TicTool.addTrait(stack, traitId, color, level)
TicTool.removeTrait(stack, traitId)
TicTool.withTrait(stack, traitId, color, level)
TicTool.withoutTrait(stack, traitId)
```

Stats：

```zenscript
TicTool.setBroken(stack as IItemStack, broken as bool) as bool
TicTool.getStats(stack as IItemStack) as string[]
TicTool.hasStat(stack as IItemStack, statName as string) as bool
TicTool.getFloatStat(stack as IItemStack, statName as string) as float
TicTool.getIntStat(stack as IItemStack, statName as string) as int
TicTool.addStat(stack as IItemStack, statName as string, amount as float, token as string) as bool
```

`addStat` 规则：

- 只允许修改当前 `Stats` 中已经存在的数值字段。
- 相同 `token` 只应用一次；建议使用 `modid:用途` 形式，例如 `examplepack:cobalt_pick_speed`。
- 整数、浮点和其它 NBT 数值字段共用同一套逻辑，写回时按原字段类型处理。

流体：

```zenscript
TicTool.hasAnyFluidTank(stack as IItemStack) as bool
TicTool.getFluidCapacity(stack as IItemStack) as int
TicTool.setFluidCapacity(stack as IItemStack, capacity as int) as bool
TicTool.getFluidAmount(stack as IItemStack) as int
TicTool.getFluidName(stack as IItemStack) as string
TicTool.clearFluid(stack as IItemStack) as bool
TicTool.fillFluid(stack as IItemStack, fluidName as string, amount as int, doFill as bool) as int
```

流体规则：

- 支持 TiC 工具和 ConArm 盔甲。
- 主 tank 容量大于 0，或 modifier 提供 tank 时，物品会暴露 Forge `FLUID_HANDLER_ITEM_CAPABILITY`。
- `fillFluid(..., false)` 只模拟填充，`fillFluid(..., true)` 才写入。
- 容量降低后，超过容量的流体会被裁剪；容量为 0 时主 tank 流体会被清空。

护甲缓存：

```zenscript
TicTool.getArmorTraits(player as IPlayer) as string[]
TicTool.getArmorSlotTraits(player as IPlayer, slotName as string) as string[]
TicTool.hasArmorTrait(player as IPlayer, traitId as string) as bool
TicTool.hasArmorSlotTrait(player as IPlayer, slotName as string, traitId as string) as bool
TicTool.refreshArmorCache(player as IPlayer) as bool
```

槽位名支持：

```text
head / helmet
chest / chestplate
legs / leggings
feet / boots
```

## IItemStack 扩展

查询方法：

```zenscript
stack.isTool() as bool
stack.isArmor() as bool
stack.getArmorType() as string
stack.getArmorSlot() as IEntityEquipmentSlot
stack.getMaterials() as string[]
stack.getTraits() as string[]
stack.hasTrait(traitId as string) as bool
stack.getTraitColor(traitId as string) as int
stack.getTraitLevel(traitId as string) as int
stack.getStats() as string[]
stack.hasStat(statName as string) as bool
stack.getFloatStat(statName as string) as float
stack.getIntStat(statName as string) as int
stack.getFluidCapacity() as int
stack.getFluidAmount() as int
stack.getFluidName() as string
stack.hasAnyFluidTank() as bool
```

返回新物品的方法：

```zenscript
stack.withTrait(traitId as string, color as int, level as int) as IItemStack
stack.withoutTrait(traitId as string) as IItemStack
stack.withBroken(broken as bool) as IItemStack
stack.withStat(statName as string, amount as float, token as string) as IItemStack
stack.withFluidCapacity(capacity as int) as IItemStack
stack.withoutFluid() as IItemStack
```

示例：

```zenscript
val sharpStack = stack.withTrait("sharp", 0xFFFFFF, 1);
val fastStack = sharpStack.withStat("MiningSpeed", 1.5, "examplepack:mining_speed");
val fluidStack = fastStack.withFluidCapacity(1000);
```

## IPlayer 扩展

```zenscript
player.getArmorTraits() as string[]
player.getArmorSlotTraits(slotName as string) as string[]
player.hasArmorTrait(traitId as string) as bool
player.hasArmorSlotTrait(slotName as string, traitId as string) as bool
player.refreshArmorCache() as bool
```

## 构建事件

事件在 TiC/ConArm 构建工具或盔甲时触发，适合根据材料、目标物品和当前 stats 动态附加 trait、base modifier 或即时 stat 增量。

```zenscript
import mods.ticlib.TicEvents;

TicEvents.onToolBuild(function(event as mods.ticlib.event.ToolBuildEvent) {
    if (event.toolId == "tconstruct:pickaxe" && event.hasMaterial("manyullyn")) {
        event.addTrait("sharp", 0xFFFFFF, 1);
    }

    if (event.hasMaterial("cobalt")) {
        event.addBaseModifier("haste");
    }

    if (event.hasStat("MiningSpeed")) {
        event.addStat("MiningSpeed", 1.0);
    }
});

TicEvents.onArmorBuild(function(event as mods.ticlib.event.ArmorBuildEvent) {
    if (event.armorId == "conarm:chestplate" && event.hasMaterial("steel")) {
        event.addTrait("reinforced", 0xB0B0B0, 1);
    }
});
```

公共事件对象 `mods.ticlib.event.ItemBuildEvent`：

```zenscript
event.itemId as string
event.materials as string[]
event.hasMaterial(materialId as string) as bool

event.getTraits() as string[]
event.hasTrait(traitId as string) as bool
event.addTrait(traitId as string, color as int, level as int) as bool
event.applyRegisteredTrait(traitId as string, color as int, level as int) as bool
event.removeTrait(traitId as string) as bool

event.getBaseModifiers() as string[]
event.hasBaseModifier(traitOrModifierId as string) as bool
event.addBaseModifier(traitOrModifierId as string) as bool
event.removeBaseModifier(traitOrModifierId as string) as bool

event.getStats() as string[]
event.hasStat(statName as string) as bool
event.getFloatStat(statName as string) as float
event.getIntStat(statName as string) as int
event.addStat(statName as string, amount as float) as bool
```

工具事件额外字段：

```zenscript
event.toolId as string
```

盔甲事件额外字段：

```zenscript
event.armorId as string
```

注意：build event 中的 `event.addStat` 是即时修改当前 build root，不带 token；如果需要让玩家已有物品在后续 rebuild 中持续保留某个加成，请对具体 `IItemStack` 使用 `TicTool.addStat(..., token)`。

## 常见注意事项

- 对脚本物品原地修改时，使用 `TicTool.applyRegisteredTrait/removeRegisteredTrait/addStat/setFluidCapacity/clearFluid/fillFluid` 这类返回 `bool/int` 的方法。
- 想得到一个修改后的副本时，使用 `withTrait/withoutTrait/withStat/withFluidCapacity`。
- `getArmorType` 返回 `helmet`、`chestplate`、`leggings`、`boots`，非盔甲返回空字符串。
- `hasAnyFluidTank` 可能因为主容量大于 0，也可能因为 modifier 提供 tank 而返回 true。
- 对非 TiC/ConArm 物品调用 API 时通常返回空数组、空字符串、0、null 或 false。
- CT 脚本 reload 时，TicLib 会清空并重新注册 `TicEvents` handler。
