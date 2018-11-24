package info.smartkit.blockchain.bigchaindb.controllers;


import com.bigchaindb.builders.BigchainDbConfigBuilder;
import com.bigchaindb.builders.BigchainDbTransactionBuilder;
import com.bigchaindb.constants.Operations;
import com.bigchaindb.model.FulFill;
import com.bigchaindb.model.GenericCallback;
import com.bigchaindb.model.MetaData;
import com.bigchaindb.model.Transaction;
import com.bigchaindb.util.Base58;
import com.wordnik.swagger.annotations.ApiOperation;
import info.smartkit.blockchain.bigchaindb.dto.JsonObject;
import info.smartkit.blockchain.bigchaindb.dto.JsonString;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Map;
import java.util.TreeMap;


/**
 * The Class BigchainDbController.
 */
@RestController
// @see: https://github.com/bigchaindb/java-bigchaindb-driver#api-wrappers
// @see: https://gist.github.com/innoprenuer/d4c6798fe5c0581c05a7e676e175e515
@RequestMapping(value = "v1/bigchaindb")
public class BigchainDbController {
	//
	private static Logger LOG = LogManager.getLogger(BigchainDbController.class);

	@RequestMapping(method = RequestMethod.GET, value = "/config")
	@ApiOperation(value = "Response a string describing bigchaindb info.")
//	@ApiImplicitParams({@ApiImplicitParam(name="Authorization", value="Authorization DESCRIPTION")})
	public @ResponseBody JsonString info() {
		this.setConfig();

		return new JsonString("v0.0.0");
	}

	//Transactions
	@RequestMapping(method = RequestMethod.GET, value = "/transaction")
	@ApiOperation(value = "Create new assets.")
//	@ApiImplicitParams({@ApiImplicitParam(name="Authorization", value="Authorization DESCRIPTION")})
	public @ResponseBody JsonString createTransaction() throws Exception {
		Map<String, String> assetData = getAssetMap();
		MetaData metaData = getMetaData();
		KeyPair keys = getKeys();
		String txId = doCreate(assetData, metaData, keys);
		//create transfer metadata
		MetaData transferMetadata = new MetaData();
		transferMetadata.setMetaData("where is he now?", "Japan");
		System.out.println("(*) Transfer Metadata Prepared..");
		//execute TRANSFER transaction on the CREATED asset
		doTransfer(txId, transferMetadata, keys);
		return new JsonString(txId);
	}

	//Outputs

	//Assets
	@RequestMapping(method = RequestMethod.POST, value = "/assets")
	@ApiOperation(value = "Create new assets.")
//	@ApiImplicitParams({@ApiImplicitParam(name="Authorization", value="Authorization DESCRIPTION")})
	public @ResponseBody JsonObject createAssets() {
		Map<String, String> assetData = getAssetMap();
		return new JsonObject(assetData);
	}

	private Map<String, String> getAssetMap() {
		Map<String, String> assetData = new TreeMap<String, String>() {{
			put("name", "James Bond");
			put("age", "doesn't matter");
			put("purpose", "saving the world");
		}};
		System.out.println("(*) Assets Prepared..");
		return assetData;
	}

	//Blocks

	//MetaData
	@RequestMapping(method = RequestMethod.GET, value = "/metadata")
	@ApiOperation(value = "Send a Transaction.")
//	@ApiImplicitParams({@ApiImplicitParam(name="Authorization", value="Authorization DESCRIPTION")})
	public @ResponseBody JsonObject  createMetadata() {
		MetaData metaData = getMetaData();
		return new JsonObject(metaData);
	}

	private MetaData getMetaData() {
		// create metadata
		MetaData metaData = new MetaData();
		metaData.setMetaData("where is he now?", "Thailand");
		System.out.println("(*) Metadata Prepared..");
		return metaData;
	}

	//Validators

