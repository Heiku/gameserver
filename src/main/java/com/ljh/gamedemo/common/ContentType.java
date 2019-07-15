package com.ljh.gamedemo.common;

public class ContentType {

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

    // DB
    public static final String INSERT_FAILED = "插入失败！\n";
}
