package com.syarq.ecommercescraper;

import java.util.List;

public interface Scraper {
    ScraperProduct scrap(String url);
    List<ScraperProduct> search(String keyword);
    List<ScraperProduct> search(String keyword, int limit);
}