	//Keys
	@RequestMapping(method = RequestMethod.GET, value = "/keys")
	@ApiOperation(value = "Get Keys.")
//	@ApiImplicitParams({@ApiImplicitParam(name="Authorization", value="Authorization DESCRIPTION")})
	public @ResponseBody JsonObject keys() {
		KeyPair keys = getKeys();
		System.out.println(Base58.encode(keys.getPublic().getEncoded()));
		System.out.println(Base58.encode(keys.getPrivate().getEncoded()));
		return new JsonObject(keys);
	}

	/**
	 * configures connection url and credentials
	 */
	public void setConfig() {
		BigchainDbConfigBuilder
				.baseUrl("http://testnet.bigchaindb.com") //or use http://testnet.bigchaindb.com
				.addToken("app_id", "6f30487a")
				.addToken("app_key", "0b84ac35ef5efbda162b9db3d1fce3f8").setup();
	}
	/**
	 * generates EdDSA keypair to sign and verify transactions
	 * @return KeyPair
	 */
	public KeyPair getKeys() {
		//  prepare your keys
		net.i2p.crypto.eddsa.KeyPairGenerator edDsaKpg = new net.i2p.crypto.eddsa.KeyPairGenerator();
		KeyPair keyPair = edDsaKpg.generateKeyPair();
		System.out.println("(*) Keys Generated..");
		return keyPair;
	}

	/**
	 * performs CREATE transactions on BigchainDB network
	 * @param assetData data to store as asset
	 * @param metaData data to store as metadata
	 * @param keys keys to sign and verify transaction
	 * @return id of CREATED asset
	 */
	public String doCreate(Map<String, String> assetData, MetaData metaData, KeyPair keys) throws Exception {

		try {
			//build and send CREATE transaction
			Transaction transaction = null;

			transaction = BigchainDbTransactionBuilder
					.init()
					.addAssets(assetData, TreeMap.class)
					.addMetaData(metaData)
					.operation(Operations.CREATE)
					.buildAndSign((EdDSAPublicKey) keys.getPublic(), (EdDSAPrivateKey) keys.getPrivate())
					.sendTransaction(handleServerResponse());

			System.out.println("(*) CREATE Transaction sent.. - " + transaction.getId());
			return transaction.getId();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * performs TRANSFER operations on CREATED assets
	 * @param txId id of transaction/asset
	 * @param metaData data to append for this transaction
	 * @param keys keys to sign and verify transactions
	 */
	public void doTransfer(String txId, MetaData metaData, KeyPair keys) throws Exception {

		Map<String, String> assetData = new TreeMap<String, String>();
		assetData.put("id", txId);

		try {


			//which transaction you want to fulfill?
			FulFill fulfill = new FulFill();
			fulfill.setOutputIndex(0);
			fulfill.setTransactionId(txId);


			//build and send TRANSFER transaction
			Transaction transaction = BigchainDbTransactionBuilder
					.init()
					.addInput(null, fulfill, (EdDSAPublicKey) keys.getPublic())
					.addOutput("1", (EdDSAPublicKey) keys.getPublic())
					.addAssets(txId, String.class)
					.addMetaData(metaData)
					.operation(Operations.TRANSFER)
					.buildAndSign((EdDSAPublicKey) keys.getPublic(), (EdDSAPrivateKey) keys.getPrivate())
					.sendTransaction(handleServerResponse());

			System.out.println("(*) TRANSFER Transaction sent.. - " + transaction.getId());


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private GenericCallback handleServerResponse() {
		//define callback methods to verify response from BigchainDBServer
		GenericCallback callback = new GenericCallback() {

			@Override
			public void transactionMalformed(Response response) {
				System.out.println("malformed " + response.message());
				onFailure();
			}

			@Override
			public void pushedSuccessfully(Response response) {
				System.out.println("pushedSuccessfully");
				onSuccess(response);
			}

			@Override
			public void otherError(Response response) {
				System.out.println("otherError" + response.message());
				onFailure();
			}
		};

		return callback;
	}

	private void onSuccess(Response response) {
		//TODO : Add your logic here with response from server
		System.out.println("Transaction posted successfully");
	}

	private void onFailure() {
		//TODO : Add your logic here
		System.out.println("Transaction failed");
	}
}
