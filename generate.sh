#!/usr/bin/env bash

generator_path="solver/src/main/resources/generators/washington"

generated_file_path="/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max"

$generator_path 3 $1 $1 100 ${generated_file_path}
