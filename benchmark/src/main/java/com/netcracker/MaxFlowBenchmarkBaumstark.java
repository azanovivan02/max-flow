package com.netcracker;

import com.netcracker.baumstark.BaumExecutor;
import com.netcracker.baumstark.BaumGraph;
import com.netcracker.baumstark.history.actions.DefaultActionRecorderAnalyzer;
import com.netcracker.baumstark.history.workingset.DummyWorkingSetRecorder;
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
public class MaxFlowBenchmarkBaumstark {

    private static final String PROBLEM_PATH_PROPERTY_KEY = "baumstark.problemPath";
    private static final String PROBLEM_PATH_DEFAULT = "/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max";

    private static final String THREAD_AMOUNT_PROPERTY_KEY = "baumstark.threadAmount";
    private static final String CORRECT_ANSWER_PROPERTY_KEY = "baumstark.correctAnswer";

    @State(Scope.Thread)
    public static class MyState {
        private BaumExecutor executor;

        @Setup(Level.Trial)
        public void doSetup() {
            String problemPath = getPropertyOrDefault(PROBLEM_PATH_PROPERTY_KEY, PROBLEM_PATH_DEFAULT);
            final int threadAmount = parseInt(getPropertyOrException(THREAD_AMOUNT_PROPERTY_KEY));

            final Graph<String, DefaultWeightedEdge> originalGraph = readGraphFromDimacsFile(problemPath);
            final BaumGraph baumGraph = createBaumGraph(originalGraph);

            final StandardOutputLogger logger = new StandardOutputLogger(false, DEBUG, ORIGINAL);
            final DummyWorkingSetRecorder recorder = new DummyWorkingSetRecorder();
            final boolean enablePreflowValidation = false;

            executor = new BaumExecutor(baumGraph, logger, threadAmount, enablePreflowValidation, recorder);
        }

        @Setup(Level.Invocation)
        public void doSetupInvocation() {
            executor.getGraph().init();
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

    private static String getPropertyOrDefault(String propertyKey, String default_value) {
        final String property = getProperty(propertyKey);
        if (property == null) {
            return default_value;
        } else {
            return property;
        }
    }

    @Measurement(iterations = 5)
    @Warmup(iterations = 5)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testMethod(Blackhole blackhole, MyState state) {
        final long maxFlowValue = state.executor.findMaxFlowValue(true, new DefaultActionRecorderAnalyzer());
        blackhole.consume(maxFlowValue);
    }
}

