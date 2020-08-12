package com.template.webserver.controllers;

import com.template.webserver.NodeRPCConnection;
import com.template.webserver.models.CordappResponse;
import com.template.webserver.models.Item;
import com.template.webserver.models.User;
import net.corda.core.messaging.CordaRPCOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> login(@RequestBody User userDetails) {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(!userDetails.getUsername().equals(this.username) || !userDetails.getPassword().equals(this.password)) {
                response.setMessage("Invalid Credentials. Please try again.");
                return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.UNAUTHORIZED);
            }
            response.setStatus(true);
            response.setMessage("Login Successful");
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
