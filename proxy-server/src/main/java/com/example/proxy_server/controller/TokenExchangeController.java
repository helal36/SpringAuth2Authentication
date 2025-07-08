package com.example.proxy_server.controller;


import java.util.Base64;
import java.util.Map;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;

@RestController
public class TokenExchangeController {

    @PostMapping("/token")
    public ResponseEntity<String> exchange(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        System.out.println("Proxy controller-->Received code: " + code);

        String params = "grant_type=authorization_code" +
                "&code=" + code +
                "&redirect_uri=http://localhost:3000/callback";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set Authorization header with Basic Auth
        String encodedCredentials = Base64.getEncoder().encodeToString(("fundtracker-client:secret").getBytes());
        headers.set("Authorization", "Basic " + encodedCredentials);

        HttpEntity<String> req = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> resp = new RestTemplate()
                    .postForEntity("http://localhost:9000/oauth2/token", req, String.class);

            System.out.println("Proxy controller-->resp: " + resp);
            return ResponseEntity.ok(resp.getBody());
        } catch (HttpClientErrorException ex) {
            System.out.println("Proxy controller-->exception occure: " + ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }
}
