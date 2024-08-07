package controllers;

import io.github.bonigarcia.wdm.WebDriverManager;
import models.Auto;
import models.Contact;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pojos.Param;
import services.DB;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

public class AdCrawlScraper {
    private static WebDriver driver;
    private static WebDriverWait wt;
    public static void connect() {
        quit();

        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        driver = new FirefoxDriver(options);

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));

        driver.get("https://jiji.ng/");

        // Javascript Executor to check page ready state
        JavascriptExecutor j = (JavascriptExecutor)driver;
        if (j.executeScript
                ("return document.readyState").toString().equals("complete")){
            System.out.println("Page loaded properly.");
        }
        //expected condition presenceOfElementLocated
        wt = new WebDriverWait(driver, Duration.ofSeconds(2));

        wt.until(ExpectedConditions
                .presenceOfElementLocated
                        (By.partialLinkText("Sign")));

        System.out.println("Login link found.");

        WebElement loginLink = driver.findElement(By.partialLinkText("Sign"));
        loginLink.click();

        System.out.println("Login link clicked.");

        WebElement loadLoginForm = driver.findElement(By.cssSelector(".fw-popup__body button.qa-fw-button"));
        loadLoginForm.click();

        wt.until(ExpectedConditions
                .presenceOfElementLocated
                        (By.cssSelector(".qa-login-field")));

        System.out.println("Login page loaded.");

        WebElement usernameField = driver.findElement(By.cssSelector(".qa-login-field"));
        WebElement passwordField = driver.findElement(By.cssSelector(".qa-password-field"));
        WebElement loginButton = driver.findElement(By.cssSelector(".qa-login-submit"));

        usernameField.sendKeys("cloudytic@gmail.com");
        passwordField.sendKeys("Sup3r@dm1n");
        loginButton.click();
    }

    private static void start(Auto auto) throws Exception {
        Thread.sleep(1000);

        driver.navigate().to(auto.crawlHref);
        System.out.println("Page loading...");

        Contact contact = auto.contact;

        if(driver.getPageSource().toLowerCase().contains("404 - oops!")) {
            System.out.println("404 - oops!");
            contact.notFound = true;
            DB.merge(contact);
            return;
        } else {
            System.out.println("Page loaded.");
        }

        wt.until(ExpectedConditions
                .presenceOfElementLocated
                        (By.cssSelector("div.b-show-contact div.b-button__text")));

        WebElement showContact = driver.findElement(By.cssSelector(".b-show-contact div.b-button__text"));
        showContact.click();

        System.out.println("Contact button clicked.");

        wt.until(ExpectedConditions
                .presenceOfElementLocated
                        (By.cssSelector(".b-show-contacts-popover-item__phone")));

        System.out.println("Contact loaded.");

        WebElement contactElem = driver.findElement(By.cssSelector(".b-show-contacts-popover-item__phone"));
        String contactText = contactElem.getText().trim();
        System.out.println("Contact: " + contactText);
        contact.phone = contactText;

        DB.merge(contact);

        AdAutoCrud.save(auto);
    }

    public static void quit() {
        if(driver != null) {
            try {
                driver.close();
                driver.quit();
            } catch (Exception e) {}
        }
    }

    public static void scrape(int pages, Param param) {
        for(int i = 0; i < pages; i++) {
            param.setPage(i);
            List<Auto> autos = DB.find(Auto.class, DB.where()
                            .field("contact.phone").isNull()
                            .field("contact.notFound").eq(false),
                    param
            );
            if(autos.isEmpty()) {
                System.out.println("No more autos to scrape.");
                break;
            }
            for(Auto auto : autos) {
                System.out.println("Scraping: " + auto.crawlHref);
                try {
                    start(auto);
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                } catch (HttpTimeoutException e) {
                    connect();
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}