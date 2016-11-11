package com.nokia.Controllers;

import com.nokia.DAO.UserTokenDAO;
import com.nokia.Models.UserToken;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
    Controller for GitApi. /welcome will be called as part of callback url
    by GIT. The acsess token will be received and view will be returned back.
 */
@Controller
@RequestMapping(value = "/git")
public class GitViewController {

    Logger log= Logger.getLogger(GitViewController.class.getName());
    String redirect_url = "https://4591e19f.ngrok.io/payload";
    String client_id="4f3cb4d16e55d1cd0f13";
    String client_secret="0354b3be9b792770fb1c9c4332d5ad7b1ed85277";


    @Autowired
    UserTokenDAO userTokenDAO;

    /*
    Authorization url
     */
    @RequestMapping(value = "/authorize/{user_id}",method = RequestMethod.GET)
    public ModelAndView authorize(@PathVariable("user_id") String user_id)
    {
        log.info("------In authorize: ------"+user_id);

        String token = userTokenDAO.getToken(user_id,"git");
        log.info("token:"+token+" userid: "+user_id);
        log.info("is token valid: "+isTokenValid(token)+"");
        if(userTokenDAO.isUserPresent(user_id,"git") && isTokenValid(token))
        {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("AlreadyRegistered");
            return mav;
        }
        else
        {
//            String url ="https://github.com/login/oauth/authorize?client_id=4f3cb4d16e55d1cd0f13&scope=admin:repo_hook&state=" + user_id;
            String url ="https://github.com/login/oauth/authorize?client_id="+client_id+"&scope=admin:repo_hook&state=" + user_id;
            log.info("git url: "+url);
            ModelAndView mav = new ModelAndView();
            mav.setViewName("redirect:"+url);
            return mav;
        }

    }

    /*
        Postback url from git
        params: code and user_id
        return view welcome page
     */
    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public ModelAndView welcome(@RequestParam String code,@RequestParam String state) {
        String user_id=state;
        log.info("Callback from Git"+user_id+"  "+code);
        String access_token_scope = getAccesstoken(code);
        String[] token_scope = access_token_scope.split(",");
        String access_token = token_scope[0];

        String scope = userTokenDAO.getScope(user_id,"git");
        if(!scope.contains("admin"))
        {
            scope = scope+" "+token_scope[1];
        }
        else
            scope=token_scope[1];

        String username = getUsername(access_token);

        UserToken usertoken = new UserToken();
        usertoken.setUser_id(user_id);
        usertoken.setAccess_token(access_token);
        usertoken.setUsername(username);
        usertoken.setProject("git");
        usertoken.setScope(scope);

        if(!userTokenDAO.isUserPresent(user_id,"git"))
        {
            log.info("userpresent: "+user_id);
            userTokenDAO.insertTokenForUser(usertoken);
        }
        else
        {
            log.info("no userpresent: "+user_id);
            userTokenDAO.updateTokenForUser(usertoken);
        }

        ModelAndView mav = new ModelAndView();
        mav.setViewName("Welcome");
        return mav;
    }

    //To get access token from Git
    public String getAccesstoken(String code)
    {
        System.out.println("\n--------------get accesstoken----------------");
        String url = "https://github.com/login/oauth/access_token";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        post.addHeader("Accept", "application/json");

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//        urlParameters.add(new BasicNameValuePair("client_id", "f641556acfa85098fd65"));
//        urlParameters.add(new BasicNameValuePair("client_secret", "5fb5807200d471937abf249eb3f1f78bfb08b7e9"));
        urlParameters.add(new BasicNameValuePair("client_id", client_id));
        urlParameters.add(new BasicNameValuePair("client_secret", client_secret));

        urlParameters.add(new BasicNameValuePair("code", code));
        //System.out.println("url parameters:: "+urlParameters.toString());
        try
        {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            CloseableHttpResponse response = client.execute(post);
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + post.getEntity());
            System.out.println("Response Code : " +
                    response.getStatusLine().getStatusCode());

            String json = EntityUtils.toString(response.getEntity(), "UTF-8");

            JSONObject resp_json=new JSONObject(json);
            System.out.println("Response: "+resp_json.toString());
            if(!json.equals("")&& json!=null)
            {
                String token = resp_json.getString("access_token");
                String scope=resp_json.getString("scope");
                System.out.println("access: "+token+","+scope);
                return token+","+scope;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "No token";
    }

    //get username for git user which will be useful in later api calls.
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
            System.out.println("json: "+json);
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
        return "No username";
    }

    /*
        Helper function to check the validity of token before using it.
     */
    public boolean isTokenValid(String token)
    {
        log.info("------is token valid------");
        String url ="https://api.github.com/applications/"+client_id+"/tokens/"+token;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet =new HttpGet(url);
        httpGet.addHeader("Accept", "application/json");

        String plainClientCredentials=client_id+":"+client_secret;
        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));
        httpGet.addHeader("Authorization", "Basic " + base64ClientCredentials);
        log.info("Request headers: "+httpGet.getLastHeader("Authorization"));
        try
        {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            log.info("Response Code : " +
                    response.getStatusLine().getStatusCode());
            String json = EntityUtils.toString(response.getEntity(), "UTF-8");
            log.info("json: "+json);
            if(response.getStatusLine().getStatusCode()==200)
            {
                return true;
            }
        }
        catch(Exception e)
        {
            log.info(e.toString());
        }

        return false;
    }

}
