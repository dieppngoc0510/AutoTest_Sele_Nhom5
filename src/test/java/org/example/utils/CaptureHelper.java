package org.example.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureHelper {
    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = screenshotName + "_" + timestamp + ".png";
            String directory = "target/screenshots/";
            
            File folder = new File(directory);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String destination = directory + fileName;
            FileHandler.copy(source, new File(destination));
            
            // Return relative path for report
            return "screenshots/" + fileName;
        } catch (IOException e) {
            System.err.println("Exception while taking screenshot: " + e.getMessage());
            return null;
        }
    }
}
