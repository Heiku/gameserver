package com.ljh.gamedemo.common;


/**
 * 协议文本内容
 */
public class ContentType {

    // Common
    public static final String FIND_SUCCESS = "查找成功！\n";

    // User Request
    public static final String USER_EMPTY_REGISTER_PARAM = "用户注册的数据为空，请输入指令\n";

    public static final String USER_EMPTY_LOGIN_PARAM = "用户登录的数据为空，请输入指令\n";

    public static final String USER_EMPTY_DATA = "数据库中暂无用户记录，请重新注册\n";

    public static final String LOGIN_SUCCESS = "登录成功！\n";

    public static final String REGISTER_SUCCESS = "注册成功！\n";

    public static final String REGISTER_FAILED = "注册失败！\n";

    public static final String BAD_PASSWORD = "密码错误，请重新登录！\n";

    public static final String USER_OUT_OF = "账号被强制下线，原因：账号异地登录！\n";

    // public static final String USER_EMPTY_TOKEN = "登录的请求token信息为空！\n";

    public static final String USER_TOKEN_DATA_EMPTY = "数据库中暂无当前token记录，请重新登录\n";

    public static final String USER_RELIVE_SUCCESS = "复活成功！\n";



    // Role
    public static final String ROLE_EMPTY_PARAM = "输入的玩家 id 为空，请重新输入！\n";

    public static final String ROLE_EMPTY = "当前玩家暂无角色！\n";

    public static final String ROLE_CHOOSE = "角色选择成功！\n";

    public static final String ROLE_TYPE = "当前职业种类如下；\n";

    public static final String ROLE_HAS = "已经存在对应的职业信息，无法重复创建角色!\n";

    public static final String ROLE_CREATE_SUCCESS = "角色创建成功！\n";

    public static final String ROLE_GET_HEAL = "获得治疗量：%s";

    public static final String ROLE_lIST = "当前玩家的角色列表如下：\n";

    public static final String NOT_FOUND_ROLE = "找不到对应的玩家信息！\n";

    public static final String WRONG_ROLE_TYPE = "请输入正确的玩家角色类型，请入【roleType】可查看所有的角色类型\n";


    // Entity
    public static final String ENTITY_FIND_ALL = "获取所有实体信息成功！\n";


    // Creep
    public static final String CREEP_PARAM_EMPTY = "请输入正确的野怪id！\n";

    public static final String CREEP_EMPTY = "找不到id对应的野怪信息，请重新输入指令！\n";

    // Site
    public static final String SITE_NOW = "你当前的位置是：";

    public static final String MOVE_EMPTY = "目的地为空，请输入正确的目的地\n";

    // DB
    public static final String UPDATE_ROLE_SITE = "更新角色位置信息失败！\n";


    // TALK
    public static final String TALK_EMPTY = "该npc对你无话可说！\n";


    // Spell
    public static final String SPELL_ALL = "查找成功！\n";

    public static final String SPELL_EMPTY = "暂无该技能，请重新输入 learn 指令！\n";

    public static final String SPELL_LEARN_SUCCESS = "技能学习成功！\n";

    public static final String SPELL_PARTER_NAME = "%s的小伙伴";

    // Exit
    public static final String EXIT_SUCCESS = "退出成功！\n";


    // Attack
    public static final String ATTACK_DEATH_CREEP = "你攻击的野怪已经死亡！\n";

    public static final String ATTACK_CURRENT = "受到直接伤害！\n";

    public static final String ATTACK_DURATION = "受到中毒伤害！\n";

    public static final String ATTACK_SPELL_EMPTY = "请输入正确的技能id！\n";

    public static final String ATTACK_SPELL_NOT_FOUND = "找不到该技能，或许你该学一学！\n";

    public static final String ATTACK_SPELL_SUCCESS = "成功施放技能成功！\n";

    public static final String ATTACK_SPELL_CD = "技能还在cd哦，请等待";

    public static final String ATTACK_SPELL_MP_NO_ENOUGH = "当前mp值不足以施放技能，请等待或使用蓝药！\n";

    public static final String ATTACK_STOP = "已经离开野怪的攻击范围，不再受到伤害！\n";


    // Item
    public static final String ITEM_PARAM_EMPTY = "输入的物品参数有误，请重新输入！\n";

    public static final String ITEM_EMPTY = "当前角色暂无物品信息！\n";

