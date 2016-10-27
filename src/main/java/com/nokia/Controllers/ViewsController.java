package com.nokia.Controllers;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.sun.deploy.net.HttpResponse;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import sun.net.www.http.HttpClient;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

@Controller
public class ViewsController {

    String redirect_url = "https://855fe5ec.ngrok.io/payload";


    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public ModelAndView hello(@RequestParam String code, @RequestParam String state) {
        System.out.println("\nreturn url called "+code+"  "+state+" ");
        String access_token = getAccesstoken(code);
        String username = getUsername(access_token);
        List<String> res = getRepos(username);

        List<String> hooks = getHooks(username,"WebHookTest",access_token);
        String hookurl =createWebHook(username,"WebHookTest",access_token);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("Welcome");

        return mav;
    }

    public String getAccesstoken(String code)
    {
        System.out.println("\n--------------get accesstoken----------------");
        String url = "https://github.com/login/oauth/access_token";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        post.addHeader("Accept", "application/json");

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("client_id", "f641556acfa85098fd65"));
        urlParameters.add(new BasicNameValuePair("client_secret", "0e6d1d9369cb227ce083762b7afd7bdc9b204858"));
        urlParameters.add(new BasicNameValuePair("code", code));
        System.out.println("url parameters:: "+urlParameters.toString());
        try
        {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            CloseableHttpResponse response = client.execute(post);
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + post.getEntity());
            System.out.println("Response Code : " +
                    response.getStatusLine().getStatusCode());

            String json = EntityUtils.toString(response.getEntity(), "UTF-8");

            //get all headers
//            Header[] headers = response.getAllHeaders();
//            for (Header header : headers) {
//                System.out.println("Key : " + header.getName()
//                        + " ,Value : " + header.getValue());
//            }

            System.out.println("json:: "+json);

            JSONObject resp_json=new JSONObject(json);
            if(!json.equals("")&& json!=null)
            {
                String token = resp_json.getString("access_token");
                System.out.println("access: "+token);
                return token;
            }

        }
        catch(Exception e)
        {
            System.out.println(e);
        }


        return "";

    }

    public String getUsername(String token)
    {
        System.out.println("\n--------------get username----------------");
        String url ="https://api.github.com/user?access_token="+token;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet =new HttpGet(url);
        httpGet.addHeader("Accept", "application/json");

        try
        {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            System.out.println("Response Code : " +
                    response.getStatusLine().getStatusCode());

            String json = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("json:: "+json);
            JSONObject resp_json=new JSONObject(json);
            if(!json.equals("")&& json!=null)
            {
                String username = resp_json.getString("login");
                System.out.println("username: "+username);
                return username;
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return "";
    }

    public List<String> getRepos(String username)
    {
        System.out.println("\n--------------get Repos----------------");
        List<String> result = new ArrayList<String>();
        String url ="https://api.github.com/users/"+username+"/repos";
        System.out.println(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet =new HttpGet(url);
        httpGet.addHeader("Accept", "application/json");

        try
        {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            System.out.println("Response Code : " +
                    response.getStatusLine().getStatusCode());

            String json = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("json:: "+json);
            JSONArray repos = new JSONArray(json);

            if(!json.equals("")&& json!=null)
            {
                for(int i=0;i<repos.length();i++)
                {
                    JSONObject rep = repos.getJSONObject(i);
                    System.out.println(rep.getString("name"));
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

    public List<String> getHooks(String username,String repo,String token)
    {
        System.out.println("\n--------------get hooks----------------");
        String url = "https://api.github.com/repos/"+username+"/"+repo+"/hooks?access_token="+token;

        //String url = "https://api.github.com/repos/"+username+"/"+repo+"/hooks?client_id=f641556acfa85098fd65&client_secret=0e6d1d9369cb227ce083762b7afd7bdc9b204858";
        //String url ="https://api.github.com/repos/"+username+"/"+repo+"/hooks";


        List<String> result = new ArrayList<String>();
        System.out.println("url:: "+url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet =new HttpGet(url);
        httpGet.addHeader("Accept", "application/json");

        try
        {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            System.out.println("getHooks Response Code : " +
                    response.getStatusLine().getStatusCode());

            String json = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("json:: "+json);


            if(!json.equals("")&& json!=null)
            {
                JSONArray repos = new JSONArray(json);
                for(int i=0;i<repos.length();i++)
                {
                    JSONObject rep = repos.getJSONObject(i);
                    System.out.println(rep.get("id").toString());
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

    public String createWebHook(String username, String repo, String token)
    {
        System.out.println("\n--------------create new hook----------------");

        String url = "https://api.github.com/repos/"+username+"/"+repo+"/hooks?access_token="+token;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        post.addHeader("Accept", "application/json");

        JSONObject postjson = new JSONObject();
        postjson.put("name","web");
        JSONObject config = new JSONObject();
        config.put("url",redirect_url);
        config.put("content_type","json");
        postjson.put("config",config);
        String[] events = new String[1];
        events[0]="*";
        postjson.put("events",events);
        System.out.println("\njson post for webhook: "+postjson.toString());
        try
        {
            StringEntity postjsonEntity = new StringEntity(postjson.toString());
            post.setEntity(postjsonEntity);
//            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            CloseableHttpResponse response = client.execute(post);
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + post.getEntity());
            System.out.println("Response Code : " +
                    response.getStatusLine().getStatusCode());

            String json = EntityUtils.toString(response.getEntity(), "UTF-8");

            System.out.println("json response:: "+json);

            if(!json.equals("")&& json!=null)
            {
                JSONObject resp_json=new JSONObject(json);

                String hookurl = resp_json.getString("url");
                System.out.println("hookurl: "+hookurl);
                return hookurl;
            }


        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return "";
    }

    public boolean deleteHook(String id,String username, String repo,String token)
    {
        System.out.println("\n-------delete hook----------");
        String url = "https://api.github.com/repos/"+username+"/"+repo+"/"+"hooks/"+id+"?access_token="+token;

        boolean result=false;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(url);

        try
        {
            CloseableHttpResponse response = httpClient.execute(httpDelete);
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

            if(response.getStatusLine().getStatusCode()==204)
            {
                return true;
            }

        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return false;
    }
}
