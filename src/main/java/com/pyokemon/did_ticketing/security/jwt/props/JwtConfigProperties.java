package com.pyokemon.did_ticketing.security.jwt.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(value = "jwt", ignoreUnknownFields = true)
@Getter
@Setter
public class JwtConfigProperties {
    private String header;
    private Integer expiresIn;
    private Integer mobileExpiresIn;
    private Integer tableExpiresIn;
    private String secretKey;
}
