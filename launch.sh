#!/usr/bin/env bash

echo "=== Launch ==="

default_generated_file_path="/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max"

thread_amount="$1"
echo "Threads: $thread_amount"
if [ -z "$2" ]
    then
        echo "Using default generated file path"
        generated_file_path=${default_generated_file_path}
else
     generated_file_path="$2"
     echo "Using provided generated file path: $generated_file_path"
fi

generated_file_path="/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max"
jar_path="solver/target/bo-hong-max-flow-1.0-SNAPSHOT-jar-with-dependencies.jar"

cat "${generated_file_path}" | java -jar ${jar_path} ${thread_amount} simple | dot -Tpng > example.png && xdg-open example.png
