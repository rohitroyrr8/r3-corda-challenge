package com.template.webserver.controllers;

import com.template.webserver.NodeRPCConnection;
import com.template.webserver.models.CordappResponse;
import com.template.webserver.models.Item;
import net.corda.core.messaging.CordaRPCOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(ItemController.class);

    public ItemController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<CordappResponse<List<Item>>> list() {
        CordappResponse<List<Item>> response = new CordappResponse<List<Item>>();
        try {
            List<Item> itemArrayList = new ArrayList<>();

            Item item1 = new Item("Swift Dezire", "2019", "Maruti Suzuki", "white", "Patrol", 650000.0);
            Item item2 = new Item("Scorpio S9", "2020", "Mahindra & Mahindra", "Black", "Diesel", 890000.0);
            Item item3 = new Item("Duster", "2019", "Renault", "Red", "Diesel", 730000.0);
            Item item4 = new Item("Breeza", "2020", "Maruti Suzuki", "White", "Diesel", 120000.0);
            Item item5 = new Item("I20", "2019", "Hyundai", "Blue", "Patrol", 890000.0);

            itemArrayList.add(item1);
            itemArrayList.add(item2);
            itemArrayList.add(item3);
            itemArrayList.add(item4);
            itemArrayList.add(item5);

            response.setStatus(true);
            response.setMessage("Item list fetched.");
            response.setData(itemArrayList);
            return new ResponseEntity<CordappResponse<List<Item>>>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<CordappResponse<List<Item>>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
