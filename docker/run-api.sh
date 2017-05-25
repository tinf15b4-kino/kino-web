#!/bin/bash

set -eu

jarfile="/home/smartcinema/api.jar"

mkdir -p /home/smartcinema/db
dbfile=/home/smartcinema/db/smartcinema
if [ -e $dbfile.mv.db ]; then
    ddl=update
else
    ddl=create
fi

exec /usr/bin/java -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=50 -jar "$jarfile" --spring.datasource.url=jdbc:h2:file:$dbfile --spring.jpa.hibernate.ddl-auto=$ddl
