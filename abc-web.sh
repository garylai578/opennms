#!/bin/sh 
git pull
cd opennms-webapp
../compile.pl
rsync -rcv target/opennms-webapp-1.12.9/ /opt/opennms/jetty-webapps/opennms/
