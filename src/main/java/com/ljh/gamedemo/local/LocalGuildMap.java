package com.ljh.gamedemo.local;

import com.google.common.collect.Lists;
import com.ljh.gamedemo.dao.GuildApplyDao;
import com.ljh.gamedemo.dao.GuildDao;
import com.ljh.gamedemo.dao.GuildGoodsDao;
import com.ljh.gamedemo.entity.Guild;
import com.ljh.gamedemo.entity.GuildApply;
import com.ljh.gamedemo.entity.GuildGoodsStore;
import com.ljh.gamedemo.entity.Member;
import com.ljh.gamedemo.local.cache.GuildCache;
import com.ljh.gamedemo.local.channel.ChannelCache;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 加载数据库中的公会信息
 *
 * @Author: Heiku
 * @Date: 2019/8/27
 */
public class LocalGuildMap {

    /**
     * GuildDao
     */
    private static GuildDao guildDao;

    /**
     * GuildApplyDao
     */
    private static GuildApplyDao guildApplyDao;

    /**
     * GuildGoodsDao
     */
    private static GuildGoodsDao guildGoodsDao;

    static {
        guildDao = SpringUtil.getBean(GuildDao.class);
        guildApplyDao = SpringUtil.getBean(GuildApplyDao.class);
        guildGoodsDao = SpringUtil.getBean(GuildGoodsDao.class);
    }

    public static void readDB(){

        // 初始化所有公会，公会成员信息
        List<Guild> guilds = guildDao.queryGuildList();
        if (guilds != null){
            guilds.forEach(g -> {
                if (g.getMembers() == null){
                    g.setMembers(Lists.newArrayList());
                }

                // 设置公会成员信息
                List<Member> members = Optional.ofNullable(guildDao.queryAllMemberByGid(g.getId()))
                        .orElse(Lists.newArrayList());
                g.setMembers(members);

                // 本地关联公会成员
                GuildCache.getIdGuildMap().put(g.getId(), g);
                members.forEach(m -> {
                    GuildCache.getRoleMemberMap().put(m.getRoleId(), m);
                    GuildCache.getRoleIdGuildMap().put(m.getRoleId(), g);
                });
                GuildCache.getIdGuildMap().put(g.getId(), g);


                // 初始化公会库存信息
                List<GuildGoodsStore> guildStoreList = Optional.ofNullable(guildGoodsDao.queryAllGuildStore(g.getId()))
                        .orElse(Lists.newArrayList());
                GuildCache.getGuildStoreMap().put(g.getId(), guildStoreList);


                // 加载所有公会的申请信息
                List<GuildApply> applyList = Optional.ofNullable(guildApplyDao.queryAllUnCheckGuildApply(g.getId()))
                        .orElse(Lists.newArrayList());
                applyList.forEach(a -> {
                    List<GuildApply> roleApplyList = Optional.ofNullable(GuildCache.getRoleGuildApplyMap().get(a.getRoleId()))
                            .orElse(Lists.newArrayList());
                    roleApplyList.add(a);
                    GuildCache.getRoleGuildApplyMap().put(a.getRoleId(), roleApplyList);
                });
            });
        }
    }
}
