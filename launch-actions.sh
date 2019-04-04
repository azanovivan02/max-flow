#!/usr/bin/env bash

thread_amount="$1"
echo "Threads: $thread_amount"

generated_file_path="generated.max"
jar_path="bo-hong-max-flow-1.0-SNAPSHOT-jar-with-dependencies.jar"

cat "${generated_file_path}" | java -jar ${jar_path} ${thread_amount} actions
