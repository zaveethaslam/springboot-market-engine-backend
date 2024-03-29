package com.app.marketengine.matchmaker.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.marketengine.matchmaker.beans.Links;
import com.app.marketengine.matchmaker.beans.Orders;
import com.app.marketengine.matchmaker.entity.BuyBook;
import com.app.marketengine.matchmaker.entity.SellBook;
import com.app.marketengine.matchmaker.services.BuyBookServices;
import com.app.marketengine.matchmaker.services.MatchingExecutorService;
import com.app.marketengine.matchmaker.services.SellBookServices;

@RestController
@ComponentScan(basePackages = "com.app.marketengine.matchmaker")
public class MatchMakerMainController {
	
	@Autowired
	private BuyBookServices buyBookServices;
	
	@Autowired
	private SellBookServices sellBookServices;
	
	@Autowired
	private MatchingExecutorService matchingService;
			
	@GetMapping("/")
	public String getRoot() {
		return getAllLinks().toString();
	}
	
	public List<Links> getAllLinks(){
		List<Links> returnList = new ArrayList<Links>();
		returnList.add(new Links("/api/v1/orders", "POST", "For Making Book/Sell Orders"));
		returnList.add(new Links("/api/v1/buyOrders", "GET", "This returns list of buy orders places in the system"));
		returnList.add(new Links("/api/v1/sellOrders", "GET", "This returns list of sell orders places in the system"));
		returnList.add(new Links("/api/v1/processOrders", "GET", "This returns list of matches and unmatched orders in the system"));
		
		return returnList;		
	}
	
	
	@PostMapping("/api/v1/orders")
	public <T> ResponseEntity<T> processOrders(@RequestBody Orders orders){
		HttpHeaders responseHeaders = new HttpHeaders();
		System.out.println(orders);
		if(orders.getSaleType().equalsIgnoreCase("Buy")) {
			buyBookServices.saveBooks(orders.getUserID(), Integer.parseInt(orders.getPrice()));
		} else if(orders.getSaleType().equalsIgnoreCase("Sell")) {
			sellBookServices.saveBooks(orders.getUserID(), Integer.parseInt(orders.getPrice()));
		}
		return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);
	}
	
	@GetMapping("/api/v1/buyOrders")
	public List<BuyBook> getAllBuyBook(){
		return buyBookServices.getAllBuyOrders();
	}
	
	@GetMapping("/api/v1/sellOrders")
	public List<SellBook> getAllSellBook(){
		return sellBookServices.getAllSellOrders();
	}
	
	@GetMapping("/api/v1/processOrders")
	public List<List<Object>> processOrders(){
		return matchingService.processOrder(buyBookServices.getAllBuyOrders(), sellBookServices.getAllSellOrders());
	}
	
	@GetMapping("/api/v1/deleteAllRecords")
	public String deleteOrders(){
		buyBookServices.deleteAllRecords();
		sellBookServices.deleteAllRecords();
		return "Success";
	}

}
