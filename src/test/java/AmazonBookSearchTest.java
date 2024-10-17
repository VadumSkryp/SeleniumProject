import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AmazonBookSearchTest {
    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        // Increase page load timeout
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    public void searchForJavaBook() throws InterruptedException {
        // Перейти на сайт Amazon
        driver.get("https://www.amazon.com/");

        // Вибрати фільтр Books у випадаючому меню
        WebElement searchDropdown = driver.findElement(By.id("searchDropdownBox"));
        searchDropdown.click();
        WebElement booksOption = driver.findElement(By.xpath("//option[@value='search-alias=stripbooks-intl-ship']"));
        booksOption.click();

        // Ввести пошукове слово "Java" і здійснити пошук
        WebElement searchBox = driver.findElement(By.id("twotabsearchtextbox"));
        searchBox.sendKeys("Java");
        WebElement searchButton = driver.findElement(By.id("nav-search-submit-button"));
        searchButton.click();

        // Затримка для завантаження результатів
        Thread.sleep(4000);

        // Отримати результати з першої сторінки
        List<WebElement> bookTitles = driver.findElements(By.xpath("//span[@class='a-size-medium a-color-base a-text-normal']"));
        List<WebElement> bookAuthors = driver.findElements(By.xpath("//a[@class='a-size-base a-link-normal s-underline-text s-underline-link-text s-link-style' and contains(@href, '/e/')]"));
        List<WebElement> bookPrices = driver.findElements(By.xpath("//span[@class='a-price']"));

        // Список для збереження інформації про книги
        List<String> bookList = new ArrayList<>();

        // Перевірити, чи є книга "Head First Java" у результатах
        boolean isBookFound = false;
        for (int i = 0; i < bookTitles.size(); i++) {
            String title = bookTitles.get(i).getText();
            String author = (i < bookAuthors.size()) ? bookAuthors.get(i).getText() : "N/A";
            String price = (i < bookPrices.size()) ? bookPrices.get(i).getText() : "N/A";
            String bestSellerTag = title.contains("Best Seller") ? "Yes" : "No";

            // Додати інформацію до списку
            bookList.add("Title: " + title + ", Author: " + author + ", Price: " + price + ", Best Seller: " + bestSellerTag);

            if (title.contains("Head First Java")) {
                isBookFound = true;
                // Перейти на сторінку книги
                bookTitles.get(i).click();
                break;
            }
        }

        // Переконатися, що книга знайдена
        assertTrue(isBookFound, "The book 'Head First Java' was not found in the search results.");

        // Вивести інформацію про книги
        for (String bookInfo : bookList) {
            System.out.println(bookInfo);
        }
    }

    @AfterEach
    public void tearDown() {
        // Закрити браузер
        if (driver != null) {
            driver.quit();
        }
    }
}
