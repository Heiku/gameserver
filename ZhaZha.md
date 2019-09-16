## ZhaZha 

ZhaZha 是一款多人在线角色扮演游戏（MMORPG）,在游戏中你将作为 ZhaZha大陆中的一名冒险家，探索ZhaZha大陆中的奇特场景，在大陆中结识其他玩家，一起征服ZhaZha大陆。  

***

游戏项目结构：

* ClientDemo：游戏客户端，用于展示数据信息

  启动路径：com.ljh.clientdemo.ClientdemoApplication

* GameDemo：游戏客户端，用户处理玩家的操作数据  

   启动路径：com.ljh.gamedemo.GamedemoApplication

***

游戏玩法：

* 用户：

  login：登录 （登录用户账号）

  register： 注册（注册用户账号）

  state：角色绑定 (绑定用户之前的角色信息)

* 角色：

  role：查看当前的角色状态信息

  rt：展示所有的角色类型信息

  create：创建对应的玩家角色信息

* 场景：

  site：查看当前玩家的位置场景信息

  move：创建对应的玩家角色信息

* 公共：

  aoi：获取当前场景下的所有实体信息

  date：获取当前的游戏时间

  talk：与实体 npc 进行对话

* 技能：

  spell：查看当前技能和所有可学习的技能

  learn：学习新技能

* 野怪：

  attackCreep：攻击野怪 （自动使用普通攻击技能）

  spCreep：使用技能攻击野怪

  stopCreep：离开野怪的攻击范围

* 道具：

  bag：查看当前背包中的所有道具

  use：使用背包中的道具

* 装备：

  equip：查看当前玩家所拥有的装备信息

  put：穿戴装备

  takeOff：卸下装备

  fix：修理装备

* 副本：

  duplicate：查看当前的所有副本信息

  enter：挑战副本

  spBoss：对Boss施放技能

  stopBoss：离开Boss的攻击范围

  leaveBoss：退出副本挑战

* 商城：

  mall：查看当前的商城信息

  buy：购买商品

* 聊天：

  chat：与玩家进行聊天

  chatGroup：与全服的玩家进行聊天

* 邮件：

  email：获取邮件列表

  receive：接收邮件物品信息

* PK：

  pk：邀请玩家进行 PK

  acceptPK：接受 PK 挑战

  spPK：使用技能攻击玩家

  escape：离开当前的 PK 挑战，本场将被判定为战败

* 组队：

  groupState：查看当前的组队状态

  group：发起组队邀请

  join：接受组队的邀请，加入队伍

  exit：退出队伍

* 面对面交易：

  initiate：发起面对面交易

  yes：接受交易申请

  no：拒绝交易申请

  ask：发送交易内容

  accept：确认交易信息

  refuse：拒绝交易

  leaveTrade：离开交易

* 公会：

  guild：查看当前所在的公会信息

  guildAll：查看当前世界的所有公会信息

  establish：建立公会

  applyGuild：申请加入公会

  applyAll：查看公会的申请列表

  approvalYes：同意审批公会申请

  approvalNo：拒绝审批公会申请

  modifyAnn：修改公会公告内容

  give：赋予职位

  donate：捐献公会物品

  takeOut：取出公会物品

  kickOut：踢出公会

  exitGuild：退出公会

* 交易行：

  tradeState：查看当前的交易状态
  fixedPriceAll：查看所有一口价的拍卖物品

  auctionAll：查看所有正在拍卖的物品

  putGoods：上架商品

  buyFixedPrice：购买一口价的物品

  outOf：将物品下架处理

* 任务：

  taskAll：查看所有的任务列表

  taskState：查看当前任务的状态信息

  taskReceive：接受任务

  taskSubmit：提交任务

  taskGiveUp：放弃任务