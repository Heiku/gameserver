package com.ljh.gamedemo.entity;

import lombok.Data;

/**
 * @Author: Heiku
 * @Date: 2019/7/19
 *
 * 用于缓存中记录当前用户的时间戳进行cd的比较
 */

@Data
public class SpellTimeStamp {

    // 技能id
    private int spellId;

    // 施放技能的时间戳
    private long timeStamp;
}
