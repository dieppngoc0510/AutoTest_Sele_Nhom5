package org.example.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    private static ExtentReports extent = ExtentReportManager.getExtentReports();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test Passed");
        if (org.example.tests.Constant.WEBDRIVER.get() != null) {
            String path = org.example.utils.CaptureHelper.captureScreenshot(org.example.tests.Constant.WEBDRIVER.get(), result.getName());
            if (path != null) {
                test.get().addScreenCaptureFromPath(path);
            }
            org.example.tests.Constant.WEBDRIVER.get().quit();
            org.example.tests.Constant.WEBDRIVER.remove();
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL, "Test Failed");
        test.get().log(Status.FAIL, result.getThrowable());
        if (org.example.tests.Constant.WEBDRIVER.get() != null) {
            String path = org.example.utils.CaptureHelper.captureScreenshot(org.example.tests.Constant.WEBDRIVER.get(), result.getName());
            if (path != null) {
                test.get().addScreenCaptureFromPath(path);
            }
            org.example.tests.Constant.WEBDRIVER.get().quit();
            org.example.tests.Constant.WEBDRIVER.remove();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "Test Skipped");
        if (org.example.tests.Constant.WEBDRIVER.get() != null) {
            org.example.tests.Constant.WEBDRIVER.get().quit();
            org.example.tests.Constant.WEBDRIVER.remove();
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}
