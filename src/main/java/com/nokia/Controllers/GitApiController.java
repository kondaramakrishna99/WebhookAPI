package com.nokia.Controllers;

import com.nokia.DAO.UserHooksDAO;
import com.nokia.DAO.UserTokenDAO;
import com.nokia.Models.AjaxRequestHook;
import com.nokia.Models.UserHooks;
import com.nokia.Models.UserToken;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@RestController
@RequestMapping(value = "/git")
public class GitApiController {

    Logger log= Logger.getLogger(GitViewController.class.getName());

    //where payload should be called from Git
    String redirect_url = "https://855fe5ec.ngrok.io/payload";

    @Autowired
    UserTokenDAO userTokenDAO;

    @Autowired
    UserHooksDAO userHooksDAO;

    //To get list of repositories
    @RequestMapping(value = "/repos/{userid}",method = RequestMethod.GET)
    public ResponseEntity<String> getRepos(@PathVariable("userid")String userid)
    {
        System.out.println("\n-----------get Repos : "+userid+"---------------");

        //get username from userid
        String username = userTokenDAO.getUsername(userid,"git");
        log.info("username: "+username);
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
            String res="";
            if(!json.equals("")&& json!=null)
            {
                for(int i=0;i<repos.length();i++)
                {
                    JSONObject rep = repos.getJSONObject(i);
                    System.out.println(""+rep.getString("name"));
                    result.add(rep.getString("name"));
                    res+=rep.getString("name")+" ";
                }
            }

            return new ResponseEntity<String>(res,HttpStatus.OK);

        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return new ResponseEntity<String>("Internal error",HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //to get hooks for specific repo
    @RequestMapping(value="/hooks/{userid}/{repo}")
    public List<String> getHooks(@PathVariable("userid") String userid,@PathVariable("repo")String repo)
    {
        List<String> result = getHookList(userid,repo);

        return result;
    }

    //Make changes in create and start
    //Create hook for repository and returns the newly created hook url.
    @RequestMapping(value = "/createhook", method = RequestMethod.POST)
    public String createHook(@RequestBody UserHooks hook)
    {
        String username=userTokenDAO.getUsername(hook.getUser_id()+"","git");
        String token = userTokenDAO.getToken(hook.getUser_id()+"","git");
        String repo = hook.getReponame();

        if(userHooksDAO.isHookPresentForUser(hook))
        {
            return "Repo/Hook already exist";
        }
        else
        {
            System.out.println("\n--------------create new hook---------------");

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
                CloseableHttpResponse response = client.execute(post);
                System.out.println("\nSending 'POST'  request to URL : " + url);
                System.out.println("Post parameters : " + post.getEntity());
                System.out.println("Response Code : " +
                        response.getStatusLine().getStatusCode());

                String json = EntityUtils.toString(response.getEntity(), "UTF-8");

                System.out.println("json response:: "+json);

                if(!json.equals("")&& json!=null)
                {
                    JSONObject resp_json=new JSONObject(json);
                    String hookurl = resp_json.getString("url");
                    String hook_id = resp_json.getInt("id")+"";
                    System.out.println("hookurl : "+hookurl);
                    hook.setHook_id(hook_id);
                    int rows= userHooksDAO.insertHookForUser(hook);
                    log.info("New hook: "+hook.toString());
                    return hookurl;
                }
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
            return "";
        }
    }

    //delete specific hook for user
    @RequestMapping(value = "/deletehook",method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteHook(@RequestBody UserHooks hook)
    {
        String username = userTokenDAO.getUsername(hook.getUser_id()+"","git");
        String token = userTokenDAO.getToken(hook.getUser_id()+"","git");
        String repo = hook.getReponame();
        String id=userHooksDAO.getHookId(hook);
        log.info("hook id: "+id);
        if(id.equals("No hook"))
        {
            return new ResponseEntity<String>("No Hook",HttpStatus.NOT_FOUND);
        }

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
                userHooksDAO.deleteHook(hook);
                return new ResponseEntity<String>(id+"",HttpStatus.OK);
            }

        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return new ResponseEntity<String>("id",HttpStatus.BAD_REQUEST);
    }

    //helper function
    public List<String> getHookList(String userid,String repo)
    {
        System.out.println("\n--------------get hooks--------------");

        //get username and token from userid
        String username = userTokenDAO.getUsername(userid,"git");
        String token = userTokenDAO.getToken(userid,"git");

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
                    System.out.println(""+rep.get("id").toString());
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

    @RequestMapping(value = "/isUserAuthorized/{user_id}")
    public ResponseEntity<Boolean> isUserAlreadyAuthorized(@PathVariable("user_id") String user_id)
    {
        if(userTokenDAO.isUserPresent(user_id,"git"))
            return new ResponseEntity<Boolean>(true,HttpStatus.OK);
        else
            return new ResponseEntity<Boolean>(false,HttpStatus.OK);
    }


}
