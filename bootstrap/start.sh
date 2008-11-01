#!/bin/sh 
export SAKAI_KERNEL_PROPERTIES=`pwd`/localkernel.properties
target/runtime/bin/catalina.sh $*
