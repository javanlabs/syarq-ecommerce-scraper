package com.syarq.ecommercescraper;

import java.util.List;

public interface Scraper {
    ScraperProduct scrape(String url);
    List<ScraperProduct> search(String keyword);
    List<ScraperProduct> search(String keyword, int limit);
    boolean shouldScrape(String host);
}