    public static final String ITEM_NOT_CONTAIN = "当前角色还没有该物品！\n";

    public static final String ITEM_USE_FAILED_FULL_BLOOD = "使用物品失败，因为当前角色的血量为满状态！\n";

    public static final String ITEM_USE_FAILED_FULL_BLUE = "使用物品失败，因为当前角色的蓝量为满状态！\n";

    public static final String ITEM_USE_SUCCESS = "物品使用成功！\n";


    // Equip
    public static final String EQUIP_SAME = "穿戴失败！该装备已经在身上了！\n";

    public static final String EQUIP_NOT_FOUND = "未找到当前装备信息，请检查 equipId 是否有误！\n";

    public static final String EQUIP_OWN_EMPTY = "当前玩家的装备栏为空，无法卸下任何道具！\n";

    public static final String EQUIP_NOT_BELONG = "当前玩家为拥有该装备，无法卸下装别！\n";

    public static final String EQUIP_WRONG_LEVEL = "当前玩家角色等级无法达到该装备的等级要求!\n";

    public static final String EQUIP_WRONG_TPYE = "当前玩家角色不能佩戴其他职业的物品！\n";

    public static final String EQUIP_PUT_SUCCESS = "装备穿戴成功！\n";

    public static final String EQUIP_TAKEOFF_SUCCESS = "成功卸下装备！\n";

    public static final String EQUIP_FIX_FAILED = "装备当前为可用状态，修理失败！\n";

    public static final String EQUIP_FIX_SUCCESS = "装备修理成功，装备可正常使用！\n";



    // Duplicate
    public static final String DUPLICATE_ALL = "查找成功！\n";

    public static final String DUPLICATE_EMPTY = "找不到对应的副本，请重新输入正确的副本id！\n";

    public static final String DUPLICATE_ENTER_SUCCESS = "已经进入副本，boss正向你走来，请继续攻击！\n";

    public static final String DUPLICATE_ENTER_NOT_FOUND = "尚未进入副本，挑战 Boss 失败！\n";

    public static final String DUPLICATE_BOSS_NOT_FOUND = "找不到对应的 Boss 信息！\n";

    public static final String DUPLICATE_BOSS_HAD_DEATH = "挑战的 Boss 已经死亡，请重新选择! \n";

    public static final String DUPLICATE_BOSS_NOW_DEATH = "当前副本中的 Boss 已经全部死亡！\n";

    public static final String DUPLICATE_BOSS_NEXT = "Boss 已经死亡，请继续攻击副本中的下一个 Boss！\n";

    public static final String DUPLICATE_CHALLENGE_SUCCESS = "副本挑战成功，奖励如下：\n";

    public static final String DUPLICATE_ATTACKED_SUCCESS = "攻击 Boss 成功！\n";

    public static final String DUPLICATE_TIME_OUT = "挑战失败，已经超出副本的挑战时间！\n";

    public static final String DUPLICATE_LEAVE_SUCCESS = "成功离开副本！\n";

    public static final String DUPLICATE_SPELL_FAILED = "施放技能失败！\n";

    public static final String DUPLICATE_DEBUFF_FAILED = "施放技能失败，当前在眩晕状态下，还需 %d 秒才可以继续行动！\n";

    public static final String DUPLICATE_DIZINESS_WAIT_SEC = "眩晕中，期间无法施放任何技能，需等待 %d 秒！\n";

    public static final String DUPLICATE_SPELL_DURATION = "Boss 施放中毒效果，开始受到中毒伤害！\n";

    // Mall
    public static final String MALL_FIND_SUCCESS = "查找成功！\n";

    public static final String MALL_FIND_FAILED = "查找成功！\n";

    public static final String MALL_MONEY_NOT_ENOUGH = "当前金币不足以购买该物品，请尝试刷副本获得更多金币！\n";

    public static final String MALL_BUY_SUCCESS = "购买成功，物品已经发送到你的背包中！\n";

    public static final String MALL_BUY_NUM_GT_LIMIT = "超过最大购买额度，请重新选择购买数量！";

    public static final String MALL_BUY_OUT_OF_LIMIT = "超过今日购买数量限制，请明天再来!\n";


    // Chat
    public static final String CHAT_NOT_CONTENT = "未输入聊天内容，请重新输入！\n";

