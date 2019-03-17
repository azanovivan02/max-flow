#!/usr/bin/env bash

thread_amount="${1}"
size="${2}"
echo "size: $size"
generator_path="solver/src/main/resources/generators/washington"
generated_file_path="/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max"

$generator_path 3 $size $size 100 ${generated_file_path}
./compare.sh "${thread_amount}" "${generated_file_path}"