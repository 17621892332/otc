#!/bin/bash
# JVM参数
JAVA_OPTS='-Xms512m -Xmx512m -XX:PermSize=64M -XX:MaxNewSize=256m -XX:MaxPermSize=128m'
# SpringBoot 启动参数
BOOT_OPTS=''
# server.port
if [ -n "$SERVER_PORT" ]
then
    BOOT_OPTS="$BOOT_OPTS -Dserver.port=$SERVER_PORT";

fi
#spring.profiles.active
if [ -n "$SPRING_PROFILES_ACTIVE" ]
then
    BOOT_OPTS="$BOOT_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE";
fi

#spring.application.name
if [ -n "$SPRING_APPLICATION_NAME" ]
then
    BOOT_OPTS="$BOOT_OPTS -Dspring.application.name=$SPRING_APPLICATION_NAME";
fi

###################
##   nacos相关   ##
###################
# spring.cloud.nacos.discovery.server-addr
if [ -n "$NACOS_DISCOVERY_SERVER_ADDR" ]
then
    BOOT_OPTS="$BOOT_OPTS -Dspring.cloud.nacos.discovery.server-addr=$NACOS_DISCOVERY_SERVER_ADDR";
fi

# spring.cloud.nacos.discovery.ip
if [ -n "$NACOS_DISCOVERY_IP" ]
then
    BOOT_OPTS="$BOOT_OPTS -Dspring.cloud.nacos.discovery.ip=$NACOS_DISCOVERY_IP";
fi

# spring.cloud.nacos.discovery.port
if [ -n "$NACOS_DISCOVERY_PORT" ]
then
    BOOT_OPTS="$BOOT_OPTS -Dspring.cloud.nacos.discovery.port=$NACOS_DISCOVERY_PORT";
fi

# spring.cloud.nacos.discovery.group
if [ -n "$NACOS_DISCOVERY_GROUP" ]
then
    BOOT_OPTS="$BOOT_OPTS -Dspring.cloud.nacos.discovery.group=$NACOS_DISCOVERY_GROUP";
fi

# spring.cloud.nacos.discovery.namespace
if [ -n "$NACOS_DISCOVERY_NAMESPACE" ]
then
    BOOT_OPTS="$BOOT_OPTS -Dspring.cloud.nacos.discovery.namespace=$NACOS_DISCOVERY_NAMESPACE";
fi

# spring.cloud.nacos.discovery.register.ip
if [ -n "$NACOS_DISCOVERY_REG_IP" ]
then
    BOOT_OPTS="$BOOT_OPTS -Dspring.cloud.nacos.discovery.register.ip=$NACOS_DISCOVERY_REG_IP";
fi

# spring.cloud.nacos.discovery.register.port
if [ -n "$NACOS_DISCOVERY_REG_PORT" ]
then
    BOOT_OPTS="$BOOT_OPTS -Dspring.cloud.nacos.discovery.register.port=$NACOS_DISCOVERY_REG_PORT";
fi

# spring.cloud.nacos.config.server-addr
if [ -n "$NACOS_CONFIG_SERVER_ADDR" ]
then
    BOOT_OPTS="$BOOT_OPTS -Dspring.cloud.nacos.config.server-addr=$NACOS_CONFIG_SERVER_ADDR";
fi
# spring.cloud.nacos.config.namespace
if [ -n "$NACOS_CONFIG_NAMESPACE" ]
then
    BOOT_OPTS="$BOOT_OPTS -Dspring.cloud.nacos.config.namespace=$NACOS_CONFIG_NAMESPACE";
fi
#是否开启计算
if [ -n "$IS_OPEN_CACU_RISK" ]
then
    BOOT_OPTS="$BOOT_OPTS -DisOpenCacuRisk=$IS_OPEN_CACU_RISK";
fi
# 是否接收场内持仓
if [ -n "$IS_CACL_TRADE_POS" ]
then
    BOOT_OPTS="$BOOT_OPTS -DisCaclTradePos=$IS_CACL_TRADE_POS";
fi
echo "BOOT_OPTS is '$BOOT_OPTS'";

# 启动
cd /work
java ${JAVA_OPTS} ${BOOT_OPTS} -jar "app.jar"
