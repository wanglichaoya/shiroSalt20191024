import com.wlc.po.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;


/**
 * describe:
 *
 * @author 王立朝
 * @date 2019/10/24
 */
public class TestShiroLogin2 {

    public Subject getSubject() {
        System.out.println("getSubject");
        //通过Factor工厂来获取安全管理者实例
        Factory<SecurityManager> securityManagerFactory =
                new IniSecurityManagerFactory("classpath:shiro2.ini");
        //获取安全管理者SecurityManager 实例
        SecurityManager securityManager = securityManagerFactory.getInstance();
        //把安全管理者SecurityManager 通过SecurityUtils 放入全局变量里面
        SecurityUtils.setSecurityManager(securityManager);
        //通过安全管理者工具 SecurityUtils获取当前对象subject
        Subject subject = SecurityUtils.getSubject();
        return subject;
    }

    public boolean login(User user) {
        System.out.println("login");
        //获取当前登录对象 Subject
        Subject subject = getSubject();
        //判断用户是否已经登录过了，如果登录过了，就退出
        if (subject.isAuthenticated()) {
            subject.logout();
        }
        UsernamePasswordToken token = new UsernamePasswordToken(user.getName(), user.getPassword());
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            System.out.println("验证失败！");
            e.printStackTrace();
            return false;
        }
        return subject.isAuthenticated();
    }

    public boolean hasRole(String role){
        Subject subject = getSubject();
        return subject.hasRole(role);
    }

    public static void main(String[] args) {
        TestShiroLogin2 testShiroLogin = new TestShiroLogin2();
        User user = new User();
        user.setName("张三");
        user.setPassword("1");

        String role = "orderManager";

        String permit="addOrder";

        if(testShiroLogin.login(user)){
            System.out.println("认证成功");
            if(testShiroLogin.hasRole(role)){
                System.out.println("用户名为： " + user.getName()+ "拥有"+ role + "角色");
                if(testShiroLogin.hasPermit(permit)){
                    System.out.println("用户名为： " + user.getName()+ "拥有"+ role + "角色,并且拥有 " + permit + "权限");
                }else{
                    System.out.println("用户名为： " + user.getName()+ "拥有"+ role + "角色,没有拥有 " + permit + "权限");
                }
            }else{
                System.out.println("用户名为： " + user.getName()+ "没有"+ role + "角色");
            }
        }else{
            System.out.println("认证失败！");
        }

    }

    private boolean hasPermit(String permit) {
        Subject subject =getSubject();
        return subject.isPermitted(permit);
    }
}
