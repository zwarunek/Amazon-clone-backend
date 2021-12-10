package com.zacharywarunek.amazonclone;

import com.zacharywarunek.amazonclone.util.AuthRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController
public class MainController {

    @Autowired
    private Service service;

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
        ResponseObject response = service.Register(payloadFromUI);
        return response;
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseObject login(@RequestBody AuthRequest payloadFromUI) throws Exception {
        ResponseObject response = service.Authenticate(payloadFromUI);
        return response;
    }
    @RequestMapping(value = "/checkAccountExists", method = RequestMethod.GET)
    public ResponseObject checkAccountExists(@RequestParam String email){
        ResponseObject response = service.checkAccountExists(email);
        return response;
    }

    @RequestMapping(value = "/getProduct", method = RequestMethod.GET)
    public ResponseObject getProduct(@RequestParam int productId){
        ResponseObject response = service.getProduct(productId);
        return response;
    }
    @RequestMapping(value = "/getAllProducts", method = RequestMethod.GET)
    public ResponseObject getAllProducts(){
        ResponseObject response = service.getAllProducts();
        return response;
    }
    @RequestMapping(value = "/searchProducts", method = RequestMethod.GET)
    public ResponseObject getAllProducts(@RequestParam String k, @RequestParam int c, @RequestParam boolean prime){

        ResponseObject response = service.searchProducts(k, c, prime);
        return response;
    }
    @RequestMapping(value = "/getProductImages", method = RequestMethod.GET)
    public ResponseObject getProductImages(@RequestParam int productId){
        ResponseObject response = service.getProductImages(productId);
        return response;
    }
    @RequestMapping(value = "/getAllCategories", method = RequestMethod.GET)
    public ResponseObject getAllCategories(){
        ResponseObject response = service.getAllCategories();
        return response;
    }
    @RequestMapping(value = "/changePrimeMembership", method = RequestMethod.POST)
    public ResponseObject getAllCategories(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        ResponseObject response = service.changePrimeMembership(!jsonPayload.getBoolean("member"), jsonPayload.getInt("accountId"));
        return response;
    }
    @RequestMapping(value = "/changeAccountDetails", method = RequestMethod.POST)
    public ResponseObject changeAccountDetails(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        ResponseObject response = service.changeAccountDetails(jsonPayload);
        return response;
    }

    @RequestMapping(value = "/checkPassword", method = RequestMethod.POST)
    public ResponseObject checkPassword(@RequestBody AuthRequest payloadFromUI){
        ResponseObject response = service.checkPassword(payloadFromUI);
        return response;
    }
    @RequestMapping(value = "/savePaymentMethod", method = RequestMethod.POST)
    public ResponseObject savePaymentMethod(@RequestBody String  payload){
        JSONObject json = new JSONObject(payload);
        ResponseObject response = service.savePaymentMethod(json.getJSONObject("paymentMethod"));
        return response;
    }
    @RequestMapping(value = "/getAllPaymentMethods", method = RequestMethod.GET)
    public ResponseObject getAllPaymentMethods(@RequestParam int accountId){
        ResponseObject response = service.getAllPaymentMethods(accountId);
        return response;
    }

    @RequestMapping(value = "/setPaymentMethodFavorite", method = RequestMethod.POST)
    public ResponseObject savePaymentMethodFavorite(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        ResponseObject response = service.setPaymentMethodFavorite(jsonPayload);
        return response;
    }
    @RequestMapping(value = "/getAllPaymentTypes", method = RequestMethod.GET)
    public ResponseObject getAllPaymentTypes(){
        ResponseObject response = service.getAllPaymentTypes();
        return response;
    }

    @RequestMapping(value = "/deletePaymentMethod", method = RequestMethod.POST)
    public ResponseObject deletePaymentMethod(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        ResponseObject response = service.deletePaymentMethod(jsonPayload);
        return response;
    }
    @RequestMapping(value = "/saveProduct", method = RequestMethod.POST)
    public ResponseObject saveProduct(@RequestBody String  payload){
        JSONObject json = new JSONObject(payload);
        ResponseObject response = service.saveProduct(json);
        return response;
    }
    @RequestMapping(value = "/saveAddress", method = RequestMethod.POST)
    public ResponseObject saveAddress(@RequestBody String  payload){
        JSONObject json = new JSONObject(payload);
        ResponseObject response = service.saveAddress(json.getJSONObject("address"));
        return response;
    }
    @RequestMapping(value = "/getAllAddresses", method = RequestMethod.GET)
    public ResponseObject getAllAddresses(@RequestParam int accountId){
        ResponseObject response = service.getAllAddresses(accountId);
        return response;
    }

    @RequestMapping(value = "/setAddressFavorite", method = RequestMethod.POST)
    public ResponseObject setAddressFavorite(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        ResponseObject response = service.setAddressFavorite(jsonPayload);
        return response;
    }

    @RequestMapping(value = "/deleteAddress", method = RequestMethod.POST)
    public ResponseObject deleteAddress(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        ResponseObject response = service.deleteAddress(jsonPayload);
        return response;
    }
    @RequestMapping(value = "/fetchCartItems", method = RequestMethod.GET)
    public ResponseObject fetchCartItems(@RequestParam int accountId){
        ResponseObject response = service.fetchCartItems(accountId);
        return response;
    }

    @RequestMapping(value = "/removeCartItem", method = RequestMethod.POST)
    public ResponseObject removeCartItem(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        ResponseObject response = service.removeCartItem(jsonPayload);
        return response;
    }

    @RequestMapping(value = "/changeQuantityCartItem", method = RequestMethod.PATCH)
    public ResponseObject changeQuantityCartItem(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        ResponseObject response = service.changeQuantityCartItem(jsonPayload);
        return response;
    }

    @RequestMapping(value = "/addToCart", method = RequestMethod.POST)
    public ResponseObject addToCart(@RequestBody String payloadFromUI){
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        ResponseObject response = service.addToCart(jsonPayload);
        return response;
    }
}
