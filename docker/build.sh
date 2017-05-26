#!/bin/bash

set -eu

# install required packages
apt-get -y update
apt-get -y install --no-install-recommends openjdk-8-jdk xvfb firefox supervisor locales \

# set locale - otherwise gradle defaults to latin-1 charset
echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen
locale-gen en_US.utf8
/usr/sbin/update-locale LANG=en_US.UTF-8
export LANG=en_US.UTF-8 LC_ALL=en_US.UTF-8

# setup user
useradd -ms /bin/bash smartcinema
chown -R smartcinema:smartcinema /tmp/kino-build

# build, but as non-root
su smartcinema -c 'cd /tmp/kino-build && ./gradlew assemble'

# install (now with root again)
cp -a "/tmp/kino-build/data api/build/libs/tinf15b4-kino-data-api-0.0.1-SNAPSHOT.jar" /home/smartcinema/api.jar
cp -a "/tmp/kino-build/web app/build/libs/tinf15b4-kino-web-app-0.0.1-SNAPSHOT.jar" /home/smartcinema/web.jar
cp -a "/tmp/kino-build/data retrieval/build/libs/tinf15b4-kino-data-retrieval-0.0.1-SNAPSHOT.jar" /home/smartcinema/scraper.jar
install -m0755 "/tmp/kino-build/docker/run-api.sh" /usr/local/bin/smartcinema-api
install -m0755 "/tmp/kino-build/docker/run-web.sh" /usr/local/bin/smartcinema-web
install -m0755 "/tmp/kino-build/docker/run-scraper.sh" /usr/local/bin/smartcinema-scraper
install -m0644 "/tmp/kino-build/docker/supervisord.cfg" /etc/supervisord.cfg

# cleanup
apt-get -y install --no-install-recommends openjdk-8-jre-headless
apt-get -y remove openjdk-8-jdk
apt-get -y autoremove
apt-get clean
su smartcinema -c 'cd /tmp/kino-build && ./gradlew clean' # leave the source files since they were added by a lower layer
rm -rf /home/smartcinema/.gradle
rm -rf /var/lib/apt/lists/

