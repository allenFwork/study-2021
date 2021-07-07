#!/bin/bash -e
cd "`dirname $0`"
. ./pom.sh

#1. download war, ready env
echo "deploy time: $work_time"
mkdir -p war/
war=war/$pom_a-$pom_v.war
download_path="$nexus_redirect?r=$pom_r&g=$pom_g&a=$pom_a&v=$pom_v&e=war"
wget  $download_path -O $war

deploy_war
