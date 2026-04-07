package br.com.locadora.selenium;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmeCrudSeleniumTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String DEFAULT_BASE_URL = "http://localhost:5173";
    private static final String DEFAULT_API_BASE_URL = "http://localhost:8080";
    private static final String BASE_URL = resolveBaseUrl();
    private static final String API_BASE_URL = resolveApiBaseUrl();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeAll
    static void setUp() throws Exception {
        resetCatalog();
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        String chromeBinary = resolveChromeBinary();
        if (chromeBinary != null) {
            options.setBinary(chromeBinary);
        }
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (driver != null) {
            driver.quit();
        }
        resetCatalog();
    }

    private static String resolveBaseUrl() {
        String systemProperty = System.getProperty("app.base-url");
        if (systemProperty != null && !systemProperty.isBlank()) {
            return systemProperty;
        }

        String envValue = System.getenv("APP_BASE_URL");
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return DEFAULT_BASE_URL;
    }

    private static String resolveChromeBinary() {
        String envValue = System.getenv("CHROME_BIN");
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        String[] knownPaths = {
                "/usr/bin/google-chrome",
                "/usr/bin/google-chrome-stable",
                "/usr/bin/chromium",
                "/usr/bin/chromium-browser"
        };

        for (String candidate : knownPaths) {
            if (Files.isExecutable(Path.of(candidate))) {
                return candidate;
            }
        }

        return null;
    }

    private static String resolveApiBaseUrl() {
        String systemProperty = System.getProperty("app.api-base-url");
        if (systemProperty != null && !systemProperty.isBlank()) {
            return systemProperty;
        }

        String envValue = System.getenv("APP_API_BASE_URL");
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return DEFAULT_API_BASE_URL;
    }

    private static void resetCatalog() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(apiUri("/api/filmes"))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Falha ao listar filmes para resetar o catálogo.");

        JsonNode filmes = OBJECT_MAPPER.readTree(response.body());
        for (JsonNode filme : filmes) {
            long id = filme.path("id").asLong(-1);
            if (id < 0) {
                continue;
            }

            HttpRequest deleteRequest = HttpRequest.newBuilder(apiUri("/api/filmes/" + id))
                    .DELETE()
                    .build();
            HttpResponse<Void> deleteResponse = HTTP_CLIENT.send(deleteRequest, HttpResponse.BodyHandlers.discarding());
            assertEquals(204, deleteResponse.statusCode(), "Falha ao excluir filme " + id + " durante reset.");
        }
    }

    private static URI apiUri(String path) {
        return URI.create(API_BASE_URL).resolve(path);
    }

    @Test
    @Order(1)
    void pageLoadsWithTitle() {
        driver.get(BASE_URL);
        WebElement title = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='app-title']")));
        assertEquals("Locadora de Filmes", title.getText());
    }

    @Test
    @Order(2)
    void tableShowsEmptyMessage() {
        driver.get(BASE_URL);
        WebElement empty = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='empty-message']")));
        assertTrue(empty.getText().contains("Nenhum filme cadastrado"));
    }

    @Test
    @Order(3)
    void tableHasCorrectColumns() {
        driver.get(BASE_URL);
        WebElement table = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='filme-table']")));
        List<WebElement> headers = table.findElements(By.tagName("th"));
        assertEquals(7, headers.size());
        assertEquals("ID", headers.get(0).getText());
        assertEquals("Título", headers.get(1).getText());
        assertEquals("Diretor", headers.get(2).getText());
        assertEquals("Ano", headers.get(3).getText());
        assertEquals("Gênero", headers.get(4).getText());
        assertEquals("Disponível", headers.get(5).getText());
        assertEquals("Ações", headers.get(6).getText());
    }

    @Test
    @Order(4)
    void validationErrorsOnEmptySubmit() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='btn-submit']")));
        driver.findElement(By.cssSelector("[data-testid='btn-submit']")).click();

        WebElement errorTitulo = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='error-titulo']")));
        assertTrue(errorTitulo.isDisplayed());

        WebElement errorDiretor = driver.findElement(By.cssSelector("[data-testid='error-diretor']"));
        assertTrue(errorDiretor.isDisplayed());
    }

    @Test
    @Order(5)
    void createFilm() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='input-titulo']")));

        driver.findElement(By.cssSelector("[data-testid='input-titulo']")).sendKeys("Matrix");
        driver.findElement(By.cssSelector("[data-testid='input-diretor']")).sendKeys("Wachowski");
        driver.findElement(By.cssSelector("[data-testid='input-ano']")).sendKeys("1999");
        driver.findElement(By.cssSelector("[data-testid='input-genero']")).sendKeys("Sci-Fi");
        driver.findElement(By.cssSelector("[data-testid='btn-submit']")).click();

        WebElement alert = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='alert-text']")));
        assertTrue(alert.getText().contains("cadastrado com sucesso"));

        WebElement titulo = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='filme-titulo']")));
        assertEquals("Matrix", titulo.getText());
    }

    @Test
    @Order(6)
    void editFilm() {
        driver.get(BASE_URL);
        WebElement editBtn = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='btn-edit']")));
        editBtn.click();

        WebElement tituloInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='input-titulo']")));

        tituloInput.clear();
        tituloInput.sendKeys("Matrix Reloaded");

        driver.findElement(By.cssSelector("[data-testid='btn-submit']")).click();

        WebElement alert = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='alert-text']")));
        assertTrue(alert.getText().contains("atualizado com sucesso"));
    }

    @Test
    @Order(7)
    void cancelDeleteKeepsFilm() {
        driver.get(BASE_URL);
        WebElement deleteBtn = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='btn-delete']")));
        deleteBtn.click();

        WebElement dialog = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='confirm-dialog']")));
        assertTrue(dialog.isDisplayed());

        driver.findElement(By.cssSelector("[data-testid='confirm-cancel']")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[data-testid='confirm-dialog']")));

        WebElement titulo = driver.findElement(By.cssSelector("[data-testid='filme-titulo']"));
        assertNotNull(titulo);
    }

    @Test
    @Order(8)
    void deleteFilmWithConfirmation() {
        driver.get(BASE_URL);
        WebElement deleteBtn = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='btn-delete']")));
        deleteBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='confirm-dialog']")));
        driver.findElement(By.cssSelector("[data-testid='confirm-ok']")).click();

        WebElement alert = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='alert-text']")));
        assertTrue(alert.getText().contains("excluído com sucesso"));

        WebElement empty = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='empty-message']")));
        assertTrue(empty.getText().contains("Nenhum filme cadastrado"));
    }
}
