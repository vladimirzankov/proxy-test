import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class ProxyTest {

    private WebDriver driver;
    public BrowserMobProxy proxy;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void setupTest() {
        proxy = new BrowserMobProxyServer();
        proxy.start(0);
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        ChromeOptions options = new ChromeOptions();
        options.setCapability(CapabilityType.PROXY, seleniumProxy);
        driver = new ChromeDriver(options);
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void getTraffic() throws Exception {
        proxy.newHar();
        driver.get("https://otus.ru");
        Har har = proxy.endHar();
        try(PrintStream out = new PrintStream(new FileOutputStream(System.currentTimeMillis() + ".log"))) {
            har.getLog().getEntries().forEach(e -> out.println(e.getRequest().getUrl() + " " + e.getResponse().getStatus() + " " + e.getResponse().getStatusText()));
        }
    }
}
