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
| 未来电路层级 ×7 | optical / electromagnetic_interference / awakened_draconic / paracausal / multiverse_parallel_computational / 24d_non_euclidean_space_time_folding / ama（各含 circuit + circuit_board，共 14 个） | `NICircuits.ALL_TIERS` 已注册；**resonant 层级已于 feat/resonant 补齐**（见第 8 节），其余待后续版本按层级补齐 |
| 未来线圈 ×7 | neutronium / infinitium / hypogen / stellarium / draconicic_prismarinium_diaquamide / eternium / terminium（各 `_coil`） | `NICoils` 的 EBF 线圈扩展已注册并挂入电爆炉 mixin；nichrome_coil、tpv_coil 与 **trinium_dinaquadide_coil（feat/resonant）** 三级已有配方，其余 7 级待补 |

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
6. **湿件电路板重排**：硅岩框架+3 PBI 板+12 高级超导线缆+16 钚电池+1 晶体电路板+4 硅岩板+2 对甲苯磺酸+1 精英泵（10% 概率消耗，低温泵站可复用）+ 变异剂 400 mB / He-3 100 mB / 氟锑酸 10 mB（8 物品+3 流体，物品数不超 MI 上限）；湿件电路的超导线缆换为高级超导线缆体系。
7. **naquadah 转子**：`NIMaterial.generateRotor()` — 注册 rotor 部件、取消 MI 自动转子配方（blade/ring/bolt 级联）、提供 装配机 1 板+1 杆→1 转子 简化配方（`materials/naquadah/assembler/rotor_simple`）；贴图/模型/lang 由 `tools/gen_naquadah_rotor.py` 生成（MI 不锈钢转子模板映射到 naquadah 绿青色阶）。
8. **贴图统一**：板件全部改用 MI 原版 plate 模板重着色（环氧板/PBI 板，对比度加强保证 3D 感）；精英马达/泵=当前 MI 原版贴图仅蓝色部分色相旋转为绿色（灰白/铜色不动，`blue_to_green`）；超能硅岩/硅岩框架/变异剂桶等同步重绘。

## 8. 2026-09-01 谐振电路程序（feat/resonant，103 个新配方）

> 全部位于 `resonant/`（101 个）+ `electric_age/circuit/assembler/resonant_{circuit,circuit_board}`（2 个终局）。
> 能耗规则：分离/合成关键步骤 **2G EU/t、4000 s**；试剂合成 2G EU/t、1000 s；终局板与电路 2G EU/t、**8000 s**；
> 聚变沿 MI 16k EU/t 先例（EBF 系配方豁免能耗规则）。

### 8.1 六条链

