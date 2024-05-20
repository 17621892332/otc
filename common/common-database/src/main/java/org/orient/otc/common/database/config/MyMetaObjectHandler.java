package org.orient.otc.common.database.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.orient.otc.common.security.dto.AuthorizeInfo;
import org.orient.otc.common.security.util.ThreadContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 插入更新填充
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if (Objects.isNull(metaObject.getValue("createTime"))) {
            this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        }
        if (Objects.isNull(metaObject.getValue("updateTime"))) {
            this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        }
        AuthorizeInfo currentUser = ThreadContext.getAuthorizeInfo();
        Integer userId;
        if(currentUser == null){
            userId = 1;
        }else {
             userId = currentUser.getId();
        }
        this.setFieldValByName("creatorId", userId, metaObject);
        this.setFieldValByName("updatorId", userId, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        AuthorizeInfo currentUser = ThreadContext.getAuthorizeInfo();
        Integer userId;
        if(currentUser == null){
            userId = 1;
        }else {
            userId = currentUser.getId();
        }
        this.setFieldValByName("updatorId", userId, metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }
}
