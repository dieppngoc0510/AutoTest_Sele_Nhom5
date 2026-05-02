package org.example.tests;

import org.openqa.selenium.WebDriver;

public class Constant {
    public static ThreadLocal<WebDriver> WEBDRIVER = new ThreadLocal<>();
}
