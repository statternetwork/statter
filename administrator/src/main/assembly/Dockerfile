#FROM imageserver:8080/vts-dss/linkis-java:8
FROM imageserver:58080/library/openjdk:8u322-jdk-buster

ENV TZ Asia/Shanghai
RUN mkdir -p /app/statter.administrator.api/logs
ADD statter.administrator.api-bin.tar.gz /app/

WORKDIR /app
EXPOSE 58086

ENTRYPOINT ["/bin/bash","/app/statter.administrator.api/bin/entrypoint.sh"]
