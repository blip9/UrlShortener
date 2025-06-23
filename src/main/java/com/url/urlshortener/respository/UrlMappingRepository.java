package com.url.urlshortener.respository;

import com.url.urlshortener.dtos.UrlMappingDTO;
import com.url.urlshortener.models.UrlMapping;
import com.url.urlshortener.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    UrlMapping findByShortUrl(String url);
    List<UrlMapping>findByUser(User user);

}
