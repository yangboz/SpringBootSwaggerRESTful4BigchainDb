/*
 * Copyright (c) 2018. SMARTKIT.INFO.
 */

package info.smartkit.blockchain.bigchaindb.services;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@Service
public class IpfsService {

    public IPFS getIpfs() {
        return ipfs;
    }

    IPFS ipfs = null;
    protected Logger logger = Logger.getLogger(IpfsService.class
            .getName());

    @PostConstruct
    public void main() throws IOException {
        // Can't do this in the constructor because the RestTemplate injection
        // happens afterwards.
        // @see: https://github.com/ipfs/java-ipfs-api
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
        logger.warning("hard coded IPFS info: "+ipfs.toString());
        ipfs.refs.local();
    }

    public String putFile(File file) throws IOException {
//        logger.info("IpfsService put() invoked: for " + file);
        NamedStreamable.FileWrapper fileWrapper = new NamedStreamable.FileWrapper(file);
        MerkleNode addResult = ipfs.add(fileWrapper).get(0);
        logger.info("IpfsService put() result: " + addResult.toJSONString());
        return addResult.toJSONString();
    }

    public String putByte(byte[] file) throws IOException {
        NamedStreamable.ByteArrayWrapper fileWrapper = new NamedStreamable.ByteArrayWrapper(file);//"hello.txt", "G'day world! IPFS rocks!".getBytes()
        MerkleNode addResult = ipfs.add(fileWrapper).get(0);
        logger.info("IpfsService put() result: " + addResult.toJSONString());
        return addResult.toJSONString();
    }

    public byte[] get(String hashStr) throws IOException {
        Multihash filePointer = Multihash.fromBase58(hashStr);//"QmPZ9gcCEpqKTo6aq61g2nXGUhM4iCL3ewB6LDXZCtioEB"
        byte[] fileContents = ipfs.cat(filePointer);
        logger.info("IpfsService get() for " + hashStr);
        return fileContents;
    }

}
