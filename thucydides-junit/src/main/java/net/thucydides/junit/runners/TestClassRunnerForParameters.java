package net.thucydides.junit.runners;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.junit.listeners.JUnitStepListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.List;

class TestClassRunnerForParameters extends ThucydidesRunner {
    private final int parameterSetNumber;
    private final List<Object[]> parameterList;

    TestClassRunnerForParameters(final Class<?> type,
                                 final List<Object[]> parameterList,
                                 final int i) throws InitializationError {
        super(type);
        this.parameterList = parameterList;
        parameterSetNumber = i;
    }

   @Override
   protected JUnitStepListener initListenersUsing(final Pages pagesObject) {
       System.out.println("TestClassRunnerForParameters initListeners for " + parameterSetNumber);
       setStepListener(new ParameterizedJUnitStepListener(Configuration.loadOutputDirectoryFromSystemProperties(),
                                                          pagesObject,
                                                          parameterSetNumber));
       return getStepListener();
   }

    @Override
    public Object createTest() throws Exception {
        return getTestClass().getOnlyConstructor().newInstance(computeParams());
    }

    private Object[] computeParams() throws Exception {
        try {
            return parameterList.get(parameterSetNumber);
        } catch (ClassCastException cause) {
            throw new Exception(String.format(
                    "%s.%s() must return a Collection of arrays.",
                    getTestClass().getName(),
                    DataDrivenAnnotations.forClass(getTestClass()).getTestDataMethod().getName()),
                    cause);
        }
    }

    @Override
    protected boolean restartBrowserBeforeTest() {
        String restartFrequencyValue
                    = System.getProperty(ThucydidesSystemProperty.RESTART_BROWSER_FREQUENCY.getPropertyName(),"3");

        int restartFrequency = Integer.valueOf(restartFrequencyValue);
        return (parameterSetNumber > 0) && (parameterSetNumber % restartFrequency == 0);
    }

    @Override
    protected String getName() {
        String firstParameter = parameterList.get(parameterSetNumber)[0].toString();
        return String.format("[%s]", firstParameter);
    }

    @Override
    protected String testName(final FrameworkMethod method) {
        return String.format("%s[%s]", method.getName(), parameterSetNumber);
    }

    @Override
    protected void validateConstructor(final List<Throwable> errors) {
        validateOnlyOneConstructor(errors);
    }

    @Override
    protected Statement classBlock(final RunNotifier notifier) {
        return childrenInvoker(notifier);
    }

}