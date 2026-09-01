# 配方覆盖清单（Recipe Coverage）

> 盘点基准：`src/generated/resources/` 下全部已生成配方（2026-09-01，feat/bacteria 分支）。
> 结论先行：**除文末所列 30 个物品与 12 个流体外，所有注册内容均有产出配方**；
> 完全重复的配方 **0 个**（多来源输出均为有意设计，见第 4 节）。

## 1. 配方数据的两个来源

| 来源 | 位置 | 内容 |
|---|---|---|
| 手写配方链（`NIRecipeProvider`） | `data/mi_nested_infinity/recipe/<链名>/` | 14 条链共 4821 个配方（其中培养组合 4657 个） |
| MI 材质标准配方（`NIMaterial` → MI Material API） | `data/modern_industrialization/recipe/materials/<材质>/` | naquadah / uranium_triplatinum / nichrome / tpv 四个材质的部件加工配方 |

材质标准配方覆盖：粉↔小粉、粒↔锭、块打包/拆包（packer/craft）、电爆炉 粉→热锭（线圈等级门槛）、热交换器 热锭→锭、压缩机 锭→板、切割机 锭→2 杆、装配机 板+杆→转子（仅 naquadah，MI 自动转子配方已取消）、拉丝机 板→2 线（nichrome/tpv）、粉碎机 各部件→粉。

## 2. 配方链总览（全部已有产出）

| 链 | 目录 | 覆盖内容 |
|---|---|---|
| 生化平台 `bioChain` | `bio/` (64) | 9 种催化剂；平台化学品（氨/一氧化碳/甲醇/甲醛/氢氰酸/乙酸/乙醛/异丁醛/异戊醛/2-甲基丁醛/苯乙醛/乙醇醛/氨氰）；石灰→熟石灰；海带引导→低纯藻类→粗洗红藻；琼脂 9 步提取链；16 种氨基酸（Strecker、酶法等）→ 蛋白质（超级搅拌机 1G EU/t）→ 营养琼脂 |
| 环氧树脂 `epoxyChain` | `epoxy/` (26) | 盐水电解→氯丙烯→环氧氯丙烷；异丙苯法→苯酚/丙酮→双酚 A→DGEBA 树脂→固化→切割成环氧板；离子交换树脂/铂网/催化剂；氟锑酸（HF+SbF5） |
| PBI 链 `pbiChain` | `pbi/` (12) | 对甲苯磺酸；苯→硝基苯→联苯胺→二硝基→二氨基联苯胺；甲苯歧化→间二甲苯→间苯二甲酸→DPIP；Celazole 缩聚→固化→切割成 PBI 板；硅岩框架（4 硅岩杆+4 硅岩板+100 mB 强力胶水） |
| 强力胶水链 `glueChain` | `glue/` (5) | 氰化钠→氰乙酸→氰基乙酸甲酯→聚氰基丙烯酸甲酯→氰基丙烯酸酯强力胶水（真实化学路线，详见第 7 节） |
| 湿件电路 `bioCircuitChain` | `wetware/` (8) + `electric_age/` (2) | 冷色培养皿（`cold_petri_dishes` 标签 21 种：菌株含 cyan/blue/teal 且不含 red/orange/pink）+naquadah 锭→超能硅岩→溶液→变异剂（流体）；生物 RAM/MMU/ALU（各 1 种四色皿+4 个 MI 部件+变异剂 100/200/300 mB）；精英马达/精英泵；湿件电路板（含精英泵与 10 mB 氟锑酸）；湿件电路（高级超导体） |
| 晶体电路 `epoxyChain` 末尾 + `circuitChain` | `electric_age/` (2) | 晶体电路板（环氧板系）；晶体电路（硅岩计算单元系） |
| naquadah 链 `niquadahChain` | `naquadah/` (16) | 氘+He-3 聚变→氙；硝酸；独居石浸出→中和→还原→铂化→UU 分离→氯聚变→中子活化→离心→naquadah 粉（MI 材质链接管后续）；氙氟化/氟氢酸；高纯单晶硅岩；硅岩计算单元 |
| 铂铀合金 `uraniumTriplatinumChain` | `uranium_triplatinum/` (1) | 铂粉+铀粉→uranium_triplatinum 粉 |
| 镍铬合金 `nichromeChain` + 线圈 | `nichrome/` (2) | 镍铬混合粉；nichrome 线圈（云母绝缘） |
| 超导链 `superconductorChain` | `superconductor/` (13) | 钒回收→TPV 合金→TPV 线缆（25% 大泵损耗）→TPV 线圈；超重元素离心→朱砂/重晶石→汞氧/钡氧→HgBaTiCuO→EBF 烧结→基材→拉丝；高级橡胶；高级超导线缆 2^33 EU/t |
| 硅橡胶链 `siliconeChain` | `silicone/` (6) | 氯甲烷→Müller-Rochow→二甲二氯硅烷→PDMS（HCl 循环）→固化→切割→硅橡胶云母片 |
| 云母系 `nichromeCoilChain` 前半 | `mica/` (4) | 云母粉→塑料云母混合料→压缩块→切割成绝缘片 |
| 玻璃熔融 | `magma_crucible/` (1) | 玻璃块→144 mB 液态玻璃 |
| 培养系统 `cultivationRecipes` + `wildIsolation` | `cultivation/` (4657) | 野外分离（泥土+琼脂，24 种距离 3/5 双色皿，单抽 1%）；97 种培养皿两两组合共 C(97,2)=4656 个配方（XAND 重组，双色产出） |

