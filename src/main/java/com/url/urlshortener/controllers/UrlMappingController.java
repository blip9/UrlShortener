package com.url.urlshortener.controllers;

import com.url.urlshortener.dtos.ClickEventDTO;
import com.url.urlshortener.dtos.UrlMappingDTO;

import com.url.urlshortener.models.User;
import com.url.urlshortener.respository.ClickEventRepository;
import com.url.urlshortener.respository.UserRepository;
import com.url.urlshortener.service.UrlMappingService;
import com.url.urlshortener.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urls")
@AllArgsConstructor
public class UrlMappingController {
    private final ClickEventRepository clickEventRepository;
    private final UserRepository userRepository;
    UrlMappingService urlMappingService;
    UserService userService;

    @PostMapping(value = "/shorten", consumes = {"application/json", "application/x-www-form-urlencoded"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDTO> shortenUrl(@RequestBody Map<String,String> request, Principal principal) {
        String originalUrl = request.get("originalUrl");
        User user = userService.findByUsername(principal.getName());
        UrlMappingDTO urlMappingDTO= urlMappingService.createShortUrl(originalUrl,user);
        return ResponseEntity.ok(urlMappingDTO);
    }

    @GetMapping(value = "/myUrls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UrlMappingDTO>> getUserUrls(Principal principal){
        User user = userService.findByUsername(principal.getName());
        List<UrlMappingDTO>userUrls = urlMappingService.getUrlsByUser(user);

        return ResponseEntity.ok(userUrls);
    }

    @GetMapping(value = "/analytics/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ClickEventDTO>> getUrlAnalytics(@PathVariable String shortUrl, @RequestParam String startDate, @RequestParam String endDate){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;
        // Expected format: 2024-12-01
        LocalDate startLocalDate = LocalDate.parse(startDate, dateTimeFormatter);
        LocalDate endLocalDate = LocalDate.parse(endDate, dateTimeFormatter);

        List<ClickEventDTO>clickEventDTOS = urlMappingService.getClickEventsByDate(shortUrl, startLocalDate, endLocalDate);
        return ResponseEntity.ok(clickEventDTOS);
    }

    @GetMapping(value = "/totalClicks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<LocalDate,Long>> getTotalClicksByDate(Principal principal, @RequestParam String startDate, @RequestParam String endDate){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;
        // Expected format: 2024-12-01
        LocalDate startLocalDate = LocalDate.parse(startDate, dateTimeFormatter);
        LocalDate endLocalDate = LocalDate.parse(endDate, dateTimeFormatter);

        User user = userService.findByUsername(principal.getName());

        Map<LocalDate,Long> analytics = urlMappingService.getTotalClicksByUserAndDate(user, startLocalDate, endLocalDate);

        return ResponseEntity.ok(analytics);
    }
}
