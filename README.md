# redis-playground

FIXME: my new application.

## Installation

Download from https://github.com/redis-playground/redis-playground

## Usage

A Playground for experimenting with [Redis](https://redis.io/) and
[Redis Stream](https://redis.io/topics/streams-intro)
using [carmine](https://github.com/ptaoussanis/carmine)


## Install Redis

First, you must have Redis (version 5 or later) installed and running. To download Redis, visit
[this page](https://hub.docker.com/_/redis/) and follow the instructions for your operating system.

### Windows users:

Windows is NOT a supported platform for Redis, so you will need to run Redis inside a Docker container.

    docker pull redis

Once installed, start the container with:

    docker run --name my-redis -p:6379:6379 -d redis

if you'd like to access the redis-cli, run:

    docker exec -it my-redis sh

that should put you into a shell in the running container, where you can run: 
    
    redis-cli

to startup the redis cli interface.  Run `ping` and you should get back `PONG` to test.

### Mac users:

I suggest installing Redis via [Homebrew](https://1upnote.me/post/2018/06/install-config-redis-on-mac-homebrew/)


## Working with the Playground

Use the deps.edn file to start a REPL and then begin evaluating code.

> This repo is not designed to be run from the clojure.cli; just use the repl and experiment.

