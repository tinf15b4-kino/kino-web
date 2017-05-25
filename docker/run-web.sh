#!/bin/bash

set -eu

jarfile="/home/smartcinema/web.jar"

# allow running with linked api
if [ -n "${API_PORT_9090_TCP_ADDR-}" ]; then
    export SMARTCINEMA_API_URL="http://$API_PORT_9090_TCP_ADDR:$API_PORT_9090_TCP_PORT"
fi

exec /usr/bin/java -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=50 -jar "$jarfile"
