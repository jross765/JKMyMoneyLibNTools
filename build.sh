#!/bin/bash
#######################################################################

# mvn package
mvn package -Dmaven.test.skip.exec

# ---------------------------------------------------------------------
# JavaDoc

cd kmymoney-api
rm -rf target/site
mvn javadoc:javadoc
cd ..
