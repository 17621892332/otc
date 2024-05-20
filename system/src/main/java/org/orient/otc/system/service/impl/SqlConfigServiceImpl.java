package org.orient.otc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.common.sqllable.SqlLabelUtil;
import org.orient.otc.common.sqllable.SqlMeta;
import org.orient.otc.system.dto.SqlRequestDto;
import org.orient.otc.system.entity.SqlConfig;
import org.orient.otc.system.exception.BussinessException;
import org.orient.otc.system.mapper.SqlConfigMapper;
import org.orient.otc.system.service.SqlConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Service
@Slf4j
public class SqlConfigServiceImpl extends ServiceImpl<BaseMapper<SqlConfig>,SqlConfig> implements SqlConfigService {
    @Autowired
    private SqlConfigMapper sqlConfigMapper;
    @Autowired @Qualifier("dataSource")
    private DataSource dataSource;
    @Override
    public Object getSqlResult(SqlRequestDto sqlRequest) {
        SqlConfig sqlConfig = sqlConfigMapper.selectOne(new LambdaQueryWrapper<SqlConfig>().eq(SqlConfig::getCode, sqlRequest.getCode()).eq(SqlConfig :: getIsDeleted,0));
        SqlMeta sqlMeta = SqlLabelUtil.getSqlMeta(sqlConfig.getSqlText(), sqlRequest.getParam());
        String sqlText = sqlMeta.getSql();
        List<Object> parameter = sqlMeta.getParameter();
        log.debug("sqlText:{}",sqlText);
        log.debug("parameter:{}",parameter);
        // 获取Connection对象
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY,  ResultSet.CONCUR_READ_ONLY)){
                for(int i = 0;i<parameter.size();i++){
                    ps.setObject(i+1,parameter.get(i));
                }
                if(sqlConfig.getType() == 1) {
                    List<Map<String, Object>> result = new ArrayList<>();
                    ResultSet resultSet = ps.executeQuery();
                    ResultSetMetaData md = resultSet.getMetaData();
                    int columnCount = md.getColumnCount();
                    //BussinessException.E_100103.assertTrue(columnCount <= 10000);
                    while (resultSet.next()) {
                        Map<String, Object> rowData = new HashMap<String, Object>();
                        for (int i = 1; i <= columnCount; i++) {
                            rowData.put(md.getColumnName(i), resultSet.getObject(i));
                        }
                        result.add(rowData);
                    }
                    return result;
                }else if (sqlConfig.getType() == 2 || sqlConfig.getType() == 3){
                    ps.execute();
                    return Boolean.TRUE;
                }else {
                    BussinessException.E_200104.doThrow();
                }
            }
        } catch (SQLException e) {
            BussinessException.E_200102.doThrow(e.getMessage());
        }
        return null;
    }
}