**流体**：68 个注册流体中 56 个有产出（涵盖上表全部中间体，含新增的变异剂/氰基乙酸甲酯/氰基丙烯酸酯胶水）；无产出的 12 个见第 3 节。

**物品**：除第 3 节所列外全部有产出，包括：97 个培养皿（野外分离播种 24 种，其余全部可由培养组合覆盖）、5 个方块（塑料云母块/固化环氧块/生石灰块/硅橡胶块/PBI 块）、全部电路（晶体/湿件）、精英马达/精英泵、强力胶水链全部中间体（氰化钠/氰乙酸/聚氰基丙烯酸甲酯）、全部材质部件中除齿轮外的所有品种。

**机器与桶**：4 台自定义机器（岩浆坩埚/离子交换/藻类培养器/超级搅拌机）由 MI 机器 API（`SingleBlockCraftingMachines.registerMachineTiers`）自动注册合成表，不在本次 datagen 盘点范围；流体桶用桶直接装取。

## 3. 无配方内容清单

### 3.1 规划中的后续内容（不算缺口）

| 类别 | 物品 | 说明 |
|---|---|---|
| 未来电路层级 ×8 | resonant / optical / electromagnetic_interference / awakened_draconic / paracausal / multiverse_parallel_computational / 24d_non_euclidean_space_time_folding / ama（各含 circuit + circuit_board，共 16 个） | `NICircuits.ALL_TIERS` 已注册，配方待后续版本按层级补齐 |
| 未来线圈 ×8 | trinium_dinaquadide / neutronium / infinitium / hypogen / stellarium / draconicic_prismarinium_diaquamide / eternium / terminium（各 `_coil`） | `NICoils` 的 EBF 线圈扩展已注册并挂入电爆炉 mixin，仅 nichrome_coil、tpv_coil 两级有配方，其余 8 级待补 |

### 3.2 有意取消 / 已知待补

| 内容 | 原因 |
|---|---|
| `modern_industrialization:{naquadah,nichrome,tpv,uranium_triplatinum}_gear`（4 个） | MI StandardRecipes 不为附属材质生成齿轮正向配方；当前也没有任何配方消费这些齿轮，属于"注册了但无用"的部件，待设计用途后补配方或移除部件 |
| `modern_industrialization:{nichrome,tpv}_block`（2 个） | 两材质的 `packer/block`、`craft/block_from_ingot` 在 `NIMaterials` 中被主动 `cancelRecipes`（存储块故意不提供）；naquadah 与 uranium_triplatinum 的块配方保留 |
| 12 个藻株流体 `mi_nested_infinity:{erythrophyta…rhodophyta}` | 无任何产出配方；其中仅 rhodophyta 被 `bio/agar_washing`（纯培养洗藻）消耗。琼脂目前由"海带→低纯藻类→粗洗"（`bio/low_purity_algae` + `bio/agar_washing_crude`，产量减半）引导，纯培养路线实际不可用。待设计"培养皿→藻株流体"的产出（例如培养器产出对应菌株液）后纯路线才能闭环 |

## 4. 多来源输出（有意设计，非重复）

同一物品有多条产出途径，均为设计而非冗余：

