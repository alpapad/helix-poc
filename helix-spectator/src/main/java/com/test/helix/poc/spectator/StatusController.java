package com.test.helix.poc.spectator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class StatusController {

    @Autowired
    private HelixSpectatorService participantService;
    
    
    @PostMapping(path = "/shutdown")
    public void shutdown() {
        participantService.shutdown();
    }
    
    
    @PostMapping(path = "/start")
    public void start() throws Exception {
        participantService.start();
    }
}
