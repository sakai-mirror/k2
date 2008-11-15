#!/bin/sh 

if [ -n "$CATALINA_HOME" ]
then
  echo "CATALINA_HOME is set, removing it ..."
  unset CATALINA_HOME
fi

export SAKAI_KERNEL_PROPERTIES=`pwd`/localkernel.properties
target/runtime/bin/catalina.sh $*
