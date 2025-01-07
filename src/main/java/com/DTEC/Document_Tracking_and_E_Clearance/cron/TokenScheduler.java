package com.DTEC.Document_Tracking_and_E_Clearance.cron;

import com.DTEC.Document_Tracking_and_E_Clearance.token.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenScheduler {
    private final TokenRepository tokenRepository;


    public TokenScheduler(
            TokenRepository tokenRepository
    ) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * MON") // this executes every monday
    public void clearRevokedToken(){
        Date now = new Date();
        this.tokenRepository.deleteAllExpiredTokens(now);
    }

}
