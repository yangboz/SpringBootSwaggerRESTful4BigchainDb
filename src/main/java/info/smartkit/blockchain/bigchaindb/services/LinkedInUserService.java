package info.smartkit.blockchain.bigchaindb.services;

import info.smartkit.blockchain.bigchaindb.domain.LiUserProfile;
import info.smartkit.blockchain.bigchaindb.dto.DknToken;

public interface LinkedInUserService {
    LiUserProfile getUserProfile(DknToken token);
}
