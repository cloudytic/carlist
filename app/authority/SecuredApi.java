package authority;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import models.Account;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import services.DB;
import services.Singletons;

import java.util.Date;
import java.util.Optional;

public class SecuredApi extends Security.Authenticator {

    @Override
    public Optional<String> getUsername(Http.Request req) {
        String token = req.headers().get("Authorization").orElse("");
        Account account = getAccount(req);
        if (account == null) {
            return Optional.empty();
        }
        if(!account.isActive()) {
            return Optional.empty();
        }
        return Optional.of(token);
    }
    
    @Override
    public Result onUnauthorized(Http.Request req) {
        return unauthorized();
    }

    private static final String DEM = "<___>";

    public static Account getAccount(Http.Request request) {
        String token = request.headers().get("Authorization").orElse("");
        if (token.isEmpty()) {
            return null;
        }

        token = token.replace("Bearer ", "");

        try {
            String subject = getSubject(token);
            String[] parts = subject.split(DEM);
            String id = parts[0];
            String email = parts[1];

            Account account = DB.findOne(Account.class, id);
            if(account == null || !account.getEmail().equals(email)) {
                return null;
            }

            return account;
        } catch (Exception e) {
            System.out.println("Api login error: " + e.getMessage())    ;
           return null;
        }
    }


    private static final long EXPIRATION_TIME = 86400000; // 24 hours in milliseconds

    public static String getAccessToken(Account account) {
        return Jwts.builder()
                .setSubject(account.getId()+DEM+account.getEmail())
                .setIssuedAt(new Date())
                //.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, Singletons.config.getString("jwt_secret_key"))
                .compact();
    }

    //get subject from token
    public static String getSubject(String token) throws Exception {
        return Jwts.parser().setSigningKey(Singletons.config.getString("jwt_secret_key")).parseClaimsJws(token).getBody().getSubject();
    }
}