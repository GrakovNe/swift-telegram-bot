#!/bin/bash

ssh $2@$1 "rm *.jar"
scp build/libs/swift* $2@$1:~
ssh $2@$1 "mv *.jar app.jar"
ssh $2@$1 "cp app.jar /opt/swift-app"
ssh $2@$1 "sudo systemctl restart swift_app.service"

echo "Done for $1 with username $2"
