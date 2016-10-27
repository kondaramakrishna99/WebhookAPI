package com.nokia.Controllers;

import com.nokia.DAO.UserTokenDAO;
import com.nokia.Models.UserToken;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
public class WebhookAPIController {

    @Autowired
    UserTokenDAO userTokenDAO;
    Logger log= Logger.getLogger(GitViewController.class.getName());

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String welcome()
    {
        System.out.println("Welcome");
//        String url ="https://github.com/login/oauth/authorize?client_id=f641556acfa85098fd65";
//
//        System.out.println(userTokenDAO.getUserTokens().toString());
//        if(userTokenDAO.isUserPresent("2","git"))
//        {
//           // userTokenDAO.insertTokenForUser(new UserToken(2,"token2"));
//            userTokenDAO.deleteUserToken(2+"","git");
//        }
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

        String url = "http://127.0.0.1:5000/postOnApp";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
//        post.addHeader("Accept", "application/json");
        post.addHeader("Content-Type","application/json");
        JSONObject postjson = new JSONObject();
        postjson.put("message",req.getBody());
        postjson.put("chat_thread_id","336da76e-9292016-10-11-15-14-56-468--1195012184");
        log.info("json: "+postjson.toString());
        try
        {
            StringEntity postjsonEntity = new StringEntity(postjson.toString());
            post.setEntity(postjsonEntity);
            CloseableHttpResponse response = client.execute(post);
            System.out.println("\nSending 'POST'  request to URL : " + url);
            System.out.println("Post parameters : " + post.getEntity());
            System.out.println("Response Code : " +
                    response.getStatusLine().getStatusCode());

            String json = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("json response:: "+json);
        }catch(Exception e)
        {
            System.out.println(e);
        }
    }

}