    public static final String CHAT_NEW_MSG_FROM_ROLE = "来自玩家私聊的新消息！\n";

    public static final String CHAT_NEW_MSG_FROM_WORLD = "来自全服的消息！\n";

    public static final String CHAT_SEND_SUCCESS = "发送成功！\n";

    public static final String CHAT_SEND_UN_LINE_SUCCESS = "发送离线消息成功！\n";

    public static final String CHAT_ROLE_NOT_LINE = "玩家不在线，暂时无法发送消息! \n";


    // Email
    public static final String EMAIL_NEW_MSG = "你有一条新邮件信息！\n";

    public static final String EMAIL_EMPTY_GOODS = "该邮件内没有物品可以领取！\n";

    public static final String EMAIL_RECEIVE_SUCCESS = "领取成功，物品已经发放到背包中！\n";

    public static final String EMAIL_RECEIVE_FAILED = "领取失败，邮件物品已经被领取过了！\n";


    // pk
    public static final String PK_SITE_NOT_IN_ARENA = "发起挑战失败，双方为同时在竞技场上！\n";

    public static final String PK_INVITE_SEND_SUCCESS =  "发起挑战成功，请等待对方接受挑战！\n";

    public static final String PK_SEND_MSG = "玩家：ID: 【%d】 名称：【%s】 向你发送挑战邀请，输入指令【acceptPK】可接受挑战！\n";

    public static final String PK_INVITE_ACCEPT_SUCCESS = "成功接受 pk 邀请，正在进入 pk 竞技场！\n";

    public static final String PK_ENTER_PK_STATE_SUCCESS = "当前双方玩家已经在擂台上，可以开始进行 PK 挑战！\n";

    public static final String PK_STATE_WRONG = "PK 状态错误！\n";

    public static final String PK_END = "PK 战斗结束!\n";

    public static final String PK_ESCAPE_SUCCESS = "成功离开 PK 战斗！\n";


    // group
    public static final String GROUP_INVITE_MSG = "【组队请求】\n玩家：【%s】向你发送组队邀请，队伍 ID：【%d】 \n输入指令【join】 + groupId 可加入队伍！\n";

    public static final String GROUP_INVITE_SEND_SUCCESS = "组队消息发送成功！\n";

    public static final String GROUP_ROLE_HAS_IN_OTHER_GROUP = "当前玩家已在队伍中，想加入其它队伍先进行退队操作\n";

    public static final String GROUP_ROLE_HAS_IN_GROUP = "当前玩家已在申请的队伍中\n";

    public static final String GROUP_NOT_IN_SAME_SITE = "组队双方不在同一地点上，请移动到相同地点进行组队邀请！\n";

    public static final String GROUP_JOIN_SUCCESS = "加入队伍成功！\n";

    public static final String GROUP_WRONG_GROUP_ID = "找不到队伍信息，请输入正确的 groupId !\n";

    public static final String GROUP_NOT_IN_ANY_GROUP = "当前不在任何队伍中，无法退出队伍！\n";

    public static final String GROUP_EXIT_SUCCESS = "退出队伍成功！\n";

    public static final String GROUP_NOT_IN_GROUP = "当前玩家没有加入任何队伍！\n";

    public static final String GROUP_STATE_SUCCESS = "查找队伍信息成功！\n";

    public static final String GROUP_SELF_FAILED = "无法邀请自己组队！\n";


    // face to face Transaction
    public static final String FACE_TRANS_APPLY_NOT_INT_SAME_SITE = "交易申请失败，交易双方应在统一场景中！\n";

    public static final String FACE_TRANS_APPLY_FAILED_LEVEL = "交易申请失败，为保证游戏平衡，交易双方等级应在最低等级限制 【%d】 以上！\n";

    public static final String FACE_TRANS_APPLY_FAILED_HONOR = "交易申请失败，为保证游戏平衡，交易双方等级应在最低荣誉值限制 【%d】 以上！\n";

    public static final String FACE_TRANS_APPLY_NOT_FOUND = "找不到对应的交易申请信息！\n";

    public static final String FACE_TRANS_APPLY_SEND_SUCCESS = "面对面交易申请发送成功！（申请超过 5 min自动失效）\n";

    public static final String FACE_TRANS_APPLY_RECEIVE = "【面对面交易申请】\n玩家：【%s】向你发送面对面交易申请，申请 ID：【%d】\n输入指令 yes + id 可进入面对面交易状态！\n";

