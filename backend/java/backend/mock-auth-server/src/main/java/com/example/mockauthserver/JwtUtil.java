public class JwtUtil {
    private static final String SECRET_KEY = "your-secret-key";

    public static String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }
}
