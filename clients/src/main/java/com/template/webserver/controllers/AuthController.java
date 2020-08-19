package com.template.webserver.controllers;

import com.template.flows.auth.Login;
import com.template.flows.auth.SignUp;
import com.template.flows.orders.ApprovePurchaseOrder;
import com.template.webserver.NodeRPCConnection;
import com.template.webserver.models.CordappResponse;
import com.template.webserver.models.Item;
import com.template.webserver.models.KYC;
import com.template.webserver.models.User;
import com.template.webserver.utils.CommonUtils;
import net.corda.core.flows.FlowException;
import net.corda.core.messaging.CordaRPCOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(AuthController.class);

    private String username = "user1";
    private String password = "test";

    public AuthController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

//    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    private ResponseEntity<CordappResponse<Void>> login(@RequestBody User userDetails) {
//        CordappResponse<Void> response = new CordappResponse<Void>();
//        try {
//            if(!userDetails.getUsername().equals(this.username) || !userDetails.getPassword().equals(this.password)) {
//                response.setMessage("Invalid Credentials. Please try again.");
//                return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.UNAUTHORIZED);
//            }
//            response.setStatus(true);
//            response.setMessage("Login Successful");
//            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);
//
//        } catch (Exception e) {
//            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> signup(@RequestBody User request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getOrganisationName() == null) { throw new IllegalArgumentException("Organisation name is required."); }
            if(request.getCountry() == null) { throw new IllegalArgumentException("Country is required."); }
            if(request.getEmail() == null) { throw new IllegalArgumentException("Email-address is required."); }
            if(request.getUsername() == null) { throw new IllegalArgumentException("Username name is required."); }
            if(request.getPassword() == null) { throw new IllegalArgumentException("Password name is required."); }
            if(request.getRegisteredAs() == null) { throw new IllegalArgumentException("Registered as is required."); }
            if(!request.getRegisteredAs().equals("Buyer") && !request.getRegisteredAs().equals("Seller")) { throw new IllegalArgumentException("Invalid Party provided."); }

            proxy.startFlowDynamic(SignUp.class, CommonUtils.randomAlphaNumeric(16), request.getOrganisationName(),
                    request.getCountry(), request.getEmail(), request.getUsername(), request.getPassword(),
                    request.getRegisteredAs(), proxy.partiesFromName("Lender", false).iterator().next()).getReturnValue().get();

            response.setMessage("User registered successfully.");
            response.setStatus(true);
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            if (e instanceof FlowException) {
                response.setMessage(e.getMessage().substring(30));
                return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> login(@RequestBody User request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if (request.getUsername() == null) {
                throw new IllegalArgumentException("Username name is required.");
            }
            if (request.getPassword() == null) {
                throw new IllegalArgumentException("Password name is required.");
            }

            proxy.startFlowDynamic(Login.class, request.getUsername(), request.getPassword()).getReturnValue().get();

            response.setMessage("User loggedIn successfully.");
            response.setStatus(true);
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            if (e instanceof IllegalArgumentException) {
                return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
