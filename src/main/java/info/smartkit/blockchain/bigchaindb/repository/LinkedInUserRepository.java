package info.smartkit.blockchain.bigchaindb.repository;

import info.smartkit.blockchain.bigchaindb.domain.LiUserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by yangboz on 15/9/20.
 */
public interface LinkedInUserRepository extends MongoRepository<LiUserProfile, String> {
}