- **离子交换树脂**：苯乙烯直接磺化 / SBR 磺化两条路线（`epoxy/ion_exchange_resin_styrene` vs `_sbr`），原料不同、产出相同；
- **湿红藻**：纯培养洗藻（`bio/agar_washing`，2 个/次）vs 海带粗洗（`bio/agar_washing_crude`，1 个/次+盐），后者为低纯引导路线；
- **氨**：Haber-Bosch 合成 + 氯化铵石灰回收（`bio/ammonium_chloride_recycle`）；
- **氯丙烯 / 环氧氯丙烷**：精制主线 + 废液回收/树脂解析副线；
- **苯 / 甲苯 / 硫酸 / 酚**：MI 原生来源 + 各反应的介质回收流；
- **培养皿（97 种）**：野外分离播种 24 种，其余由大量培养组合产出（培养系统的核心玩法）；
- **水 / 盐粉 / 氯化铵** 等常见副产物出现在多个配方中。

## 5. 重复配方检查

以"机器类型 + 输入集合 + 输出集合"完全一致为判据扫描全部 4900+ 个生成配方：**0 个完全重复**。

## 6. 本次盘点伴随的修正

1. **苯丙氨酸断链（已修）**：`phenylalanine` 被蛋白质（超级搅拌机）与酪氨酸羟化消耗，但此前没有任何产出配方，导致 蛋白质 → 营养琼脂 → 全部培养链不可达。已按其余氨基酸同款 Strecker 双步路线补齐：苯乙醛（苯乙烯氧化已有产出）+ NH3 + HCN → `phenylalanine_aminonitrile`（新物品）→ 盐酸水解 → 苯丙氨酸 + 氯化铵。新配方：`bio/phenylalanine_aminonitrile`、`bio/phenylalanine`。
2. 盘点过程中确认的其余无配方内容均在上表分类记录，未做改动。

## 7. 2026-09-01 硅岩重构记录（盘点之后的一次大重排）

1. **命名统一**：`silicon rock` 系列全部更名为 naquadah（硅岩）— 物品 `naquadah_frame`（原 silicon_rock_frame）、`supercharged_naquadah`（原 supercharged_silicon_rock_ingot）、流体 `supercharged_naquadah_solution`；对应配方 `pbi/naquadah_frame`、`wetware/supercharged_naquadah*`。
2. **强力胶水链（`glue/` ×5，真实化学路线）**：氰基丙烯酸酯（502 胶主体）— NaOH+HCN→氰化钠；氰化钠+氯乙酸→氰乙酸（SN2 氰代）；氰乙酸+甲醇（对甲苯磺酸催化酯化）→氰基乙酸甲酯；+甲醛（熟石灰缩合）→聚氰基丙烯酸甲酯；二氧化硫阻聚下热解聚→氰基丙烯酸酯胶水（流体，抗拉强度约为丙烯酸酯类胶黏剂的 2 倍）。
3. **变异剂改为流体**：`wetware/mutagen` 输出 500 mB 变异剂流体；生物 RAM/MMU/ALU 改为各用 1 种对应四色培养皿 + 4 个 MI 部件（RAM/MMU/ALU）+ 变异剂 100/200/300 mB。
4. **超能硅岩冷色门控**：mutagenic bombardment 只接受 `mi_nested_infinity:cold_petri_dishes`（21 种：菌株含 cyan/blue/teal 且不含 red/orange/pink）。
5. **精英部件（新物品，均有配方）**：精英马达 = 9 大型高级马达+4 硅岩杆+2 量子电路+1000 mB 润滑剂（512k EU, 4000t）；精英泵 = 3 大型高级泵+1 精英马达+6 硅岩转子+4 量子电路（1M EU, 4000t）。
6. **湿件电路板重排**：硅岩框架+3 PBI 板+12 高级超导线缆+16 钚电池+1 晶体电路板+4 硅岩板+2 对甲苯磺酸+1 精英泵 + 变异剂 400 mB / He-3 100 mB / 氟锑酸 10 mB（8 物品+3 流体，物品数不超 MI 上限）；湿件电路的超导线缆换为高级超导线缆体系。
7. **naquadah 转子**：`NIMaterial.generateRotor()` — 注册 rotor 部件、取消 MI 自动转子配方（blade/ring/bolt 级联）、提供 装配机 1 板+1 杆→1 转子 简化配方（`materials/naquadah/assembler/rotor_simple`）。
8. **贴图统一**：板件全部改用 MI 原版 plate 模板重着色（环氧板/PBI 板，对比度加强保证 3D 感）；精英马达/泵复用 MI 马达/泵模板；超能硅岩/硅岩框架/变异剂桶等同步重绘。
