package tests;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

public class SeleniumSprintChallenge {

    WebDriver driver;

    @BeforeMethod
    public void setup() {
        System.out.println("Launching browser...");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://demoqa.com/");
        
     // 🚫 Hide sticky ad & iframe ads
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById('fixedban').style.display='none';");
        js.executeScript("document.querySelectorAll('iframe').forEach(e => e.remove());");
    }
    // Task 1: Forms and Basic Element Interaction
    @Test
    public void fillFormAndRadioButtonTest() {
    	// For Elements card on home page:
    	driver.findElement(By.xpath("//h5[text()='Elements']")).click();

    	// For "Text Box" option in side panel:
    	driver.findElement(By.xpath("//span[text()='Text Box']")).click();

        driver.findElement(By.id("userName")).sendKeys("John Doe");
        driver.findElement(By.id("userEmail")).sendKeys("john@example.com");
        driver.findElement(By.id("currentAddress")).sendKeys("123 Main Street");
        driver.findElement(By.id("permanentAddress")).sendKeys("456 Secondary Street");
        driver.findElement(By.id("submit")).click();

        driver.findElement(By.xpath("//span[text()='Radio Button']")).click();
        driver.findElement(By.xpath("//label[text()='Impressive']")).click();
        WebElement result = driver.findElement(By.className("text-success"));
        Assert.assertEquals(result.getText(), "Impressive");
    }

    // Task 2: Dropdowns and Calendar Handling
    @Test
    public void handleDropdownsAndCalendar() {
        driver.findElement(By.xpath("//h5[text()='Widgets']")).click();
        driver.findElement(By.xpath("//span[text()='Select Menu']")).click();

        WebElement dropdown = driver.findElement(By.id("oldSelectMenu"));
        new Select(dropdown).selectByVisibleText("Dr.");

        driver.findElement(By.xpath("//div[contains(text(),'Select...')]")).click();
        driver.findElement(By.xpath("//div[text()='Group 1, option 1']")).click();

        driver.findElement(By.xpath("//span[text()='Date Picker']")).click();
        WebElement dateInput = driver.findElement(By.id("datePickerMonthYearInput"));
        dateInput.sendKeys(Keys.chord(Keys.CONTROL, "a")); // select all
        dateInput.sendKeys("10/15/2025");
        dateInput.sendKeys(Keys.ENTER);
    }

    // Task 3: Alerts, Windows, and IFrames
    @Test
    public void handleWindowsAlertsIframes() {
        driver.findElement(By.xpath("//h5[text()='Alerts, Frame & Windows']")).click();

        // New Tab
        driver.findElement(By.xpath("//span[text()='Browser Windows']")).click();
        driver.findElement(By.id("tabButton")).click();
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));
        Assert.assertTrue(driver.findElement(By.id("sampleHeading")).getText().contains("sample page"));
        driver.close();
        driver.switchTo().window(tabs.get(0));

        // Alerts
        driver.findElement(By.xpath("//span[text()='Alerts']")).click();
        driver.findElement(By.id("alertButton")).click();
        driver.switchTo().alert().accept();

        driver.findElement(By.id("confirmButton")).click();
        driver.switchTo().alert().dismiss();
        Assert.assertTrue(driver.getPageSource().contains("You selected Cancel"));

        // IFrame
        driver.findElement(By.xpath("//span[text()='Frames']")).click();
        driver.switchTo().frame("frame1");
        String frameText = driver.findElement(By.id("sampleHeading")).getText();
        Assert.assertEquals(frameText, "This is a sample page");
        driver.switchTo().defaultContent();
    }

    // Task 4: JavaScript Executor and Screenshot
    @Test
    public void useJavaScriptExecutorTest() {
        driver.findElement(By.xpath("//h5[text()='Elements']")).click();
        driver.findElement(By.xpath("//span[text()='Buttons']")).click();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement button = driver.findElement(By.xpath("//button[text()='Click Me']"));
        js.executeScript("arguments[0].scrollIntoView(true);", button);
        js.executeScript("arguments[0].click();", button);

        WebElement msg = driver.findElement(By.id("dynamicClickMessage"));
        Assert.assertTrue(msg.getText().contains("You have done a dynamic click"));
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            try {
                File dest = new File("screenshots/" + result.getName() + ".png");
                FileUtils.copyFile(src, dest);
                System.out.println("Screenshot saved: " + dest.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        driver.quit();
    }
}

