package com.netcracker;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import com.netcracker.simple.SimpleExecutor;

import java.util.Set;

import static com.netcracker.util.IoUtilsKt.readGraphFromDimacsFile;
import static java.lang.System.getProperty;

@Threads(1)
@Fork(value = 1)
public class MaxFlowBenchmarkSimple {

    private static final String PROBLEM_PATH_PROPERTY_KEY = "baumstark.problemPath";
    private static final String PROBLEM_PATH_DEFAULT = "/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max";

    @State(Scope.Thread)
    public static class MyState {

        SimpleExecutor executor;

        @Setup(Level.Trial)
        public void doSetup() {
            String problemPath = getPropertyOrDefault(PROBLEM_PATH_PROPERTY_KEY, PROBLEM_PATH_DEFAULT);
            final Graph<String, DefaultWeightedEdge> graph = readGraphFromDimacsFile(problemPath);
            final int verticesAmount = graph.vertexSet().size();
            executor = new SimpleExecutor(verticesAmount);
            for (DefaultWeightedEdge edge : graph.edgeSet()) {
                int from = Integer.parseInt(graph.getEdgeSource(edge));
                int to = Integer.parseInt(graph.getEdgeTarget(edge));
                int cap = (int) graph.getEdgeWeight(edge);
                executor.addEdge(
                        from,
                        to,
                        cap
                );
            }
        }

        @Setup(Level.Invocation)
        public void doSetupInvocation() {
            executor.reset();
        }

        @TearDown(Level.Trial)
        public void doTearDown() {
            System.out.println("Do TearDown");
        }
    }

    @Measurement(iterations = 2)
    @Warmup(iterations = 2)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testMethod(Blackhole blackhole, MyState state) {
        final int verticesAmount = state.executor.getVerticesAmount();
        final int maxFlowValue = state.executor.getMaxFlow(1, verticesAmount);
        blackhole.consume(maxFlowValue);
    }

    private static String getPropertyOrDefault(String propertyKey, String default_value) {
        final String property = getProperty(propertyKey);
        if (property == null) {
            return default_value;
        } else {
            return property;
        }
    }
}

