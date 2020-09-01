package com.template.webserver.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.template.flows.kyc.ApproveKYC;
import com.template.flows.kyc.RejectKYC;
import com.template.flows.kyc.SubmitKYC;
import com.template.states.KYCState;
import com.template.states.UserState;
import com.template.webserver.NodeRPCConnection;
import com.template.webserver.models.CordappResponse;
import com.template.webserver.models.KYC;
import com.template.webserver.utils.CommonUtils;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.messaging.CordaRPCOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Define your API endpoints here.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }


    @GetMapping(value = "/buyer/list", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<List<UserState>>> listBuyer() throws Exception {
        CordappResponse<List<UserState>> response = new CordappResponse<List<UserState>>();
        try {
            List<UserState> userList = new ArrayList<>();
            List<StateAndRef<UserState>> kycStateAndRefList = proxy.vaultQuery(UserState.class).getStates();

            for(StateAndRef<UserState> item: kycStateAndRefList) {
                if(!"Buyer".equals(item.getState().getData().getRegisteredAs())) { continue; }

                item.getState().getData().setBuyer(null);
                item.getState().getData().setSeller(null);
                item.getState().getData().setLender(null);

                userList.add(item.getState().getData());
            }
            response.setMessage("User listed successfully.");
            response.setStatus(true);
            response.setData(userList);
            return new ResponseEntity<CordappResponse<List<UserState>>>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            if(e instanceof IllegalArgumentException) {
                return new ResponseEntity<CordappResponse<List<UserState>>>(response, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<CordappResponse<List<UserState>>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/seller/list", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<List<UserState>>> listSeller() throws Exception {
        CordappResponse<List<UserState>> response = new CordappResponse<List<UserState>>();
        try {
            List<UserState> userList = new ArrayList<>();
            List<StateAndRef<UserState>> kycStateAndRefList = proxy.vaultQuery(UserState.class).getStates();

            for(StateAndRef<UserState> item: kycStateAndRefList) {
                if(!"Seller".equals(item.getState().getData().getRegisteredAs())) { continue; }

                item.getState().getData().setBuyer(null);
                item.getState().getData().setSeller(null);
                item.getState().getData().setLender(null);

                userList.add(item.getState().getData());
            }
            response.setMessage("User listed successfully.");
            response.setStatus(true);
            response.setData(userList);
            return new ResponseEntity<CordappResponse<List<UserState>>>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            if(e instanceof IllegalArgumentException) {
                return new ResponseEntity<CordappResponse<List<UserState>>>(response, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<CordappResponse<List<UserState>>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}