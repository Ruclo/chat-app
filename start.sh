#!/bin/bash
service rabbitmq-server start
echo AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
rabbitmq-plugins enable rabbitmq_stomp
echo AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
java -jar app.jar