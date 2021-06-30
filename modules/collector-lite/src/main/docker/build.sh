#!/usr/bin/env bash

cp ../../../target/native-image/trace4cats-collector-lite .

docker build -t janstenpickle/trace4cats-collector-lite:$GITHUB_RUN_NUMBER .
