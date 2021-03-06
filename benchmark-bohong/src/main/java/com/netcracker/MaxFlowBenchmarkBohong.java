package com.netcracker;

import com.netcracker.bohong.LockFreeGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import static com.netcracker.bohong.GraphCreationUtilsKt.createLockFreeGraph;
import static com.netcracker.util.IoUtilsKt.readGraphFromDimacsFile;
import static java.lang.Integer.parseInt;
import static java.lang.System.getProperty;

@Threads(1)
@Fork(value = 1)
public class MaxFlowBenchmarkBohong {

    private static final String PROBLEM_PATH_PROPERTY_KEY = "baumstark.problemPath";
    private static final String PROBLEM_PATH_DEFAULT = "/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max";

    private static final String THREAD_AMOUNT_PROPERTY_KEY = "baumstark.threadAmount";
    private static final String CORRECT_ANSWER_PROPERTY_KEY = "baumstark.correctAnswer";

    @State(Scope.Thread)
    public static class MyState {
        private LockFreeGraph lockFreeGraph;
        final int threadAmount = parseInt(getPropertyOrException(THREAD_AMOUNT_PROPERTY_KEY));

        @Setup(Level.Trial)
        public void doSetup() {
            String problemPath = getPropertyOrDefault(PROBLEM_PATH_PROPERTY_KEY, PROBLEM_PATH_DEFAULT);

            final Graph<String, DefaultWeightedEdge> originalGraph = readGraphFromDimacsFile(problemPath);

            lockFreeGraph = createLockFreeGraph(originalGraph);
        }

        @Setup(Level.Invocation)
        public void doSetupInvocation() {
            final int sourceVertexId = lockFreeGraph.getSourceVertexId();
            lockFreeGraph.init(sourceVertexId);
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
        final long maxFlowValue = state.lockFreeGraph.findMaxFlowParallel(state.threadAmount, true);
        blackhole.consume(maxFlowValue);
    }

    private static String getPropertyOrException(String propertyKey) {
        final String property = getProperty(propertyKey);
        if (property == null) {
            throw new IllegalStateException("Property is expected but was not found: " + propertyKey);
        }
        return property;
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

