package com.comsoc.api.security;



import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import io.jsonwebtoken.*;

@Component
public class JwtTokenProvider implements Serializable
{
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secrete}")
    private String jwtSecret;

    @Value("${app.jwt.expriration}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication)
    {
        MemberDetailsImpl userPrincipal = (MemberDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername().toLowerCase()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getRegNumberFromToken(String token)
    {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject().toLowerCase();
    }

    public String getExpirationMinutes(String token)  {

       Date then =Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration();
       Date now = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getIssuedAt();

       Long diff = then.getTime()-now.getTime();
       long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

       return minutes + " minutes";

    }

    public boolean validateJwtToken(String authToken)
    {
        try
        {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }
        catch (SignatureException e)
        {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        }
        catch (MalformedJwtException e)
        {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        catch (ExpiredJwtException e)
        {
            logger.error("JWT token is expired: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e)
        {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
