#!/bin/bash

# If no port number is provided, use the default port 9501
if [ "$#" -eq 0 ]; then
    port=9501
    echo "No port specified. Using default port: $port"
else
    port="$1"
fi

# Run ./gradlew run with the specified port
./gradlew run --args="$port"

