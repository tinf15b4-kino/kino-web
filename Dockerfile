FROM ubuntu:16.04

# install required packages
RUN apt-get -y update \
    && apt-get -y install openjdk-8-jdk xvfb firefox supervisor locales \
    && rm -rf /var/lib/apt/lists/*

# set locale - otherwise gradle defaults to latin-1 charset
RUN echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen && \
    locale-gen en_US.utf8 && \
    /usr/sbin/update-locale LANG=en_US.UTF-8
ENV LANG=en_US.UTF-8 LC_ALL=en_US.UTF-8

# copy source code into container
RUN mkdir -p /tmp/kino-build
ADD . /tmp/kino-build

# don't do root while building
RUN useradd -ms /bin/bash smartcinema
RUN chown -R smartcinema:smartcinema /tmp/kino-build
USER smartcinema

RUN cd /tmp/kino-build && ./gradlew assemble

# install (now with root again)
USER root
RUN cp -a "/tmp/kino-build/data api/build/libs/tinf15b4-kino-data-api-0.0.1-SNAPSHOT.jar" /home/smartcinema/api.jar \
    && cp -a "/tmp/kino-build/web app/build/libs/tinf15b4-kino-web-app-0.0.1-SNAPSHOT.jar" /home/smartcinema/web.jar \
    && cp -a "/tmp/kino-build/data retrieval/build/libs/tinf15b4-kino-data-retrieval-0.0.1-SNAPSHOT.jar" /home/smartcinema/scraper.jar \
    && install -m0755 "/tmp/kino-build/docker/run-api.sh" /usr/local/bin/smartcinema-api \
    && install -m0755 "/tmp/kino-build/docker/run-web.sh" /usr/local/bin/smartcinema-web \
    && install -m0755 "/tmp/kino-build/docker/run-scraper.sh" /usr/local/bin/smartcinema-scraper \
    && install -m0644 "/tmp/kino-build/docker/supervisord.cfg" /etc/supervisord.cfg

# cleanup
RUN rm -rf /tmp/kino-build

USER smartcinema
WORKDIR /home/smartcinema
EXPOSE 9090
EXPOSE 8080

CMD ["supervisord", "-n", "-c", "/etc/supervisord.cfg"]
