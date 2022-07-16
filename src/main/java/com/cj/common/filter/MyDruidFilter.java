/**
 * Copyright 2019-2022 覃海林(qinhaisenlin@163.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package com.cj.common.filter;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 * 打印带参数的SQL语句
 * @author qinhailin
 * @date 2020-05-07
 */
public class MyDruidFilter extends FilterAdapter {
	@Override
    public void statement_close(FilterChain chain, StatementProxy statement) throws SQLException {
        super.statement_close(chain, statement);
        Map<Integer, JdbcParameter> parameters = statement.getParameters();
        String sql = statement.getBatchSql();
        if(StrKit.notBlank(sql)){
            for (Map.Entry<Integer,JdbcParameter> lEntry : parameters.entrySet()){
                JdbcParameter value = lEntry.getValue();
                if(value == null){
                    continue;
                }
                Object obj = value.getValue();
                if(obj == null){
                    continue;
                }
                if(obj instanceof Date){
                	obj=DateKit.toStr((Date)obj,"yyyy-MM-dd HH:mm:ss");
                }
                String str = obj.toString();
                switch(value.getSqlType()){
	                case 12 :
	                case 1 :
	                case 91 :
	                str = "'"+str+"'";
	                break;
                }
                str =escapeExprSpecialWord(str);
                sql = sql.replaceFirst("\\?",str);
            }
            //非开发模式，记录到日志中
            if(!PropKit.getBoolean("devMode")){
            	LogKit.info(sql);
            }else{
            	System.out.println("Sql= " + sql);           	
            }
        }
    }
	/**
	 * 转义正则特殊字符 （$()*+.[]?\^{},|）
	 * 
	 * @param keyword
	 * @return
	 */
	public static String escapeExprSpecialWord(String keyword) {
		if (StrKit.notBlank(keyword)) {
			String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
			for (String key : fbsArr) {
				if (keyword.contains(key)) {
					keyword = keyword.replace(key, "\\" + key);
				}
			}
		}
		return keyword;
	}
}
