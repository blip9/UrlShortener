package com.url.urlshortener.service;

import com.url.urlshortener.dtos.ClickEventDTO;
import com.url.urlshortener.dtos.UrlMappingDTO;
import com.url.urlshortener.models.ClickEvent;
import com.url.urlshortener.models.UrlMapping;
import com.url.urlshortener.models.User;
import com.url.urlshortener.respository.ClickEventRepository;
import com.url.urlshortener.respository.UrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UrlMappingService {

    UrlMappingRepository urlMappingRepository;
    ClickEventRepository clickEventRepository;

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

    public List<ClickEventDTO> getClickEventsByDate(String shortUrl, LocalDate startDate, LocalDate endDate) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if(urlMapping != null) {
            List<ClickEventDTO>clickEventDTOS = clickEventRepository.findByUrlMappingAndClickDateBetween(urlMapping, startDate, endDate)
                    .stream().collect(Collectors.groupingBy(ClickEvent::getClickDate, Collectors.counting()))
                    .entrySet().stream().map(entry->{
                        ClickEventDTO clickEventDTO = new ClickEventDTO();
                        clickEventDTO.setClickDate(entry.getKey());
                        clickEventDTO.setCount(entry.getValue().intValue());
                        return clickEventDTO;
                    }).collect(Collectors.toList());
            return clickEventDTOS;
        }
        return null;
    }


    public Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDate startDate, LocalDate endDate) {
        List<UrlMapping>urlMappings = urlMappingRepository.findByUser(user);
        // Add one day to endDate to include clicks on the end date
        LocalDate adjustedEndDate = endDate.plusDays(1);
        List<ClickEvent>clickEvents = clickEventRepository.findByUrlMappingInAndClickDateBetween(urlMappings, startDate, adjustedEndDate);
        return clickEvents.stream().collect(Collectors.groupingBy(ClickEvent::getClickDate, Collectors.counting()));
    }

    public UrlMapping getOriginalUrl(String shortUrl) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if(urlMapping!=null){
            urlMapping.setClickCount(urlMapping.getClickCount() + 1);
            urlMappingRepository.save(urlMapping);

            //Record click event
            ClickEvent clickEvent = new ClickEvent();
            clickEvent.setUrlMapping(urlMapping);
            clickEvent.setClickDate(LocalDate.now());
            clickEventRepository.save(clickEvent);
        }
        return urlMapping;
    }
}
