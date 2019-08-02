package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.EntityType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.RoleAttrDao;
import com.ljh.gamedemo.dao.RoleEquipDao;
import com.ljh.gamedemo.entity.Equip;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.dto.RoleAttr;
import com.ljh.gamedemo.entity.dto.RoleEquip;
import com.ljh.gamedemo.entity.dto.RoleEquipHas;
import com.ljh.gamedemo.local.LocalEquipMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.RoleAttrCache;
import com.ljh.gamedemo.proto.protoc.MsgEquipProto;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: Heiku
 * @Date: 2019/7/24
 *
 * 装备操作
 */

@Slf4j
@Service
public class EquipService {

    @Autowired
    private RoleEquipDao equipDao;

    @Autowired
    private RoleAttrDao attrDao;

    private MsgEquipProto.ResponseEquip response;

    private ProtoService protoService = ProtoService.getInstance();

    /**
     * 获取当前的所有装备信息
     *
     * 1.判断玩家的状态
     * 2.判断玩家的类型，筛选对应的装备
     * 3.构造返回
     *
     * @param request
     * @param channel
     */
    public void getEquip(MsgEquipProto.RequestEquip request, Channel channel) {
        // 用户状态判断
        response = userStateInterceptor(request);
        if (response != null){
            channel.writeAndFlush(response);
            return;
        }

        // 获取请求参数
        long userId = request.getUserId();
        Role role = LocalUserMap.userRoleMap.get(userId);

        // TODO: 暂时查询所有的装备信息，后期将改为按照角色的类别划分
        // 获取所有的装备信息
        // allList <Equip>  idEquipMap中获取
        // hasList <RoleEquip> roleEquipMap中获取
        // setMap <equipId, equip>
        List<Equip> allList;
        allList = LocalEquipMap.getHasEquipMap().get(role.getRoleId());

        List<RoleEquip> hasPutOn = LocalEquipMap.getRoleEquipMap().get(role.getRoleId());
        List<Equip> resHasList = new ArrayList<>();

        // 返回结果去除已经穿戴的装备
        Map<Long, Equip> setMap = new HashMap<>();
        for (Equip equip : allList) {
            // equip -> roleEquip
            setMap.put(equip.getEquipId(), equip);
        }
        // 去重
        if (hasPutOn != null && !hasPutOn.isEmpty()){
            for (RoleEquip re : hasPutOn){
                Equip data = LocalEquipMap.getIdEquipMap().get(re.getEquipId());
                Equip equip = new Equip();
                BeanUtils.copyProperties(data, equip);

                equip.setDurability(re.getDurability());
                equip.setState(re.getState());

                resHasList.add(equip);
                setMap.remove(re.getEquipId());
            }
        }

        // 剩下的map，即还未装备上的装备，返回的全部装备
        List<Equip> res = new ArrayList<>();
        setMap.forEach((k, v) -> {
            res.add(v);
        });

        // 构造返回
        response = MsgEquipProto.ResponseEquip.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.FIND_SUCCESS)
                .setRole(protoService.transToRole(role))
                .setType(MsgEquipProto.RequestType.EQUIP)
                .addAllEquip(protoService.transToEquipList(res))
                .addAllOwn(protoService.transToEquipList(resHasList))
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
     * @param request
     * @param channel
     */
    public void putEquip(MsgEquipProto.RequestEquip request, Channel channel) {
        // 用户的状态判断
        response = userStateInterceptor(request);
        if (response != null){
            channel.writeAndFlush(response);
            return;
        }

        // 获取用户状态
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());

        // 获取装备的基本信息
        long equipId = request.getEquipId();
        Equip equip = LocalEquipMap.getIdEquipMap().get(equipId);

        // 装备判断
        response = equipPutInterceptor(equip, role);
        if (response != null){
            channel.writeAndFlush(response);
            return;
        }