| 链 | 配方数 | 覆盖内容 |
|---|---|---|
| 全元素分离级联 `resonantSeparationChain` | 46 | 进料 3（氙+氧聚变→氡；氡裂解超能硅岩溶液→超重裂化液；重元素残渣+氡+硝酸备用固料入口）；Cycle A PUREX ×8（亚硝酸钠调价 25% 概耗→TBP 萃取 10% 概耗+氙/碲副产→肼反萃→硝酸 U/Np 分离→煅烧氢还原 U/Pu/Np→Th 酸洗）；Cycle B TRUEX+TALSPEAK ×12（CMPO 5% 概耗→硝酸反萃 TBP 回收→DTPA 10% 概耗分组→Bk 氯酸钠氧化 10% 概耗+碲 15% 副产→α-HIBA 淋洗阶梯 100/200/300/400 mB 水：Am/Cm/Cf/Es 各 80%、Fm/Md/No/Lr 各 70%）；Cycle C 轻锕系 ×2（Ac 独居石镧载体 50% 概耗 75%、Pa 二氧化锰吸附+HF 淋洗 75%）；Cycle D 超锕系单原子化学 ×11（Rf/Db 氟络阴离子交换 60%；Sg 氧氯化挥发（碲代硒）+冷凝 50%；Bh 氧氯化盐酸裂解 50%；Hs 碲酸氧化挥发 55%；Cn 金箔捕集（5% 概耗）+热脱附 65%；Mt/Ds 王水分级浸出 55%；Rg 硫醚络合 60%）；试剂 10（TBP（异丁醛+独居石磷源）/CMPO/DTPA/α-HIBA/肼/亚硝酸钠/氯酸钠/碲酸/王水/金箔），PGM 残渣王水浸出返回超重蒸气 |
| 聚变三重链+轻硅岩+线圈合金 `resonantFusionChain` | 15 | 惰性硅岩液离心→粗轻硅岩粉 75%→氢还原 90%；金/银/𬬭/鎶坩埚熔融 4；聚变 3（金+𬬭→精金、银+鎶→秘银、精金+秘银→翠尼特）；He-3 急冷铸锭 3（翠尼特锭唯一来源——EBF 路线已 `skipEbfRecipes` 抑制，真空冷冻热锭死配方已 `cancelRecipes("vacuum_freezer/hot_ingot")` 取消）；精金/秘银压缩机轧板 2（MI 惯例 eu2/200t，终局电路外壳）；**翠尼特二硅岩化物混料**（翠尼特粉×1+轻硅岩×2→粉×3）——dinaquide 材料的粉唯一源，EBF 铸锭走材料自动配方（TPV 线圈 4096 门控，避免与自家线圈循环） |
| 聚酰亚胺+导电银胶 `resonantPolyimideChain` | 10 | 真实 Kapton 化学：间二甲苯+氯甲烷→杜烯→钒催化硝酸氧化（10% 概耗）90%→均苯四甲酸→脱水 PMDA；硝基苯→对硝基氯苯 80%→铜催化 Ullmann 醚化（15% 概耗）→二硝基二苯醚→加氢→ODA；PMDA+ODA→聚酰胺酸→亚胺化→PI 粉→PI 板；银粉+环氧树脂+ODA 固化剂（25% 概耗）→导电银胶 |
| 氟橡胶+谐振超导 `resonantFluoroChain` | 12 | FKM：氯甲烷→氯仿→R-22→VDF 热解、丙烯+氟→HFP、过硫酸盐（硫酸钠 10% 概耗）共聚→FKM 板；YBCO：独居石重稀土残液+离子交换树脂（50% 概耗）→氧化钇 90%+PGM 残渣 50%、铜氧化、Y2O3+2BaO+3CuO 混料、蓝宝石基板（EBF 翠尼特线圈 32768）、RF 溅射（靶材 75% 概耗+氩）→谐振超导带、8 带+谐振合金线×4+**高级超导线缆×2（叠层上代缆芯）**+**精英泵×1（低温回路）**+导电银胶 500 mB+液态玻璃 144 mB→**2^36 EU/t 谐振超导线缆** |
| resonite+压电+调律 `resonantTuningChain` | 14 | 末影之眼×4+硅岩粉×2+PI 粉→resonite 粉（EBF 翠尼特线圈自动配方 32768）；resonite 线缆（PI+FKM+银胶，自动配方已取消）；石英→压电晶圆；铅+钛+氧→钛酸铅（MI 无锆，真实 PbTiO₃）→陶瓷板；石英振荡器；SAW 谐振器；变异剂+末影之眼→谐振母液；白音符（晶体电路+高纯单晶硅岩+振荡器+母液 100 mB）；红/黄/蓝音符（振荡器+钛酸铅板+resonite 板+母液 100/200/300 mB 阶梯）；调律方块（晶体电路+单晶硅岩×2+钛酸铅板×2+resonite 板+**白/黑音符×1 各 50% 概耗**）；谐振调律机（钛酸铅板+resonite 板+谐振超导线缆×4+湿件电路+银胶） |
| 处理单元+单体+终局 `resonantCircuitChain` | 9 | 谐振 RAM（**绿×2/蓝×2 各 50% 概耗**+bio RAM）/MMU（**青×1/红×1 各 50%**）/ALU（**紫×1/黄×1 各 50%**）+钛酸铅板+resonite 板+母液 100/200/300 mB；声子激光器（SAW×2+**黑×1/白×1 各 50%**+精英泵）；共振腔（钛酸铅×6+resonite×6+**黑×2/白×2 各 50%**+超导线缆×8+FKM×2）；锁相环（振荡器×2+resonite 线×8+FKM）；**翠尼特二硅岩化物线圈**（dinaquide 板×2+硅橡胶云母片×6+湿件电路×2，材料路线见聚变链行）；谐振电路板（湿件板+PI 板×4+超导线缆×12+saser+共振腔+锁相环+resonite 板×6+钚电池×16+精英泵×2 恰 9 物品=装配机 3×3 上限+母液/He-3/变异剂，8000 s）；谐振电路（湿件×4+RAM×2+MMU+ALU+板+**精金板×8+秘银板×8** 共 7 物品，8000 s） |

### 8.2 Q₈ 调律系统（全新机制，非配方）

