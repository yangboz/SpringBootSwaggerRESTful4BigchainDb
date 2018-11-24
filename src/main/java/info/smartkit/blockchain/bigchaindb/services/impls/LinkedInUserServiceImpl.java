package info.smartkit.blockchain.bigchaindb.services.impls;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.smartkit.blockchain.bigchaindb.domain.LiUserProfile;
import info.smartkit.blockchain.bigchaindb.dto.DknToken;
import info.smartkit.blockchain.bigchaindb.repository.LinkedInUserRepository;
import info.smartkit.blockchain.bigchaindb.services.LinkedInUserService;
import info.smartkit.blockchain.bigchaindb.utils.OAuthCookie;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by yangboz on 9/24/15.
 */
@Service
public class LinkedInUserServiceImpl implements LinkedInUserService {


    // ==============
    // PRIVATE FIELDS
    // ==============

    private final Logger LOG = LoggerFactory.getLogger(LinkedInUserServiceImpl.class);

    // Autowire an object of type UserDao
    @Autowired
    private LinkedInUserRepository _liUserDao;

    //    @Autowired
    private LinkedIn linkedIn;

    @Inject
    private ConnectionRepository connectionRepository;

    //
    @Inject
    public LinkedInUserServiceImpl(LinkedIn linkedIn, ConnectionRepository connectionRepository) {
        this.linkedIn = linkedIn;
        this.connectionRepository = connectionRepository;
    }


    @Autowired
    Environment environment;

    public static final String OAUTH2_ACCESS_TOKEN = "xoauth_oauth2_access_token";
    public static final String X_LI_FORMAT = "x-li-format";

    //
    public final String KEY() {
        return environment.getProperty("spring.social.linkedin.consumerKey");
    }

    public final String SECRET() {
        return environment.getProperty("spring.social.linkedin.consumerSecret");
    }

    public static final String ACCESS_TOKEN_ENDPOINT = "https://api.linkedin.com/uas/oauth/accessToken";
    public static final String PROFILE_URL = "http://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline)";

    @Override
    public LiUserProfile getUserProfile(DknToken token) {
        //@see: https://github.com/fernandezpablo85/scribe-java/wiki/getting-started
        //@see: https://github.com/fernandezpablo85/TokenExchangeSample/blob/master/src/main/java/com/linkedin/oauth/ExchangeService.java
        //@see: https://github.com/fernandezpablo85/scribe-java/tree/master/src/test/java/org/scribe/examples
        //1
        OAuthService service = new ServiceBuilder()
                .apiKey(KEY())
                .apiSecret(SECRET())
                .provider(LinkedInApi.withScopes("r_basicprofile r_network r_emailaddress rw_company_admin"))
                .build();

        // Exchange 2.0 token for 1.0a (long lived)
        OAuthRequest oAuthRequest = new OAuthRequest(Verb.POST, ACCESS_TOKEN_ENDPOINT);

        // Add the 2.0 token as a parameter
        oAuthRequest.addHeader(X_LI_FORMAT, "json");
        oAuthRequest.addBodyParameter(OAUTH2_ACCESS_TOKEN, token.getToken());

        // Use an empty 1.0a access_token
        Token emptyToken = new Token("", "");

        // Sign and then send the request
        service.signRequest(emptyToken, oAuthRequest);
        Response oAuthRequestResp = oAuthRequest.send();
        LOG.info("Raw oAuthRequestResp.getBody():" + oAuthRequestResp.getBody());
        OAuthCookie oAuthCookie = this.plainTextToOAuthCookie(oAuthRequestResp.getBody());
        LOG.info("Parsed OAuthCookie:" + oAuthCookie);
        //Profile fetch
        Token fetchRequestToken = new Token(oAuthCookie.getOauth_token(), oAuthCookie.getOauth_token_secret());
        //
        OAuthRequest anotherOauthRequest = new OAuthRequest(Verb.GET, PROFILE_URL);
        anotherOauthRequest.addHeader(X_LI_FORMAT, "json");//format as JSON
        service.signRequest(fetchRequestToken, anotherOauthRequest);
        Response response = anotherOauthRequest.send();
        LOG.info("FetchReqeustResp.getBody():" + response.getBody());
        //
        LiUserProfile liUserProfile = null;
        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        try {
            liUserProfile = mapper.readValue(response.getBody(), LiUserProfile.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("Final mapped LiUserProfile:" + liUserProfile);
        return liUserProfile;
    }


    private byte[] shaSign(String baseString, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
            mac.init(secretKey);
            return mac.doFinal(baseString.getBytes());
        } catch (Exception e) {
            throw new IllegalStateException("Error while generating the HMAC-SHA1 signature", e);
        }
    }

    private OAuthCookie plainTextToOAuthCookie(String plainText) {
        //oauth_token=78--b367d0e9-a312-4fc8-814f-c20444c9f4bd&oauth_token_secret=b89bd23c-459f-4698-a6ba-5e76cd5d8d07&oauth_expires_in=4742507&oauth_authorization_expires_in=4742507
        String jsonStr = "{";
        String[] plainTexts = plainText.split("&");
        for (int i = 0; i < plainTexts.length; i++) {
            String[] textElements = plainTexts[i].split("=");
            if (i > 0) {
                jsonStr += ",'" + textElements[0] + "'";
            } else {
                jsonStr += "'" + textElements[0] + "'";
            }
            //
            jsonStr += ":";
            jsonStr += "'" + textElements[1] + "'";
        }
        jsonStr += "}";

        OAuthCookie oAuthCookie = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        try {
            oAuthCookie = mapper.readValue(jsonStr, OAuthCookie.class);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Translated oAuthCookie:" + oAuthCookie.toString());

        return oAuthCookie;
    }
}
