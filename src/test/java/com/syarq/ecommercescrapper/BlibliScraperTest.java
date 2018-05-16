package com.syarq.ecommercescrapper;

import com.syarq.ecommercescraper.BlibliScraper;
import com.syarq.ecommercescraper.Scraper;
import com.syarq.ecommercescraper.ScraperProduct;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

public class BlibliScraperTest {

    @Test
    public void scrapTest() {
        Scraper scraper = new BlibliScraper();
        String url = "https://www.blibli.com/p/ramadhan-fair-radysa-organizer-rak-sepatu-portable-abu-abu-6-susun/ps--RAO.12841.00666";
        ScraperProduct p = scraper.scrape(url);
        assertEquals(url, p.getUrl());
        assertEquals("Ramadhan Fair - Radysa Organizer Rak Sepatu Portable - Abu Abu [6 Susun]", p.getName());
        assertEquals(199000, p.getPrice(), 0.1);
        assertNotNull(p.getPhotoUrl());
        assertNotNull(p.getDescription());
    }

    @Test
    public void searchTest() {
        Scraper scraper = new BlibliScraper();
        String keyword = "notebook";
        List<ScraperProduct> products = scraper.search(keyword);
        assertFalse(products.isEmpty());
    }
}
