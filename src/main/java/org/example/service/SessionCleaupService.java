package org.example.service;

import org.example.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SessionCleaupService {

    @Autowired private SessionRepository sessionRepository;

    @Scheduled(fixedRate = 3600000) // every hour
    public void cleanExpiredSessions() {
        sessionRepository.deleteByExpiresAtLessThan(System.currentTimeMillis());
    }
}
