package com.template.webserver.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.template.flows.IssueMetal;
import com.template.flows.TransferMetal;
import com.template.flows.kyc.RejectKYC;
import com.template.flows.kyc.SubmitKYC;
import com.template.flows.orders.*;
import com.template.states.KYCState;
import com.template.states.MetalState;
import com.template.states.PurchaseOrderState;
import com.template.states.UserState;
import com.template.webserver.NodeRPCConnection;
import com.template.webserver.models.CordappResponse;
import com.template.webserver.models.KYC;
import com.template.webserver.models.Metal;
import com.template.webserver.models.PurchaseOrder;
import com.template.webserver.utils.CommonUtils;
import net.corda.client.rpc.reconnect.CouldNotStartFlowException;
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
@CrossOrigin(origins = "*", allowedHeaders = "*")
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
            if(request.getUsername() == null ) { throw new IllegalArgumentException("Buyer Name is required."); }

            if(request.getInterestRate() <= 0) { throw new IllegalArgumentException("Interest rate is required."); }
            if(request.getPeriod() <= 0) {throw new IllegalArgumentException("Period is required."); }

            if(request.getBuyerName() == null ) { throw new IllegalArgumentException("Buyer name is required."); }
            if(request.getSellerName() == null ){  throw new IllegalArgumentException("Seller name is required."); }

            // checking whether asked amount is under user's credit limit or not
            List<StateAndRef<UserState>> userStateAndRefList = proxy.vaultQuery(UserState.class).getStates();
            boolean isUserRegistered=false;
            UserState userState = null;
            for(StateAndRef<UserState> item : userStateAndRefList) {
                if(item.getState().getData().getUsername().equals(request.getUsername())) {
                    isUserRegistered = true;
                    userState = item.getState().getData();
                    break;
                }
            }

            if(!isUserRegistered) { throw new IllegalArgumentException("Invalid username provided."); }
            if(userState.getLastCreditLimit() < request.getRate()*request.getQuantity()) { throw new IllegalArgumentException("This order is exceeding approved credit limit"); }

            proxy.startFlowDynamic(SubmitPurchaseOrder.class, CommonUtils.randomAlphaNumeric(4), request.getInterestRate(), request.getPeriod(),
                    request.getBuyerName(), request.getSellerName(),
                    request.getName(),request.getModel(), request.getCompanyName(), request.getColor(),
                    request.getFuelType(), request.getRate(), request.getQuantity(), request.getRate()*request.getQuantity(),
                    request.getUsername(), proxy.partiesFromName("Seller", false).iterator().next(),
                    proxy.partiesFromName("Lender", false).iterator().next()).getReturnValue().get();

            response.setMessage("Order submitted successfully");
            response.setStatus(true);
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            if(e instanceof IllegalArgumentException) { return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST); }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> confirmPurchaseOrder(@RequestBody PurchaseOrder request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getIdentifier() == null) { throw new IllegalArgumentException("Identifier name is required."); }

            proxy.startFlowDynamic(ConfirmPurchaseOrder.class, request.getIdentifier(), proxy.partiesFromName("Buyer", false).iterator().next(),
                    proxy.partiesFromName("Seller", false).iterator().next()).getReturnValue().get();
            response.setMessage("Order confirmed successfully.");
            response.setStatus(true);
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            if(e instanceof IllegalArgumentException) { return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST); }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/deny", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> denyPurchaseOrder(@RequestBody PurchaseOrder request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getIdentifier() == null) { throw new IllegalArgumentException("Identifier name is required."); }

            proxy.startFlowDynamic(DenyPurchaseOrder.class, request.getIdentifier(), proxy.partiesFromName("Buyer", false).iterator().next(),
                    proxy.partiesFromName("Seller", false).iterator().next()).getReturnValue().get();
            response.setMessage("Order denied successfully.");
            response.setStatus(true);
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            if(e instanceof FlowException) {
                response.setMessage(e.getMessage().substring(30));
                return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST);
            }
            if(e instanceof IllegalArgumentException) { return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST); }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/approve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> approvePurchaseOrder(@RequestBody PurchaseOrder request) throws Exception {
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
            if(e instanceof IllegalArgumentException) { return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST); }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> rejectPurchaseOrder(@RequestBody PurchaseOrder request) throws Exception {
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
            if(e instanceof IllegalArgumentException) { return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST); }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/sanction", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> sanctionPurchaseOrder(@RequestBody PurchaseOrder request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getIdentifier() == null) { throw new IllegalArgumentException("Identifier name is required."); }

            proxy.startFlowDynamic(SanctionPurchaseOrder.class, request.getIdentifier()).getReturnValue().get();
            response.setMessage("Order's sanctioned successfully.");
            response.setStatus(true);
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            if(e instanceof FlowException) {
                response.setMessage(e.getMessage().substring(30));
                return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST);
            }
            if(e instanceof IllegalArgumentException) { return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST); }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/start-shipment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> startShipmentPurchaseOrder(@RequestBody PurchaseOrder request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getIdentifier() == null) { throw new IllegalArgumentException("Identifier name is required."); }
            if(request.getSupplyBillsUrl() == null) { throw new IllegalArgumentException("Supply Bills Url is required."); }

            proxy.startFlowDynamic(StartShipmentPurchaseOrder.class, request.getIdentifier(), request.getSupplyBillsUrl()).getReturnValue().get();
            response.setMessage("Order's shipment started successfully.");
            response.setStatus(true);
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            if(e instanceof FlowException) {
                response.setMessage(e.getMessage().substring(30));
                return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST);
            }
            if(e instanceof IllegalArgumentException) { return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST); }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/mark-receive", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> markReceivePurchaseOrder(@RequestBody PurchaseOrder request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getIdentifier() == null) { throw new IllegalArgumentException("Identifier name is required."); }
            if(request.getGrnUrl() == null) { throw new IllegalArgumentException("GRN Url is required."); }

            proxy.startFlowDynamic(MarkReceivedPurchaseOrder.class, request.getIdentifier(), request.getGrnUrl()).getReturnValue().get();
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
            if(e instanceof IllegalArgumentException) { return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST); }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/invoice/raise", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> raiseInvoice(@RequestBody PurchaseOrder request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getIdentifier() == null) { throw new IllegalArgumentException("Identifier name is required."); }

            proxy.startFlowDynamic(RaiseInvoiceOnPurchaseOrder.class, request.getIdentifier()).getReturnValue().get();
            response.setMessage("Invoice against purchase order raised successfully.");
            response.setStatus(true);
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            if(e instanceof FlowException) {
                response.setMessage(e.getMessage().substring(30));
                return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST);
            }
            if(e instanceof IllegalArgumentException) { return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST); }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/invoice/pay", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<Void>> payInvoice(@RequestBody PurchaseOrder request) throws Exception {
        CordappResponse<Void> response = new CordappResponse<Void>();
        try {
            if(request.getIdentifier() == null) { throw new IllegalArgumentException("Identifier name is required."); }

            proxy.startFlowDynamic(PayInvoiceOnPurchaseOrder.class, request.getIdentifier()).getReturnValue().get();
            response.setMessage("Invoice against purchase order paid successfully.");
            response.setStatus(true);
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            if(e instanceof FlowException) {
                response.setMessage(e.getMessage().substring(30));
                return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST);
            }
            if(e instanceof IllegalArgumentException) { return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.BAD_REQUEST); }
            return new ResponseEntity<CordappResponse<Void>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<List<PurchaseOrderState>>> listPurchaseOrder(@RequestParam(name = "username") String username,
                                                                                   @RequestParam(name = "registeredAs") String registeredAs) throws Exception {
        CordappResponse<List<PurchaseOrderState>> response = new CordappResponse<List<PurchaseOrderState>>();
        try {
            if(username == null) { throw new IllegalArgumentException("Username is required."); }
            if(registeredAs == null) { throw new IllegalArgumentException("registeredAs is required."); }

            List<PurchaseOrderState> orderList = new ArrayList<>();
            List<StateAndRef<PurchaseOrderState>> purchaseOrderStateAndRefList = proxy.vaultQuery(PurchaseOrderState.class).getStates();

            for(StateAndRef<PurchaseOrderState> item: purchaseOrderStateAndRefList) {
                if(!registeredAs.equals("Lender")) {
                    if(!username.equals(item.getState().getData().getSellerName()) && !username.equals(item.getState().getData().getBuyerName())) { continue; }
                }

//                PurchaseOrder order = new PurchaseOrder(item.getState().getData().getIdentifier(), item.getState().getData().getInterestRate(),
//                        item.getState().getData().getPeriod(), item.getState().getData().getBuyerName(), item.getState().getData().getSellerName(),
//                        item.getState().getData().getName(), item.getState().getData().getModel(), item.getState().getData().getCompanyName(),
//                        item.getState().getData().getColor(), item.getState().getData().getFuelType(), item.getState().getData().getRate(),
//                        item.getState().getData().getQuantity(), item.getState().getData().getAmount(), item.getState().getData().getSupplyBillsUrl(),
//                        item.getState().getData().getGrnUrl(), item.getState().getData().getBuyerName(), item.getState().getData().getCreatedOn(),
//                        item.getState().getData().getStatus(), item.getState().getData().getAmountPaid(), item.getState().getData().getInvoices());

                item.getState().getData().setBuyer(null);
                item.getState().getData().setSeller(null);
                item.getState().getData().setLender(null);

//                ObjectMapper objectMapper = new ObjectMapper();
//                objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//                String responseString = objectMapper.writeValueAsString(order);

                orderList.add(item.getState().getData());
            }
            response.setMessage("Purchase Order listed");
            response.setStatus(true);
            response.setData(orderList);
            return new ResponseEntity<CordappResponse<List<PurchaseOrderState>>>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            if(e instanceof IllegalArgumentException) { return new ResponseEntity<CordappResponse<List<PurchaseOrderState>>>(response, HttpStatus.BAD_REQUEST); }
            return new ResponseEntity<CordappResponse<List<PurchaseOrderState>>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}