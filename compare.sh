#!/usr/bin/env bash

echo "=== Compare ==="

thread_amount="$1"
problem_path="$2"

echo "Threads: $thread_amount"
echo "Using provided problem path: $problem_path"

solver_path="solver/src/main/resources/solvers/dinic"
jar_path="solver/target/bo-hong-max-flow-1.0-SNAPSHOT-jar-with-dependencies.jar"

etalon_result=`$solver_path $problem_path | grep value | awk '{ print $3}'`
echo "etalon: ${etalon_result}"

my_result=`cat $problem_path | java -jar $jar_path $thread_amount`
echo "mine:   ${my_result}"

if [ "$etalon_result" == "$my_result" ]
then
    echo "CORRECT"
else
    echo "WRONG"
fi
