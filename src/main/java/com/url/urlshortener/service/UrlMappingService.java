package com.url.urlshortener.service;

import com.url.urlshortener.dtos.UrlMappingDTO;
import com.url.urlshortener.models.UrlMapping;
import com.url.urlshortener.models.User;
import com.url.urlshortener.respository.UrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UrlMappingService {

    UrlMappingRepository urlMappingRepository;

    public UrlMappingDTO createShortUrl(String originalUrl, User user) {
        String shortUrl = generateShortUrl();
        UrlMapping urlMapping = new UrlMapping();

        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setUser(user);
        urlMapping.setCreated(LocalDateTime.now());

        UrlMapping saved = urlMappingRepository.save(urlMapping);

        return convertToDto(saved);

    }
    private UrlMappingDTO convertToDto(UrlMapping urlMapping) {
        UrlMappingDTO urlMappingDTO = new UrlMappingDTO();
        urlMappingDTO.setShortUrl(urlMapping.getShortUrl());
        urlMappingDTO.setOriginalUrl(urlMapping.getOriginalUrl());
        urlMappingDTO.setId(urlMapping.getId());
        urlMappingDTO.setCreated(urlMapping.getCreated());
        urlMappingDTO.setClickCount(urlMapping.getClickCount());
        return urlMappingDTO;

    }
    public String generateShortUrl() {
        Random random = new Random();
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            string.append((char)('a'+random.nextInt(+26)));
        }
        return string.toString();
    }

    public List<UrlMappingDTO> getUrlsByUser(User user) {
        List<UrlMappingDTO>urls = urlMappingRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .toList();
        return urls;
    }
}
