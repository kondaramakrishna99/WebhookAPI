package com.nokia.Controllers;

import com.nokia.DAO.UserTokenDAO;
import com.nokia.Models.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class WebhookAPIController {

    @Autowired
    UserTokenDAO userTokenDAO;

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String welcome()
    {
        System.out.println("Welcome");
        String url ="https://github.com/login/oauth/authorize?client_id=f641556acfa85098fd65";

        System.out.println(userTokenDAO.getUserTokens().toString());
        if(userTokenDAO.isUserPresent("2","git"))
        {
           // userTokenDAO.insertTokenForUser(new UserToken(2,"token2"));
            userTokenDAO.deleteUserToken(2+"","git");
        }
        return "Welcome";
    }

    @RequestMapping(value = "/payload",method = RequestMethod.POST)
    public void payload(RequestEntity<String> req, @RequestHeader HttpHeaders headers)
    {
        System.out.println("\n----------Webhook response----------");
        System.out.println(req.getUrl());
        System.out.println("\n--body:-----\n"+req.getBody());

        System.out.println();
        for(Map.Entry<String,List<String>> header:headers.entrySet())
        {
            System.out.println(header.getKey()+"    "+header.getValue());
        }
    }

}
