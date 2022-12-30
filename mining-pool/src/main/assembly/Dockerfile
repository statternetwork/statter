#FROM imageserver:8080/vts-dss/linkis-java:8
FROM imageserver:58080/library/openjdk:8u322-jdk-buster

ENV TZ Asia/Shanghai
RUN mkdir -p /app/statter.mining.pool.api/logs
ADD statter.mining.pool.api-bin.tar.gz /app/

WORKDIR /app
EXPOSE 58085

ENTRYPOINT ["/bin/bash","/app/statter.mining.pool.api/bin/entrypoint.sh"]
