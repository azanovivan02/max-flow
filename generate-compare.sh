#!/usr/bin/env bash

thread_amount="${1}"
rows="$2"
width="$3"

echo "Threads: $thread_amount"

generator_path="solver/src/main/resources/generators/washington"
generated_file_path="/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max"

./generate.sh ${rows} ${width} ${generated_file_path}
./compare.sh "${thread_amount}" "${generated_file_path}"