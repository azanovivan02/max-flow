#!/usr/bin/env bash

thread_amount="$1"
rows="$2"
width="$3"

echo "Threads: $thread_amount"

generator_path="solver/src/main/resources/generators/washington"
generated_file_path="/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max"
jar_path="solver/target/bo-hong-max-flow-1.0-SNAPSHOT-jar-with-dependencies.jar"

./generate.sh ${rows} ${width} ${generated_file_path}
./launch.sh ${thread_amount} ${generated_file_path}
