package org.chrisli.utils.encrypt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.chrisli.utils.date.DateUtil;
import org.chrisli.utils.exception.FrameworkException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * [JWT工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class JwtUtil {
    /**
     * [存放JWT头信息]
     */
    private static Map<String, Object> headMap = new HashMap<String, Object>();
    /**
     * [JWT签发者]
     */
    private static final String ISSUER = "ChrisLi";
    /**
     * [加密密钥(仅存于服务器端)]
     */
    private static final String SECRET = "IlovEthiSgamE";
    /**
     * [默认的生成令牌的当前时间格式]
     */
    private static final String DEFAULT_CURRENT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static {
        headMap.put("typ", "JWT");
        headMap.put("alg", "HS256");
    }

    /**
     * [创建JWT Token]
     */
    public static String createToken(Map<String, String> claimMap, long activeTime) {
        return createToken(claimMap, DEFAULT_CURRENT_TIME_FORMAT, activeTime);
    }

    /**
     * [创建JWT Token]
     */
    public static String createToken(Map<String, String> claimMap, String currentTimePattern, long activeTime) {
        try {
            long currentMillis = System.currentTimeMillis();
            Date issuedAt = DateUtil.millisToDate(currentMillis, currentTimePattern);
            Date expiresAt = DateUtil.millisToDate(System.currentTimeMillis() + activeTime, currentTimePattern);
            JWTCreator.Builder builder = JWT.create().withHeader(headMap).withIssuer(ISSUER);
            for (String claimKey : claimMap.keySet()) {
                builder.withClaim(claimKey, claimMap.get(claimKey));
            }
            return builder.withIssuedAt(issuedAt).withExpiresAt(expiresAt).sign(Algorithm.HMAC256(SECRET));
        } catch (Exception e) {
            throw new FrameworkException(e.getLocalizedMessage());
        }
    }

    /**
     * [验证JWT Token,并获取存放的信息]
     */
    public static String verifyToken(String token, String claimKey) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).withIssuer(ISSUER).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim(claimKey).asString();
        } catch (InvalidClaimException e) {
            throw new InvalidClaimException("Token非法!");
        } catch (Exception e) {
            throw new FrameworkException(e.getLocalizedMessage());
        }
    }
}