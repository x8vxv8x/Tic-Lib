# TicLib 文档索引

TicLib 是一个面向 Minecraft 1.12.2、Tinkers' Construct 和 Construct's Armory 的辅助库，提供统一的工具/盔甲识别、NBT 访问、CraftTweaker API、构建事件、持久 stat 增量、流体能力和玩家盔甲 trait 缓存。

## 文档

- [CraftTweaker 使用说明](Crt.md)：脚本入口、扩展方法、build event、流体能力和常见注意事项。
- [Java 使用说明](Java.md)：Java facade、NBT 数据语义、build root 方法和流体 modifier 扩展点。
- [项目架构说明](../说明.md)：项目分层、NBT 数据边界、模块生命周期和内部开发规范。

## 快速入口

Java 侧优先使用：

```text
com.smd.ticlib.api.*
```

CraftTweaker 侧优先使用：

```zenscript
mods.ticlib.TicTool
mods.ticlib.TicEvents
```

同时支持 `IItemStack` 和 `IPlayer` 扩展方法，详见 [CraftTweaker 使用说明](Crt.md)。
