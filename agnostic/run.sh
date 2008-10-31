#!/bin/sh
rm -rf apache-tomcat-5.5.26
tar xzf ../../../tars/apache-tomcat-5.5.26.tar.gz
cd apache-tomcat-5.5.26
unzip -o ../overlay/target/assembly-0.1-SNAPSHOT-kernel-loader.zip
sh bin/catalina.sh run
