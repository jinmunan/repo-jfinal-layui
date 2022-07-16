package com.cj.common.safe;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.validate.Validator;

/**
 * token验证
 *
 * @author THINKPAD
 */
public class TokenValidator extends Validator {
    @Inject
    TokenService tokenService;

    @Override
    protected void validate(Controller controller) {
        // 验证token
        boolean flag = tokenService.validateToken(controller);
        if (!flag) {
            // 判断是否是ajax请求
            boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(controller.getHeader("X-Requested-With"));
            if (isAjax) {
                controller.renderJson(Ret.fail("msg", "token验证失败"));
            } else {
                controller.setAttr("msg", "token验证失败");
                controller.renderError(403);
            }
        }
    }

    /**
     * 注意handleError(Controller controller)只有在校验失败时才会调用
     *
     * @param controller
     */
    @Override
    protected void handleError(Controller controller) {

    }

}
