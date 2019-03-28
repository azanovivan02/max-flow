#!/usr/bin/env bash

echo "=== Generate ==="

generator_path="solver/src/main/resources/generators/washington"
default_generated_file_path="/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max"

width="$1"
rows="$2"

echo "Rows: $rows"
echo "Width: $width"

if [ -z "$3" ]
    then
        echo "Using default generated file path"
        generated_file_path=${default_generated_file_path}
else
     generated_file_path="$3"
     echo "Using provided generated file path: $generated_file_path"
fi

$generator_path 3 ${width} ${rows} 100 ${generated_file_path}
