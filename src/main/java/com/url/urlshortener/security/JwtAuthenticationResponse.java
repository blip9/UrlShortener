package com.url.urlshortener.security;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthenticationResponse {

    private String token;

}
