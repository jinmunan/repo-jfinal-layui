package com.cj.common.module.login.service;

import com.cj.common.config.WebContant;
import com.cj.common.entity.loginuser.impl.LoginUserImpl;
import com.cj.common.entity.visit.Visitor;
import com.cj.common.entity.visit.impl.VisitorImpl;
import com.cj.common.model.Session;
import com.cj.common.model.SysUser;
import com.cj.common.module.login.exception.LoginException;
import com.cj.common.module.portal.core.service.SysUserService;
import com.cj.common.util.kit.Md5Kit;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @author THINKPAD
 */
public class LoginService {

    @Inject
    SysUserService sysUserService;

    /**
     * 存放登录用户的 cacheName
     */
    public static final String LOGIN_ACCOUNT_CACHE_NAME = "loginAccount";

    /**
     * "jfinalLayuiId" 仅用于 cookie 名称，其它地方如 cache 中全部用的 "sessionId" 来做 key
     */
    public static final String SESSION_ID_NAME = "jfinalLayuiId";

    /**
     * 登陆验证
     */
    public Ret aopLogin(String userCode, String password, HttpServletRequest req) throws LoginException {
        // 验证数据
        if ((userCode == null) || (userCode.trim().length() == 0) || (password == null)
                || (password.trim().length() == 0)) {
            throw new LoginException("请输入用户名和密码");
        }
        // 验证登录,返回用户身份信息
        SysUser user = sysUserService.findByUserCode(userCode.toLowerCase());
        if (user == null) {
            // 开始锁定账号
            checkUserCode(req, userCode);
        }
        // 验证登录权限 allowlogin=0的时候可以登录
        if ((user.getAllowLogin() != null) && (!user.getAllowLogin().equals(WebContant.ALLOW_LOGIN))) {
            throw new LoginException("没有登录权限，请联系管理员");
        }
        //验证码 只有输错密码的时候才有失败次数加一
        checkVerifyCode(user, req);
        //验证通过
        //验证密码
        if (user.getPasswd().equals(Md5Kit.md5(password))) {
            // 保持登录
            String keepLogin = req.getParameter("keepLogin");
            if (keepLogin != null) {
                user.setKeepLogin(Integer.parseInt(keepLogin));
            }
            // 验证成功 返回中间对象 vistor,其实就是loginuser对象,其实返回了ret对象 和vistor无关
            return returnVistor(user, req);
        }

        //验证失败，记录失败次数，达到fail_num值将锁定账号
        loginFail(user);

        return null;
    }

    /**
     * 标记登陆账号信息，锁定账号
     * userCode是用户名 ,但在request域中作为输错次数的key
     */
    public void checkUserCode(HttpServletRequest req, String userCode) throws LoginException {
        if (WebContant.IS_LOCK_USER == 1) {
            // 账号输错的次数
            Integer num = (Integer) req.getSession().getAttribute(userCode.toLowerCase());
            if (num != null) {
                num += 1;
                // 重新设置到request域中
                req.getSession().setAttribute(userCode.toLowerCase(), num);
                if (num >= WebContant.FAIL_NUM) {
                    throw new LoginException("账号已被锁定24小时，请联系管理员");
                } else {
                    throw new LoginException("账号密码错误 " + num + " 次，第" + WebContant.FAIL_NUM + "次错误账号将被锁定24小时");
                }
            } else {
                // 第一次输错
                Integer first = 1;
                // 重新设置到request域中
                req.getSession().setAttribute(userCode.toLowerCase(), first);
                throw new LoginException("账号密码错误 1 次，第" + WebContant.FAIL_NUM + "次错误账号将被锁定24小时");
            }
        } else {
            throw new LoginException("账号密码错误");
        }
    }

