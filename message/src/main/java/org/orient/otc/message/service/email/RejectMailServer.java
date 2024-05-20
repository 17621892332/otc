package org.orient.otc.message.service.email;

/**
 * 退件
 * @author  chengqiang
 */
public interface RejectMailServer {
    /**
     * 异步获取并处理退件
     */
    void doRejectMailList();

    /**
     * 立即处理
     */
    void asyncDoRejectMail () throws Exception;
}
