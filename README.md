# SyarQ Ecommerce Scraper
Scraping product page of ecommerce web site to get its data, e.g. name, description, price, etc

# Installation
Maven: to be updated

# Usage

```java
// scrap product page
Scraper scraper = new BukalapakScraper();  
String url = "https://www.bukalapak.com/p/handphone/hp-smartphone/44j3iq-jual-iphone-6-16gb-gold-grey-garansi-1thn?from=old-popular-section-1";  
ScraperProduct p = scraper.scrap(url);  
assertEquals(url, p.getUrl());  
assertEquals("iphone 6 16GB GOLD  garansi 1THN", p.getName());  
assertEquals(3499000, p.getPrice(), 0.1);  
assertNotNull(p.getPhotoUrl());  
assertNotNull(p.getDescription());

// search product
Scraper scraper = new BukalapakScraper();  
String keyword = "notebook";  
List<ScraperProduct> products = scraper.search(keyword);  
assertFalse(products.isEmpty());
for(ScraperProduct product: products) {
	assertNotNull(p.getName());
}
```

