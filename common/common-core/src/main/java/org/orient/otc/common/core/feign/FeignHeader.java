package org.orient.otc.common.core.feign;



public enum FeignHeader {
    REQUEAST_HEADER("FEIGN_TOKEN", "426c333a-4116-4e1a-b9a0-5c6e51badcc0");
    FeignHeader(String key,String value){
        this.key = key;
        this.value = value;
    }
    private final String key;
    private final String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
