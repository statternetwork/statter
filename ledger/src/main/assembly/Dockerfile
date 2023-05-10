FROM openjdk:8u322-jdk-buster

ENV TZ Asia/Shanghai
RUN mkdir -p /app/statter.ledger.api/logs
ADD statter.ledger.api-bin.tar.gz /app/

WORKDIR /app
EXPOSE 58081

ENTRYPOINT ["/bin/bash","/app/statter.ledger.api/bin/entrypoint.sh"]