    public static final String FACE_TRANS_APPLY_HAS_IN = "当前已经在其他交易当中，无法接收申请！\n";

    public static final String FACE_TRANS_APPLY_NO_IN = "已经断开交易连接，无法进行交易！\n";

    public static final String FACE_TRANS_APPLY_JOIN_FAILED = "加入交易失败，交易信息不存在!\n";

    public static final String FACE_TRANS_APPLY_AGREE   = "与玩家：【%s】的面对面交易申请已经被同意，开始你们的交易吧！\n";

    public static final String FACE_TRANS_APPLY_REFUSE = "与玩家：【%s】的面对面交易申请已被拒绝！\n";

    public static final String FACE_TRANS_APPLY_AGREE_SUCCESS = "完成交易申请通过，开始交易吧！\n";

    public static final String FACE_TRANS_APPLY_REFUSE_SUCCESS = "完成交易申请拒绝！\n";

    public static final String FACE_TRANS_SUBMIT_FAILED_WITHOUT_GOODS = "提交交易失败，背包中不存在交易的物品！（请核对交易的物品，数量）\n";

    public static final String FACE_TRANS_SUMMIT_FAILED_NO_ENOUGH_GOLD = "交易金额小于最小的物品最低交易总额，请重新输入交易金额！\n";

    public static final String FACE_TRANS_SUBMIT_RECORD_SEND_SUCCESS = "交易记录发送成功，请等待对方确认！\n";

    public static final String FACE_TRANS_SUBMIT_RECORD_RECEIVE = "【交易单记录】\n请确认是否进行本次交易！\n";

    public static final String FACE_TRANS_NO_ENOUGH_GOLD_PAY = "没有足够得金额支付本次交易！\n";

    public static final String FACE_TRANS_SUCCESS = "交易成功，交易物品随后将通过邮件发送，请注意查收！\n";

    public static final String FACE_TRANS_SUCCESS_GAIN_GOLD = "交易成功，获得金币：%d\n";

    public static final String FACE_TRANS_RECEIVER_REFUSE_SUCCESS = "成功拒绝交易订单，订单失效！\n";

    public static final String FACE_TRANS_PROMOTER_REFUSE_SUCCESS = "交易订单已经被取消，订单失效！\n";

    public static final String FACE_TRANS_OTHERS_LEAVE_STATE = "对方离开交易状态，交易取消！\n";

    public static final String FACE_TRANS_LEAVE_STATE_SUCCESS = "成功离开交易状态，交易取消！\n";


    // Guild
    public static final String GUILD_NOT_IN = "当前尚未加入任何公会！\n";

    public static final String GUILD_ESTABLISH_HAS_IN = "建立公会失败,玩家已经在公会中，需要先退出公会！\n";

    public static final String GUILD_ESTABLISH_HONOR_NOT_ENOUGH = "建立公会失败,玩家的荣誉值低于 %d ！\n";

    public static final String GUILD_ESTABLISH_SUCCESS = "建立公会成功！\n";

    public static final String GUILD_APPLY_HAS_IN = "申请加入公会失败，当前玩家已经在公会中，需要先退出公会！\n";

    public static final String GUILD_APPLY_NOT_FOUND = "申请加入的公会不存在！\n";

    public static final String GUILD_APPLY_HAS_APPLY = "已经申请加入该公会了，请不要重复申请！\n";

    public static final String GUILD_APPLY_SUCCESS = "申请进入成功，等待公会审批！\n";

    public static final String GUILD_NOT_PERMISSION = "当前用户没有该权限！\n";

    public static final String GUILD_OUT_OF_MAX_NUM = "超过公会的最大容纳量！\n";

    public static final String GUILD_APPLY_AGREE = "加入公会【 %s 】 申请已经被同意！\n";

    public static final String GUILD_APPLY_REFUSE = "加入公会【 %s 】的申请被拒绝！\n";

    public static final String GUILD_APPROVAL_SUCCESS = "审批成功！\n";

    public static final String GUILD_CHANGE_INFO_SUCCESS = "修改公会信息成功！\n";

    public static final String GUILD_GIVE_FAILED_WRONG_INFO = "更改权限失败，该玩家非公会成员！\n";

