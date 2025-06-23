package com.url.urlshortener.controllers;

import com.url.urlshortener.dtos.UrlMappingDTO;
import com.url.urlshortener.models.UrlMapping;
import com.url.urlshortener.models.User;
import com.url.urlshortener.service.UrlMappingService;
import com.url.urlshortener.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urls")
@AllArgsConstructor
public class UrlMappingController {
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

}
