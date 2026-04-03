#!/bin/bash

java -Dlog4j.configuration=$CONFIGPATH/log4j.cfg \
     -Dconfig=$CONFIGFILE \
     org.kmymoney.tools.xml.get.info.GetPrcInfo ${1+"$@"}
