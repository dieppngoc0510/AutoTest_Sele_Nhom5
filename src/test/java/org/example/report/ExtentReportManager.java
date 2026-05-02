package org.example.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager {
    private static ExtentReports extent;

    public static ExtentReports getExtentReports() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("target/ExtentReport.html");
            
            String customCss = ".report-name { font-family: 'Montserrat', sans-serif !important; font-weight: 800 !important; }" +
                               ".nav-logo { font-family: 'Montserrat', sans-serif !important; }" +
                               "body, td, th { font-family: 'Montserrat', sans-serif !important; }" +
                               ".card { border-radius: 15px !important; border: none !important; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1) !important; }" +
                               ".status.pass { background-color: #d1fae5 !important; color: #065f46 !important; }" +
                               ".status.fail { background-color: #fee2e2 !important; color: #991b1b !important; }";

            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle("Hani Shop Automation Report");
            spark.config().setReportName("HOLOGRAM TEST RESULTS");
            spark.config().setCss(customCss);
            spark.config().setJs("document.head.innerHTML += '<link href=\"https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700;800&display=swap\" rel=\"stylesheet\">';");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Project", "Hani Shop");
            extent.setSystemInfo("Tester", "Antigravity");
            extent.setSystemInfo("OS", "Windows");
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        }
        return extent;
    }
}
