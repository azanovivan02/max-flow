#!/usr/bin/env bash
cat solver/src/main/resources/example.max | java -jar solver/target/bo-hong-max-flow-1.0-SNAPSHOT-jar-with-dependencies.jar graph | dot -Tpng > example.png && xdg-open example.png
