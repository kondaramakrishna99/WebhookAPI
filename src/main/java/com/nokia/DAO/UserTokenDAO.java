package com.nokia.DAO;

import com.nokia.Models.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rkonda on 10/17/2016.
 */

@org.springframework.stereotype.Repository

public class UserTokenDAO {


    JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource)
    {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<UserToken> getUserTokens()
    {
        String sql ="select * from authorize_table";
        List<UserToken> results = new ArrayList<UserToken>();


        return results;
    }

}
