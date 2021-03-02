package com.zacharywarunek.kettering.cs461project;

import com.zacharywarunek.kettering.cs461project.entitys.AuthRequest;
import com.zacharywarunek.kettering.cs461project.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
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

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseObject register(@RequestBody String payloadFromUI){
        ResponseObject response = service.Register(payloadFromUI);
        return response;
    }

    @GetMapping("/")
    public String welcome() {
        return "Welcome to Amazon";
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseObject login(@RequestBody AuthRequest payloadFromUI) throws Exception {
        ResponseObject response = service.Authenticate(payloadFromUI);
        return response;
    }
}
