package com.zacharywarunek.kettering.cs461project;

import com.zacharywarunek.kettering.cs461project.util.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://loaclhost:4200", allowedHeaders = "*")
@RestController
public class MainController {

    @Autowired
    private Service service;


    @GetMapping("/")
    public String welcome() {
        return "Welcome to Amazon";
    }

    @RequestMapping(value = "/apiTest", method = RequestMethod.GET)
    public ResponseObject apiTest(){
        ResponseObject response = new ResponseObject();
        response.setData("inside /apiTest");
        response.setStatus(200);
        response.setMessage("All Good Here");
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
    public ResponseObject getAllProducts(@RequestParam String search){
        ResponseObject response = service.searchProducts(search);
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
}
