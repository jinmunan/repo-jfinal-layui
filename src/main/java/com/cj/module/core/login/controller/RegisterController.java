///**
// * Copyright 2019-2022 覃海林(qinhaisenlin@163.com).
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.cj.publicmodule.login.controller;
//
//import com.jfinal.aop.Inject;
//import com.jfinal.core.Path;
//import com.qinhailin.common.base.BaseController;
//import com.qinhailin.pub.login.exception.LoginException;
//import com.qinhailin.pub.login.service.RegisterService;
//
///**
// * 第三方库登录接口
// * @author qinhailin
// * @date 2020-10-22
// */
//@Path("/pub/reg")
//public class RegisterController extends BaseController {
//
//	@Inject
//	RegisterService registerService;
//
//	/**
//	 * 用户名、密码
//	 */
//	public void index(){
//		String userCode = getPara("userCode");
//		String password = getPara("password");
//		try {
//			registerService.doLogin(userCode, password, getRequest());
//			renderJson(ok());
//		} catch (LoginException e) {
//			e.printStackTrace();
//			renderJson(fail(e.getCause()!=null?e.getCause().getMessage():e.getMessage()));
//		}
//	}
//}