    /**
     * 只有输错密码的时候才有失败次数加一
     * 检查验证码
     */
    private void checkVerifyCode(SysUser user, HttpServletRequest req) throws LoginException {
        Date allowLogTime = user.getAllowLoginTime();
        if (allowLogTime != null) {
            Date nowDate = new Date();
            Calendar cal = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal.setTime(allowLogTime);
            cal2.setTime(nowDate);
            // -123123123123 是现在和上次允许登录的时间戳
            float f = cal.getTimeInMillis() - cal2.getTimeInMillis();
            // 从请求参数中获取验证码
            String verifyCode = req.getParameter("verifyCode");
            // 验证码验证 ,时间戳小于0或者非空
            if (f < 0 || StrKit.notBlank(verifyCode)) {
                // 登录次数大于0
                if (user.getFailureNumber() > 0) {
                    // 从请求域中获取并对比
                    String code = (String) req.getSession().getAttribute("verifyCode");
                    // 更换验证码！防止暴力破解
                    Random random = new Random();
                    String sRand = "";
                    for (int i = 0; i < 4; i++) {
                        String rand = String.valueOf(random.nextInt(10));
                        sRand += rand;
                    }
                    // 设置新的验证码
                    req.getSession().setAttribute("verifyCode", sRand);
                    // 验证验证码
                    if ((verifyCode == null) || !verifyCode.equalsIgnoreCase(code)) {
                        throw new LoginException("验证码错误，请重新输入");
                    }
                }
            } else {
                throw new LoginException("账号已被锁定24小时，请联系管理员");
            }
        }
    }

    /**
     * 返回登录者信息,使用ret封装
     */
    public Ret returnVistor(SysUser user, HttpServletRequest req) throws LoginException {
        // 记录最后一次登录时间
        user.setAllowLoginTime(new Date());
        user.setFailureNumber(0);
        // 更新时间和失败次数
        sysUserService.update(user);
        return saveSession(user);
    }

    /**
     * 用户点击保持登录状态
     * 保存session到数据库
     */
    public Ret saveSession(SysUser user) throws LoginException {
        // 如果用户勾选保持登录，暂定过期时间为 1 年，否则为 12 小时，单位为秒
        boolean keepLogin = user.getKeepLogin() == 1 ? true : false;
        long liveSeconds = keepLogin ? 1 * 360 * 24 * 60 * 60 : 12 * 60 * 60;
        // 传递给控制层的 cookie 最大生存时间
        int maxAgeInSeconds = (int) (keepLogin ? liveSeconds : -1);
        // expireAt 用于设置 session 的过期时间点，需要转换成毫秒
        long expireAt = System.currentTimeMillis() + (liveSeconds * 1000);
        // 保存登录 session 到数据库
        Session session = new Session();
        String sessionId = StrKit.getRandomUUID();
        // id
        session.setId(sessionId);
        // 用户id
        session.setUserCode(user.getUserCode());
        // session生存时间 12小时到1年
        session.setExpiretTime(expireAt);
        user.setSessionId(sessionId);
        // 保存到数据库中
        if (!session.save()) {
            throw new LoginException("保存 session 到数据库失败，请联系管理员");
        }
        // 缓存到loginAccount ehcache.xml中 put(String cacheName, Object key, Object value)
        // 保存一份 sessionId 到 loginAccount 备用
        CacheKit.put(LOGIN_ACCOUNT_CACHE_NAME, sessionId, user);
        return Ret.ok(SESSION_ID_NAME, sessionId)
                .set(LOGIN_ACCOUNT_CACHE_NAME, user)
                // 用于设置 cookie 的最大存活时间
                .set("maxAgeInSeconds", maxAgeInSeconds);
    }

    /**
     * 获取登陆对象
     * user == loginAccount
     */
    public Visitor getVisitor(SysUser loginAccount) {
        LoginUserImpl loginUser = new LoginUserImpl();
        loginUser.setId(loginAccount.getId());
        loginUser.setUserCode(loginAccount.getUserCode());
        loginUser.setUserName(loginAccount.getUserName());
        loginUser.setOrgId(loginAccount.getOrgId());
        VisitorImpl visitor = new VisitorImpl(loginUser);
        visitor.setType(loginAccount.getSex());
        // 权限
//        Map<String, Boolean> funcMap = sysUserService.getUserFuncMap(user.getUserCode());
//        visitor.setFuncMap(funcMap);
        visitor.setTheme(loginAccount.getTheme());
        return visitor;
    }

