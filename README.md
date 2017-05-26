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