- **谐振调律机**（单方块机器，`blocks/resonance/`）正上方放置**调律方块**（8 态 `color` 属性，放置初始白）；
- 放入音符 n（寄存器态 b）→ 输出音符 = **b×n**（Q₈ 四元数群乘法），方块色 ← n，但**有 50% 概率谐振漂移为下一个颜色**（白→红→黄→蓝→绿→青→紫→黑→白，GUI 色带即此顺序）；白音符 = 读出（复位同样受漂移影响——要保底复位需破坏重放）；
- 颜色映射：白=1、黑=−1、红=i、青=−i、黄=j、紫=−j、蓝=k、绿=−k；互补色恰为逆元；红×黄=蓝而黄×红=绿（非交换）；
- 交互：右键打开**两槽 GUI**（`ResonanceAttunerMenu/Screen`：音符输入槽+产物槽+八色音色指示条，当前音色高亮并显示名称）或漏斗自动化（`WorldlyContainer`：上/侧面进音符、底面出产物，服务器 tick 处理输入槽——GUI 放入的音符走同一条 Q8 路径）；
- 音符经济闭环：白/红/黄/蓝可直接合成；绿/青/紫/黑**仅**调律机可产（如 蓝=红×黄、绿=黄×红、黑=红×红、青=黑×红）；下游配方一律写成**一对相反色（Q₈ 逆元对：白↔黑、红↔青、黄↔紫、蓝↔绿）各 50% 概率消耗**——机器专属色的随机产率压力减半，另一半需求由可合成的互补色承担；`NINotes.init()` 启动时对乘法表与色带循环做 Q₈ 一致性自检。

### 8.3 催化剂概率消耗与概率产出一览

催化剂（`itemIn` 第 3 参 = 每次合成独立消耗概率）：亚硝酸钠 25%、TBP 10%、肼 10%、CMPO 5%、DTPA 10%、α-HIBA 10%、氯酸钠 10%、独居石（Ac 载体）50%、金箔 5%、钒 10%、铜 15%、硫酸钠（引发剂）10%、离子交换树脂 50%、ODA（固化剂）25%、溅射靶材 75%、碲（Sg 挥发载体）25%、**调律音符（Q₈ 逆元色对，两色各 50%，见 §8.2）**。
产率（`itemOut` 第 3 参 = 产出概率，即化学产率叙事）：U 90%、Pu/Np 85%、Th 90%、Ac/Pa 75%、Bk 80%、Am/Cm/Cf/Es 80%、Fm/Md/No/Lr 70%、Rf/Db 60%、Sg/Bh 50%、Hs 55%、Mt/Ds 55%、Rg 60%、Cn 65%、粗轻硅岩粉 75%、轻硅岩 90%、均苯四甲酸 90%、对硝基氯苯 80%、氧化钇 90%、PGM 残渣 50%、氙/碲裂变副产 10%/15%（碲回供碲酸与 Sg 载体，闭环）。

### 8.4 新注册内容

材料 4：trinium（全部件，EBF+真空冷冻热锭路线取消，聚变锭唯一源）、**trinium_dinaquadide**（全部件标准集+存储块，EBF 粉→热锭 TPV 线圈 4096 门控，线圈合金本体，`setTier(3)`）、resonite（全部件+线+线缆 2^25，`setTier(4)`=翠尼特线圈 32768）、resonant_superconductor（仅线缆 2^36）；流体 32（氡/裂化液/PUREX 与 TRUEX 各相/锕系组液/超重蒸气/鎶冷凝液/𬬭配位液/碲酸/王水/6 种熔融金属/母液/聚酰胺酸/银胶/VDF/HFP/氯仿/R-22/铀镎混合料液）；物品 ~70（22 种元素粉、9 种分离试剂、轻硅岩+粗粉、精金/秘银锭、钛酸铅粉/板、压电晶圆、振荡器、SAW、PI 中间体×6+粉/板、FKM 板、氧化钇/氧化铜/靶材/蓝宝石/超导带、谐振 RAM/MMU/ALU、saser/共振腔/锁相环、金箔、PGM 残渣）；方块 2（8 态调律方块、调律机+BE+GUI/Menu 注册）+ 8 音符物品 + 精金/秘银板；EMI 专属分类「谐振调律机」（`NIEmiPlugin`，`@EmiEntrypoint`，调律机为 workstation）为每种音符渲染一页**可视化凯莱表**（`NIAttuningRecipe`：每行 = 寄存器色块 + 音符 → 箭头 → 两个新寄存器色块（音符色 50% / 漂移色 50%，悬停有说明）+ 产物音符，共 8 行），机器专属色的获取路径由此图形化。

### 8.5 战略储备（有意不消费）

除 Rg/Cn（聚变）、U/Pu（MI 生态）、Th/Te/Na 等试剂自用外，Ac/Bk/Cf/Es/Fm/Lr/Md/No/Pa 及 Rf/Db/Sg/Bh/Hs/Mt/Ds 共 16 种元素粉当前无下游——为后续电路层级（EMI/…）预留的战略储备（用户明示豁免）；**Am/Cm/Np 已进入超铀电池（§9.1）**。精金/秘银锭经压缩机轧板后进入谐振电路外壳（不再是死胡同，熔体仍是翠尼特聚变的正途消耗）。