        // 判断玩家是否已经穿戴过该装备 ， 判断玩家是否佩戴同一种类型的装备
        synchronized (this){
            List<RoleEquip> ownList = LocalEquipMap.getRoleEquipMap().get(role.getRoleId());
            if (ownList != null) {
                for (RoleEquip re : ownList) {
                    if (re.getEquipId() == equipId){
                        response = MsgEquipProto.ResponseEquip.newBuilder()
                                .setResult(ResultCode.FAILED)
                                .setContent(ContentType.EQUIP_SAME)
                                .build();
                        channel.writeAndFlush(response);
                        return;
                    }
                }
            }
            // 已经拥有的装备
            if (ownList != null){
                for (RoleEquip re : ownList){
                    // 找到已有装备的具体类型
                    Equip e = LocalEquipMap.getIdEquipMap().get(re.getEquipId());
                    if (e.getPart().intValue() == equip.getPart()){
                        synRemoveEquip(e, role);
                        synAddRoleAttr(role, e, false);
                        break;
                    }
                }
            }
        }
        // 玩家能够佩戴装备
        // 更新玩家的装备佩戴信息
        // 更新缓存及数据库
        synAddRoleEquip(equip, role);

        // 接着更新玩家角色的属性值
        // 保证线程安全，因为你再更新属性值的同时，有可能其他线程在读取你的属性值进行野怪攻击
        synAddRoleAttr(role, equip, true);

        // 获取玩家的装备信息
        List<Equip> resList = getOwnEquipList(role);

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
    public void takeOffEquip(MsgEquipProto.RequestEquip request, Channel channel) {
        // 用户的状态判断
        response = userStateInterceptor(request);
        if (response != null){
            channel.writeAndFlush(response);
            return;
        }

        // 初始化输信息
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());
        long equipId = request.getEquipId();

        // 装备状态判断
        response = equipTakeOffInterceptor(equipId, role);
        if (response != null){
            channel.writeAndFlush(response);
            return;
        }

        // 玩家存在装备信息
        // 更新玩家的装备栏（缓存 + db）
        Equip equip = LocalEquipMap.getIdEquipMap().get(equipId);
        synRemoveEquip(equip, role);

        // 线程安全修改角色的属性
        synAddRoleAttr(role, equip, false);

        // 获取玩家的装备信息
        List<Equip> resList = getOwnEquipList(role);

