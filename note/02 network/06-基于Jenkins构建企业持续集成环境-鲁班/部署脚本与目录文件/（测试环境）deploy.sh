#!/bin/bash -e
. ../bin/env-set.sh
cd "`dirname $0`"

pom_a=$1
pom_v=$2

#1. download war, ready env
echo "deploy time: $work_time"
mkdir -p war/
war=war/${pom_a}_${pom_v}.war
#svn export http://10.200.51.181/svn/bbg-doc/项目日常/03编码/$pom_v/${pom_a}_${pom_v}.war && mv ${pom_a}_${pom_v}.war war

deploy_war() {
        target_d=war/${pom_a}-${pom_v}-$work_time
        target_dir=`pwd`/$target_d
        if [ ! -f "$war" ]; then
                echo "war not exist: $war"
                exit 1
        fi
        unzip -q $war -d $target_dir
        cp -r app-conf/* $target_dir/WEB-INF/classes/
        rm -f appwar
	ln -sf $target_d appwar

	if [ -f current_deploy.sh ]
		then
			./tomcat.sh stop
			cat current_deploy_dir  > last_deploy		
	fi

        target_ln=`pwd`/appwar
        echo '<?xml version="1.0" encoding="UTF-8" ?>
<Context docBase="'$target_ln'" allowLinking="true">
</Context>' > conf/Catalina/localhost/ROOT.xml
	echo -ne "#!/bin/bash -e\npom_a=${pom_a}\npom_v=${pom_v}" > current_deploy.sh
	echo -ne "${target_d}" > current_deploy_dir
	chmod +x current_deploy.sh
        ./tomcat.sh start
}

deploy_war
