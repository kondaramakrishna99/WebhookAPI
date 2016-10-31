package com.nokia.DAO;

import com.nokia.Controllers.GitViewController;
import com.nokia.Models.UserHooks;
import com.nokia.Models.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

@Repository
public class UserHooksDAO {

    Logger log= Logger.getLogger(UserHooksDAO.class.getName());
    JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource)
    {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //List all hooks of specific user
    public List<UserHooks> getHooksForUser(String user_id)
    {
        String sql = "select * from hooks_table where user_id=?";
        Object[] params={user_id};
        List<UserHooks> hooksList = jdbcTemplate.query(sql, params, new RowMapper<UserHooks>() {
            @Override
            public UserHooks mapRow(ResultSet resultSet, int i) throws SQLException {
                UserHooks hook =new UserHooks();
                hook.setUser_id(resultSet.getString("user_id"));
                hook.setChat_thread_id(resultSet.getString("chat_thread_id"));
                hook.setProject(resultSet.getString("project"));
                hook.setReponame(resultSet.getString("reponame"));
                hook.setHook_id(resultSet.getString("hook_id"));
                return hook;
            }
        });
        return hooksList;
    }

    //List all hooks of specific user
    public List<UserHooks> getHooksForChat(String chat_thread_id)
    {

        String sql = "select * from hooks_table where chat_thread_id=?";
        Object[] params={chat_thread_id};
        List<UserHooks> hooksList = jdbcTemplate.query(sql, params, new RowMapper<UserHooks>() {
            @Override
            public UserHooks mapRow(ResultSet resultSet, int i) throws SQLException {
                UserHooks hook =new UserHooks();
                hook.setUser_id(resultSet.getString("user_id"));
                hook.setChat_thread_id(resultSet.getString("chat_thread_id"));
                hook.setProject(resultSet.getString("project"));
                hook.setReponame(resultSet.getString("reponame"));
                hook.setHook_id(resultSet.getString("hook_id"));
                return hook;
            }
        });
        return hooksList;
    }

    //Insert a hook into webhook for specific user
    public int insertHookForUser(UserHooks hook)
    {
        String sql="insert into hooks_table(user_id,chat_thread_id,reponame,project,hook_id) values(?,?,?,?,?)";
        log.info("sql: "+sql);
        Object[] params ={hook.getUser_id(),hook.getChat_thread_id(),hook.getReponame(),hook.getProject(),hook.getHook_id()};
        int rowsaffected = jdbcTemplate.update(sql,params);
        log.info("rows:"+rowsaffected );
        return rowsaffected;
    }

    /*
        delete specific hook
        @param: hook
        return hook id
     */
    public String getHookId(UserHooks hook)
    {
        log.info("------Get Hook Id-------");
        List<UserHooks> listHooks =getHooksForUser(hook.getUser_id()+"");
        for(UserHooks h:listHooks)
        {
            if(h.getReponame().equals(hook.getReponame()) && h.getProject().equals(hook.getProject()))
            {
                return h.getHook_id();
            }
        }
        return "No hook";
    }

     //check whether hook present for sepecific repo,chatthread
    public boolean isHookPresentinChatThread(UserHooks hook)
    {
        List<UserHooks> listHooks =getHooksForChat(hook.getChat_thread_id()+"");
        for(UserHooks h:listHooks)
        {
            if(h.getReponame().equals(hook.getReponame()) && h.getProject().equals(hook.getProject()))
            {
                return true;
            }
        }
        return false;
    }

    //check wheter hook present for specific user
    public boolean isHookPresentForUser(UserHooks hook)
    {
        List<UserHooks> listHooks =getHooksForUser(hook.getUser_id()+"");
        log.info("list hooks: " +listHooks.toString());
        for(UserHooks h:listHooks)
        {
            if(h.getReponame().equals(hook.getReponame()) && h.getProject().equals(hook.getProject()))
            {
                log.info("hook present: "+hook.toString());
                return true;
            }
        }
        return false;
    }

    public boolean isHookPresentForUserInChatThread(UserHooks hook)
    {
        log.info("isHookPresentForUserInChatThread");
        List<UserHooks> listHooks =getHooksForUser(hook.getUser_id()+"");
        log.info("list hooks: " +listHooks.toString());
        for(UserHooks h:listHooks)
        {
            if(h.getReponame().equals(hook.getReponame()) && h.getProject().equals(hook.getProject()) && h.getChat_thread_id().equals(hook.getChat_thread_id()))
            {
                log.info("hook present: "+hook.toString());
                return true;
            }
        }
        return false;
    }

    //delete webhook for user and project
    public int deleteHook(UserHooks hook)
    {
        String sql ="delete from hooks_table where user_id=? AND chat_thread_id=? AND reponame=? AND project=?";
        Object[] params= {hook.getUser_id(),hook.getChat_thread_id(),hook.getReponame(),hook.getProject()};
        int rowsaffected = jdbcTemplate.update(sql,params);
        return rowsaffected;
    }
}
