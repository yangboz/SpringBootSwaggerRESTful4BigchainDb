package info.smartkit.blockchain.bigchaindb.controllers;


import com.wordnik.swagger.annotations.ApiOperation;
import info.smartkit.blockchain.bigchaindb.domain.LiUserProfile;
import info.smartkit.blockchain.bigchaindb.dto.DknToken;
import info.smartkit.blockchain.bigchaindb.dto.JsonObject;
import info.smartkit.blockchain.bigchaindb.dto.LiUserConnection;
import info.smartkit.blockchain.bigchaindb.repository.LinkedInUserRepository;
import info.smartkit.blockchain.bigchaindb.services.LinkedInUserService;
import org.scribe.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.linkedin.api.NetworkStatistics;
import org.springframework.social.linkedin.api.impl.LinkedInTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;


/**
 * The Class LinkedInConnectController.
 *
 * @author yangboz
 */
@RestController
@RequestMapping("/v1/connect/linkedin/")
public class LinkedInConnectController {

    // ==============
    // PRIVATE FIELDS
    // ==============

    private final Logger LOG = LoggerFactory.getLogger(LinkedInConnectController.class);

    // Autowire an object of type UserDao
    @Autowired
    private LinkedInUserRepository _liUserDao;

    //    @Autowired
    private LinkedIn linkedIn;

    @Inject
    private ConnectionRepository connectionRepository;

    //
    @Inject
    public LinkedInConnectController(LinkedIn linkedIn, ConnectionRepository connectionRepository) {
        this.linkedIn = linkedIn;
        this.connectionRepository = connectionRepository;
    }

    @Autowired
    private LinkedInUserService _linkedInUserService;
    //
//    @Inject
//    public LinkedInConnectController(LinkedIn linkedIn) {
//        this.linkedIn = linkedIn;
//    }
//    private LinkedInRequestToken sessionRequestToken = null;
    private Token sessionRequestToken = null;
    //


    //
    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the access_token related user profile is successfully received.")
    public JsonObject getAccessToken(@RequestBody @Valid DknToken dknToken) {
        //
        return new JsonObject(this._linkedInUserService.getUserProfile(dknToken));
    }

    @RequestMapping(value = "/access", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "Response a string describing if the access_token related user profile is successfully received.")
    public JsonObject getUserProfile(@RequestBody @Valid DknToken dknToken) {
        /**
         * Programmatically signs in the user with the given the user ID.
         * @see: spring-social-showcase-boot(SignInUtil)
         */
        LOG.info("dknToken:" + dknToken.toString());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(dknToken.getId(), null, null));
        //@see: https://developer-programs.linkedin.com/documents/exchange-jsapi-tokens-rest-api-oauth-tokens
//		String accessToken = "f8FX29g..."; // access token received from Facebook after OAuth authorization
//		Facebook facebook = new FacebookTemplate(accessToken);
        LOG.info("connectionRepository.findAllConnections():" + connectionRepository.findAllConnections().toString());
        Connection<LinkedIn> connection = connectionRepository.findPrimaryConnection(LinkedIn.class);
//        Connection<LinkedIn> connection = connectionRepository.
//        "77nayor82qqip3", "UJOUycxP5UgdD3da"
        LOG.info("Connection<LinkedIn>:" + connection);
        LinkedIn linkedIn = connection != null ? connection.getApi() : new LinkedInTemplate(dknToken.getToken());
        LOG.info("linkedIn,isAuthorized():" + linkedIn.isAuthorized() + "," + linkedIn.toString());
        //Retrieving a user's profile data.
        //@see: http://docs.spring.io/spring-social-facebook/docs/2.0.1.RELEASE/reference/htmlsingle/
        LOG.info("linkedIn getProfileById:" + linkedIn.profileOperations().getProfileById(dknToken.getId()));
        System.out.println("linkedIn.profileOperations():" + linkedIn.profileOperations().toString());
        LinkedInProfile profile = linkedIn.profileOperations().getUserProfile();
        LOG.info("LinkedInProfile: id:" + profile.getId());
        //Synchronize the FB user profile to DB.
        LiUserProfile liUser = new LiUserProfile(profile.getId(), profile.getLastName(), profile.getLastName(), profile.getHeadline(), profile.getIndustry(), profile.getPublicProfileUrl(), profile.getSiteStandardProfileRequest(), profile.getPublicProfileUrl());
        //
        this._liUserDao.save(liUser);
        return new JsonObject(profile);

    }

    @RequestMapping(value = "/connections", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Response a string describing if the user connnection is successfully received.")
    public JsonObject getConnections(@RequestBody @Valid DknToken dknToken) {
        NetworkStatistics statistics = linkedIn.connectionOperations().getNetworkStatistics();
        LiUserConnection connection = new LiUserConnection();
        connection.setFirstDegreeCount(statistics.getFirstDegreeCount());
        connection.setSecondDegreeCount(statistics.getSecondDegreeCount());
        connection.setConnections(linkedIn.connectionOperations().getConnections());
        return new JsonObject(connection);
    }

}


