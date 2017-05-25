#!/bin/bash

set -eu

jarfile="/home/smartcinema/scraper.jar"

# allow running with linked api
if [ -n "${API_PORT_9090_TCP_ADDR-}" ]; then
    export SMARTCINEMA_API_URL="http://$API_PORT_9090_TCP_ADDR:$API_PORT_9090_TCP_PORT"
fi

run() {
    GEOMETRY=1360x1020x24
    exec xvfb-run --server-args="-screen 0 $GEOMETRY -ac +extension RANDR" \
        /usr/bin/java -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=50 -jar "$jarfile"
}

# HACK: xvfb-run can't run as PID1, needs someone to clean up zombies
if [ "$$" = 1 ]; then
    (run)
else
    run
fi
