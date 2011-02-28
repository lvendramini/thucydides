package net.thucydides.core.model;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SUCCESS;

import java.util.ArrayList;
import java.util.List;

import ch.lambdaj.function.convert.Converter;

import com.google.common.collect.ImmutableList;

/**
 * Represents the results of an acceptance test (or "scenario") execution. This
 * includes the narrative steps taken during the test, screenshots at each step,
 * the results of each step, and the overall result. An Acceptance test scenario
 * can be associated with a UserStory using the UserStory annotation.
 * 
 * @composed 1..* steps * TestStep
 * 
 * @author johnsmart
 * 
 */
public class AggregateTestResults {

    private List<AcceptanceTestRun> testRuns;

    private final String title;
    
    /**
     * Create a new acceptance test run instance.
     */
    public AggregateTestResults(final String title) {
        testRuns = new ArrayList<AcceptanceTestRun>();
        this.title = title;
    }

    /**
     * Add a test run result to the aggregate set of results.
     */
    public void recordTestRun(final AcceptanceTestRun testRun) {
        testRuns.add(testRun);
    }

    /**
     * How many test runs in total have been recorded.
     *
     */
    public int getTotal() {
       return testRuns.size();
    }

    /**
     * How many test cases contain at least one failing test.
     */
    public int getFailureCount() {
        return select(testRuns, having(on(AcceptanceTestRun.class).isFailure())).size();
    }

    /**
     * How many test cases contain only successful or ignored tests.
     */
    public int getSuccessCount() {
        return select(testRuns, having(on(AcceptanceTestRun.class).isSuccess())).size();
    }

    public Integer getPendingCount() {
        return select(testRuns, having(on(AcceptanceTestRun.class).isPending())).size();
    }

    public List<AcceptanceTestRun> getTestRuns() {
        return ImmutableList.copyOf(testRuns);
    }

    public String getTitle() {
        return title;
    }

    private static class ExtractTestResultsConverter implements Converter<AcceptanceTestRun, TestResult> {
        public TestResult convert(final AcceptanceTestRun step) {
            return step.getResult();
        }
    }

    private List<TestResult> getCurrentTestResults() {
        return convert(getTestRuns(), new ExtractTestResultsConverter());
    }

    
    public TestResult getResult() {
        List<TestResult> allTestResults = getCurrentTestResults();

        if (allTestResults.contains(FAILURE)) {
            return FAILURE;
        }

        if (allTestResults.contains(PENDING)) {
            return PENDING;
        }

        if (containsOnly(allTestResults, IGNORED)) {
            return IGNORED;
        }

        return SUCCESS;
    }

    private boolean containsOnly(final List<TestResult> testResults, final TestResult value) {
        for (TestResult result : testResults) {
            if (result != value) {
                return false;
            }
        }
        return true;
    }

}
