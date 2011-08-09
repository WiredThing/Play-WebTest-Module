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

    protected void assertEquals(String expected, String actual) {
        Assert.assertEquals("", expected, actual);
    }

    protected void open(String url) {
        driver.get("http://localhost:" + WebTest.httpPort + url);
    }

    protected void waitForAWhile() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void assertElementDoesNotHaveText(String cssSelector, String text) {
        try {
            String elementText = findByCss(cssSelector).getText();
            Assert.assertFalse("Element '" + cssSelector + "' has text '" + elementText + "' but expected it to not have text '"
                    + text + "'", elementText.equals(text));
        } catch (NoSuchElementException e) {
            // OK, if it's not present then it doesn't have the text
        }
    }

    protected void assertElementHasText(String cssSelector, String text) {
        String elementText = findByCss(cssSelector).getText();
        Assert.assertTrue("Element '" + cssSelector + "' has text '" + elementText + "' but expected it to have text '" + text
                + "'", elementText.equals(text));
    }

    protected void assertStartsWith(String cssSelector, String text) {
        String elementText = findByCss(cssSelector).getText();
        Assert.assertTrue("Expected '" + cssSelector + "' to start with '" + text + "', but had text '" + elementText + "'",
                elementText.startsWith(text));
    }

    protected void assertContainsText(String cssSelector, String message) {
        String elementText = findByCss(cssSelector).getText();
        Assert.assertTrue(elementText.contains(message));
    }

    protected WebElement findByCss(String cssSelector) {
        return driver.findElement(By.cssSelector(cssSelector));
    }
}
