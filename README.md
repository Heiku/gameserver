# gameserver

7.8 完成更改客户端的控制台接口，新增channel 映射
 
7.9 修改client和server的断线重连机制，新增map<userId, channel>，完成服务端npc对话的协议定制和代码

7.10 完成npc对话，新增野怪协议，完成野怪信息的编写，重构之前的 csv 改成 excel

7.11 新增技能协议，完成技能地展示，技能地学习

7.12 新增攻击野怪协议，完成攻击普通野怪的技能代码

7.13 补充攻击也怪技能协议，完成野怪的攻击

7.14 新增背包，物品协议，完成服务端背包物品的获取和使用功能

7.15 修改背包协议，更改背包功能部分代码

7.16 完成背包功能，及药水物品的使用功能

7.17 修改攻击野怪的线程模型，新增装备协议

7.19 又又又更改线程模型，完成玩家的扣血和野怪扣血，等待更改用户的技能施放

7.22 完成线程模型的更改，修改用户自动恢复的机制，完成用户物品使用恢复机制，新增野怪持续掉血的任务

7.23 测试完成野怪的中毒效果，新增玩家的护盾机制，开始编装备任务

7.24 编写装备任务，进度50%

7.25 完成服务端装备任务的编写，测试通过，完成装备整体任务，完成项目阶段二

7.29 修改服务端的背包装备部分，新增副本协议，补充副本mock数据，开始编写副本boss

7.30 继续编写副本代码，进度30%，修改野怪攻击的攻击机制

7.31 新增多玩家攻击野怪时，攻击目标转移的问题，完成副本中的普攻攻击Boss的功能，修改之前的攻击取消的问题

8.1 修改刷野怪扣蓝机制，完成副本的单人挑战

8.2 修改玩家的回血机制，完成单人的副本测试，完成单人通关多Boss的副本。新增商店协议，开始编写商店代码

8.5 修改玩家的背包代码，完成商店的购买功能，商店模块完成

8.6 完成玩家与玩家，玩家与世界的聊天功能，开始准备增加玩家的离线消息发送

8.7 新增玩家的上下线记录，完成玩家离线消息发送接收的功能，编写邮件功能协议，开始编写邮件功能

8.8 更新商城的购买机制，修改邮件协议，补充邮件的内容，增加邮件的记录，进度60%

8.9 继续补充邮件功能，修改之前的玩家的奖励发放部分，修改物品的使用状态更新，完成全部邮件功能，测试通过

8.12 修改补充玩家的扣血机制，新增pk协议，开始编写pk相关的代码，完成pk发起，接受等功能，等待测试

8.13 新增玩家的中毒效果，修改玩家属性的更新机制，新增玩家的pk打斗，新增玩家pk战斗的荣誉值奖励，测试并完成全部PK功能

8.14 修改Boss的扣血任务机制，新增玩家的组队协议，完成组队功能的编写，测试通过

8.15 修改组队的离线机制，重新编写副本Boss相关功能

8.16 修改副本挑战的奖励发放机制，修改组队挑战副本的攻击目标，完成组队挑战副本的编写，等待测试