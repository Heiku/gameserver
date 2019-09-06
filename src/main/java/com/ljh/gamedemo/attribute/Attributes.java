package com.ljh.gamedemo.attribute;

import io.netty.util.AttributeKey;

/**
 * channel
 */
public interface Attributes {

    AttributeKey<Long> USER_ID = AttributeKey.newInstance("userId");
}
