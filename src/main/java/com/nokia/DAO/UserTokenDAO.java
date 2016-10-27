package com.nokia.DAO;

import com.nokia.Models.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *This class is mainly for all data access operations for authorize_table
 */

@Repository
public class UserTokenDAO {

    Logger log = Logger.getLogger(UserTokenDAO.class.getName());
    JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource)
    {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //List all usertokens
    public List<UserToken> getUserTokens()
    {
        String sql ="select * from authorize_table";
        List<UserToken> results = new ArrayList<UserToken>();
        if(jdbcTemplate==null)
        {
            System.out.println("jdbc template is null");
            return results;
        }
        try{
            results=jdbcTemplate.query(sql, new RowMapper<UserToken>() {
                @Override
                public UserToken mapRow(ResultSet resultSet, int i) throws SQLException {
                    UserToken token = new UserToken();
                    token.setUser_id(resultSet.getString("user_id"));
                    token.setAccess_token(resultSet.getString("access_token"));
                    token.setProject(resultSet.getString("project"));
                    token.setUsername(resultSet.getString("username"));
                    return token;
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return results;
    }


    //get Token for a user
    public String getToken(String user_id,String project)
    {
        List<UserToken> tokens = getUserTokens();
        log.info("tokens list: "+tokens.toString());
        String resToken="";
        for(UserToken t:tokens)
        {
            log.info("token "+t.toString()+"  "+user_id+"  "+project);
            if(t.getUser_id().equals(user_id) && t.getProject().equals(project))
            {
                resToken=t.getAccess_token();
                log.info("token found: "+resToken+" "+t.getUser_id()+"  "+t.getProject());
                break;
            }
        }
        if(resToken.equals(""))
            return "NULL";
        log.info("res token: "+resToken);
        return resToken;
    }

    //insert token into table with specific user_id
    public int insertTokenForUser(UserToken usertoken)
    {
        String sql = "insert into authorize_table(user_id,access_token,username,project) values(?,?,?,?)";
        Object[] params={usertoken.getUser_id(),usertoken.getAccess_token(),usertoken.getUsername(),usertoken.getProject()};
        int rowsaffected= jdbcTemplate.update(sql,params);
        return rowsaffected;
    }

    //update token for a given user
    public int updateTokenForUser(UserToken usertoken)
    {
        String sql = "update authorize_table set access_token=? where user_id=? && project=?";
        Object[] params={usertoken.getAccess_token(),usertoken.getUser_id(),usertoken.getProject()};
        int rowsaffected = jdbcTemplate.update(sql,params);
        return rowsaffected;
    }

    //to check whether user already present or not before inserting
    public boolean isUserPresent(String user_id,String project)
    {
        if(getToken(user_id,project).equals("NULL"))
        {
            log.info("isuserpresent: token present for "+user_id);
            return false;
        }
        else
            return true;
    }

    //To delete specific user_id from list
    public int deleteUserToken(String user_id,String project)
    {
        String sql = "delete from authorize_table where user_id=? && project=?";
        Object[] params = {user_id,project};
        int rows = jdbcTemplate.update(sql,params);
        return rows;
    }

    //get Username of user from their profile like git username
    public String getUsername(String user_id,String project)
    {
        List<UserToken> tokens = getUserTokens();
        String resUsername="";
        for(UserToken t:tokens)
        {
            if(t.getUser_id().equals(user_id) && t.getProject().equals(project))
            {
                resUsername=t.getUsername();
                break;
            }
        }
        if(resUsername.equals(""))
            return "NULL ";
        System.out.println("restoken: "+resUsername);
        return resUsername;
    }

}
