package info.smartkit.blockchain.bigchaindb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.linkedin.api.UrlResource;

/**
 * Created by yangboz on 15/9/19.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("serial")
@Document(collection = "snap415_li_user_profile")
public class LiUserProfile extends LinkedInProfile {

    @Id
    @Field("uuid")
    private String id;

    public LiUserProfile(String id, String firstName, String lastName, String headline, String industry, String publicProfileUrl, UrlResource siteStandardProfileRequest, String profilePictureUrl) {
        super(id, firstName, lastName, headline, industry, publicProfileUrl, siteStandardProfileRequest, profilePictureUrl);
    }

    //Introducing the dummy constructor for JSON Mapper
    public LiUserProfile() {
        super(null, null, null, null, null, null, null, null);
    }
}
