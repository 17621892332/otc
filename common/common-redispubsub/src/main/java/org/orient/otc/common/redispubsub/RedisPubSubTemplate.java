package org.orient.otc.common.redispubsub;

public interface RedisPubSubTemplate {
    void onMessage(String message);

    String setChannel();
}
