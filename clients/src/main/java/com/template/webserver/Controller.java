package com.template.webserver;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.template.flows.IssueMetal;
import com.template.flows.TransferMetal;
import com.template.states.MetalState;
import com.template.webserver.models.CordappResponse;
import com.template.webserver.models.KYC;
import com.template.webserver.models.Metal;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Define your API endpoints here.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/metal") // The paths for HTTP requests are relative to this base path.
public class Controller {

     private final CordaRPCOps proxy;
//    @Autowired
//    private CordaRPCOps mintProxy;
//
//    @Autowired
//    private CordaRPCOps traderAProxy;
//
//    @Autowired
//    private CordaRPCOps traderBProxy;
//
//    @Autowired
//    @Qualifier("mintProxy")
//    private CordaRPCOps proxy;

    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @GetMapping(value = "/templateendpoint", produces = "text/plain")
    private String templateendpoint() {

        // For example, here we print the nodes on the network.
        final List<NodeInfo> nodes = proxy.networkMapSnapshot();
        System.out.println("printing nodes");
        logger.info("{}", nodes);

        return "Define an endpoint here.";
    }

    @PostMapping(value = "/issue", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> issueMetal(@RequestBody  Metal metal) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(metal.getName() == null) { throw new IllegalArgumentException("Metal name is required."); }
            if(metal.getWeight() == 0 ) { throw new IllegalArgumentException("metal weight is required."); }
            System.out.println(metal.getName());
            proxy.startFlowDynamic(IssueMetal.class, metal.getName(), metal.getWeight(), proxy.partiesFromName("TraderA", false).iterator().next()).getReturnValue().get();
            response.setMessage("metal issued.");
            response.setStatus(true);
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            if(e instanceof IllegalArgumentException) {
                return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/transfer", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> transferMetal() throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            System.out.println(proxy.toString());
            proxy.startFlowDynamic(TransferMetal.class, "gold", 30, proxy.partiesFromName("TraderB", false).iterator().next()).getReturnValue().get();
            response.setMessage("metal transferred.");
            response.setStatus(true);
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            if(e instanceof FlowException) {
                response.setMessage(e.getMessage().substring(30));
                return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<List<MetalState>>> listMetal() throws Exception {
        CordappResponse<List<MetalState>> response = new CordappResponse<List<MetalState>>();
        try {
            List<MetalState> metalList = new ArrayList<>();
            List<StateAndRef<MetalState>> metalStateList = proxy.vaultQuery(MetalState.class).getStates();

            for(StateAndRef<MetalState> metalStateStateAndRef: metalStateList) {
                MetalState metal = new MetalState(metalStateStateAndRef.getState().getData().getName(),
                                                metalStateStateAndRef.getState().getData().getWeight(),
                                                null, null );

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                String responseString = objectMapper.writeValueAsString(metal);
                metalList.add(metal);
            }
            response.setMessage("metal listed");
            response.setStatus(true);
            response.setData(metalList);
            return new ResponseEntity<CordappResponse<List<MetalState>>>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return new ResponseEntity<CordappResponse<List<MetalState>>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PostMapping(value = "/upload")
//    private ResponseEntity<CordappResponse<Void>> submitKYC(@RequestParam("file") MultipartFile file) throws Exception {
//        CordappResponse<Void> response = new CordappResponse<Void>();
//        try {
//            String fileName = fileStorageService.storeFile(file);
//
//            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                    .path("/downloadFile/")
//                    .path(fileName)
//                    .toUriString();
//
//           System.out.println(fileName);
//            System.out.println(fileDownloadUri);
//            System.out.println(file.getContentType());
//            System.out.println(file.getSize());
//            return null;
//        } catch (Exception e) {
//
//        }
//        return null;
//    }

//    @GetMapping("/downloadFile/{fileName:.+}")
//    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
//        // Load file as Resource
//        Resource resource = fileStorageService.loadFileAsResource(fileName);
//
//        // Try to determine file's content type
//        String contentType = null;
//        try {
//            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
//        } catch (IOException ex) {
//            logger.info("Could not determine file type.");
//        }
//
//        // Fallback to the default content type if type could not be determined
//        if(contentType == null) {
//            contentType = "application/octet-stream";
//        }
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);
//    }

//    @PostMapping(value = "switch-party/{party}")
//    public ResponseEntity<CordappResponse<Void>> switchParty(@PathVariable String party){
//        if(party.equals("PartyA")){
//            activeParty = partyAProxy;
//        }else if(party.equals("PartyB")){
//            activeParty = partyBProxy;
//        }else if(party.equals("PartyC")){
//            activeParty = partyCProxy;
//        }else{
//            return APIResponse.error("Unrecognised Party");
//        }
//        return getCashBalance();
//    }
}