package com.zacharywarunek.amazonclone;

import com.zacharywarunek.amazonclone.util.AuthRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MainController {

    private final Service service;

    @Autowired
    public MainController(Service service){
        this.service = service;
    }

    @RequestMapping(value = "/apiTest", method = RequestMethod.GET)
    public ResponseObject apiTest(){
        ResponseObject response = new ResponseObject();
        response.setData("inside /apiTest");
        response.setStatus(200);
        response.setMessage("API is functioning normally for environment: " + System.getenv("ENV"));
        return response;
    }
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseObject register(@RequestBody String payloadFromUI){
        return service.Register(payloadFromUI);
    }
//    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
//    public ResponseObject login(@RequestBody AuthRequest payloadFromUI) {
//        return service.Authenticate(payloadFromUI);
//    }
    @RequestMapping(value = "/checkAccountExists", method = RequestMethod.GET)
    @GetMapping
    public ResponseEntity<Object> checkAccountExists(@RequestParam String email){
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                new HashMap<>().put("exists", Boolean.toString(service.checkAccountExists(email))),
                httpHeaders,
                HttpStatus.FOUND);
    }
    @RequestMapping(value = "/getProduct", method = RequestMethod.GET)
    public ResponseObject getProduct(@RequestParam int productId){
        return service.getProduct(productId);
    }
    @RequestMapping(value = "/getAllProducts", method = RequestMethod.GET)
    public ResponseObject getAllProducts(){
        return service.getAllProducts();
    }
    @RequestMapping(value = "/searchProducts", method = RequestMethod.GET)
    public ResponseObject getAllProducts(@RequestParam String k, @RequestParam int c, @RequestParam boolean prime){

        return service.searchProducts(k, c, prime);
    }
    @RequestMapping(value = "/getProductImages", method = RequestMethod.GET)
    public ResponseObject getProductImages(@RequestParam int productId){
        return service.getProductImages(productId);
    }
    @RequestMapping(value = "/getAllCategories", method = RequestMethod.GET)
    public ResponseObject getAllCategories(){
        return service.getAllCategories();
    }
//    @RequestMapping(value = "/changePrimeMembership", method = RequestMethod.POST)
//    public ResponseObject getAllCategories(@RequestBody String payloadFromUI){
//        JSONObject jsonPayload = new JSONObject(payloadFromUI);
//        return service.changePrimeMembership(!jsonPayload.getBoolean("member"), jsonPayload.getInt("accountId"));
//    }
//    @RequestMapping(value = "/changeAccountDetails", method = RequestMethod.POST)
//    public ResponseObject changeAccountDetails(@RequestBody String payloadFromUI){
//        JSONObject jsonPayload = new JSONObject(payloadFromUI);
//        return service.changeAccountDetails(jsonPayload);
//    }
//    @RequestMapping(value = "/checkPassword", method = RequestMethod.POST)
//    public ResponseObject checkPassword(@RequestBody AuthRequest payloadFromUI){
//        return service.checkPassword(payloadFromUI);
//    }
    @RequestMapping(value = "/savePaymentMethod", method = RequestMethod.POST)
    public ResponseObject savePaymentMethod(@RequestBody String  payload){
        JSONObject json = new JSONObject(payload);
        return service.savePaymentMethod(json.getJSONObject("paymentMethod"));
    }
    @RequestMapping(value = "/getAllPaymentMethods", method = RequestMethod.GET)
    public ResponseObject getAllPaymentMethods(@RequestParam int accountId){
        return service.getAllPaymentMethods(accountId);
    }

    @RequestMapping(value = "/setPaymentMethodFavorite", method = RequestMethod.POST)
    public ResponseObject savePaymentMethodFavorite(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        return service.setPaymentMethodFavorite(jsonPayload);
    }
    @RequestMapping(value = "/getAllPaymentTypes", method = RequestMethod.GET)
    public ResponseObject getAllPaymentTypes(){
        return service.getAllPaymentTypes();
    }
    @RequestMapping(value = "/deletePaymentMethod", method = RequestMethod.POST)
    public ResponseObject deletePaymentMethod(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        return service.deletePaymentMethod(jsonPayload);
    }
    @RequestMapping(value = "/saveProduct", method = RequestMethod.POST)
    public ResponseObject saveProduct(@RequestBody String  payload){
        JSONObject json = new JSONObject(payload);
        return service.saveProduct(json);
    }
    @RequestMapping(value = "/saveAddress", method = RequestMethod.POST)
    public ResponseObject saveAddress(@RequestBody String  payload){
        JSONObject json = new JSONObject(payload);
        return service.saveAddress(json.getJSONObject("address"));
    }
    @RequestMapping(value = "/getAllAddresses", method = RequestMethod.GET)
    public ResponseObject getAllAddresses(@RequestParam int accountId){
        return service.getAllAddresses(accountId);
    }
    @RequestMapping(value = "/setAddressFavorite", method = RequestMethod.POST)
    public ResponseObject setAddressFavorite(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        return service.setAddressFavorite(jsonPayload);
    }
    @RequestMapping(value = "/deleteAddress", method = RequestMethod.POST)
    public ResponseObject deleteAddress(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        return service.deleteAddress(jsonPayload);
    }
    @RequestMapping(value = "/fetchCartItems", method = RequestMethod.GET)
    public ResponseObject fetchCartItems(@RequestParam int accountId){
        return service.fetchCartItems(accountId);
    }
    @RequestMapping(value = "/removeCartItem", method = RequestMethod.POST)
    public ResponseObject removeCartItem(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        return service.removeCartItem(jsonPayload);
    }
    @RequestMapping(value = "/changeQuantityCartItem", method = RequestMethod.PATCH)
    public ResponseObject changeQuantityCartItem(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        return service.changeQuantityCartItem(jsonPayload);
    }
    @RequestMapping(value = "/addToCart", method = RequestMethod.POST)
    public ResponseObject addToCart(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        return service.addToCart(jsonPayload);
    }
}
