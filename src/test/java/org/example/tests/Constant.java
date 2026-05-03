package org.example.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Constant {
    public static ThreadLocal<WebDriver> WEBDRIVER = new ThreadLocal<>();

    // ── Profile fields ────────────────────────────────────────────────────────
    public static final By FIELD_FULLNAME    = By.name("first_name");
    public static final By FIELD_EMAIL       = By.name("email");
    public static final By FIELD_PHONE       = By.name("phone");
    public static final By FIELD_BIRTHDATE   = By.name("birthdate");
    public static final By FIELD_ADDRESS     = By.name("address");
    public static final By RADIO_NU          = By.xpath(
            "//div[contains(@class,'s-radio-group')]//label[contains(.,'Nữ')]//input");
    public static final By RADIO_NAM         = By.xpath(
            "//div[contains(@class,'s-radio-group')]//label[contains(.,'Nam')]//input");
    public static final By INPUT_AVATAR      = By.cssSelector(
            "div.shopee-avatar-upload input[type='file']");
    public static final By BTN_CHOOSE_AVATAR = By.cssSelector("button.s-btn-upload");
    public static final By BTN_SAVE          = By.cssSelector(
            "form.shopee-form-section button[type='submit']");

    public static final String NEW_EMAIL = "thuha_test_new@gmail.com";

    // ── Change-password fields ────────────────────────────────────────────────
    public static final By FIELD_OLD_PW     = By.name("current_password");
    public static final By FIELD_NEW_PW     = By.name("new_password");
    public static final By FIELD_CONFIRM_PW = By.name("confirm_password");
    public static final By BTN_CHANGE_PW    = By.cssSelector("button.s-btn-save");

    // ── Default test account (Admin) ──────────────────────────────────────────
    public static final String TEST_USERNAME = "admin";
    public static final String TEST_PASSWORD  = "hani12345";

    // ── API-test account (regular user) ──────────────────────────────────────
    public static final String VALID_USERNAME = "ngocdiep";
    public static final String VALID_PASSWORD = "12345678";

    // ── API endpoints / page URLs ─────────────────────────────────────────────
    public static final String API_PROFILE    = "/api/user/profile/";
    public static final String API_CHANGE_PW  = "/api/user/change-password/";
    public static final String PROFILE_URL    = "http://127.0.0.1:8000/profile/";
    public static final String CHANGE_PW_URL  = "http://127.0.0.1:8000/change-password/";

    // ── Toast notification ────────────────────────────────────────────────────
    public static final By TOAST = By.cssSelector("#toast-container .toast");
}
