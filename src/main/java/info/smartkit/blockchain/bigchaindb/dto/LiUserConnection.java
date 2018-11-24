package info.smartkit.blockchain.bigchaindb.dto;

import org.springframework.social.linkedin.api.LinkedInProfile;

import java.util.List;

/**
 * Created by yangboz on 15/9/20.
 */
public class LiUserConnection {

    private int firstDegreeCount;
    private int secondDegreeCount;
    private List<LinkedInProfile> connections;

    public int getFirstDegreeCount() {
        return firstDegreeCount;
    }

    public void setFirstDegreeCount(int firstDegreeCount) {
        this.firstDegreeCount = firstDegreeCount;
    }

    public int getSecondDegreeCount() {
        return secondDegreeCount;
    }

    public void setSecondDegreeCount(int secondDegreeCount) {
        this.secondDegreeCount = secondDegreeCount;
    }

    public List<LinkedInProfile> getConnections() {
        return connections;
    }

    public void setConnections(List<LinkedInProfile> connections) {
        this.connections = connections;
    }
}
