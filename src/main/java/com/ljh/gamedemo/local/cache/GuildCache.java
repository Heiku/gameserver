package com.ljh.gamedemo.local.cache;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Guild;
import com.ljh.gamedemo.entity.GuildApply;
import com.ljh.gamedemo.entity.GuildGoodsStore;
import com.ljh.gamedemo.entity.Member;

import java.util.List;
import java.util.Map;

/**
 * 本地缓存公会信息记录
 *
 * @Author: Heiku
 * @Date: 2019/8/26
 */
public class GuildCache {

    /**
     * 公会id信息 (guildId, Guild)
     */
    private static Map<Long, Guild> idGuildMap = Maps.newConcurrentMap();

    /**
     * 玩家公会信息 (roleId, Guild)
     */
    private static Map<Long, Guild> roleIdGuildMap = Maps.newConcurrentMap();

    /**
     * 公会成员信息 (roleId, Member)
     */
    private static Map<Long, Member> roleMemberMap = Maps.newConcurrentMap();

    /**
     * 公会申请信息 (guildId, List<GuildApply>)
     */
    private static Map<Long, List<GuildApply>> guildApplyMap = Maps.newConcurrentMap();

    /**
     * 玩家公会申请信息 (roleId, List<GuildApply>)
     */
    private static Map<Long, List<GuildApply>> roleGuildApplyMap = Maps.newConcurrentMap();

    /**
     * 公会物品仓库信息 (guildId, List<GuildStoreMap>)
     */
    private static Map<Long, List<GuildGoodsStore>> guildStoreMap = Maps.newConcurrentMap();



    public static Map<Long, Guild> getIdGuildMap() {
        return idGuildMap;
    }

    public static Map<Long, Member> getRoleMemberMap() {
        return roleMemberMap;
    }

    public static Map<Long, Guild> getRoleIdGuildMap() {
        return roleIdGuildMap;
    }

    public static Map<Long, List<GuildApply>> getGuildApplyMap() {
        return guildApplyMap;
    }

    public static Map<Long, List<GuildGoodsStore>> getGuildStoreMap() {
        return guildStoreMap;
    }

    public static Map<Long, List<GuildApply>> getRoleGuildApplyMap() {
        return roleGuildApplyMap;
    }
}
