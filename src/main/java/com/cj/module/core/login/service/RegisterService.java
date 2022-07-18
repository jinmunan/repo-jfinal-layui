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
//package com.cj.publicmodule.login.service;
//
//import com.cj.common.kit.Md5Kit;
//import com.cj.common.model.SysUser;
//import com.cj.common.model.SysUserRole;
//import com.cj.portal.core.service.SysUserService;
//import com.jfinal.aop.Inject;
//import com.jfinal.kit.Ret;
//import com.jfinal.plugin.activerecord.Record;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * 第三方库登录实现
// * @author qinhailin
// * @date 2020-10-22
// *
// */
//public class RegisterService extends DbService{
//
//	/**数据源名称*/
//	private final String DB_CONFIG_NAME ="oracle";
//	/**用户表*/
//	private final String TABLE="SYS_USER";
//	/**主键*/
//	private final String PRIMARY_KEY="ID";
//	/**登录字段*/
//	private final String USER_CODE_KEY="USER_CODE";
//	/**姓名字段*/
//	private final String USER_NAME_KEY="USER_NAME";
//	/**密码字段*/
//	private final String PASSWORD_KEY="PASSWD";
//
//	@Inject
//	LoginService loginService;
//	@Inject
//	SysUserService sysUserService;
//
//	@Override
//	public String getTable() {
//		return TABLE;
//	}
//
//	@Override
//	public String getPrimaryKey() {
//		return PRIMARY_KEY;
//	}
//
//	@Override
//	public String getDb() {
//		return DB_CONFIG_NAME;
//	}
//
//	/**
//	 * 登录验证，第一次登录会注册用户到本系统
//	 * @param userCode
//	 * @param password
//	 * @param req
//	 * @return
//	 * @throws LoginException
//	 */
//	public Ret doLogin(String userCode,String password,HttpServletRequest req) throws LoginException{
//		//验证数据
//		if ((userCode == null) || (userCode.trim().length() == 0) || (password == null)
//				|| (password.trim().length() == 0)) {
//			throw new LoginException("请输入用户名和密码");
//		}
//		//验证用户
//		Record user=this.findPk(USER_CODE_KEY, userCode);
//		if(user==null){
//			throw new LoginException("用户不存在");
//		}
//		//验证用户密码
//		if(checkPassword(user,password)){
//			SysUser sysUser=sysUserService.findByUserCode(userCode);
//			if(sysUser==null){
//				//录入本系统用户
//				sysUser=new SysUser();
//				sysUser.setId(user.getStr(getPrimaryKey()));
//				sysUser.setUserCode(userCode);
//				sysUser.setPasswd(Md5Kit.md5(password));
//				sysUser.setUserName(user.getStr(USER_NAME_KEY));
//				sysUser.setOrgId("sys");
//				sysUser.setSex(1);
//				sysUser.save();
//				//授权系统角色 TODO
//				SysUserRole sysUserRole=new SysUserRole();
//				sysUserRole.setUserCode(userCode);
//				sysUserRole.setRoleCode("admin");
//				sysUserRole.setId("admin"+userCode);
//				sysUserRole.save();
//			}
//			return loginService.returnVistor(sysUser, req);
//		}else{
//			throw new LoginException("帐号或密码错误");
//		}
//
//	}
//
//	/**
//	 * 验证密码
//	 * @param user
//	 * @param password
//	 * @return
//	 */
//	public boolean checkPassword(Record user,String password){
//		return user.get(PASSWORD_KEY).equals(Md5Kit.md5(password));
//	}
//}
