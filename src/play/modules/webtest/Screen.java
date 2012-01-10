package play.modules.webtest;

import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class Screen {

    protected WebDriver driver;

    public Screen(WebDriver driver) {
        this.driver = driver;
    }

    protected void assertTitle(String title) {
        assertEquals(title, driver.getTitle());
    }
    
    protected void assertEquals(String expected, String actual) {
        Assert.assertEquals("", expected, actual);
    }

    protected void open(String url) {
        driver.get("http://localhost:" + WebTest.httpPort + url);
    }

    protected void waitForALittleBit() {
        waitFor(500);
    }

    protected void waitForAWhile() {
        waitFor(2000);
    }

    protected void waitFor(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    protected void assertElementDoesNotHaveText(String cssSelector, String text) {
        try {
            String elementText = findByCss(cssSelector).getText();
            Assert.assertFalse("Element '" + cssSelector + "' has text '" + elementText + "' but expected it to not have text '" + text + "'", elementText.equals(text));
        } catch (NoSuchElementException e) {
            // OK, if it's not present then it doesn't have the text
        }
    }

    protected void assertElementHasText(String cssSelector, String text) {
        String elementText = findByCss(cssSelector).getText();
        Assert.assertTrue("Element '" + cssSelector + "' has text '" + elementText + "' but expected it to have text '" + text + "'", elementText.equals(text));
    }

    protected void assertElementStartsWith(String cssSelector, String text) {
        String elementText = findByCss(cssSelector).getText();
        Assert.assertTrue("Expected '" + cssSelector + "' to start with '" + text + "', but had text '" + elementText + "'", elementText.startsWith(text));
    }

    protected void assertElementContainsText(String cssSelector, String message) {
        String elementText = findByCss(cssSelector).getText();
        Assert.assertTrue("Expected '" + cssSelector + "' to contain '" + message + "', but had text '" + elementText + "'", elementText.contains(message));
    }

    protected void assertElementDoesNotContainText(String cssSelector, String message) {
        try {
            String elementText = findByCss(cssSelector).getText();
            Assert.assertFalse("Expected '" + cssSelector + "' not to contain '" + message + "', but had text '" + elementText + "'", elementText.contains(message));
        } catch (NoSuchElementException e) {
            // OK, if it's not present then it doesn't have the text
        }
    }
    
    protected void assertElementHasClass(String cssSelector, String className) {
        String classes = findByCss(cssSelector).getAttribute("class");
        Assert.assertTrue("Expected '" + cssSelector + "' to have class '" + className + "', but had classes '" + classes + "'", classes.contains(className));
    }

    protected void assertElementDoesNotHaveClass(String cssSelector, String className) {
        try {
            String classes = findByCss(cssSelector).getAttribute("class");
            Assert.assertFalse("Expected '" + cssSelector + "' not to have class '" + className + "', but had classes '" + classes + "'", classes.contains(className));
        } catch (NoSuchElementException e) {
            // OK, if it's not present then it doesn't have the text
        }
    }

    protected void assertElementIsVisible(String cssSelector) {
        Assert.assertTrue("Expected '" + cssSelector + "' to be visible, but was hidden", findByCss(cssSelector).isDisplayed());
    }

    protected void assertElementIsHidden(String cssSelector) {
        try {
            Assert.assertFalse("Expected '" + cssSelector + "' to be hidden, but was visible", findByCss(cssSelector).isDisplayed());
        } catch (NoSuchElementException e) {
            // OK, if it's not present then it doesn't have the text
        }
    }

    protected void assertElementIsEnabled(String cssSelector) {
        Assert.assertTrue("Expected '" + cssSelector + "' to be enabled, but was disabled", findByCss(cssSelector).isEnabled());
    }

    protected void assertElementIsDisabled(String cssSelector) {
        try {
            Assert.assertFalse("Expected '" + cssSelector + "' to be disabled, but was enabled", findByCss(cssSelector).isEnabled());
        } catch (NoSuchElementException e) {
            // OK, if it's not present then it doesn't have the text
        }
    }

    protected void assertElementIsSelected(String cssSelector) {
        Assert.assertTrue("Expected '" + cssSelector + "' to be selected, but was not", findByCss(cssSelector).isSelected());
    }

    protected void assertElementIsNotSelected(String cssSelector) {
        try {
            Assert.assertFalse("Expected '" + cssSelector + "' to be unselected, but was selected", findByCss(cssSelector).isSelected());
        } catch (NoSuchElementException e) {
            // OK, if it's not present then it doesn't have the text
        }
    }

    protected boolean isVisible(String cssSelector) {
        WebElement element = findByCss(cssSelector);
        String display = element.getCssValue("display");
        String opacity = element.getCssValue("opacity");
        return "hidden".equalsIgnoreCase(display) || "none".equalsIgnoreCase(display) || "0".equals(opacity);
    }

    protected WebElement findByCss(String cssSelector) {
        return driver.findElement(By.cssSelector(cssSelector));
    }
}
