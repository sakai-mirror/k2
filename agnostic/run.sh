#!/bin/sh
if [[ a$1 = 'aclean' ]]
then
  echo cleaning
  rm -rf apache-tomcat-5.5.26
  tar xzf ../../../tars/apache-tomcat-5.5.26.tar.gz
fi
cd apache-tomcat-5.5.26
unzip -o ../overlay/target/assembly-0.1-SNAPSHOT-kernel-loader.zip
# add in the kernel to the component locations already listed.

export SAKAI_KERNEL_PROPERTIES="inline://+component.locations=;../kernel/target/kernel-0.1-SNAPSHOT.jar"
sh bin/catalina.sh run
