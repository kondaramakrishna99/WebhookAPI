package com.nokia.Controllers;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rkonda on 10/17/2016.
 */

@RestController
@RequestMapping(value = "/git")
public class GitApiController {

    //To get list of repositories
    @RequestMapping(value = "/repos/{username}")
    public List<String> getRepos(@PathVariable("username")String username)
    {
        System.out.println("\n-----------get Repos : "+username+"---------------");
        List<String> result = new ArrayList<String>();
        String url ="https://api.github.com/users/"+username+"/repos";
        System.out.println(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet =new HttpGet(url);
        httpGet.addHeader("Accept", "application/json");

        try
        {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
            String json = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("json:: "+json);
            JSONArray repos = new JSONArray(json);

            if(!json.equals("")&& json!=null)
            {
                for(int i=0;i<repos.length();i++)
                {
                    JSONObject rep = repos.getJSONObject(i);
                    System.out.println(""+rep.getString("name"));
                    result.add(rep.getString("name"));
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return result;
    }

    //to get hooks for specific repo
    //
    public List<String> getHooks(String username,String repo,String token)
    {
        System.out.println("\n--------------get hooks--------------");
        String url = "https://api.github.com/repos/"+username+"/"+repo+"/hooks?access_token="+token;
        List<String> result = new ArrayList<String>();
        System.out.println("url:: "+url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet =new HttpGet(url);
        httpGet.addHeader("Accept", "application/json");

        try
        {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            System.out.println("getHooks Response Code: " + response.getStatusLine().getStatusCode());

            String json = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("json:: "+json);


            if(!json.equals("")&& json!=null)
            {
                JSONArray repos = new JSONArray(json);
                for(int i=0;i<repos.length();i++)
                {
                    JSONObject rep = repos.getJSONObject(i);
                    System.out.println(" "+rep.get("id").toString());
                    result.add(rep.get("id").toString()+"");
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return result;
    }
}
