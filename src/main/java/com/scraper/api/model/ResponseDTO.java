package com.scraper.api.model;

import lombok.Data;

@Data
public class ResponseDTO {

    String title;
    String imageUrl;
    String author;
    String description;
    String isbn10;
    String isbn13;
}
