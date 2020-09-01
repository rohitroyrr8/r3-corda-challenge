package com.template.webserver.controllers;

import com.template.flows.auth.Login;
import com.template.flows.auth.SignUp;
import com.template.flows.orders.ApprovePurchaseOrder;
import com.template.states.UserState;
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

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
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

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> signUp(@RequestBody User request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getOrganisationName() == null) { throw new IllegalArgumentException("Organisation name is required."); }
            if(request.getCountry() == null) { throw new IllegalArgumentException("Country is required."); }
            if(request.getEmail() == null) { throw new IllegalArgumentException("Email-address is required."); }
            if(request.getUsername() == null) { throw new IllegalArgumentException("Username name is required."); }
            if(request.getPassword() == null) { throw new IllegalArgumentException("Password name is required."); }
            if(request.getRegisteredAs() == null) { throw new IllegalArgumentException("Registered as is required."); }

            if(!request.getRegisteredAs().equals("Buyer") && !request.getRegisteredAs().equals("Seller") && !request.getRegisteredAs().equals("Lender")) {
                throw new IllegalArgumentException("Invalid party provided,");
            }

            proxy.startFlowDynamic(SignUp.class, CommonUtils.randomAlphaNumeric(16), request.getOrganisationName(),
                    request.getCountry(), request.getEmail(), request.getUsername(), CommonUtils.hash(request.getPassword()),
                    request.getRegisteredAs(),
                    proxy.partiesFromName("Buyer", false).iterator().next(),
                    proxy.partiesFromName("Seller",false).iterator().next(),
                    proxy.partiesFromName("Lender", false).iterator().next()).getReturnValue().get();

            response.setMessage("user registered successfully.");
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

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<UserState>> login(@RequestBody User request) throws Exception {
        CordappResponse<UserState> response = new CordappResponse<UserState>();
        try {
            if (request.getUsername() == null) { throw new IllegalArgumentException("Username name is required."); }
            if (request.getPassword() == null) { throw new IllegalArgumentException("Password name is required."); }
            if (request.getRegisteredAs() == null) { throw new IllegalArgumentException("RegisteredAs name is required."); }

            UserState output = proxy.startFlowDynamic(Login.class, request.getUsername(), CommonUtils.hash(request.getPassword())).getReturnValue().get();
            System.out.println(output.toString());
            if(!output.getRegisteredAs().equals(request.getRegisteredAs())) { throw new AuthenticationException("Invalid party selected."); }
            response.setMessage("User logged-in successfully");
            response.setData(output);
            return new ResponseEntity<CordappResponse<UserState>>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof ExecutionException) { return new ResponseEntity<CordappResponse<UserState>>(response.error(e.getMessage()), HttpStatus.UNAUTHORIZED); }
            if(e instanceof ExecutionException) { return new ResponseEntity<CordappResponse<UserState>>(response.error("Invalid username or password"), HttpStatus.BAD_REQUEST); }
            if (e instanceof IllegalArgumentException) { return new ResponseEntity<CordappResponse<UserState>>(response.error(e.getMessage()), HttpStatus.BAD_REQUEST);  }
            response.error("Something went wrong");
            return new ResponseEntity<CordappResponse<UserState>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
