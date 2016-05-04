#!/bin/sh
export LANG=en.US_UTF-8
git pull
./compile.pl
./assemble.pl -Dopennms.home=/opt/opennms
/opt/opennms/bin/opennms stop
rm -rf /opt/opennms
mkdir /opt/opennms
tar zxf target/opennms-1.12.9.tar.gz -C /opt/opennms
cd /opt/opennms
./bin/runjava -s
./bin/install -dis
./bin/opennms start