## 9. 2026-09-01 光学程序（optical：100 宝石 + 超级组装机，340 个配方）

### 9.1 百宝石链（`opticalGemChain`，2026-09-01 二次调整：合并配方 + 石墨烯路线 + 液氙）

- **宝石孕育（藻类培养器 8 EU/200t，合并配方）**：玻璃×1 + 培养皿×1 + 稀有气体 50 mB → 该组全部宝石（各 `0.5/k` 独立概率，k=组内宝石数，**每次合成期望产出恒 0.5 颗**）。同皿同气的宝石共用一条配方（100 颗 → 33 条，最大组 12 输出 ≤ 培育器 16 输出槽）。**皿映射**：97 皿全搜，取「培养色」（成员轮色均值，单菌皿即本色）与宝石 RGB 最近邻——鲜艳宝石落单色皿，暗沉宝石（碧玉/烟晶/灰黑系）落双菌/三菌混色皿（13/22 个在用皿是混菌皿）。**气体映射**：HSV 色相加权距离（消色差宝石退回 RGB 最近邻），He 桃/Ne 橙红/Ar 薰衣草/Kr 冰蓝/Xe 青/Rn 淡紫六气全用（27/27/23/11/9/3）。**颜色表**：`respread_gem_colors.py` 重算过饱和度/明度阶梯（12 相位交错表）+ 族内色相拉伸 + 跨族冲突消解，任意两宝石 RGB 距离 ≥24（原最近对仅 2-5）。
- **压缩（压缩机 eu2/200t）**：宝石×9 → 存储块（100 个真实可放置方块，带 loot table，nichrome 块模板重着色）。
- **切割（切割机 eu2/200t + 润滑油 1 mB）**：块×1 → 板×9（质量平衡 1 宝石 = 1 板）。
- **石墨烯化工路线（2G/4000s）**：碳粉×2 + 硝酸 250 mB —化学反应器→ **氧化石墨烯×4**（Brodie 式插层氧化）；氧化石墨烯×2 + 肼×1 —化学反应器→ **石墨烯×2**（肼还原脱氧，肼复用 PUREX 试剂线）；石墨烯×4 —打包机→ **石墨烯杆×1**。
- **液态氙（真空冷冻机 4096 EU/400t）**：氙 1000 mB → 液态氙 1000 mB（氙本身是 D+He-3 聚变产物，100 mB/次——每支管 16 桶 = 1600 桶/qubit 的聚变经济闸门）。
- **辉光管（装配机 2G/4000s）**：板×1 + 超铀电池×1 + 晶体二极管×1 + 石墨烯电极×2 + **石墨烯杆×4** + 本色气体 100 mB + **液态氙 16000 mB** + 熔融 trinium 50 mB → 辉光管×1（真实霓虹管叙事：气体决定辉光色）。
- **部件**：超铀电池 = 钠夸德板×16 + **镅×24/锔×16/镎×12 粉**（三种此前无下游的重元素，Am/Cm 是真实 RTG 同位素燃料）（2G/4000s）；晶体二极管 = 硅片×2+铜板；石墨烯电极 = 碳粉×4+铜板→2。
- **终局（2026-09-01 改为 MI 机器配方）**：**超级组装机**经 `SingleBlockCraftingMachines` 注册为真 MI 机器（`modern_industrialization:super_assembler`，**100 物品入/1 出**，单电力级，lv 机壳 + 琥珀前窗 overlay，EMI/REI 分类由 MI 自动注册）。MI 机器 GUI 背景从 256×256 雪碧图切片，面板硬上限 176×260——100 槽 10×10 网格按标准 18px 槽距放不下，故**槽距压缩到 15px**（相邻槽背景 1px 重叠）。GUI 内有两件 MI 固定绘制、不可移动的“家具”需要绕行：配方锁定按钮恒画在 (152,4) 20×20、物品栏标题恒画在 (8,166)。布局为：网格贴左缘（背景 0..152 × 17..169）；**输出口 (158,26) 与能量条 (160,48) 组成右栏**，位于锁定按钮正下方、与网格隔 4px 分栏；效率条 (2,176) 塞进物品栏标题与玩家背包（y=182 起）之间的窄带；进度箭头留在标题行 (100,0) 指向右栏。单配方 = **100 支辉光管（每色各一）→ 光学量子比特组件**（2G EU/t、8000 s，总耗能 160 kt）。附带修复：培育器/离子交换/坩埚/超混机四台旧机器与超级组装机自身此前缺战利品表（挖掉不掉落），已在 MI 命名空间补齐五份。

### 9.2 新注册内容

