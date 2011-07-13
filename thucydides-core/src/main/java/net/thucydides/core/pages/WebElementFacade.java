package net.thucydides.core.pages;

import com.thoughtworks.selenium.Selenium;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.util.concurrent.TimeUnit;

import static net.thucydides.core.webdriver.Configuration.*;

/**
 * A proxy class for a web element, providing some more methods.
 */
public class WebElementFacade {

    private final WebElement webElement;
    private final WebDriver driver;
    private RenderedPageObjectView renderedView;

    public WebElementFacade(final WebDriver driver, final WebElement webElement) {
        this.driver = driver;
        this.webElement = webElement;
    }

    /**
     * Is this web element present and visible on the screen
     * This method will not throw an exception if the element is not on the screen at all.
     * If the element is not visible, the method will wait a bit to see if it appears later on.
     */
    public boolean isVisible() {
        try {
            return webElement.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Is this web element present and visible on the screen
     * This method will not throw an exception if the element is not on the screen at all.
     * The method will fail immediately if the element is not visible on the screen.
     */
    public boolean isCurrentlyVisible() {
        try {
            return webElement.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Checks whether a web element is visible.
     * Throws an AssertionError if the element is not rendered.
     */
    public void shouldBeVisible() {
        if (!isVisible()) {
            throw new AssertionError("Element should be visible");
        }
    }

    /**
     * Checks whether a web element is visible.
     * Throws an AssertionError if the element is not rendered.
     */
    public void shouldBeCurrentlyVisible() {
        if (!isCurrentlyVisible()) {
            throw new AssertionError("Element should be visible");
        }
    }

    /**
     * Checks whether a web element is not visible.
     * Throws an AssertionError if the element is not rendered.
     */
    public void shouldNotBeVisible() {
        if (isVisible()) {
            throw new AssertionError("Element should not be visible");
        }
    }

    /**
     * Checks whether a web element is not visible straight away.
     * Throws an AssertionError if the element is not rendered.
     */
    public void shouldNotBeCurrentlyVisible() {
        if (isCurrentlyVisible()) {
            throw new AssertionError("Element should not be visible");
        }
    }

    /**
     * Does this element currently have the focus.
     */
    public boolean hasFocus() {
        WebDriverFacade driverFacade = (WebDriverFacade) driver;
        WebDriver proxiedDriver = driverFacade.getProxiedDriver();
        Selenium selenium = new WebDriverBackedSelenium(proxiedDriver , proxiedDriver.getCurrentUrl());
        String activeElement = selenium.getEval("window.document.activeElement");
        return webElement.toString().equals(activeElement);
    }

    /**
     * Does this element contain a given text?
     */
    public boolean containsText(final String value) {
        return webElement.getText().contains(value);
    }

    /**
     * Check that an element contains a text value
     * @param textValue
     */
    public void shouldContainText(String textValue) {
        if (!containsText(textValue)) {
            String errorMessage = String.format(
                    "The text '%s' was not found in the web element", textValue);
            throw new AssertionError(errorMessage);
        }
    }

    /**
     * Check that an element does not contain a text value
     * @param textValue
     */
    public void shouldNotContainText(String textValue) {
        if (containsText(textValue)) {
            String errorMessage = String.format(
                    "The text '%s' was not found in the web element", textValue);
            throw new AssertionError(errorMessage);
        }
    }

    private void enterValueOnClearField(String textValue) {
        webElement.clear();
        webElement.sendKeys(textValue);
    }

    /**
     * Waits for the text element to be rendered, clear the element, and enters the value followed by enter.
     * @param textValue
     */
    public void enterValueWithEnter(String textValue) {
        waitForElementToBeVisible();
        enterValueOnClearField(textValue);
        webElement.sendKeys(Keys.ENTER);
    }


    /**
     * Waits for the text element to be rendered, clear the element, and enters the value followed by tab.
     * @param textValue
     */
    public void enterValueWithTab(String textValue) {
       //todo need a waitForRenderedElements but by the webElement (not by By.id)
        enterValueOnClearField(textValue);
        webElement.sendKeys(Keys.TAB);
    }

    /**
     * Waits for the drop down to be rendered and selects the element based off the text displayed
     * @param textValue
     */
    public void selectValueWithText(String textValue) {
        waitForElementToBeVisible();
        try {
            Select select = new Select(webElement);
            select.selectByVisibleText(textValue);
        }
        catch(Exception ex) {
            String errorMessage = String.format(
                    "The text '%s' was not found in the selectable element", textValue);
            throw new AssertionError(errorMessage);
        }

    }

    /**
     * Waits for the drop down to be rendered and selects the element based off the value
     * @param value
     */
    public void selectValueWithValue (String value) {
        waitForElementToBeVisible();
        try {
            Select select = new Select(webElement);
            select.selectByValue(value);
        }
        catch(Exception ex) {
            String errorMessage = String.format(
                    "The value '%s' was not found in the selectable element", value);
            throw new AssertionError(errorMessage);
        }
    }

    /**
    * Waits for the drop down to be rendered and selects the element based off the index
    * @param index
    */
    public void selectValueWithIndex(Integer index) {
        waitForElementToBeVisible();
        try {
            Select select = new Select(webElement);
            select.selectByIndex(index);
        }
        catch(Exception ex) {
            String errorMessage = String.format(
                    "The index '%s' was not found in the selectable element", index);
            throw new AssertionError(errorMessage);
        }

    }


    protected RenderedPageObjectView getRenderedView() {
        long waitForTimeout = 5;
        if (renderedView == null) {
            renderedView = new RenderedPageObjectView(driver, waitForTimeout);
        }
        return renderedView;
    }

    private void waitForElementToBeVisible()
    {
        getRenderedView().waitForRenderedElement(webElement);
    }
}
