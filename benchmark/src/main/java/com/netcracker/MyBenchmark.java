package com.netcracker;

import com.netcracker.baumstark.BaumGraph;
import com.netcracker.baumstark.history.DummyWorkingSetRecorder;
import com.netcracker.util.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import static com.netcracker.baumstark.BaumGraphCreationUtilsKt.createBaumGraph;
import static com.netcracker.util.IoUtilsKt.readGraphFromDimacsFile;
import static com.netcracker.util.LogLevel.DEBUG;
import static com.netcracker.util.Logger.ThreadIdOption.ORIGINAL;
import static java.lang.Integer.parseInt;
import static java.lang.System.getProperty;

@Threads(1)
@Fork(value = 1)
public class MyBenchmark {

    private static final String path = "/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max";

    private static final String THREAD_AMOUNT_PROPERTY_KEY = "baumstark.threadAmount";
    private static final String CORRECT_ANSWER_PROPERTY_KEY = "baumstark.correctAnswer";

//    private static final int = Integer.parseInt(System.getProperty(CORREC))

    @State(Scope.Thread)
    public static class MyState {
        private BaumGraph baumGraph;

        @Setup(Level.Trial)
        public void doSetup() {
            final Graph<String, DefaultWeightedEdge> originalGraph = readGraphFromDimacsFile(path);
            baumGraph = createBaumGraph(originalGraph);

            final StandardOutputLogger logger = new StandardOutputLogger(false, DEBUG, ORIGINAL);
            baumGraph.setLogger(logger);
            final DummyWorkingSetRecorder recorder = new DummyWorkingSetRecorder();
            baumGraph.setWorkingSetRecorder(recorder);
            baumGraph.setEnablePreflowValidation(false);

            final int threadAmount = parseInt(getPropertyOrException(THREAD_AMOUNT_PROPERTY_KEY));
            baumGraph.setThreadAmount(threadAmount);
        }

        @Setup(Level.Invocation)
        public void doSetupInvocation() {
            baumGraph.init();
        }

        @TearDown(Level.Trial)
        public void doTearDown() {
            System.out.println("Do TearDown");
        }
    }

    private static String getPropertyOrException(String propertyKey) {
        final String property = getProperty(propertyKey);
        if (property == null) {
            throw new IllegalStateException("Property is expected but was not found: " + propertyKey);
        }
        return property;
    }

    @Measurement(iterations = 3)
    @Warmup(iterations = 3)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testMethod(Blackhole blackhole, MyState state) {
        final long maxFlowValue = state.baumGraph.findMaxFlowValue(true);
        blackhole.consume(maxFlowValue);
    }
}

