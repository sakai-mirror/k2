#!/bin/sh 

if [ -n "$CATALINA_HOME" ]
then
  echo "CATALINA_HOME is set, removing it ..."
  unset CATALINA_HOME
fi

if [ "${SAKAI_KERNEL_PROPERTIES}" = "" ] 
then
  export SAKAI_KERNEL_PROPERTIES=`pwd`/localkernel.properties
fi
echo "SAKAI_KERNEL_PROPERTIES is set to $SAKAI_KERNEL_PROPERTIES"


target/runtime/bin/catalina.sh $*
