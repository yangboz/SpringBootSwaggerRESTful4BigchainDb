//@see: http://www.infoq.com/cn/articles/spring-data-intro
package info.smartkit.blockchain.bigchaindb.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("serial")
public abstract class BaseEntity implements Serializable {
    @Id
    @Field("uuid")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BaseEntity other = (BaseEntity) obj;
        if (id == null) return other.id == null;
        return id.equals(other.id);
    }


    /**
     * All objects will have a unique UUID which allows for the decoupling from DB generated ids
     */
//    @Column(length=36)
    private String uuid;

    private Date timeCreated;

    public BaseEntity() {
        this(UUID.randomUUID());
    }

    public BaseEntity(UUID guid) {
//        Assert.notNull(guid, "UUID is required");
        setUuid(guid.toString());
        this.timeCreated = new Date();
    }

    public UUID getUuid() {
        return UUID.fromString(uuid);
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

//    public int hashCode() {
//        return getUuid().hashCode();
//    }

    /**
     * In most instances we can rely on the UUID to identify the object.
     * Subclasses may want a user friendly identifier for constructing easy to read urls
     * <p/>
     * <code>
     * /user/1883c578-76be-47fb-a5c1-7bbea3bf7fd0 using uuid as the identifier
     * <p/>
     * /user/jsmith using the username as the identifier
     * <p/>
     * </code>
     *
     * @return Object unique identifier for the object
     */
    public Object getIdentifier() {
        return getUuid().toString();
    }


    public Date getTimeCreated() {
        return timeCreated;
    }

}