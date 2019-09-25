package com.ljh.gamedemo.module.equip.service;

import com.google.common.collect.Lists;
import com.ljh.gamedemo.common.CommonDBType;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.EntityType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.module.equip.asyn.EquipSaveManager;
import com.ljh.gamedemo.module.equip.asyn.run.EquipEntitySaveRun;
import com.ljh.gamedemo.module.equip.asyn.run.RoleEquipSaveRun;
import com.ljh.gamedemo.module.equip.bean.Equip;
import com.ljh.gamedemo.module.equip.bean.RoleEquip;
import com.ljh.gamedemo.module.equip.dao.RoleEquipDao;
import com.ljh.gamedemo.module.equip.local.LocalEquipMap;
import com.ljh.gamedemo.module.goods.bean.Goods;
import com.ljh.gamedemo.module.role.asyn.RoleSaveManager;
import com.ljh.gamedemo.module.role.asyn.run.RoleAttrSaveRun;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.bean.RoleAttr;
import com.ljh.gamedemo.module.role.cache.RoleAttrCache;
import com.ljh.gamedemo.module.role.dao.RoleAttrDao;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.proto.protoc.MsgEquipProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 装备操作的具体操作
 *
 * @Author: Heiku
 * @Date: 2019/7/24
 */

@Slf4j
@Service
public class EquipService {

    /**
     * 装备修理的最低值
     */
    public static final int MIN_FIX_DUR = 10;

    /**
     * roleAttr：玩家属性
     */
    @Autowired
    private RoleAttrDao attrDao;

    /**
     * 协议服务
     */
    @Autowired
    private ProtoService protoService;


    /**
     * 装备返回
     */
    private MsgEquipProto.ResponseEquip response;

    /**
     * 获取当前的所有装备信息
     *
     * 1.判断玩家的状态
     * 2.判断玩家的类型，筛选对应的装备
     * 3.构造返回
     *
     * @param request   请求
     * @param channel   channel
     */
    public void getEquip(MsgEquipProto.RequestEquip request, Channel channel) {
        // 获取请求参数
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());

        // 玩家拥有的装备
        List<Equip> allList = Optional.ofNullable(LocalEquipMap.getHasEquipMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList());

        // 穿戴上的装备
        List<Equip> hasOn = Optional.ofNullable(LocalEquipMap.getRoleEquipMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList());

        List<Equip> notOnList = new ArrayList<>();
        allList.forEach(e -> {
            if (e.getHasOn() == null || e.getHasOn() != 1){
                notOnList.add(e);
            }
        });

