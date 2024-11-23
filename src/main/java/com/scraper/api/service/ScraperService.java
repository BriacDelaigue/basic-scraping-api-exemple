package com.scraper.api.service;

import com.scraper.api.model.ResponseDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScraperService {

    @Value("${website.url}")
    String url;

    @Value("${api.token}")
    String token;

    public List<ResponseDTO> searchBooks(String token, String search, int page, String language) {
        if (!this.token.isEmpty() && !this.token.equals(token)) {
            throw new RuntimeException("Bad login");
        }

        List<String> urls = new ArrayList<>();
        try {
            String urlWithParam = url + "/s/" + search + "?page="+page;
            if(language != null && !language.isEmpty()) {
                urlWithParam = urlWithParam + "&languages%5B%5D="+language;
            }
            Document document = Jsoup.connect(urlWithParam)
                    .userAgent("Mozilla")
                    .get();
            Element element = document.getElementById("searchResultBox");
            Elements elements = element.getElementsByTag("bookcard");

            for (Element ads: elements) {
                if (!ads.getElementsByTag("bookcard").isEmpty()) {
                    urls.add(ads.getElementsByTag("bookcard").attr("href"));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return getBooksDetail(urls);
    }

    private List<ResponseDTO> getBooksDetail(List<String> urls) {
        List<ResponseDTO> responses = new ArrayList<>();
        for (String bookUrl: urls) {
            try {
                Document document = Jsoup.connect(this.url + bookUrl).userAgent("Mozilla").get();
                Elements cardBooks = document.getElementsByClass("cardBooks");

                if (cardBooks.get(0) != null) {
                    ResponseDTO response = new ResponseDTO();

                    //Image..
                    Elements bookCovers = cardBooks.get(0).getElementsByClass("details-book-cover-container");
                    if(bookCovers.get(0) != null) {
                        response.setImageUrl(bookCovers.get(0).getElementsByTag("img").attr("data-src"));
                    }

                    //Title..
                    Elements titleElement = cardBooks.get(0).getElementsByTag("h1");
                    if(titleElement.get(0) != null) {
                        response.setTitle(titleElement.get(0).text());
                    }

                    //Author..
                    Elements authorElement = cardBooks.get(0).getElementsByTag("i");
                    if(authorElement.get(0) != null) {
                        response.setAuthor(authorElement.get(0).getElementsByTag("a").get(0).text());
                    }

                    //Description..
                    Element descriptionElement = cardBooks.get(0).getElementById("bookDescriptionBox");
                    if(descriptionElement != null) {
                        response.setDescription(descriptionElement.text());
                    }

                    //ISBNS..
                    Elements isbns = cardBooks.get(0).getElementsByClass("property_isbn");
                    for (Element isbn : isbns) {
                        //ISBN 10..
                        if (!isbn.getElementsByClass("10").isEmpty()) {
                            response.setIsbn10(isbn.getElementsByClass("10").get(0).getElementsByClass("property_value").get(0).text());
                        }

                        //ISBN 13..
                        if (!isbn.getElementsByClass("13").isEmpty()) {
                            response.setIsbn13(isbn.getElementsByClass("13").get(0).getElementsByClass("property_value").get(0).text());
                        }
                    }
                    responses.add(response);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return responses;
    }

}
