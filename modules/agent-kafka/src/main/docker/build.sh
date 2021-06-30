#!/usr/bin/env bash

cp ../../../target/native-image/trace4cats-agent-kafka .

docker build -t janstenpickle/trace4cats-agent-kafka:$GITHUB_RUN_NUMBER .
