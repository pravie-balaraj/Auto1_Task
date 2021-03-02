package testcase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchFunctionality {
	
	@Test
	public void searchTest() {
		int year = 2015;
		// Initializing the browser and navigating to the webpage
		System.setProperty("webdriver.chrome.driver", "D:\\selenium\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.navigate().to("https://www.autohero.com/de/search/");
		driver.manage().window().maximize();
		
		// Selecting the search filter for year and price descending
		driver.findElement(By.id("yearFilter")).click();
		WebElement selectYear = driver.findElement(By.xpath("//div[@aria-labelledby='yearFilter']/*/*/*/select[@id='rangeStart']"));
		selectYear.click();
		selectYear.sendKeys(Integer.toString(year));
		driver.findElement(By.xpath("//select[@id='sortBy']/option[@value='2']")).click();
		
		// Waiting for the search results to display
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		// Scrolling through the page and scrapping the results
		JavascriptExecutor js = (JavascriptExecutor) driver;
		long scrollHeight = 0;
		long newScrollHeight = 0;
		ArrayList<Float> priceList = new ArrayList<Float>();
		List<WebElement> elementList = null;
		do {
			scrollHeight = newScrollHeight;
			elementList = driver.findElements(By.xpath("//div[@data-qa-selector='price']"));
			List<WebElement> years = driver.findElements(By.xpath("//ul[@data-qa-selector='spec-list']/child::li[1]"));
			// Scrapping year and Verify all cars are filtered by first registration (2015+)
			for(WebElement we: years) {
				if (Integer.valueOf(we.getText().trim()) < year) {
					Assert.assertFalse(true, "Year not matching with the selected criteria");
					break;
				}			
			}
			// Scrolling through the page
			for(WebElement we: elementList) {
				String temp = we.getText();
				String[] tempArr = temp.split(" ");
				priceList.add(Float.valueOf(tempArr[0].trim()));
				newScrollHeight = new Long(we.getLocation().y + (long)js.executeScript("return arguments[0].parentNode.parentNode.parentNode.parentNode.offsetHeight;", we));
			}
			js.executeScript("window.scrollTo(0,arguments[0]);",newScrollHeight);
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);			
		} while(scrollHeight!=newScrollHeight);
		
		// Removing duplicate entries
		for(int i=0; i<elementList.size(); i++) {
			priceList.remove(priceList.size()-1);
		}
				
		// Verify all cars are sorted by price descending
		ArrayList<Float> sortedList = new ArrayList<Float>();
		for (Float s: priceList) {
			sortedList.add(s);
		}
		Collections.sort(sortedList);
		Collections.reverse(sortedList);
		Assert.assertEquals(priceList, sortedList);				
		
		driver.close();
	}
}
