package com.ljh.gamedemo.module.talk.bean;

import lombok.Data;

/**
 * npc对话实体类
 */
@Data
public class TalkText implements Comparable<TalkText> {

    /**
     * 谈话的npc名称
     */
    private String entityName;

    /**
     * 对话等级，相同的npc可对话多次，根据你的等级而定
     */
    private Integer level;

    /**
     * 判断是谁先发起对话
     */
    private Integer first;

    /**
     * 对话文本
     */
    private String content;

    /**
     * 定义TalkText的排序规则，按照level进行倒序
     */
    @Override
    public int compareTo(TalkText o) {
        return o.getLevel().compareTo(this.getLevel());
    }
}
