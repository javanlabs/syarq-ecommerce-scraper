package com.syarq.ecommercescrapper;

import com.syarq.ecommercescraper.BukalapakScraper;
import com.syarq.ecommercescraper.Scraper;
import com.syarq.ecommercescraper.ScraperProduct;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class BukalapakScraperTest {

    @Test
    public void scrapTest() {
        Scraper scraper = new BukalapakScraper();
        String url = "https://www.bukalapak.com/p/handphone/hp-smartphone/44j3iq-jual-iphone-6-16gb-gold-grey-garansi-1thn?from=old-popular-section-1";
        ScraperProduct p = scraper.scrape(url);
        assertEquals(url, p.getUrl());
        assertEquals("iphone 6 16GB GOLD  garansi 1THN", p.getName());
        assertEquals(3499000, p.getPrice(), 0.1);
        assertNotNull(p.getPhotoUrl());
        assertNotNull(p.getDescription());
    }

    @Test
    public void searchTest() {
        Scraper scraper = new BukalapakScraper();
        String keyword = "notebook";
        List<ScraperProduct> products = scraper.search(keyword);
        assertFalse(products.isEmpty());
    }
}
