package com.nokia.Controllers;

import com.nokia.DAO.UserHooksDAO;
import com.nokia.DAO.UserTokenDAO;
import com.nokia.Models.UserToken;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
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

    @Autowired
    UserHooksDAO userHooksDAO;

    Logger log= Logger.getLogger(GitViewController.class.getName());
    String pythonAPIurl = "http://127.0.0.1:5000/postOnApp";
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

        String result="";

        JSONObject responseJson = new JSONObject(req.getBody());
        JSONObject repository = responseJson.getJSONObject("repository");
        String reponame= repository.getString("name");
        result+="repository: "+reponame+"\n";
        JSONArray committers = responseJson.getJSONArray("commits");
        String commitInfo="";
        for(int i=0;i<committers.length();i++)
        {
            JSONObject committer = committers.getJSONObject(i);
            String committername = "committer : "+committer.getJSONObject("committer").getString("name")+"\n";
            result+=committername+"\n";
            JSONArray addedArray = committer.getJSONArray("added");
            if(addedArray.length()!=0)
            {
                String added="Added: \n";
                for(int j=0;j<addedArray.length();j++)
                {
                    added=added+""+(j+1)+". "+addedArray.getString(j)+"\n";
                }
                result+=added+"\n";
            }

            JSONArray removedArray = committer.getJSONArray("removed");
            if(removedArray.length()!=0)
            {
                String removed="Removed: \n";
                for(int j=0;j<removedArray.length();j++)
                {
                    removed=removed+""+(j+1)+". "+removedArray.getString(j)+"\n";
                }
                result+=removed+"\n";
            }

            JSONArray modifiedArray = committer.getJSONArray("modified");
            if(modifiedArray.length()!=0)
            {
                String modified="Modified: \n";
                for(int j=0;j<modifiedArray.length();j++)
                {
                    modified=modified+""+(j+1)+". "+modifiedArray.getString(j)+"\n";
                }
                result+=modified+"\n";
            }
        }

        log.info("message: "+result);

        //get chat threadids
        List<String> chatthreadList= userHooksDAO.getChatThreads(reponame,"git");
        String chatthreads=String.join(",",chatthreadList);
        log.info(reponame+"  "+chatthreads);

        log.info("post to python API: "+result);
        String url = pythonAPIurl;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
//        post.addHeader("Accept", "application/json");
        post.addHeader("Content-Type","application/json");
        JSONObject postjson = new JSONObject();
        postjson.put("message",result);
        postjson.put("chat_thread_id",chatthreads);

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
