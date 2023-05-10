#FROM imageserver:8080/vts-dss/linkis-java:8
FROM openjdk:8u322-jdk-buster

ENV TZ Asia/Shanghai
RUN mkdir -p /app/statter.mock.api/logs
ADD statter.mock.api-bin.tar.gz /app/

WORKDIR /app
EXPOSE 9010

ENTRYPOINT ["/bin/bash","/app/statter.mock.api/bin/entrypoint.sh"]
