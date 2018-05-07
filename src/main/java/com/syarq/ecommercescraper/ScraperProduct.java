package com.syarq.ecommercescraper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ScraperProduct {
    private String url;
    private String name;
    private String description;
    private Double price;
    private String photoUrl;
}
