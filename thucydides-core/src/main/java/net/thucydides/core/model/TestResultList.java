package net.thucydides.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;

/**
 * A list of test results, used to determine the overall test result.
 */
public class TestResultList {

    private final List<TestResult> testResults;

    public TestResultList(final List<TestResult> testResults) {
        this.testResults = testResults;
    }

    public boolean isEmpty() {
        return testResults.isEmpty();
    }

    public TestResult getOverallResult() {
        if (testResults.isEmpty()) {
            return PENDING;
        }

        if (testResults.contains(FAILURE)) {
            return FAILURE;
        }

        if (testResults.contains(PENDING)) {
            return PENDING;
        }

        if (containsOnly(IGNORED)) {
            return IGNORED;
        }

        if (containsOnly(SKIPPED)) {
            return SKIPPED;
        }

        if (containsOnly(SUCCESS, IGNORED, SKIPPED)) {
            return SUCCESS;
        }
        return PENDING;
    }

    private boolean containsOnly(final TestResult... value) {
        if (testResults.isEmpty()) {
            return false;
        } else {
            return containsOnlyType(value);
        }
    }

    private boolean containsOnlyType(final TestResult... values) {
        List<TestResult> authorizedTypes = Arrays.asList(values);
        for (TestResult result : testResults) {
            if (!authorizedTypes.contains(result)) {
                return false;
            }
        }
        return true;
    }

}
