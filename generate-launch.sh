#!/usr/bin/env bash

size="${1}"
echo $size

generator_path="solver/src/main/resources/generators/washington"

generated_file_path="/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max"

$generator_path 3 $size $size 100 ${generated_file_path} && cat "${generated_file_path}" | java -jar target/bo-hong-max-flow-1.0-SNAPSHOT-jar-with-dependencies.jar simple
