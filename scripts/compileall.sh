#!/bin/bash  

cd ../src
export CLASSPATH=../externaljar/commons-io-2.4.jar:../externaljar/hamcrest-core-1.3.jar:../externaljar/junit-4.12.jar:../externaljar/junit-addons-1.4.jar:.
javac *.java