package com.tomas.miproyecto.clients;

import com.tomas.miproyecto.clients.dtos.TeamClientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TeamClientRestTemplate {

    @Autowired
    RestTemplate restTemplate;

    @Value("${api.url}")
    private String URL;

    public ResponseEntity<TeamClientDTO[]> getTeams() {
        ResponseEntity<TeamClientDTO[]> response = null;
        try {
            response = restTemplate.getForEntity(URL + "equipos", TeamClientDTO[].class);
            if(response.getStatusCode().equals(200)){
                return response;
            }
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex);
        }
        return response;
    }
}
