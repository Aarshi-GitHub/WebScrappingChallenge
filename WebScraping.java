import org.openqa.selenium.*;
import org.apache.poi.*;
import java.io.*;
import java.util.*;

class WebScraping{

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); 
        
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, 10);

        try {
            driver.get("https://rategain.com/blog");
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("BlogData");
            int rowIdx = 0;
            while (true) {
                List<WebElement> blogPosts = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("blog-post")));
                
                for (WebElement post : blogPosts) {
                    String title = post.findElement(By.className("blog-title")).getText();
                    String date = post.findElement(By.className("blog-date")).getText();
                    String imageUrl = post.findElement(By.tagName("img")).getAttribute("src");
                    String likesCount = post.findElement(By.className("like-count")).getText();

                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(title);
                    row.createCell(1).setCellValue(date);
                    row.createCell(2).setCellValue(imageUrl);
                    row.createCell(3).setCellValue(likesCount);
                }

                WebElement nextPageButton = driver.findElement(By.className("next"));
                if (nextPageButton.isEnabled()) {
                    nextPageButton.click();
                } else {
                    break;
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream("blog_data.xlsx")) {
                workbook.write(fileOut);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
