FROM openjdk:8u322-jdk-buster

ENV TZ Asia/Shanghai
RUN mkdir -p /app/statter.task/logs
ADD statter.task-bin.tar.gz /app/

WORKDIR /app
EXPOSE 58081

ENTRYPOINT ["/bin/bash","/app/statter.task/bin/entrypoint.sh"]