物品 407（100 宝石 gem_*、100 存储块物品、100 板、100 辉光管、电池/二极管/电极/氧化石墨烯/石墨烯/石墨烯杆/qubit）+ 方块 100（宝石块）+ 超级组装机（经 MI 公共机器 API 注册，方块/物品归 `modern_industrialization` 命名空间，机壳贴图与 lang 由本模提供）+ 流体 4（neon/argon/krypton/liquid_xenon 四件套齐全；修复了三者未进 `NIFluids.ALL` 导致缺客户端渲染着色的隐患）。宝石表以 `NIGems.java` 为单一来源（100 条，启动断言查重），资产脚本 `gen_optical_assets.py` 直接解析该 Java 生成材质/模型/loot/lang，杜绝两处漂移。宝石贴图为原版钻石风格刻面剪影（每色独立明暗阶梯），qubit 贴图取 MI 原版 qubit 色相平移至黄。超级组装机的配方页由 MI 自带的机器配方查看器渲染（REI/EMI 分类自动注册）。

## 10. 2026-09-01 光子学等级（optical circuit：四族材料 + 元素链 + DUV 光刻机 + HNIW + 中子素，116 个新配方）

等级时长阶梯（上一级 ×4）：关键化学/装配 **320,000t（16000s）**、试剂步骤 80,000t（4000s）、**终局板/电路 640,000t（32000s）**、**巨型物质球 7,200,000t（360000s）**、EBF/物理加工照旧豁免；主力 EU **2G**（光刻升级后机器可承载）。新超导线缆 **2^39 EU/t**。

### 10.1 四族更强的材料（opticalMaterialChain）
- **FFKM 全氟橡胶**（9 步）：Al+萤石+氟→R22 裂解催化剂；**R-22 过热蒸汽裂解**（催化剂 15% 概耗）→TFE+HCl（未反应 R-22 回料 1000）；HFP+O₂→HFPO→裂解→**PMVE**；PMVE+氟→全氟固化点单体；过氧化氢异丙苯+苯乙烯→**DCP**；氯丙烯+氰胺→**TAIC**；TFE+PMVE+CSM+DCP（20% 概耗）→FFKM 生胶→+TAIC/DCP 压制→FFKM 板。下游：光刻机密封、中子素线缆、HNIW 衬里、大型精英泵。
- **PEEK**（10 步）：盐卤离心分盐→KCl→碱碳化→K₂CO₃；苯+氟→氟苯→羰基氯化→对氟苯甲酰氯→**Friedel-Crafts 酰化（Al 25% 概耗，70% 概率产出 + 氟苯回料）→DFBP**；苯酚+电子级 H₂O₂→对苯二酚；**无水 1:1 亲核缩聚**（K₂CO₃ 25% 概耗、0.5 概率回料）→PEEK 粉→压缩板→+二苯砜→绝缘片（中子素导线镜像配方用料）。
- **电子级化学品**（12 步）：**AO 法 H₂O₂ 全循环**——乙苯+m二甲苯+O₂→EAQ；EAQ+甲苯+TBP（10% 概耗，复用 PUREX 线）→工作液；**Pd 氢化（Pt 10% 概耗）→氢化工作液；空气氧化**→工作液回料 1800 + 粗 H₂O₂；离子交换树脂（25% 概耗）纯化→**电子级 H₂O₂**。尿素（NH₃+CO 折叠 Bosch-Meiser，产水）除亚硝酸→**电子级硝酸/硫酸**；甲醇+氨→**TMAH 显影液**；丙烯+甲醇+O₂→**PGMEA**。
- **DUV 光刻胶/UV 胶**（8 步）：苯+苯酚→**聚对羟基苯乙烯树脂**（KrF 主树脂）；乙烯/丙烯/甲烷→**脂环族丙烯酸酯**（ArF 主树脂）；苯+SO₂+氯→**三苯基硫鎓 PAG**；苯+甲醛→UV 光引发剂；树脂+PAG+PGMEA 各调 **KrF/ArF 光刻胶**；MI 丙烯酸胶（流体）+光引发剂+PGMEA→**UV 光学胶**。

