package admin.admin.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWT_Service {

    private String secretKey="";

    public JWT_Service(){
        try {
            KeyGenerator keyGenerator=KeyGenerator.getInstance("HmacSHA256");

            SecretKey key=keyGenerator.generateKey();

            secretKey=Base64.getEncoder().encodeToString(key.getEncoded());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateJWToken(String adminEmailId) {
// Building the JWT Token

        //Map to save the Token with respect to all the data
        Map<String,Object>mapOfClaims=new HashMap<>();

        //Building the JWT using the email id ,issue time , expire time and signing it
        return Jwts
                .builder()
                .claims()
                .add(mapOfClaims)
                .subject(adminEmailId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .and()
                .signWith(getKeyForGeneratingTokens())
                .compact();

    }

    private SecretKey getKeyForGeneratingTokens() {
        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


//Validating the JWT Token
    public String extractEmailIdFromToken(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    private <T>T extractClaim(String token, Function<Claims,T>claimResolver) {
        final Claims claims=extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKeyForGeneratingTokens())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String adminEmailId=extractEmailIdFromToken(token);

        //⚠️ Important:We are setting the getUsername() method to return the emailId
        return (adminEmailId.equals(userDetails.getUsername()) );
    }

//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }
}
