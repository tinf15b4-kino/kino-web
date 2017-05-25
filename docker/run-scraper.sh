#!/bin/bash

set -eu

jarfile="/home/smartcinema/scraper.jar"

GEOMETRY=1360x1020x24
exec xvfb-run --server-args="-screen 0 $GEOMETRY -ac +extension RANDR" \
    /usr/bin/java -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=50 -jar "$jarfile"
