package com.nokia.Controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by rkonda on 10/13/2016.
 */
@RestController
public class WebhookAPIController {

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String welcome()
    {
        System.out.println("Welcome");
        String url ="https://github.com/login/oauth/authorize?client_id=f641556acfa85098fd65";
        return url;
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
