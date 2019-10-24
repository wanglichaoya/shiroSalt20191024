package com.wlc.dao;

import com.wlc.po.User;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * describe:
 *
 * @author 王立朝
 * @date 2019/10/24
 */
public class DAO_Old {

    public DAO_Old() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     **/
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/shiro02?characterEncoding=UTF-8", "scott", "tiger");
    }

    /**
     * 注册用户
     **/
    public int createUser(String name, String password) {
        int result = 0;
        try {
            Connection connection = getConnection();
            String sql = "insert into user values(null,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            String salt = new SecureRandomNumberGenerator().nextBytes().toString();
            int time = 2;
            String encodePassword = new SimpleHash("md5", password, salt, time).toString();
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, encodePassword);
            preparedStatement.setString(3, salt);
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据用户名获取用户的所有信息，包括盐和密码
     * **/
    public User getUser(String userName){
        User user = null;
        try {
            Connection connection = getConnection();
            String sql = "select name,password,salt from user where name=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                user = new User();
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                user.setSalt(resultSet.getString("salt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 根据用户名 获取用户名密码
     **/
    public String getPassword(String userName) {
        try {
            Connection connection = getConnection();
            String sql = "select password from user where name =?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据用户名获取用户的角色
     **/
    public Set<String> listRole(String userName) {
        Set<String> roleSet = new HashSet<>();
        String sql = "select r.name from user u left join user_role ur on ur.uid = u.id left join role r on ur.rid = r.id where u.name = ?";
        roleSet = commonSqlPre(sql, userName, roleSet);
        return roleSet;
    }

    /**
     * 公共部分的查询，抽出到一个方法中，提高重用性
     **/
    public Set<String> commonSqlPre(String sql, String userName, Set<String> permissionSet) {
        try {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                permissionSet.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permissionSet;
    }

    /**
     * 根据用户名，获取用户所具有的权限
     **/
    public Set<String> listPermissions(String userName) {
        Set<String> permissionSet = new HashSet<>();
        String sql = "SELECT p.name FROM USER u LEFT JOIN user_role ur ON ur.uid = u.id left join role r on ur.rid = r.id LEFT JOIN role_permission rp ON rp.rid = ur.rid LEFT JOIN permission p ON rp.pid = p.id where  u.name=?";
        permissionSet = commonSqlPre(sql, userName, permissionSet);
        return permissionSet;
    }

    public static void main(String[] args) {
        DAO_Old dao = new DAO_Old();
        //int result = dao.createUser("张三","1");
       // System.out.println("新增结果为： " + result);
        System.out.println(dao.getUser("张三"));
    }

}
