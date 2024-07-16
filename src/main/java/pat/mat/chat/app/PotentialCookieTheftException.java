package pat.mat.chat.app;

public class PotentialCookieTheftException extends RuntimeException {

    public PotentialCookieTheftException() {
        super("Your credentials have possibly been stolen! Logging you out of all devices");
    }
}
