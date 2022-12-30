#!/bin/bash

mvn clean install -N
mvn clean install -f component
mvn clean install -f common
