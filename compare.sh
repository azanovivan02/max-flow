#!/usr/bin/env bash

thread_amount="$1"
problem_path="$2"

echo "thread amount: $thread_amount"

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