### 10.2 新元素链（opticalElementChain）
- **Ge（5）**：闪锌矿焙烧→**锌烟道灰**+SO₂；盐酸浸出→**GeCl₄**（+氯代废液）；水解→GeO₂（HCl 回料）；H₂ 还原→锗锭；+石英粉（25% 概耗）装配→**锗晶片×8**。
- **Nb（5）**：锡石离心（产 MI 锡粉）→**钽铌精矿**；HF+H₂SO₄ 消解→氟铌酸；丙酮+H₂（Ni 10% 概耗）→**MIBK**；溶剂萃取→**Nb₂O₅**；铝热→**铌锭**（SNSPD 用）。
- **Li（1）**：盐卤+纯碱沉锂→Li₂CO₃（铌酸锂用）。
- **Er/Eu/Ce（3）**：镧系重残液+**α-HIBA 淋洗剂（25% 概耗）**→Er₂O₃（掺铒光纤）/CeO₂（抛光/基板）；**Eu 电解还原**（真实 Eu³⁺→Eu²⁺）→铕粉（合金×31 主料）。
- **Ru（2）**：铂族残渣+王水+氯酸钠（15% 概耗，RuO₄ 蒸馏氧化）→RuO₂→H₂ 还原→钌粉（合金×1）。
- **Be（2）**：祖母绿（绿柱石）碱熔→MI 铍粉；铍粉×4 压制→**铍反射层**。
- **GST（1）**：Ge+Sb+碲→GST 靶（溅射）。
- **石英玻璃/光纤（6）**：EBF（32768 档）电熔石英粉→石英玻璃锭→切割板；石英粉+碳+氯→SiCl₄；**OVD 气相沉积**（SiCl₄+GeCl₄+Er₂O₃+O₂）→预制棒→拉丝（板 25% 概耗）→**掺铒光纤×32**。

### 10.3 光子学部件 + DUV 光刻机（opticalPhotonicChain，27 步）
激光二极管（Ge 晶片+晶体二极管+红宝石板）→固体激光器（红宝石/变石板，**光掩模写入器光源**）；**ArF 准分子激光器**（Ar+F₂+CaF₂ 透镜组+光学平台+二极管）；萤石+CeO₂→CaF₂ 透镜组；石英玻璃板+resonite 板+PEEK 绝缘片→光学平台；Li₂CO₃+Nb₂O₅→**铌酸锂晶片**→+波导+金粉+谐振电路→**电光调制器**；铌锭+蓝宝石基板+N₂→**SNSPD 单光子探测器**；硅片+石英玻璃板→**光波导**；二极管+调制器+SNSPD+光纤+谐振超导电缆→**光收发模块**；石英玻璃板+铬铁矿→掩模 blank→**固体激光器写入（10% 概耗）+KrF 胶→光掩模**；GST 靶（0.75 概耗）+光刻基板+氩溅射→**GST 相变存储单元**。
**DUV 光刻机**（新机器 `modern_industrialization:duv_stepper`，经 `SingleBlockCraftingMachines` 注册，3 物品入/1 出/2 流体入/1 流体出，superconductor 机壳）：准分子激光器+CaF₂ 透镜组×2+光学平台+FFKM 板×4+PEEK 板×2+**大型精英泵×2+大型精英电机×2**+谐振超导电缆×4+钛机壳×2。
**机内工艺（2G/16000s）**：CMP（CeO₂ 25% 概耗）→光刻基板；ArF 胶涂胶→**光掩模曝光（5% 概耗）**→TMAH 显影（产苯酚焦油）→**Piranha 刻蚀（电子级硫酸+电子级 H₂O₂）**→铜金属化→切割（快速）→**光学芯片×8**。三件套：GST 单元+谐振 RAM+波导+PEEK 绝缘片+电子级 H₂O₂→光学 RAM；光收发+谐振 MMU+电子级硝酸→光学 MMU；调制器+**光学芯片×2**+谐振 ALU+TMAH→光学 ALU。

### 10.4 HNIW 复杂链（opticalHniwChain，16 步）
甲苯+氯（**激光二极管作 UV 灯 5% 概耗**）→氯苄→氨解→苄胺（+盐）；乙醛醇+O₂（银网催化剂 10% 概耗）→乙二醛；+苄胺+对甲苯磺酸（20% 概耗）→HBIW 粗品→PGMEA 重结晶→HBIW 晶体；**一段氢解**（H₂+乙酸酐+Pt 10% 概耗）→TADBIW；**二段氢解**（H₂+Pt 10% 概耗）→TAIW；电子级硝酸+O₂→N₂O₅；**硝化（90% 概率产出）→HNIW 粗晶→ε 晶型转晶→ε-HNIW 粉末**。副链：乙酸→**乙烯酮**→+乙酸→乙酸酐（真实酮法）；钠+氨+氮→叠氮化钠；铅粉+叠氮化钠+铜板→**叠氮化铅雷管**；PBX：HNIW×6+氰基丙烯酸酯胶+FFKM 衬里+雷管→**HNIW 聚爆透镜**。

