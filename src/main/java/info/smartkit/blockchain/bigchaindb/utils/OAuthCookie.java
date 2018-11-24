package info.smartkit.blockchain.bigchaindb.utils;


/**
 * Created by yangboz on 9/24/15.
 *
 * @see https://github.com/fernandezpablo85/TokenExchangeSample/blob/master/src/main/java/com/linkedin/oauth/ExchangeService.java
 */
public class OAuthCookie {

    public OAuthCookie() {
    }

    //    public String signature_method;
//    public String signature_order;
//    public String access_token;
//    public String signature;
//    public String member_id;
//    public String oauth_one_token;
    private String oauth_token;
    private String oauth_token_secret;
    private String oauth_expires_in;
    private String oauth_authorization_expires_in;

    private String oauth_problem;//Esp for problem parsing.

    public String getOauth_token() {
        return oauth_token;
    }

    public void setOauth_token(String oauth_token) {
        this.oauth_token = oauth_token;
    }

    public String getOauth_token_secret() {
        return oauth_token_secret;
    }

    public void setOauth_token_secret(String oauth_token_secret) {
        this.oauth_token_secret = oauth_token_secret;
    }

    public String getOauth_expires_in() {
        return oauth_expires_in;
    }

    public void setOauth_expires_in(String oauth_expires_in) {
        this.oauth_expires_in = oauth_expires_in;
    }

    public String getOauth_authorization_expires_in() {
        return oauth_authorization_expires_in;
    }

    public void setOauth_authorization_expires_in(String oauth_authorization_expires_in) {
        this.oauth_authorization_expires_in = oauth_authorization_expires_in;
    }

    public String toString() {
        return "oauth_token:" + getOauth_token() + ",oauth_token_secret:" + getOauth_token_secret()
                + ",oauth_expires_in:" + getOauth_expires_in() + ",oauth_authorization_expires_in:" + getOauth_authorization_expires_in();
    }

    public String getOauth_problem() {
        return oauth_problem;
    }

    public void setOauth_problem(String oauth_problem) {
        this.oauth_problem = oauth_problem;
    }
}
