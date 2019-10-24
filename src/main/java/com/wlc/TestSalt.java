package com.wlc;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * describe:
 *
 * @author 王立朝
 * @date 2019/10/23
 */

public class TestSalt {

    /**
     * 使用MD5 对密码加密，是不可逆的，但是如果2个人的密码一样，name通过md5加密过后的随机数也是一样的，这样就
     * 可以反推密码，所以这样是不够安全的，所以就推出了‘盐’ , 就好比炒菜， 食材都是一样的，但是放盐的分量
     * 不一样，炒出来的菜的味道也就不一样。而且，可以加密的次数也是可以自定义的。
     * **/

    public String testSaltPassword(String password){

        //随机数
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        //机密次数
        int time = 3;
        String algorithmName = "md5";
        String encodePassword = new SimpleHash(algorithmName,password,salt,time).toString();
        System.out.println("原密码是：" + password + "盐是：" + salt+ "加密次数是： " +time+ ",加密后的面试"
                +encodePassword);
        return encodePassword;
    }

    public String Md5Hash(String password){
        return new Md5Hash(password).toString();
    }

    /**
     * 没有加盐的加密后的密码是： 202cb962ac59075b964b07152d234b70
     * 加盐后加密的密码是： 3ea214c9a580b28a76a9f9e7242e7577
     *
     * 没有加盐的加密后的密码是： 202cb962ac59075b964b07152d234b70
     * 加盐后加密的密码是： 11686205822bc5371fedd056a529819b
     *
     * 没有加盐的加密后的密码是： 202cb962ac59075b964b07152d234b70
     * 加盐后加密的密码是： b976d0b8289f468a4cb1ffe1231ce89e
     * **/
    public static void main(String[] args){
        TestSalt testSalt = new TestSalt();
        String password = "123";
        String Md5Password = testSalt.Md5Hash(password);
        String Md5SaltPassword = testSalt.testSaltPassword(password);
        System.out.println("没有加盐的加密后的密码是： "+ Md5Password);
        System.out.println("加盐后加密的密码是： " + Md5SaltPassword);

    }
}
