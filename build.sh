#!/bin/bash

mvn clean install jib:build -f task
mvn clean install jib:build -f manager
mvn clean install jib:build -f ledger
mvn clean install jib:build -f mining-pool
mvn clean install jib:build -f administrator
mvn clean install jib:build -f mockserver


