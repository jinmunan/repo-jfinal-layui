package com.cj.common.safe;

import com.jfinal.aop.Inject;
import com.jfinal.core.Const;
import com.jfinal.core.Controller;
import com.jfinal.token.TokenManager;


/**
 * token缓存策略
 * token
 * @author THINKPAD
 */
public class TokenService {
    private static boolean flag = false;

    @Inject
    private TokenCacheImpl tokenCache;


    /**
     * 创建token
     *
     * @param controller
     */
    public void createToken(Controller controller) {
        // 只会执行一次,接下来都只会执行最后的代码
        if (!flag) {
            // 令牌管理器初始化,将自己写的实现类传进去 主要是初始化自己写的token缓存策略
            TokenManager.init(tokenCache);
            flag = true;
        }


        // 在页面中生成一个隐藏域,保存token信息
        // sb.append("<input type='hidden' name='").append(tokenName).append("' id='").append(tokenName).append("' value='").append(tokenId).append("' />");
        // 使用 #(token) 指令，将 token 隐藏域输出到页面表单之中，表单提交的时候该表单域会被提交
        TokenManager.createToken(controller, Const.DEFAULT_TOKEN_NAME, Const.DEFAULT_SECONDS_OF_TOKEN_TIME_OUT);
    }

    /**
     * 验证token
     *
     * @param controller
     */
    public boolean validateToken(Controller controller) {
        // _jfinal_token
        return TokenManager.validateToken(controller, Const.DEFAULT_TOKEN_NAME);
    }
}
