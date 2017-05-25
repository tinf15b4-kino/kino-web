#!/bin/bash

set -eu

jarfile="/home/smartcinema/web.jar"

exec /usr/bin/java -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=50 -jar "$jarfile"
