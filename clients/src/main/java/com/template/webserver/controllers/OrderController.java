package com.template.webserver.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.template.flows.IssueMetal;
import com.template.flows.TransferMetal;
import com.template.flows.kyc.RejectKYC;
import com.template.flows.kyc.SubmitKYC;
import com.template.flows.orders.ApprovePurchaseOrder;
import com.template.flows.orders.MarkReceivedPurchaseOrder;
import com.template.flows.orders.RejectPurchaseOrder;
import com.template.flows.orders.SubmitPurchaseOrder;
import com.template.states.KYCState;
import com.template.states.MetalState;
import com.template.states.PurchaseOrderState;
import com.template.webserver.NodeRPCConnection;
import com.template.webserver.models.CordappResponse;
import com.template.webserver.models.KYC;
import com.template.webserver.models.Metal;
import com.template.webserver.models.PurchaseOrder;
import com.template.webserver.utils.CommonUtils;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
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
@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600)
@RestController
@RequestMapping("/api/order") // The paths for HTTP requests are relative to this base path.
public class OrderController {

     private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @PostMapping(value = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> submitPurchaseOrder(@RequestBody PurchaseOrder request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getName() == null) { throw new IllegalArgumentException("Name is required."); }
            if(request.getModel() == null) { throw new IllegalArgumentException("PAN Number is required."); }
            if(request.getCompanyName() == null) { throw new IllegalArgumentException("Company Name is required."); }
            if(request.getColor() == null ) { throw new IllegalArgumentException("Color is required."); }
            if(request.getFuelType() == null) { throw new IllegalArgumentException("Fuel Type is required."); }
            if(request.getRate() == null) { throw new IllegalArgumentException("Rate is required."); }
            if(request.getQuantity() <= 0 ) { throw new IllegalArgumentException("Quantity is required."); }
            if(request.getBuyerName() == null ) { throw new IllegalArgumentException("Buyer Name is required."); }

            proxy.startFlowDynamic(SubmitPurchaseOrder.class, CommonUtils.randomAlphaNumeric(16), request.getName(),
                    request.getModel(), request.getCompanyName(), request.getColor(),
                    request.getFuelType(), request.getRate(), request.getQuantity(), request.getRate()*request.getQuantity(),
                    request.getBuyerName(), proxy.partiesFromName("Seller", false).iterator().next(),
                    proxy.partiesFromName("Lender", false).iterator().next()).getReturnValue().get();

            response.setMessage("Order submitted successfully");
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
    private ResponseEntity<CordappResponse<Void>> approvePurchaseOrder(@RequestBody KYC request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getIdentifier() == null) { throw new IllegalArgumentException("Identifier name is required."); }

            proxy.startFlowDynamic(ApprovePurchaseOrder.class, request.getIdentifier(), proxy.partiesFromName("Buyer", false).iterator().next(),
                    proxy.partiesFromName("Lender", false).iterator().next()).getReturnValue().get();
            response.setMessage("Order approved successfully.");
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

    @PostMapping(value = "/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> rejectPurchaseOrder(@RequestBody KYC request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getIdentifier() == null) { throw new IllegalArgumentException("Identifier name is required."); }

            proxy.startFlowDynamic(RejectPurchaseOrder.class, request.getIdentifier(), proxy.partiesFromName("Buyer", false).iterator().next(),
                    proxy.partiesFromName("Lender", false).iterator().next()).getReturnValue().get();
            response.setMessage("Order rejected successfully.");
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

    @PostMapping(value = "/mark-receive", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> markReceivePurchaseOrder(@RequestBody KYC request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getIdentifier() == null) { throw new IllegalArgumentException("Identifier name is required."); }

            proxy.startFlowDynamic(MarkReceivedPurchaseOrder.class, request.getIdentifier(), proxy.partiesFromName("Seller", false).iterator().next(),
                    proxy.partiesFromName("Lender", false).iterator().next()).getReturnValue().get();
            response.setMessage("Order marked as received successfully.");
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

    @PostMapping(value = "/pay-emi", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> payEMIOnPurchaseOrder(@RequestBody KYC request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getIdentifier() == null) { throw new IllegalArgumentException("Identifier name is required."); }

            proxy.startFlowDynamic(MarkReceivedPurchaseOrder.class, request.getIdentifier(), proxy.partiesFromName("Seller", false).iterator().next(),
                    proxy.partiesFromName("Lender", false).iterator().next()).getReturnValue().get();
            response.setMessage("EMI paid for Order successfully.");
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
    private ResponseEntity<CordappResponse<List<PurchaseOrder>>> listPurchaseOrder(@RequestParam(name = "username") String username) throws Exception {
        CordappResponse<List<PurchaseOrder>> response = new CordappResponse<List<PurchaseOrder>>();
        try {
            if(username == null) { throw new IllegalArgumentException("Username is required."); }

            List<PurchaseOrder> orderList = new ArrayList<>();
            List<StateAndRef<PurchaseOrderState>> purchaseOrderStateAndRefList = proxy.vaultQuery(PurchaseOrderState.class).getStates();

            for(StateAndRef<PurchaseOrderState> item: purchaseOrderStateAndRefList) {
                if(!username.equals(item.getState().getData().getUsername())) { continue;}

                PurchaseOrder order = new PurchaseOrder(item.getState().getData().getIdentifier(),
                        item.getState().getData().getName(),
                        item.getState().getData().getModel(),
                        item.getState().getData().getCompanyName(),
                        item.getState().getData().getColor(),
                        item.getState().getData().getFuelType(),
                        item.getState().getData().getRate(),
                        item.getState().getData().getQuantity(),
                        item.getState().getData().getAmount(),
                        item.getState().getData().getUsername(),
                        item.getState().getData().getCreatedOn(),
                        item.getState().getData().getStatus(),
                        item.getState().getData().getAmountPaid(),
                        null, null);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                String responseString = objectMapper.writeValueAsString(order);

                orderList.add(order);
            }
            response.setMessage("Purchase Order listed");
            response.setStatus(true);
            response.setData(orderList);
            return new ResponseEntity<CordappResponse<List<PurchaseOrder>>>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return new ResponseEntity<CordappResponse<List<PurchaseOrder>>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}