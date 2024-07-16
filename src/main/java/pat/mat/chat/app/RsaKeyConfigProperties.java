package pat.mat.chat.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix="rsa")
public record RsaKeyConfigProperties(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
}
