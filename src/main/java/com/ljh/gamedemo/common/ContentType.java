package com.ljh.gamedemo.common;

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

    // public static final String USER_EMPTY_TOKEN = "登录的请求token信息为空！\n";

    public static final String USER_TOKEN_DATA_EMPTY = "数据库中暂无当前token记录，请重新登录\n";

    public static final String USER_RELIVE_SUCCESS = "复活成功！\n";



    // Role
    public static final String ROLE_EMPTY = "当前玩家暂无角色！\n";

    public static final String ROLE_CHOOSE = "角色选择成功！\n";

    public static final String ROLE_TYPE = "当前职业种类如下；\n";

    public static final String ROLE_HAS = "已经存在对应的职业信息，无法重复创建角色!\n";

    public static final String ROLE_CREATE_SUCCESS = "角色创建成功！\n";

    public static final String ROLE_GET_HEAL = "获得治疗量：%s";


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

    public static final String ATTACK_CURRENT = "正在受到攻击！\n";

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

    public static final String PK_SEND_MSG = "玩家：ID: 【%d】 名称：【%s】 向你发送挑战邀请，输入指令【ac】可接受挑战！\n";

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

    // DB
    public static final String INSERT_FAILED = "插入失败！\n";

}