        // 构造返回
        response = MsgEquipProto.ResponseEquip.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.FIND_SUCCESS)
                .setRole(protoService.transToRole(role))
                .setType(MsgEquipProto.RequestType.EQUIP)
                .addAllEquip(protoService.transToEquipList(notOnList))
                .addAllOwn(protoService.transToEquipList(hasOn))
                .build();
        channel.writeAndFlush(response);
    }

    /**
     * 装备穿戴
     *
     * 1. 玩家判断
     * 2. 装备判断
     * 3. 更新玩家装备
     * 4. 更新玩家属性
     *
     * @param request       请求
     * @param channel       channel
     */
    public synchronized void putEquip(MsgEquipProto.RequestEquip request, Channel channel) {
        // 获取用户状态
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());

        // 获取装备的基本信息
        long id = request.getId();
        Equip equip = getEquipFromBag(role, id);

        // 装备是否符合职业，等级判断
        response = equipPutInterceptor(equip, role);
        if (response != null){
            channel.writeAndFlush(response);
            return;
        }

        // 判断玩家是否已经穿戴过该装备
        List<Equip> ownList = LocalEquipMap.getRoleEquipMap().get(role.getRoleId());
        if (ownList != null) {
            Optional<Equip> idResult = ownList.stream().
                    filter(e -> e.getId() == id)
                    .findFirst();
            if (idResult.isPresent()){
                sendFailedMsg(channel, ContentType.EQUIP_SAME);
                return;
            }

            // 找到相同部位的装备
            Optional<Equip> typeResult = ownList.stream().
                    filter(e -> e.getPart().intValue() == equip.getPart())
                    .findFirst();
            if (typeResult.isPresent()){
                Equip e = typeResult.get();

                // 移除装备，并修改属性
                removeOnEquip(role, e);
                addRoleAttr(role, e, false);
            }
        }
        // 穿上装备
        addRoleEquip(role, equip, ownList);

        // 接着更新玩家角色的属性值
        // 保证线程安全，因为你再更新属性值的同时，有可能其他线程在读取你的属性值进行野怪攻击
        addRoleAttr(role, equip, true);

        // 获取玩家的装备信息
        List<Equip> resList = LocalEquipMap.getRoleEquipMap().get(role.getRoleId());

        // 构造返回
        response = MsgEquipProto.ResponseEquip.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgEquipProto.RequestType.PUT)
                .setContent(ContentType.EQUIP_PUT_SUCCESS)
                .setRole(protoService.transToRole(role))
                .addAllOwn(protoService.transToEquipList(resList))
                .build();
        channel.writeAndFlush(response);
    }


    /**
     * 玩家卸下装备信息
     *
     * 1.用户状态判断
     * 2.判断物品是否在用户身上
     * 3.玩家的装备数据
     * 4.更新玩家的属性数据
     *
     * @param request
     * @param channel
     */
    public synchronized void takeOffEquip(MsgEquipProto.RequestEquip request, Channel channel) {
        // 初始化输信息
        Role role = LocalUserMap.getUserRoleMap().get(request.getUserId());
        long id = request.getId();

        // 装备状态判断
        response = equipTakeOffInterceptor(id, role);
        if (response != null){
            channel.writeAndFlush(response);
            return;
        }

        // 获取玩家要卸下的装备信息
        List<Equip> onList = Optional.ofNullable(LocalEquipMap.getRoleEquipMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList());
        Optional<Equip> result = onList.stream().filter(e -> e.getId() == id).findFirst();
        if (result.isPresent()){
            Equip e = result.get();

            // 更新缓存，db 中的卸下信息
            removeOnEquip(role, e);
            // 更新角色属性
            addRoleAttr(role, e, false);
        }


        // 更新onList
        onList = LocalEquipMap.getRoleEquipMap().get(role.getRoleId());

        // 构造返回
        response = MsgEquipProto.ResponseEquip.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgEquipProto.RequestType.TAKEOFF)
                .setContent(ContentType.EQUIP_TAKEOFF_SUCCESS)
                .setRole(protoService.transToRole(role))
                .addAllOwn(protoService.transToEquipList(onList))
                .build();
        channel.writeAndFlush(response);
    }


    /**
     * 装备的修复
     *
     * 1.用户状态判断
     * 2.装备判断
     * 3.装备修复，更新装备信息
     *
     * @param request   请求
     * @param channel   channel
     */
    public synchronized void fixEquip(MsgEquipProto.RequestEquip request, Channel channel) {
        // 获取基本信息
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());
        long id = request.getId();

        // 装备状态判断
        response = equipTakeOffInterceptor(id, role);
        if (response != null){
            channel.writeAndFlush(response);
            return;
        }

        // 找到对应的装备信息，进行修复
        List<Equip> allEquipList = LocalEquipMap.getHasEquipMap().get(role.getRoleId());
        Optional<Equip> result = allEquipList.stream().
                filter(e -> e.getId() == id)
                .findFirst();

        if (result.isPresent()){
            Equip e = result.get();

            // 当装备的持久度 durability 小于 10，并且可用状态为0的时候，才可以修理
            if (e.getDurability() > MIN_FIX_DUR || e.getState() == 1){
                sendFailedMsg(channel, ContentType.EQUIP_FIX_FAILED);
                return;
            }
            // 更新装备的记录信息
            doFixEquip(e);
        }

        response = MsgEquipProto.ResponseEquip.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgEquipProto.RequestType.FIX)
                .setContent(ContentType.EQUIP_FIX_SUCCESS)
                .build();
        channel.writeAndFlush(response);
    }


    /**
     * 加锁同步玩家的装备数据
     *
     * @param role      玩家信息
     * @param tmp       装备信息
     * @param list      玩家身上的装备列表
     */
    private void addRoleEquip(Role role, Equip tmp, List<Equip> list){
        // 获取所有的背包装备信息
        if (list == null){
            list = new ArrayList<>();
        }

        // 找到背包中要穿上的装备
        List<Equip> equipList = LocalEquipMap.getHasEquipMap().get(role.getRoleId());
        Optional<Equip> result = equipList.stream()
                .filter(e -> e.getId().longValue() == tmp.getId())
                .findFirst();

        // 找到装备并添加到玩家身上
        if (result.isPresent()){
            Equip equip = result.get();
            equip.setHasOn(1);
            list.add(equip);

            // 更新身上的装备信息
            LocalEquipMap.getRoleEquipMap().put(role.getRoleId(), list);

            // 更新数据库中的装备信息
            EquipSaveManager.getExecutorService().submit(new EquipEntitySaveRun(equip, CommonDBType.UPDATE));
        }
    }


    /**
     * 加锁卸下玩家的装备
     *
     * @param equip    需要卸下的装备
     * @param role     玩家信息
     */
    private void removeOnEquip(Role role, Equip equip){
        List<Equip> ownList = LocalEquipMap.getRoleEquipMap().get(role.getRoleId());
        ownList.removeIf(e -> e.getId().longValue() == equip.getId());

        // 更新装备的 on 属性
        equip.setHasOn(0);
        EquipSaveManager.getExecutorService().submit(new EquipEntitySaveRun(equip, CommonDBType.UPDATE));
    }



    /**
     * 加锁修复装备的耐久度
     *
     * @param equip     装备信息
     */
    private void doFixEquip(Equip equip){
        // 更新装备信息
        equip.setDurability(100);
        equip.setState(1);

        // 更新数据库信息
        EquipSaveManager.getExecutorService().submit(new EquipEntitySaveRun(equip, CommonDBType.UPDATE));
    }


    /**
     * 在玩家的装备背包中添加新装备 (缓存 + DB)
     *
     * @param role      玩家信息
     * @param goods     装备物品
     */
    public void addRoleEquips(Role role, List<Goods> goods){
        // cache
        List<Equip> hasEquips = Optional.ofNullable(LocalEquipMap.getHasEquipMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList());
        goods.forEach(g -> {

            // 构建新装备信息
            Equip tmp = LocalEquipMap.getIdEquipMap().get(g.getGid());
            Equip data = new Equip();
            BeanUtils.copyProperties(tmp, data);

            // 插入数据库
            RoleEquip re = new RoleEquip();
            re.setEquipId(g.getGid());
            re.setRoleId(role.getRoleId());
            re.setDurability(tmp.getDurability());
            re.setState(tmp.getState());
            re.setHasOn(0);

            // 异步save
            EquipSaveManager.getExecutorService().submit(new RoleEquipSaveRun(re, CommonDBType.INSERT));

            // 设置装备编号
            data.setId(re.getId());
            data.setHasOn(re.getHasOn());

            // 添加到装备背包中
            hasEquips.add(data);
        });
        LocalEquipMap.getHasEquipMap().put(role.getRoleId(), hasEquips);
    }


    /**
     * 移除玩家的装备信息
     *
     * @param role      玩家信息
     * @param goodsId   装备id
     * @param num       数量
     */
    public void removeRoleEquips(Role role, long goodsId, int num){
        List<Equip> equipList = LocalEquipMap.getHasEquipMap().get(role.getRoleId());

        while (num > 0){
            Optional<Equip> result = equipList.stream().filter(e -> e.getEquipId() == goodsId).findFirst();
            if (result.isPresent()){

                // 删除缓存信息
                LocalEquipMap.getHasEquipMap().get(role.getRoleId()).removeIf(e -> e.getEquipId() == goodsId);

                // 同时删除数据库信息
                EquipSaveManager.getExecutorService().submit(new EquipEntitySaveRun(result.get(), CommonDBType.DELETE));
                num--;
            }
        }
    }


    /**
     * 加锁同步装备的耐久度信息
     *
     * @param role
     */
    public synchronized void synCutEquipDurability(Role role){
        List<Equip> equipList = LocalEquipMap.getRoleEquipMap().get(role.getRoleId());
        if (equipList == null || equipList.isEmpty()){
            return;
        }
        log.info("攻击野怪之前，装备栏的耐久度为：" + equipList);
        for (Equip e : equipList) {

            e.setDurability(e.getDurability() - 1);
            if (e.getDurability() < 10){
                e.setState(0);
            }

            // 更新db
            EquipSaveManager.getExecutorService().submit(new EquipEntitySaveRun(e, CommonDBType.UPDATE));
        }
        log.info("攻击野怪之后，装备栏的耐久度为：" + equipList);
    }



    /**
     * 玩家背包中的对应操作的装备
     *
     * @param r     玩家信息
     * @param id    E
     * @return      装备信息
     */
    private Equip getEquipFromBag(Role r, long id){
        List<Equip> hasList = LocalEquipMap.getHasEquipMap().get(r.getRoleId());
        Optional<Equip> result = hasList.stream().filter(e -> e.getId() == id).findFirst();
        return result.orElse(null);
    }



    /**
     * 加锁更新用户的属性值
     *
     * @param role      玩家信息
     * @param equip     装备信息
     */
    private void addRoleAttr(Role role, Equip equip, boolean add){
        // 初始化数据
        RoleAttr attr;

        int damage = equip.getAUp();
        int sp = equip.getSpUp();
        int hp = equip.getHpUp();
        int armor = equip.getArmor();

        // 读取旧属性值，优先从缓存中读取，再去数据库
        attr = RoleAttrCache.getRoleAttrMap().get(role.getRoleId());
        if (attr == null){
            attr = attrDao.selectAttrById(role.getRoleId());
        }
        if (attr == null){
            attr = new RoleAttr();
        }

        // 穿戴装备
        if (add) {
            attr.setRoleId(role.getRoleId());
            attr.setDamage(Optional.ofNullable(attr.getDamage()).orElse(0) + damage);
            attr.setSp(Optional.ofNullable(attr.getSp()).orElse(0)+ sp);
            attr.setHp(Optional.ofNullable(attr.getHp()).orElse(0) + hp);
            attr.setArmor(Optional.ofNullable(attr.getArmor()).orElse(0) + armor);
        } else {
            // 卸下装备
            attr.setRoleId(role.getRoleId());
            attr.setDamage(attr.getDamage() - damage);
            attr.setSp(attr.getSp() - sp);
            attr.setHp(attr.getHp() - hp);
            attr.setArmor(attr.getArmor() - armor);
        }

        // 更新缓存和db
        RoleAttrCache.getRoleAttrMap().put(role.getRoleId(), attr);


        log.info("穿戴装备前，用户的血量为：" + role.getHp());
        int i;
        // 进行数据库的更新，同时更新玩家的最新血量信息
        if (add){
            // 装备替换，只需更新数据库中属性的增量
            if (attrDao.selectAttrById(role.getRoleId()) != null){
                RoleSaveManager.getExecutorService().submit(new RoleAttrSaveRun(attr, CommonDBType.UPDATE));

                // 替换装备，血量只加上增量
                role.setHp((role.getHp() + attr.getHp()));
                role.setMaxHp(role.getMaxHp() + attr.getHp());

            }else {
                // 装备新添，进行装备数据的插入
                RoleSaveManager.getExecutorService().submit(new RoleAttrSaveRun(attr, CommonDBType.INSERT));

                // 装备增加后，用户的属性得到提升
                role.setHp(role.getHp() + equip.getHpUp());
                role.setMaxHp(role.getMaxHp() + equip.getHpUp());
            }
        }else {
            // 装备卸下，属性下降，更新玩家的属性信息
            RoleSaveManager.getExecutorService().submit(new RoleAttrSaveRun(attr, CommonDBType.UPDATE));
            i = attrDao.updateRoleAttr(attr);

            // 卸下装备，玩家的血量下降
            role.setHp(role.getHp() - equip.getHpUp());
            role.setMaxHp(role.getMaxHp() - equip.getHpUp());
        }


        LocalUserMap.idRoleMap.put(role.getRoleId(), role);
        for (Role r : LocalUserMap.siteRolesMap.get(role.getSiteId())){
            if (r.getRoleId().longValue() == role.getRoleId()){
                r.setHp(role.getHp());
            }
        }
        log.info("穿戴装备后，用户的血量为：" + LocalUserMap.idRoleMap.get(role.getRoleId()).getHp());
    }




    /**
     * 判断用户是否属于佩戴装备的类别 及 判断玩家等级是否能够佩戴
     *
     * @param equip     装备信息
     * @param role      玩家信息
     * @return          协议返回
     */
    public MsgEquipProto.ResponseEquip equipPutInterceptor(Equip equip, Role role){
        if (equip == null){
            return combineFailedMsg(ContentType.EQUIP_NOT_FOUND);
        }

        int type = equip.getType();
        int level = equip.getLevel();

        // 进行玩家的角色类别 及 玩家的角色等级判断
        if (type != role.getType() && type != EntityType.COMMON.getCode()){
            return combineFailedMsg(ContentType.EQUIP_WRONG_TPYE);
        }

        if (level > role.getLevel()){
            return combineFailedMsg(ContentType.EQUIP_WRONG_LEVEL);
        }
        return null;
    }




    /**
     * 判断装备的具体信息 （是否正确输入equipId, 当前用户是否持有该装备）
     *
     * @param id        装备编号
     * @param role      玩家角色
     * @return          消息返回
     */
    public MsgEquipProto.ResponseEquip equipTakeOffInterceptor(long id, Role role){
        Equip equip = getEquipFromBag(role, id);
        if (equip == null){
            return combineFailedMsg(ContentType.EQUIP_NOT_FOUND);
        }
        long roleId = role.getRoleId();

        // 查询当前的装备栏
        List<Equip> ownList = LocalEquipMap.getRoleEquipMap().get(roleId);

        // 装备栏为空
        if (ownList == null || ownList.isEmpty()){
            return combineFailedMsg(ContentType.EQUIP_OWN_EMPTY);
        }

        // 当玩家存在该装备时，返回正确
        Optional<Equip> result = ownList.stream().filter(e -> e.getId() == id).findFirst();
        if (result.isPresent()){
            return null;
        }
        return combineFailedMsg(ContentType.EQUIP_NOT_BELONG);
    }


    /**
     * 发送失败消息
     *
     * @param channel       channel
     * @param msg           消息
     */
    private void sendFailedMsg(Channel channel, String msg){
        response = MsgEquipProto.ResponseEquip.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
        channel.writeAndFlush(response);
    }

    /**
     * 构建失败消息
     *
     * @param msg       消息
     * @return          协议返回
     */
    private MsgEquipProto.ResponseEquip combineFailedMsg(String msg){
        response = MsgEquipProto.ResponseEquip.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
        return response;
    }
}
