package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SampleController {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostMapping(value = "/login")
    public String authenticateUser(@RequestBody Map<String,String> request){
        // verify over dataabse over here
        String username = request.get("username");
        String password = request.get("password");
        System.out.println("Username ="+username);
        System.out.println("Passsword ="+password);
        int res = jdbcTemplate.queryForObject("select count(*) from users where username=? and password=?",Integer.class, new Object[]{username,password});
        if(res > 0) {
            return jwtUtil.generateToken(username);
        }
        else{
            return "Invalid login";
        }
    }

    @PostMapping(value = "/info")
    public String info(@RequestBody Map<String,String> request){
        // verify over dataabse over here
        String token = request.get("token");
        String username = request.get("username");
         if(jwtUtil.validateToken(token,username)){
             return "Welocme "+username;
         }else{
             return "Unauthorized";
         }
    }
}
