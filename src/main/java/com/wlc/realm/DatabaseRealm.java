package com.wlc.realm;

import com.wlc.dao.DAO;
import com.wlc.dao.DAO_Old;
import com.wlc.po.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Set;

/**
 * describe:
 *
 * @author 王立朝
 * @date 2019/10/24
 */
public class DatabaseRealm extends AuthorizingRealm {
    /**
     * 授权
     **/
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //能进入到这里就说明已经登录成功了
        System.out.println("可以进入到这里就说明登录成功了");
        //获取用户名
        String userName = principalCollection.getPrimaryPrincipal().toString();

        //获取用户名对应的角色和权限
        Set<String> roleSet = new DAO().listRoles(userName);
        Set<String> permitSet = new DAO().listPermissions(userName);

        //授权对象
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(roleSet);
        simpleAuthorizationInfo.setStringPermissions(permitSet);
        return simpleAuthorizationInfo;
    }


    /**
     * 认证
     **/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //获取用户的账号和密码
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String userName = token.getPrincipal().toString();
        String password = new String(token.getPassword());

        //获取数据库中的密码
        User user = new DAO_Old().getUser(userName);
        String passwordInDb = user.getPassword();
        String salt = user.getSalt();
        //对账号的密码再次加密
        String passwordEncode = new SimpleHash("md5", password, salt, 2).toString();
        if (null == user || !passwordEncode.equals(passwordInDb)) {
            throw new AuthenticationException();
        }
        //把账号和密码放入到认证信息里面，getName() 是当前Realm的继承方法,通常返回当前类名 :databaseRealm
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(userName, password, getName());
        return simpleAuthenticationInfo;
    }
}
