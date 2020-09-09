package com.template.webserver.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.template.flows.auth.UpdateCreditLimit;
import com.template.flows.kyc.ApproveKYC;
import com.template.flows.kyc.RejectKYC;
import com.template.flows.kyc.SubmitKYC;
import com.template.states.KYCState;
import com.template.webserver.NodeRPCConnection;
import com.template.webserver.models.CordappResponse;
import com.template.webserver.models.KYC;
import com.template.webserver.utils.CommonUtils;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Define your API endpoints here.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/kyc")
public class KYCController {

    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(KYCController.class);

    public KYCController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }


    @PostMapping(value = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> submitKYC(@RequestBody KYCState request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getUsername() == null) { throw new IllegalArgumentException("Username name is required."); }
            if(request.getAadharNumber() == null) { throw new IllegalArgumentException("Aadhar Number is required."); }
            if(request.getPanNumber() == null) { throw new IllegalArgumentException("PAN Number is required."); }
            if(request.getCompanyPanNumber() == null) { throw new IllegalArgumentException("Company PAN Number is required."); }
            if(request.getCompanyName() == null) { throw new IllegalArgumentException("Company Name is required."); }
            if(request.getIncorporationNumber() <=0 ) { throw new IllegalArgumentException("Incorporation Number is required."); }
            if(request.getIncorporationDate() == null) { throw new IllegalArgumentException("Incorporation Date is required."); }
            if(request.getIncorporationPlace() == null) { throw new IllegalArgumentException("Incorporation Place is required."); }
            if(request.getCibilScore() <= 0) { throw new IllegalArgumentException("CIBIL Score is required."); }
            if(request.getCreditLimit() == null) { throw new IllegalArgumentException("Credit Limit is required."); }
            if(request.getCibilScore() < 300 || request.getCibilScore() > 900) { throw new IllegalArgumentException("CIBIL Score must be between 300 to 900.."); }

            if(request.getAadharUrl() == null) { throw new IllegalArgumentException("Aadhar URL is required."); }
            if(request.getPersonalPANUrl() == null) { throw new IllegalArgumentException("Personal PAN url is required."); }
            if(request.getCompanyPANUrl() == null) { throw new IllegalArgumentException("Company PAN url is required."); }
            if(request.getCertificateOfIncorporationUrl() == null) { throw new IllegalArgumentException("Incorporation Certificate URL is required."); }

            if(request.getLastYearStatement() == null) { throw new IllegalArgumentException("Last year statement url is required."); }
            if(request.getSecondLastYearStatement() == null) { throw new IllegalArgumentException("Second last year url is required."); }
            if(request.getThirdLastYearStatement() == null) { throw new IllegalArgumentException("Third last year url is required."); }

            proxy.startFlowDynamic(SubmitKYC.class, CommonUtils.randomAlphaNumeric(4), request.getUsername(),
                    request.getAadharNumber(), request.getPanNumber(), request.getCompanyPanNumber(), request.getIncorporationNumber(),
                    request.getCompanyName(), request.getIncorporationDate(),request.getIncorporationPlace(),
                    request.getCibilScore(), request.getCreditLimit(), proxy.partiesFromName("Lender", false).iterator().next(),
                    request.getAadharUrl(), request.getPersonalPANUrl(), request.getCompanyPANUrl(), request.getCertificateOfIncorporationUrl(),
                    request.getLastYearStatement(), request.getSecondLastYearStatement(), request.getThirdLastYearStatement()).getReturnValue().get();

            response.setMessage("KYC submitted successfully");
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

    @PostMapping(value = "/approve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> approveKYC(@RequestBody KYC request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getIdentifier() == null) { throw new IllegalArgumentException("Identifier name is required."); }
            if(request.getCreditLimit() <= 0) { throw new IllegalArgumentException("Credit Limit is required."); }
            if(request.getUsername() == null) { throw new IllegalArgumentException("Username is required."); }

            DecimalFormat decimalFormat = new DecimalFormat("0.00");

            proxy.startFlowDynamic(ApproveKYC.class, request.getIdentifier(), Double.parseDouble(decimalFormat.format(request.getCreditLimit())), proxy.partiesFromName("Buyer", false).iterator().next()).getReturnValue().get();
            // update approved credit limit in user object
            System.out.println("in approve kyc");
            System.out.println(request.getCreditLimit());
            System.out.println(request.getIdentifier());
            System.out.println(request.getUsername());
            System.out.println(Double.parseDouble(decimalFormat.format(request.getCreditLimit())));
            proxy.startFlowDynamic(UpdateCreditLimit.class, request.getUsername(), Double.parseDouble(decimalFormat.format(request.getCreditLimit()))).getReturnValue().get();

            response.setMessage("KYC approved successfully.");
            response.setStatus(true);
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            if(e instanceof IllegalArgumentException) {
                return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> rejectKYC(@RequestBody KYCState request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getIdentifier() == null) { throw new IllegalArgumentException("Identifier name is required."); }

            proxy.startFlowDynamic(RejectKYC.class, request.getIdentifier(), proxy.partiesFromName("Buyer", false).iterator().next()).getReturnValue().get();
            response.setMessage("KYC rejected successfully.");
            response.setStatus(true);
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            if(e instanceof IllegalArgumentException) {
                return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<List<KYCState>>> list(@RequestParam(name = "username") String username,
                                                                 @RequestParam(name = "registeredAs") String registeredAs) throws Exception {
        CordappResponse<List<KYCState>> response = new CordappResponse<List<KYCState>>();
        try {
            if(username == null) { throw new IllegalArgumentException("Username is required."); }
            if(registeredAs == null) { throw new IllegalArgumentException("registeredAs is required."); }

            List<KYCState> kycList = new ArrayList<>();
            List<StateAndRef<KYCState>> kycStateAndRefList = proxy.vaultQuery(KYCState.class).getStates();

            for(StateAndRef<KYCState> item: kycStateAndRefList) {
                if(!registeredAs.equals("Lender")) {
                    if(!username.equals(item.getState().getData().getUsername())) { continue; }
                }

//                KYCState kyc = new KYCState(item.getState().getData().getIdentifier(),
//                        item.getState().getData().getUsername(),
//                        item.getState().getData().getAadharNumber(),
//                        item.getState().getData().getPanNumber(),
//                        item.getState().getData().getCompanyPanNumber(),
//                        item.getState().getData().getIncorporationNumber(),
//                        item.getState().getData().getCompanyName(),
//                        item.getState().getData().getIncorporationDate(),
//                        item.getState().getData().getIncorporationPlace(),
//                        item.getState().getData().getCibilScore(),
//                        item.getState().getData().getCreditLimit(),
//                        item.getState().getData().getStatus(),
//                        item.getState().getData().getCreatedOn(),
//                        null, null);

                item.getState().getData().setLender(null);
                item.getState().getData().setOwner(null);

//                ObjectMapper objectMapper = new ObjectMapper();
//                objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//                String responseString = objectMapper.writeValueAsString(kyc);

                kycList.add(item.getState().getData());
            }
            response.setMessage("KYC listed");
            response.setStatus(true);
            response.setData(kycList);
            return new ResponseEntity<CordappResponse<List<KYCState>>>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            if(e instanceof IllegalArgumentException) {
                return new ResponseEntity<CordappResponse<List<KYCState>>>(response, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<CordappResponse<List<KYCState>>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}