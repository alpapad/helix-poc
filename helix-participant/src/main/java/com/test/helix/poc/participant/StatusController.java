package com.test.helix.poc.participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class StatusController {

    @Autowired
    private ParticipantService participantService;
    
    
    @PostMapping(path = "/shutdown")
    public void shutdown() {
        participantService.shutdown();
    }
    
    
    @PostMapping(path = "/start")
    public void start() throws Exception {
        participantService.start();
    }
}
