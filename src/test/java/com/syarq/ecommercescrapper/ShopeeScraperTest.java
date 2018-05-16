package com.syarq.ecommercescrapper;

import com.syarq.ecommercescraper.Scraper;
import com.syarq.ecommercescraper.ScraperProduct;
import com.syarq.ecommercescraper.ShopeeScraper;

import java.util.List;

import static org.junit.Assert.*;

public class ShopeeScraperTest {

//    @Test
    public void scrapTest() {
        Scraper scraper = new ShopeeScraper();
        String url = "https://shopee.co.id/Buku-Pintar-Layar-Sentuh-muslim-3-bahasa-the-first-e-book-for-children-i.30670219.820045914";
        ScraperProduct p = scraper.scrape(url);
        assertEquals(url, p.getUrl());
        assertEquals("Buku Pintar Layar Sentuh muslim 3 bahasa / the first e book for children", p.getName());
        assertEquals( 124500, p.getPrice(),0.1);
        assertNotNull(p.getPhotoUrl());
        assertNotNull(p.getDescription());
    }

//    @Test
    public void searchTest() {
        Scraper scraper = new ShopeeScraper();
        String keyword = "xiaomi";
        List<ScraperProduct> products = scraper.search(keyword);
        assertFalse(products.isEmpty());
    }
}