        // 构造返回
        response = MsgEquipProto.ResponseEquip.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgEquipProto.RequestType.TAKEOFF)
                .setContent(ContentType.EQUIP_TAKEOFF_SUCCESS)
                .setRole(protoService.transToRole(role))
                .addAllOwn(protoService.transToEquipList(resList))
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
     * @param request
     * @param channel
     */
    public void fixEquip(MsgEquipProto.RequestEquip request, Channel channel) {
        // 用户状态判断
        response = userStateInterceptor(request);
        if (response != null){
            channel.writeAndFlush(response);
            return;
        }

        // 获取基本信息
        long userId = request.getUserId();
        long equipId = request.getEquipId();
        Role role = LocalUserMap.userRoleMap.get(userId);

        // 装备状态判断
        response = equipTakeOffInterceptor(equipId, role);
        if (response != null){
            channel.writeAndFlush(response);
            return;
        }

        // 获取装备状态，进行修复
        RoleEquip fixEquip = new RoleEquip();
        List<RoleEquip> ownList = LocalEquipMap.getRoleEquipMap().get(role.getRoleId());
        for (RoleEquip re : ownList) {
            if (re.getEquipId() == equipId){
                fixEquip = re;
            }
        }

        // 暂时设置：当装备的持久度 durability 小于 10，并且可用状态为0的时候，才可以修理
        if (fixEquip.getDurability() > 10 || fixEquip.getState() == 1){
            response = MsgEquipProto.ResponseEquip.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.EQUIP_FIX_FAILED)
                    .build();
        }

        synFixEquip(fixEquip, role);


        response = MsgEquipProto.ResponseEquip.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgEquipProto.RequestType.FIX)
                .setContent(ContentType.EQUIP_FIX_SUCCESS)
                .build();
    }


    /**
     * 加锁同步玩家的装备数据
     *
     * @param equip
     * @param role
     */
    private synchronized void synAddRoleEquip(Equip equip, Role role){
        List<RoleEquip> ownList = LocalEquipMap.getRoleEquipMap().get(role.getRoleId());
        if (ownList == null) {
            ownList = new ArrayList<>();
        }

        RoleEquip roleEquip = new RoleEquip();
        roleEquip.setRoleId(role.getRoleId());
        roleEquip.setEquipId(equip.getEquipId());
        roleEquip.setDurability(equip.getDurability());
        roleEquip.setState(equip.getState());
        ownList.add(roleEquip);

        LocalEquipMap.getRoleEquipMap().put(role.getRoleId(), ownList);

        int n = equipDao.insertRoleEquip(roleEquip);
        log.info("插入 role_equip成功，插入的记录为：" + n + " ,主键id为：" + roleEquip.getId());
    }


    /**
     * 加锁卸下玩家的装备
     *
     * @param equip
     * @param role
     */
    private synchronized void synRemoveEquip(Equip equip, Role role){
        List<RoleEquip> ownList = LocalEquipMap.getRoleEquipMap().get(role.getRoleId());
        log.info("卸下装备前，当前的装备栏为：" + ownList);
        if (ownList != null){
            for (int i = 0; i < ownList.size(); i++){
                RoleEquip re = ownList.get(i);

                if (re.getEquipId().longValue() == equip.getEquipId() && re.getRoleId().longValue() == role.getRoleId()){
                    ownList.remove(re);
                    i--;
                }
            }
        }

        log.info("卸下装备后，当前的装备栏为：" + ownList);
        int n = equipDao.deleteRoleEquip(equip.getEquipId(), role.getRoleId());
        log.info("卸下装备后，删除db中的装备记录：" + n);
    }


    /**
     * 加锁更新用户的属性值
     *
     * @param role
     * @param equip
     */
    private synchronized void synAddRoleAttr(Role role, Equip equip, boolean add){
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
                i = attrDao.updateRoleAttr(attr);

                // 替换装备，血量只加上增量
                role.setHp((role.getHp() + attr.getHp()));
                role.setMaxHp(role.getMaxHp() + attr.getHp());

            }else {
                // 装备新添，进行装备数据的插入
                i = attrDao.insertRoleAttr(attr);

                // 装备增加后，用户的属性得到提升
                role.setHp(role.getHp() + equip.getHpUp());
                role.setMaxHp(role.getMaxHp() + equip.getHpUp());
            }
        }else {
            // 装备卸下，属性下降，更新玩家的属性信息
            i = attrDao.updateRoleAttr(attr);

            // 卸下装备，玩家的血量下降
            role.setHp(role.getHp() - equip.getHpUp());
            role.setMaxHp(role.getMaxHp() - equip.getHpUp());
        }
        log.info("更新用户属性成功，当前的更新记录为：" + i);


        LocalUserMap.idRoleMap.put(role.getRoleId(), role);
        for (Role r : LocalUserMap.siteRolesMap.get(role.getSiteId())){
            if (r.getRoleId().longValue() == role.getRoleId()){
                r.setHp(role.getHp());
            }
        }
        log.info("穿戴装备后，用户的血量为：" + LocalUserMap.idRoleMap.get(role.getRoleId()).getHp());
    }


    /**
     * 加锁修复装备的耐久度
     *
     * @param re
     * @param role
     */
    private synchronized void synFixEquip(RoleEquip re, Role role){
        log.info("修理前的装备为：" + LocalEquipMap.getRoleEquipMap().get(role.getRoleId()));
        re.setDurability(100);
        re.setState(1);
        log.info("修理后修改bean后装备为：" + LocalEquipMap.getRoleEquipMap().get(role.getRoleId()));

        // 更新db
        RoleEquip roleEquip = equipDao.selectRoleEquip(role.getRoleId(), re.getEquipId());
        roleEquip.setDurability(re.getDurability());
        roleEquip.setState(re.getState());
        int n = equipDao.updateRoleEquip(roleEquip);
        log.info("修理后，更新数据库中的数据，更新的记录为：" + n);
    }


    /**
     * 判断装备的具体信息 （是否正确输入equipId, 当前用户是否持有该装备）
     *
     * @param equipId
     * @param role
     * @return
     */
    public MsgEquipProto.ResponseEquip equipTakeOffInterceptor(long equipId, Role role){
        Equip equip = LocalEquipMap.getIdEquipMap().get(equipId);
        if (equip == null){
            return MsgEquipProto.ResponseEquip.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.EQUIP_NOT_FOUND)
                    .build();
        }

        long roleId = role.getRoleId();

        // 查询当前的装备栏
        List<RoleEquip> ownList = LocalEquipMap.getRoleEquipMap().get(roleId);

        // 装备栏为空
        if (ownList == null || ownList.isEmpty()){
            return MsgEquipProto.ResponseEquip.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.EQUIP_OWN_EMPTY)
                    .build();
        }

        // 当前玩家并不持有该装备
        for (RoleEquip re : ownList) {
            if (re.getEquipId() == equipId){
                return null;
            }
        }
        return MsgEquipProto.ResponseEquip.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(ContentType.EQUIP_NOT_BELONG)
                .build();
    }


    /**
     * 判断用户是否属于佩戴装备的类别 及 判断玩家等级是否能够佩戴
     *
     * @param equip
     * @param role
     * @return
     */
    public MsgEquipProto.ResponseEquip equipPutInterceptor(Equip equip, Role role){
        if (equip == null){
            return MsgEquipProto.ResponseEquip.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.EQUIP_NOT_FOUND)
                    .build();
        }

        int type = equip.getType();
        int level = equip.getLevel();

        // 进行玩家的角色类别 及 玩家的角色等级判断
        if (type != role.getType() && type != EntityType.COMMON.getCode()){
            return MsgEquipProto.ResponseEquip.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.EQUIP_WRONG_TPYE)
                    .build();
        }

        if (level > role.getLevel()){
            return MsgEquipProto.ResponseEquip.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.EQUIP_WRONG_LEVEL)
                    .build();
        }
        return null;
    }


    /**
     * 用户状态拦截器，检验参数
     *
     * @param requestEquip
     * @return
     */
    private MsgEquipProto.ResponseEquip userStateInterceptor(MsgEquipProto.RequestEquip requestEquip){
        // 用户id标识判断
        long userId = requestEquip.getUserId();
        if (userId <= 0){
            return MsgEquipProto.ResponseEquip.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.USER_TOKEN_DATA_EMPTY)
                    .build();
        }
        // 找不到对应的角色信息
        Role role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            return MsgEquipProto.ResponseEquip.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }
        return null;
    }


    /**
     * 返回当前玩家的所有已装备物品
     *
     * @param role
     * @return
     */
    private List<Equip> getOwnEquipList(Role role){
        List<Equip> resList = new ArrayList<>();
        List<RoleEquip> ownList = LocalEquipMap.getRoleEquipMap().get(role.getRoleId());
        for (RoleEquip re : ownList) {
            Equip e = LocalEquipMap.getIdEquipMap().get(re.getEquipId());
            e.setDurability(re.getDurability());
            e.setState(re.getState());
            resList.add(e);
        }

        return resList;
    }


    /**
     * 在玩家的装备背包中新增装备 (缓存 + DB)
     *
     * @param role
     * @param equips
     */
    public void addRoleEquips(Role role, List<Equip> equips){

        // cahce
        List<Equip> hasEquips = LocalEquipMap.getHasEquipMap().get(role.getRoleId());
        equips.forEach(e -> {
            Equip tmp = LocalEquipMap.getIdEquipMap().get(e.getEquipId());
            Equip des = new Equip();
            BeanUtils.copyProperties(tmp, des);
            hasEquips.add(des);


            // db
            RoleEquipHas has = new RoleEquipHas();
            has.setEquipId(e.getEquipId());
            has.setRoleId(role.getRoleId());

            equipDao.addHasEquips(has);
        });
        LocalEquipMap.getHasEquipMap().put(role.getRoleId(), hasEquips);
    }


    /**
     * 加锁同步装备的耐久度信息
     *
     * @param role
     */
    public synchronized void synCutEquipDurability(Role role){
        List<RoleEquip> roleEquipList = LocalEquipMap.getRoleEquipMap().get(role.getRoleId());

        log.info("攻击野怪之前，装备栏的耐久度为：" + roleEquipList);
        for (RoleEquip re : roleEquipList) {

            re.setDurability(re.getDurability() - 1);
            if (re.getDurability() < 10){
                re.setState(0);
            }

            // 更新db
            int n = equipDao.updateRoleEquip(re);
            log.info("攻击野怪之后，装备栏中的更新的行数为：" + n);
        }
        log.info("攻击野怪之后，装备栏的耐久度为：" + roleEquipList);
    }
}
