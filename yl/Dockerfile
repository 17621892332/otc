#基础镜像
FROM otc/jdk11:latest

#创建文件夹
RUN mkdir "/work"

#复制文件
COPY entrypoint.sh /work/entrypoint.sh
COPY target/yl-1.0-SNAPSHOT.jar /work/app.jar

#设置语言
ENV LANG "C.UTF-8"

WORKDIR  /work
USER root
RUN chmod +x /work/entrypoint.sh
CMD  sh /work/entrypoint.sh