    /**
     * 登陆失败
     */
    private void loginFail(SysUser user) throws LoginException {
        int failureNumber = user.getFailureNumber() == null ? 0 : user.getFailureNumber();
        user.setFailureNumber(failureNumber + 1);
        String msg = "帐号或密码错误";
        if (WebContant.IS_LOCK_USER == 1) {
            // 错误次数
            if (failureNumber >= WebContant.FAIL_NUM - 1) {
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                //锁定账号1天
                cal.add(Calendar.DATE, 1);
                user.setAllowLoginTime(cal.getTime());
                msg = "账号已被锁定24小时，请联系管理员";
            } else {
                msg = "密码或错误 " + user.getFailureNumber() + " 次，第" + WebContant.FAIL_NUM + "次错误账号将被锁定24小时";
            }
        }
        sysUserService.update(user);
        throw new LoginException(msg);
    }


    /**
     * 获取具有会话 ID 的登录帐户
     *
     * @param sessionId
     * @return
     */
    public SysUser getLoginAccountWithSessionId(String sessionId) {
        return CacheKit.get(LOGIN_ACCOUNT_CACHE_NAME, sessionId);
    }

    /**
     * 通过 sessionId 获取登录用户信息
     * sessoin表结构：session(id, user_code, expire_time)
     * 1：先从缓存里面取，如果取到则返回该值，如果没取到则从数据库里面取
     * 2：在数据库里面取，如果取到了，则检测是否已过期，如果过期则清除记录，
     * 如果没过期则先放缓存一份，然后再返回
     */
    public SysUser loginWithSessionId(String sessionId, String loginIp) {
        Session session = Session.dao.findById(sessionId);
        // session不存在
        if (session == null) {
            return null;
        }
        // session 已过期
        if (session.isExpired()) {
            // 被动式删除过期数据，此外还需要定时线程来主动清除过期数据
            session.delete();
            return null;
        }

        // 如果session存在 返回一个用户对象
        SysUser loginAccount = sysUserService.findByUserCode(session.getUserCode());
        // 找到 loginAccount 并且 是正常状态 才允许登录
        if (loginAccount != null && loginAccount.getAllowLogin() == 0) {
            // 保存一份 sessionId 到 loginAccount 到缓存中
            loginAccount.setSessionId(sessionId);
            CacheKit.put(LOGIN_ACCOUNT_CACHE_NAME, sessionId, loginAccount);
            return loginAccount;
        }
        return null;
    }


    /**
     * 退出登录
     */
    public void logout(String sessionId) {
        if (sessionId != null) {
            // 缓存中移出sessionid
            CacheKit.remove(LOGIN_ACCOUNT_CACHE_NAME, sessionId);
            // 删除sessionid
            Session.dao.deleteById(sessionId);
        }
    }
//
//    /**
//     * 从数据库重新加载登录账户信息
//     */
//    public void reloadLoginAccount(SysUser loginAccountOld) {
//        String sessionId = loginAccountOld.getSessionId();
//        SysUser user = sysUserService.findByUserCode(loginAccountOld.getUserCode());
//        user.setSessionId(sessionId);// 保存一份 sessionId 到 loginAccount 备用
//        // 集群方式下，要做一通知其它节点的机制，让其它节点使用缓存更新后的数据，
//        // 将来可能把 account 用 id : obj 的形式放缓存，更新缓存只需要 CacheKit.remove("account", id) 就可以了，
//        // 其它节点发现数据不存在会自动去数据库读取，所以未来可能就是在 AccountService.getById(int id)的方法引入缓存就好
//        // 所有用到 account 对象的地方都从这里去取
//        CacheKit.put(loginAccountCacheName, sessionId, user);
//    }
}
