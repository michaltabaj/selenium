package demoForMedius;

import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class demo {
	public static void main(String[] args) {
		
	        WebDriver driver = new FirefoxDriver();
	        
	        // Testcase variables
	        String url = "http://demo.opencart.com";
	        String desiredCurrency = "GBP"; // GBP, EUR or USD
	        String searchedProduct = "iPod"; // This TC is flexible, so it can be also tested with other keywords e.g. "macbook" with other product specifications' format
	        
	        // STEP 1 - Open browser and url
	        System.out.println("INFO: STEP 1 -> Opening "+url);
	        driver.get(url);
	        
	        // STEP 2 - Change currency
	        System.out.println("INFO: STEP 2 -> Changing currency to "+desiredCurrency);
	        WebElement currency = driver.findElement(By.xpath("//form[@id='currency']/div/button"));
	        currency.click();        
	        WebElement chosenCurrency = driver.findElement(By.name(desiredCurrency));
	        chosenCurrency.click();
	        
	        // STEP 3 - Search for product
	        System.out.println("INFO: STEP 3 -> Searching for "+searchedProduct);
	        WebElement searchInput = driver.findElement(By.name("search"));
	        searchInput.clear();
	        searchInput.sendKeys(searchedProduct);
	        WebElement searchButton = driver.findElement(By.xpath("//div[@id='search']/span/button"));
	        searchButton.click();
	        
	        // STEP 4 - Add all products in search results to product comparison
	        List<WebElement> searchResult = driver.findElements(By.xpath("//button[@data-original-title='Compare this Product']"));
	        System.out.println("INFO: STEP 4 -> There are "+searchResult.size()+" product(s) added to comparison");
	        // Check if there are any products found
	        if(searchResult.size()==0){
	        	// There's nothing to compare, so we end this TC
	        	System.out.println("ERROR: There are no products to be compared, please refine your search!");
	        }
	        else{
	        	// There are some products found, so add all of them to comparison
	        	for (WebElement e : searchResult) {
	                	e.click();
            		}
        	
	        	// STEP 5 - Go to comparison page
	        	System.out.println("INFO: STEP 5 -> Going to comparison page");
	        	WebElement comparisonLink = driver.findElement(By.id("compare-total"));
	        	comparisonLink.click();
	        	
	        	// STEP 6 - Remove all 'Out Of Stock' products from comparison
	        	System.out.println("INFO: STEP 6 -> Removing 'Out Of Stock' products");
	        	int column = 0;
	        	int removed = 0;
	        	// Start from 2nd column in a comparison table (the 1st column doesn't contain any products - just description of rows)
		        for (column = 2; column < searchResult.size()+2-removed; column++){ 
		        	String availabilityPath = "//table/tbody[1]/tr[6]/td["+column+"]";
		        	WebElement availability = driver.findElement(By.xpath(availabilityPath));
		        	// Check availability
		        	if(availability.getText().equals("Out Of Stock")){
		        		System.out.println("INFO: Removing the product from column: "+column);
		        		// As the location of Remove button may differ as it depends on product's specification (can be e.g. in tbody[2] or tbody[4]), 
		        		// let's search for product ID in the 1st row of table
		        		String productIdPath = "//table/tbody[1]/tr[1]/td["+column+"]/a";
		        		WebElement productId = driver.findElement(By.xpath(productIdPath));
		        		// Product ID is always right after the string "product_id" in href attribute
		        		int idPosition = productId.getAttribute("href").indexOf("product_id");
		        		// Here we have extracted product ID
		        		String id = productId.getAttribute("href").substring(idPosition+11);
		        		// Search for Remove button with this specific link with product ID
		        		WebElement removeLink = driver.findElement(By.cssSelector("a[href=\"http://demo.opencart.com/index.php?route=product/compare&remove="+id+"\"]"));
		        		// Remove the 'Out Of Stock' product
		        		removeLink.click();
		        		removed++;
		        		// Start again from the current column as it was removed and may contain another 'Out Of Stock' product
		        		column--;
		        	}
		        }
	        	
        		System.out.println("INFO: "+removed+" 'Out Of Stock' product(s) of all "+ searchResult.size() + " product(s) removed from comparison"); 
        	
	        	// Check if there is anything left in the comparison
	        	if (removed == searchResult.size()){
	        		// There's nothing to be added to shopping cart, so we end this TC
	            		System.out.println("ERROR: There are no products available for adding to the shopping cart, please refine your search!");
	        	}
	        	else{
	        		// STEP 7 - Adding random available product to shopping cart
	            		System.out.println("INFO: STEP 7 -> Adding random available product to shopping cart");
				Random generator = new Random(); 
				int random = generator.nextInt(searchResult.size()-removed) + 2;
				System.out.println("INFO: Adding the product to shopping cart from random column: "+random);
				// Store the price for random product
				String expectedPricePath = "//table/tbody[1]/tr[3]/td["+random+"]";
				WebElement expectedPrice = driver.findElement(By.xpath(expectedPricePath));
				String expectedPriceValue = expectedPrice.getText();
				System.out.println("INFO: Expected price for random product is: "+expectedPriceValue);
				// As the location of Add to Cart button may differ as it depends on product's specification (can be e.g. in tbody[2] or tbody[4]), 
        			// let's search for product ID in the 1st row of table
				String randomProductIdPath = "//table/tbody[1]/tr[1]/td["+random+"]/a";
        			WebElement randomProductId = driver.findElement(By.xpath(randomProductIdPath));
        			// Product ID is always right after the string "product_id" in href attribute
        			int randomIdPosition = randomProductId.getAttribute("href").indexOf("product_id");
        			// Here we have extracted product ID
	        		String randomId = randomProductId.getAttribute("href").substring(randomIdPosition+11);
	        		// Search for Add to Cart button with this specific link with product ID
	        		WebElement addButton = driver.findElement(By.cssSelector("input[onclick=\"cart.add('"+randomId+"');\"]"));
	        		// Add random product to a cart
	        		addButton.click();
	        		        		
	        		// STEP 8 - Going to shopping cart
	        		System.out.println("INFO: STEP 8 -> Going to shopping cart");
	        		WebElement cartLink = driver.findElement(By.cssSelector("a[title=\"Shopping Cart\"]"));
	        		cartLink.click();
	        		// Verify the total price
				String totalPricePath = "//table/tbody/tr/td[6]";
				WebElement totalPrice = driver.findElement(By.xpath(totalPricePath));
				String totalPriceValue = totalPrice.getText();
				System.out.println("INFO: Total price for product in the shopping cart is: "+totalPriceValue);
        		
        			if(expectedPriceValue.equals(totalPriceValue)){
        				System.out.println("RESULT: TEST PASSED -> Price from comparison page and total price in shopping cart are the same.");
        			}
        			else{
        				System.out.println("RESULT: TEST FAILED -> Price from comparison page and total price in shopping cart are different.");
        			}
        		}
        	}
        
        	driver.close();
        	System.exit(0);
           
    	}
 
}	
