package org.orient.otc.common.redisstream;

import redis.clients.jedis.StreamEntryID;

import java.util.Map.Entry;
public class MyJedisEntry implements Entry<String, StreamEntryID>{
    private String k;
    private StreamEntryID id;
    public MyJedisEntry(String key, String id){
        this.k = key;
        if("0".equals(id)) {
            this.id = new StreamEntryID();
        }else {
            this.id = new StreamEntryID(id);
        }
    }
    public MyJedisEntry(String key, StreamEntryID ID) {
        this.k = key;
        this.id = ID;
    }
    @Override
    public String getKey() {
        return k;
    }
    @Override
    public StreamEntryID getValue() {
        return id;
    }
    @Override
    public StreamEntryID setValue(StreamEntryID value) {
        this.id = value;
        return id;
    }
}
