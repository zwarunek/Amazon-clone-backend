package com.zacharywarunek.kettering.cs461project;

import com.zacharywarunek.kettering.cs461project.repositories.IAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class MainController {

    @Autowired
    private Service service;

    @RequestMapping(value = "/apiTest", method = RequestMethod.GET)
    public ResponseObject apiTest(){
        ResponseObject response = new ResponseObject();
        response.setData("inside /apiTest");
        response.setStatus(200);
        response.setMessage("All Good Here");
        return response;
    }

    @RequestMapping(value = "/createAccount", method = RequestMethod.POST)
    public ResponseObject createAccount(@RequestBody String payloadFromUI){
        ResponseObject response = service.CreateAccount(payloadFromUI);
        return response;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseObject login(@RequestBody String payloadFromUI){
        ResponseObject response = service.Login(payloadFromUI);
        return response;
    }
}
