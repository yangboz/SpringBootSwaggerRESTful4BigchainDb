package info.smartkit.blockchain.bigchaindb.controllers;


import info.smartkit.blockchain.bigchaindb.dto.JsonObject;
import info.smartkit.blockchain.bigchaindb.dto.JsonString;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.web.bind.annotation.*;



/**
 * The Class OCRsController.
 */
@RestController
// @see: http://spring.io/guides/gs/reactor-thumbnailer/
@RequestMapping(value = "/bigchaindb")
public class BlockChainDbController {
	//
	private static Logger LOG = LogManager.getLogger(BlockChainDbController.class);

	@RequestMapping(method = RequestMethod.GET, value = "/info")
	@ApiOperation(value = "Response a string describing bigchaindb info.")
//	@ApiImplicitParams({@ApiImplicitParam(name="Authorization", value="Authorization DESCRIPTION")})
	public @ResponseBody JsonString info() {
		return new JsonString("v0.0.0");
	}

}
