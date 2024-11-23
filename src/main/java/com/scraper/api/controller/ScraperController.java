package com.scraper.api.controller;

import com.scraper.api.model.ResponseDTO;
import com.scraper.api.service.ScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/")
public class ScraperController {

    private final ScraperService scraperService;

    @GetMapping(path = "/{search}")
    public List<ResponseDTO> searchBooks(@PathVariable String search,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(required = false) String language,
                                         @RequestParam(required = false) String token) {
        return scraperService.searchBooks(token, search, page, language);
    }
}
