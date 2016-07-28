#!/bin/bash

cd src
tar -xvzf referencelogfiles.tar
export CLASSPATH=../externaljar/commons-io-2.4.jar:../externaljar/hamcrest-core-1.3.jar:../externaljar/junit-4.12.jar:../externaljar/junit-addons-1.4.jar:. 
javac *.java
java -cp .:../externaljar/commons-io-2.4.jar:../externaljar/hamcrest-core-1.3.jar:../externaljar/junit-4.12.jar:../externaljar/junit-addons-1.4.jar org.junit.runner.JUnitCore UnitTester