#!/usr/bin/env bash

problem_path="/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max"

solver_path="solver/src/main/resources/solvers/dinic"
jar_path="solver/target/bo-hong-max-flow-1.0-SNAPSHOT-jar-with-dependencies.jar"

etalon_result=`$solver_path $problem_path | grep value | awk '{ print $3}'`
echo "etalon: ${etalon_result}"