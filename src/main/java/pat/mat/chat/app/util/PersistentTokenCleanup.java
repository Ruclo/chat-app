package pat.mat.chat.app.util;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pat.mat.chat.app.repository.PersistentTokenRepository;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class PersistentTokenCleanup {

    public static final int CLEANUP_FREQUENCY_IN_HOURS = 24;

    @Autowired
    PersistentTokenRepository persistentTokenRepository;

    @Scheduled(fixedRate = CLEANUP_FREQUENCY_IN_HOURS, timeUnit = TimeUnit.HOURS)
    @Transactional
    public void cleanupExpiredTokens() {
        System.out.println("Removing expired tokens");
        persistentTokenRepository.deleteExpiredTokens(Instant.now());
    }
}
