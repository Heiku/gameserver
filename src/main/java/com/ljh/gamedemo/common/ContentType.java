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

    public static final String USER_EMPTY_TOKEN = "登录的请求token信息为空！\n";

    public static final String USER_TOKEN_DATA_EMPTY = "数据库中暂无当前token记录，请重新登录\n";



    // Role
    public static final String ROLE_EMPTY = "当前玩家暂无角色！\n";

    public static final String ROLE_CHOOSE = "角色选择成功！\n";


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

    // Exit
    public static final String EXIT_SUCCESS = "退出成功！\n";


    // Attack
    public static final String ATTACK_DEATH_CREEP = "你攻击的野怪已经死亡！\n";

    public static final String ATTACK_CURRENT = "正在攻击野怪！\n";

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

    // DB
    public static final String INSERT_FAILED = "插入失败！\n";
}
