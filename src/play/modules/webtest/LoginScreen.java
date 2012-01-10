package play.modules.webtest;

import org.openqa.selenium.WebDriver;

public class LoginScreen extends Screen {

    public LoginScreen(WebDriver driver) {
        this(driver, null);
    }

    public LoginScreen(WebDriver driver, String title) {
        super(driver);
        open("/login");

        if (title != null) {
            assertEquals(title, driver.getTitle());
        }
    }

    public Screen login(String user, String password) {
        findByCss("#username").sendKeys(user);
        findByCss("#password").sendKeys(password);
        findByCss("#signin").click();

        assertElementIsHidden("#login .error");

        return this;
    }
    
    public Screen loginAndExpectError(String user, String password, String error) {
        findByCss("#username").sendKeys(user);
        findByCss("#password").sendKeys(password);
        findByCss("#signin").click();

        assertElementIsVisible("#login .error");

        if (error != null) {
            assertElementHasText("#login .error", error);
        }

        return this;
    }

}
