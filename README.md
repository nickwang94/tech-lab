## Geode Learning Sandbox

这个仓库已被“清空”并重建为一个 **Maven 多模块**骨架，用于后续学习 Apache Geode。

- **父工程**：`pom.xml`
- **子模块**：`geode-learning/`（后续所有 Geode 相关实验代码建议放在这里，或在未来继续新增更多 module）

### 快速开始

- **编译/测试**：

```bash
./mvnw test
```

- **运行示例 main**：

```bash
./mvnw -pl geode-learning exec:java
```

> 说明：当前仅提供最小可编译骨架（不依赖 locator/server），后续你可以在 `geode-learning` 里逐步加入 client/server/locator、region、query、security、WAN 等实验。
