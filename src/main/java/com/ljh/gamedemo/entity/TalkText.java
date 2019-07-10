package com.ljh.gamedemo.entity;

import lombok.Data;

/**
 * npc对话实体类
 */
@Data
public class TalkText implements Comparable<TalkText> {

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
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(TalkText o) {
        return o.getLevel().compareTo(this.getLevel());
    }
}
