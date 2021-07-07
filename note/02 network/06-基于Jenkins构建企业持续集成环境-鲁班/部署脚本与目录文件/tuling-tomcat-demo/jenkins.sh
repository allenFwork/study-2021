#!/bin/bash -e
cd "`dirname $0`"
. ./pom.sh

#BUILD_URL=http://10.200.51.105:9000/jenkins/job/bubugao-goods/269/
#pom_v=0.5.0

#1. download war, ready env
echo "deploy time: $work_time"
mkdir -p war/
war=war/$pom_a-$pom_v.war
wget  "${BUILD_URL}${pom_g}\$${pom_a}/artifact/$pom_g/$pom_a/$pom_v/$pom_a-$pom_v.war" -O $war

deploy_war


