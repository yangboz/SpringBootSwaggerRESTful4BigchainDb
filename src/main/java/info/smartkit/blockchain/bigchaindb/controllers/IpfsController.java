package info.smartkit.blockchain.bigchaindb.controllers;


import com.wordnik.swagger.annotations.ApiOperation;
import info.smartkit.blockchain.bigchaindb.dto.JsonObject;
import info.smartkit.blockchain.bigchaindb.dto.JsonString;
import info.smartkit.blockchain.bigchaindb.services.IpfsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The Class IpfsController.
 */
@RestController
// @see: https://github.com/ipfs/java-ipfs-api
@RequestMapping(value = "v1/ipfs")
public class IpfsController {
    //
    private static Logger LOG = LogManager.getLogger(IpfsController.class);

    @Autowired
    IpfsService ipfsService;

    //get ipfs file
    @RequestMapping(method = RequestMethod.GET, value = "/{hashID}")
    @ApiOperation(value = "Response an IPFS file.")
//	@ApiImplicitParams({@ApiImplicitParam(name="Authorization", value="Authorization DESCRIPTION")})
    public @ResponseBody
    JsonObject getOne(@PathVariable("hashID") String hashID) throws IOException {
        byte[] fileContents = ipfsService.get(hashID);
        return new JsonObject(fileContents);
    }

    //put ipfs file
    @RequestMapping(method = RequestMethod.POST, value = "/",consumes = MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(value = "Create new IPFS file.")
//	@ApiImplicitParams({@ApiImplicitParam(name="Authorization", value="Authorization DESCRIPTION")})
    public @ResponseBody JsonString putOne(@RequestPart(value = "file") @Valid @NotNull @NotBlank MultipartFile multipartFile) throws Exception {
        File rawFile = convert(multipartFile);
        String ipfsHashID = ipfsService.putFile(rawFile);
        return new JsonString(ipfsHashID);

    }

    //@see: https://stackoverflow.com/questions/24339990/how-to-convert-a-multipart-file-to-file
    public File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