### 10.5 中子素与聚爆（opticalNeutroniumChain，14 步）
**Cf-252 自发裂变**：锎粉+石墨慢化（25% 概耗）+氦冷却→**中子流体**+裂变碎片（50% 概率）；**锎引发器**=锎×4+铍×2+不锈钢板×2+中子流体 4000 mB。**巨型物质球**：超级组装机 **100×64=6400 个 MI 超高密度金属球**，2G/**360000s**→1（自定义 2.4× GUI 放大模型）。**聚爆走 MI 原版爆破压缩机**：巨型物质球+聚爆透镜×6+引发器×4+铍反射层×2→**中子素粉×4（95% 概率）**。熔融（巨型坩埚 2G/16000s）→**真空冷冻浇铸（大型精英泵作低温回路，5% 概耗）→中子素锭**；压缩机 1 锭→1 板。**中子素导线镜像上代线圈配方**：板×2+PEEK 绝缘片×6+谐振电路×2→导线；**线圈=导线×32**。**光学超导合金 2^39**：Ru×1+Am×2+中子素粉×3+超能钠夸德×6+**Eu×31**+石墨烯×12→混合料×3→**EBF（262144=中子素线圈档闸门）**→锭→拉丝×4→导线×2+**谐振超导线缆×2（叠层上代缆芯）+大型精英泵×1（低温回路）**+FFKM×2+PEEK×2+液态玻璃 144 mB→**光学超导线缆**。

### 10.6 大型精英泵/电机与终局（opticalFinaleChain）
时间×4 耗材×2 对齐 elite 系（前级泵/电机消耗再翻倍）：**大型精英电机**=elite_motor×18+钠夸德杆×8+量子电路×4+谐振电路×2+润滑油 2000（2G/16000s）；**大型精英泵**=elite_pump×6+大型精英电机×2+钠夸德转子×12+量子电路×8+FFKM 板×4。
**光学电路板**（装配机 2G/32000s，9 物品+UV 胶 500）：谐振板×1+刻蚀基板×2+石英玻璃板×4+掺铒光纤×8+电光调制器×2+光收发×4+SNSPD×2+PEEK 板×4+FFKM 板×4。**光学电路**（超混机 2G/32000s）：**谐振电路×8+光学 qubit×16**+光学 RAM/MMU/ALU 各×2+光学板×1+光学超导线缆×4。

### 10.7 机器槽位核验与存量修复（MI 2.5.6 字节码实测）
经 javap 反编译 `SingleBlockCraftingMachines` 取得真实槽位表：**装配机 9 物品入+2 流体入**、化学反应器 3+3、蒸馏器 **0 物品口+1 流体入/出**、混合机 4+2、离心 1 入 4 出、电解 1+1、线材轧机 1、压缩机 1、切割 1+1、爆破压缩机 4 物品入（nether_star 先例）。据此修复：
- **存量 bug**：谐振电路板（9 物品+3 流体→去掉 mutagen）、湿件电路板（8+3→去掉氦-3）、100 支辉光管（5+3 流体→熔融 trinium 改为 trinium 粉物品投入）、环氧两条蒸馏配方（2 流体出超蒸馏器 1 出口→改化学反应器）此前均**物理上无法合成**。
- 本级配方全部按表重排：电子级酸走反应器（蒸馏器无物品口）、KCl 改离心（分盐结晶）、钾碱/铌萃取/镧系淋洗改反应器（离子交换机无固体出口）、PEEK 缩聚收到 3 物品入、聚爆 4 入恰好对齐、中子素低温泵从坩埚（1 物品口）挪到真空冷冻浇铸。
- 中子素材料部件：StandardRecipes 全套自动生成于 MI 命名空间（`materials/neutronium/`），与 trinium 同构；`heat_exchanger/hot_ingot` 与 `wiremill/wire` 取消（无热锭来源、导线有手写镜像配方），`skipEbfRecipes` 生效。

### 10.8 新注册内容
物品 81（NIOpticalItems；trioctyl_phosphate 在核对后移除——AO 萃取相复用 PUREX 的磷酸三丁酯更真实）+ 流体 26（TFE/HFPO/PMVE/蒽醌工作液×2/粗+电子级 H₂O₂/电子级硝酸+硫酸/TMAH/PGMEA/KrF+ArF 胶/UV 胶/氯苄/氟苯/苄胺/乙二醛/乙烯酮/乙酸酐/MIBK/SiCl₄/GeCl₄/氟铌酸/中子流体/熔融中子素）+ 材料 2（neutronium 全件含 wire、optical_superconductor cableOnly 2^39）+ 机器 1（duv_stepper）+ 配方 116（`tools/audit_optical.py` 静态审计：生产覆盖/下游/槽位/时长四项全绿；phantom 部件 gear/hot_ingot 与 trinium 先例一致）。巨型物质球自定义大模型（GUI 2.4×/地面 1.6×）+ DUV 机身 overlay（透镜柱+晶圆台+193nm 紫光束 active 变体）由 `gen_photonic_assets.py` 生成。大型精英电机/泵贴图取 MI 大型高级原图做蓝→绿通道交换与橙色 +120° 色相旋转（灰白机身逐像素保留，`gen_algae_assets.blue_and_orange_to_green`）。

## 11. 2026-09-02 微缩宇宙投影仪（feat/microverse，23 个新配方 + 多方块机器）

### 11.1 新注册内容（com.nestedinfinity.mod.microverse，13 个 Java 文件）
- **方块 23**：中子素机器外壳×1、核心火种×12（`coreflame_<suffix>`，带 1 槽 BE 的 BaseEntityBlock，只接受配对奇点物品）、时间膨胀装置×9（`time_dilation_unit_t1..t9`，TIERS 自注册）、投影仪控制器×1（RUNNING 方块属性 + 服务端 ticker）。
- **BE 2 + 菜单 2**：coreflame（12 方块共用）、microverse_projector；GUI 走 `IMenuTypeExtension` + RegistryFriendlyByteBuf 传 BlockPos（ResonanceAttuner 先例）。
- **物品 22**：无心世界之心×1 + 奇点×12（对应 12 火种）+ 宇宙物质×9（夸克胶子等离子体→千新星抛射物，按宇宙学纪元排序）。**全部有意无配方**——是下一阶段（层级 2+ 投影产出消费）的挂点。
- 能量走 `EnergyApi.SIDED`（grandpower `MIEnergyStorage` 匿名实现，只进不出，容量 120G=2G EU/t×60t）。GrandPower 未发布到 Modrinth/Central 且 jarjar 嵌于 MI jar（javac 不可见），故抽取内嵌副本至 `libs/grandpower-3.0.0.jar` 仅供编译（`compileOnly files(...)`，与运行时版本严格一致）。

### 11.2 结构与状态机（MicroverseStructure + MicroverseProjectorBlockEntity）
- 7×3×7 共 99 格：L1 六角 37 外壳；L2 中心 3×3 外壳 + 4 同档 TDU + 12 火种（固定坐标）；L3 六角 37（中心=控制器，TDU 正上方 4 格强制外壳）。外壳可被任意 MI 舱室替换（namespace `modern_industrialization` + path 以 `_hatch` 结尾）。
- 校验顺序 L3→L2→L1，问题码：layer1/layer2_center/layer3/tdu_missing/tdu_mixed/coreflame_missing/coreflame_duplicate（GUI 状态栏显示 `problem_<key>` 本地化键）。
- 运行：放入心脏自动开始（消耗心脏+12 奇点，逐火种记录 pending）；2G EU/t，`baseTicks(n)=round(20×2.1^(n-1))`（T1=20t … T9=7565t），`microPerTick(n)=round(2.1^(n-1)×50000)`；结束时产出 `floor(累计micro/1e6)` 物质（输出槽上限 64，超出落地），每奇点按 `max(0, 95−5×延长次数)`% 概率返还回火种 BE。
- 延长：巨型物质球消耗 1→2→4→8（每次翻倍，上限 2^20），每延长一次剩余时长 +baseTicks/2；结构破坏=宇宙坍缩，心脏/奇点/球全部损失（POOF 粒子+爆炸音）。
- 客户端：12 格奇点指示灯条（HSB 循环色相）、倒计时/累计/返还率读数、延长按钮（`handleInventoryButtonClick`）；BER 以 `RenderType.endPortal()` 绘制半径 2.0 二十面体细分球（中心 (0.5, 3.5, 0.5)，前后 40t 缩放动画，双面绕序防剔除）。

### 11.3 配方（microverseChain，全部装配机 2G/320000t）
外壳=中子素板×4+中子素杆×2→2；火种=光学电路板×1+FFKM 板×2+石英玻璃板×2+对应宝石×2+液氙 1000（12 宝石一对一：heliodor/tanzanite/azurite/garnet/opal/emerald/amethyst/sapphire/ruby/jade/sodalite/alexandrite）；TDU1=光学电路×2+中子素板×4+光学超导线缆×2；TDU n=2..9 为 2×前级+4×matter(n−1)（火种→TDU 阶梯与宇宙物质纪元一一对应，T9=2×t8+4×超新星重元素）；投影仪=外壳×4+光学电路×4+大型精英电机+大型精英泵。
资产（`tools/gen_microverse_assets.py`，167 文件）：23 blockstates + 24 方块模型（控制器 front/front_on 变体）+ 45 物品模型 + 27 方块贴图 + 22 物品贴图 + 2 GUI（176×166/176×184）+ 24 战利品表 + 双语 lang（含 problem_* 状态键与 GUI 文案）。
