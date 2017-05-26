# Welcome to SmartCinemaProject

## Our fancy badge collection
### Sonarqube
[![Quality Gate](https://sonarqube.com/api/badges/gate?key=de.tinf15b4.kino%3AHEAD)](https://sonarqube.com/dashboard/index/de.tinf15b4.kino%3AHEAD) 
[![Coverage](https://sonarqube.com/api/badges/measure?key=de.tinf15b4.kino%3AHEAD&metric=coverage)](https://sonarqube.com/component_measures/domain/Coverage?id=de.tinf15b4.kino%3AHEAD)
[![Maintainability](https://sonarqube.com/api/badges/measure?key=de.tinf15b4.kino%3AHEAD&metric=sqale_rating)](https://sonarqube.com/component_measures/domain/Maintainability?id=de.tinf15b4.kino%3AHEAD)
[![Security](https://sonarqube.com/api/badges/measure?key=de.tinf15b4.kino%3AHEAD&metric=security_rating)](https://sonarqube.com/component_measures/domain/Security?id=de.tinf15b4.kino%3AHEAD)
[![Reliability](https://sonarqube.com/api/badges/measure?key=de.tinf15b4.kino%3AHEAD&metric=reliability_rating)](https://sonarqube.com/component_measures/domain/Reliability?id=de.tinf15b4.kino%3AHEAD)
[![Complexity](https://sonarqube.com/api/badges/measure?key=de.tinf15b4.kino%3AHEAD&metric=complexity)](https://sonarqube.com/component_measures/domain/Complexity?id=de.tinf15b4.kino%3AHEAD)
[![Duplication](https://sonarqube.com/api/badges/measure?key=de.tinf15b4.kino%3AHEAD&metric=duplicated_lines_density)](https://sonarqube.com/component_measures/domain/Duplications?id=de.tinf15b4.kino%3AHEAD)
[![LOC](https://sonarqube.com/api/badges/measure?key=de.tinf15b4.kino%3AHEAD&metric=ncloc)](https://sonarqube.com/component_measures/domain/Size?id=de.tinf15b4.kino%3AHEAD)
[![Issues](https://sonarqube.com/api/badges/measure?key=de.tinf15b4.kino%3AHEAD&metric=violations)](https://sonarqube.com/component_issues?id=de.tinf15b4.kino%3AHEAD#resolved=false)

### Docker

[![Docker Stars](https://img.shields.io/docker/stars/smartcinema/smartcinema.svg)](https://hub.docker.com/r/smartcinema/smartcinema/)
[![Docker Pulls](https://img.shields.io/docker/pulls/smartcinema/smartcinema.svg)](https://hub.docker.com/r/smartcinema/smartcinema/)
[![Docker Automated buil](https://img.shields.io/docker/automated/smartcinema/smartcinema.svg)](https://hub.docker.com/r/smartcinema/smartcinema/)
[![Docker Build Statu](https://img.shields.io/docker/build/smartcinema/smartcinema.svg)](https://hub.docker.com/r/smartcinema/smartcinema/)

### GitHub

[![GitHub forks](https://img.shields.io/github/forks/tinf15b4-kino/kino-web.svg?style=social&label=Fork)](https://github.com/tinf15b4-kino/kino-web)
[![GitHub stars](https://img.shields.io/github/stars/tinf15b4-kino/kino-web.svg?style=social&label=Star)](https://github.com/tinf15b4-kino/kino-web)
[![GitHub watchers](https://img.shields.io/github/watchers/tinf15b4-kino/kino-web.svg?style=social&label=Watch)](https://github.com/tinf15b4-kino/kino-web)
[![GitHub followers](https://img.shields.io/github/followers/tinf15b4-kino.svg?style=social&label=Follow)](https://github.com/tinf15b4-kino)


[![GitHub pull requests](https://img.shields.io/github/issues-pr/tinf15b4-kino/kino-web.svg)](https://github.com/tinf15b4-kino/kino-web/pulls)
[![GitHub closed pull requests](https://img.shields.io/github/issues-pr-closed/tinf15b4-kino/kino-web.svg)](https://github.com/tinf15b4-kino/kino-web/pulls?q=is%3Apr+is%3Aclosed)
[![Issue Stats](https://img.shields.io/issuestats/p/github/tinf15b4-kino/kino-web.svg)](https://github.com/tinf15b4-kino/kino-web/pulls)
[![GitHub contributors](https://img.shields.io/github/contributors/tinf15b4-kino/kino-web.svg)](https://github.com/tinf15b4-kino/kino-web/graphs/contributors)

### Continuous Integration

[![Jenkins](https://img.shields.io/jenkins/s/https/jenkins.genosse-einhorn.de/job/SmartCinema%20Web%20App%20MULTIBRANCH/job/master.svg)](https://jenkins.genosse-einhorn.de/job/SmartCinema%20Web%20App%20MULTIBRANCH/job/master/)
[![Jenkins tests](https://img.shields.io/jenkins/t/https/jenkins.genosse-einhorn.de/job/SmartCinema%20Web%20App%20MULTIBRANCH/job/master.svg)](https://jenkins.genosse-einhorn.de/job/SmartCinema%20Web%20App%20MULTIBRANCH/job/master/lastCompletedBuild/testReport/)

### Project Health

[![Maintenance](https://img.shields.io/maintenance/yes/2017.svg)](https://github.com/tinf15b4-kino/kino-web)
[![Prod Website](https://img.shields.io/website-up-down-green-red/https/smartcinema.tinf15b4.de.svg?label=production%20web%20app)](https://smartcinema.tinf15b4.de)
[![Prod API](https://img.shields.io/website-up-down-green-red/https/smartcinema.tinf15b4.de.svg?label=production%20api)](https://smartcinema.tinf15b4.de/rest/ping)
[![Dev Website](https://img.shields.io/website-up-down-green-red/https/smartcinema-dev.tinf15b4.de.svg?label=development%20web%20app)](https://smartcinema-dev.tinf15b4.de)
[![Dev API](https://img.shields.io/website-up-down-green-red/https/smartcinema-dev.tinf15b4.de.svg?label=development%20api)](https://smartcinema-dev.tinf15b4.de/rest/ping)
![](https://img.shields.io/badge/developer%20sanity-lost-red.svg)

# How To Run

The SmartCinema server components are available in a docker container. We only have one container where
all server programs are installed, but you can start it in various ways to run a single component only.

## Quick Start

Run the web app with an integrated API server

    docker run -d --name=smartcinema -p 8080:8080 smartcinema/smartcinema

You can then view the web app at http://localhost:8080/

Since the database is empty, you might want to run the data scraper to fill
the database with movies and cinemas:

    docker run -it --rm --link smartcinema:api --shm-size 2g smartcinema/smartcinema smartcinema-scraper

(The `--shm-size` is necessary otherwise the included firefox will crash)

## Proper Deployment

In a real-world situation, you would want to run the API server and web app in different containers,
and you'd want to make sure that the database is persistent:

So here's how you start the API alone:

    docker run -d --name=smartcinema-api -p 9090:9090 -v /INSERT/DATABASE/DIRECTORY/ON/HOST/HERE:/home/smartcinema/db smartcinema/smartcinema smartcinema-api

Make sure the database directory is accessible by the user in the container (UID 1000).

And the web app:

    docker run -d --name=smartcinema-web -p 8080:8080 --link smartcinema-api:api smartcinema/smartcinema smartcinema-web

The scraper is supposed to be started via a cron job, and you can run it as follows:

    docker run -it --rm --link smartcinema-api:api --shm-size 2g smartcinema/smartcinema smartcinema-scraper

While the web app contains a poor man's proxy for the REST API, you'll want to
configure your reverse proxy (you're using one for TLS termination anyway, right?)
to handle this. Here's a relevant nginx snippet:

    location / {
        proxy_pass http://127.0.0.1:8080/;
    }

    location /rest/ {
        proxy_pass http://127.0.0.1:9090/rest/;
    }
