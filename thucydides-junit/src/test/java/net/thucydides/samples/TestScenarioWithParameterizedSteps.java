package net.thucydides.samples;

import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.TestsRequirement;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesRunner.class)
public class TestScenarioWithParameterizedSteps {
    
    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;
    
    @Steps
    public SampleScenarioSteps steps;
        
    @Test
    @TestsRequirement("SOME_BUSINESS_RULE")
    public void happy_day_scenario() {
        steps.stepWithAParameter("foo");
        steps.stepWithTwoParameters("foo", 2);
        steps.stepThatSucceeds();
        steps.stepThatIsIgnored();
        steps.stepThatIsPending();
        steps.anotherStepThatSucceeds();
    }    
}