    public static final String GUILD_GIVE_FAILED_WRONG_PERMISSION = "更改权限失败，权限不足！\n";

    public static final String GUILD_GIVE_SUCCESS = "修改权限成功！\n";

    public static final String GUILD_HAS_KICK_OUT = "已经被踢出公会！\n";

    public static final String GUILD_KICK_OUT_SUCCECSS = "成功踢出玩家！\n";

    public static final String GUILD_LEAVE_SUCCESS = "成功离开公会！\n";

    public static final String GUILD_LEAVE_FAILED_NOT_EMPTY = "离开公会失败，公会人数大于2，请尝试将职位转移给其他公会成员再进行退出公会操作！\n";

    public static final String GUILD_STORE_NO_ENOUGH = "公会仓库不足，无法取出物品！\n";

    public static final String GUILD_DONATE_FAILED_WITHOUT_GOODS = "捐献失败，背包中物品数量不足！\n";

    public static final String GUILD_DONATE_SUCCESS = "捐献成功！\n";

    public static final String GUILD_TAKE_OUT_FAILED = "取出失败！\n";

    public static final String GUILD_TAKE_OUT_SUCCESS = "取出成功！\n";

    public static final String GUILD_DONATE_MSG = "【公会消息】\n玩家：%s 成功捐献物品：%s 数量：%d\n";


    // Trade
    public static final String TRADE_PUT_FAILED_NOT_ENOUGH = "上架物品失败，物品不足！\n";

    public static final String TRADE_PUT_SUCCESS = "上架物品成功！\n";

    public static final String TRADE_NOT_FOUND = "交易信息不存在！\n";

    public static final String TRADE_AUCTION_SELL_SUCCESS = "【交易行消息】\n你在拍卖行出售的物品：%s 成功出售，获得金币 %d ！\n";

    public static final String TRADE_AUCTION_PRICE_FAILED = "出价竞拍失败，最低的起拍价不应低于 %d ！\n";

    public static final String TRADE_AUCTION_BY_OTHERS = "【交易行消息】\n你在交易行竞拍的物品: %s，被他人成功竞拍！\n";

    public static final String TRADE_AUCTION_SUCCESS_GET = "【交易行消息】\n恭喜你，你在交易行以金币 %d 成功竞拍到物品：%s ，物品随后将通过邮件的方式发送，请注意接收!\n";

    public static final String TRADE_AUCTION_SUCCESS = "竞拍成功！\n";

    public static final String TRADE_OUT_OF_FAILED_OTHERS = "无法下架他人的交易信息！\n";

    public static final String TRADE_OUT_OF_FAILED_SOMEONE_HAS_TRADE = "下架物品失败，该交易物品已经被他人竞拍！\n";

    public static final String TRADE_OUT_OF_SUCCESS = "下架物品成功，物品已经返回玩家背包中！\n";

    public static final String TRADE_BUY_SUCCESS = "购买成功，物品随后将通过邮件发送，请注意接收！\n";

    public static final String TRADE_AUCTION_TIME_END = "【交易行消息】\n你在拍卖行出售的物品：%s 在拍卖时间内没有人竞价，物品已经返回你的背包中！\n";


    // Task
    public static final String TASK_ALL = "【任务列表】\n当前可接的任务如下所示：\n\n";

    public static final String TASK_STATE = "【任务列表】\n当前正在进行中的任务如下所示：\n\n";

    public static final String TASK_WRONG_PARAM = "任务id有误，请重新输入！\n";

    public static final String TASK_RECEIVE_SUCCESS = "任务领取成功！\n";

    public static final String TASK_RECEIVE_FAILED = "任务领取失败，已经领取过该任务，无法重复领取！\n";

    public static final String TASK_GIVE_UP_SUCCESS = "任务放弃成功！\n";

    public static final String TASK_GIVE_UP_FAILED = "任务放弃失败，无法放弃未领取接受的任务！\n";

    public static final String TASK_SUBMIT_FAILED = "任务未完成，无法进行任务提交操作！\n";

    public static final String TASK_COMPLETE_ANN = "【任务通知】\n恭喜你完成任务：%s，请提交任务进行获取任务奖励！\n";

    public static final String TASK_SUBMIT_SUCCESS = "任务提交成功，请等待邮件通知奖励信息！\n";

    // DB
    public static final String INSERT_FAILED = "插入失败！\n";



}
