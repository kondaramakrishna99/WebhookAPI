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

/**
 *This class is mainly for all data access operations for authorize_table
 */

@Repository
public class UserTokenDAO {


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
                    token.setUser_id(resultSet.getInt("user_id"));
                    token.setAccess_token(resultSet.getString("access_token"));
                    return token;
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

//        for(UserToken token:results)
//        {
//            System.out.println(token.getUser_id()+"  "+token.getAccess_token());
//        }

        return results;
    }


    //get Token for a user
    public String getToken(String user_id)
    {
        List<UserToken> tokens = getUserTokens();
        String resToken="";
        for(UserToken t:tokens)
        {
            if(t.getUser_id()==Integer.parseInt(user_id))
            {
                resToken=t.getAccess_token();
                break;
            }
        }
        if(resToken.equals(""))
            return "NULL";
        System.out.println("restoken: "+resToken);
        return resToken;
    }

    //insert token into table with specific user_id
    public int insertTokenForUser(UserToken usertoken)
    {
        //String sql = "insert into authorize_table(user_id,access_token) values("+usertoken.getUser_id()+","+usertoken.getAccess_token()+")";
        String sql = "insert into authorize_table(user_id,access_token) values(?,?)";
        Object[] params={usertoken.getUser_id(),usertoken.getAccess_token()};
        int rowsaffected= jdbcTemplate.update(sql,params);
        return rowsaffected;
    }

    //to check whether user already present or not before inserting
    public boolean isUserPresent(String user_id)
    {
        if(getToken(user_id).equals("NULL"))
            return false;
        else
            return true;
    }

    //To delete specific user_id from list
    public int deleteUserToken(String user_id)
    {
        String sql = "delete from authorize_table where user_id="+user_id;
        int rows = jdbcTemplate.update(sql);
        return rows;
    }

}
