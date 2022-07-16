package com.cj.common.base.service;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.util.kit.IdKit;
import com.cj.common.vo.Grid;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 基于JFinal的通用service接口进行增强
 *
 * @author THINKPAD
 */
@SuppressWarnings("all")
public abstract class BaseService {

    protected Logger logger = Logger.getLogger(getClass());

    /**
     * 获取model dao
     */
    public abstract Model<?> getDao();

    /**
     * 指定数据源,多数据源情况下使用<br/>
     * 列如oracle数据源别名为oracle，在service重写该方法:
     */
    public String getDb() {
        return null;
    }

    /**
     * 获取DBPro数据源
     */
    private DbPro getDbPro() {
        if (getDb() != null) {
            return Db.use(getDb());
        }
        return Db.use();
    }

    /**
     * 获取table名称
     */
    public String getTable() {
        return _getTable().getName();
    }

    /**
     * 获取表主键（单键表）
     */
    public String getPK() {
        return _getTable().getPrimaryKey()[0];
    }

    /**
     * 获取表
     */
    protected Table _getTable() {
        if (getDao() == null) {
            logger.error("请实现getDao()方法,且不能返回null");
        }
        return TableMapping.me().getTable(getDao().getClass());
    }


    /**
     * 通用findById
     */
    public Model<?> findById(String id) {
        return getDao().findById(id);
    }

    /**
     * 通过主键查找对象数据
     */
    public Model<?> findByPk(String pk, String value) {
        List<?> list = getDao().find(getQuerySql() + " where " + pk + "=?", value);
        if (list.size() > 0) {
            return (Model<?>) list.get(0);
        }
        return null;
    }

    /**
     * 通用save
     */
    public boolean save(Model<?> entity) {
        //主键赋值uuid
        if (entity.get(getPK()) == null) {
            entity.set(getPK(), IdKit.createUUID());
        }
        ;
        return entity.save();
    }

    /**
     * 通用update
     */
    public boolean update(Model<?> entity) {
        return entity.update();
    }

    /**
     * 通用delete
     */
    public boolean delete(Model<?> entity) {
        return entity.delete();
    }

    /**
     * 通用deleteById
     */
    public boolean deleteById(String id) {
        return getDao().deleteById(id);
    }

    /**
     * 通用deleteByIds
     */
    public void deleteByIds(List<String> ids) {
        Object[][] paras = new Object[ids.size()][1];
        for (int i = 0; i < ids.size(); i++) {
            paras[i][0] = ids.get(i);
        }
        String sql = "delete from " + getTable() + " where " + getPK() + "=?";
        getDbPro().batch(sql, paras, ids.size());
    }

    /**
     * 根据字段删除数据
     */
    public void deleteByPk(List<String> ids, String pk) {
        Object[][] paras = new Object[ids.size()][1];
        for (int i = 0; i < ids.size(); i++) {
            paras[i][0] = ids.get(i);
        }
        String sql = "delete from " + getTable() + " where " + pk + "=?";
        getDbPro().batch(sql, paras, ids.size());
    }

    /**
     * 是否存在对象数据
     */
    public boolean isExists(String pk, String value) {
        List<?> list = getDbPro().find(getQuerySql() + "where " + pk + "=?", value);
        return list.size() > 0;
    }

    /**
     * 通用查询列表
     */
    public List<Record> queryAllList() {
        return getDbPro().find(getQuerySql());
    }

    /**
     * 通用查询列表
     */
    public List<Record> queryAllList(String groupOrderBy) {
        return getDbPro().find(getQuerySql() + groupOrderBy);
    }

    /**
     * 通用查询
     */
    public List<Record> queryForList(String sql) {
        return getDbPro().find(sql);
    }

    /**
     * 通用查询
     */
    public List<Record> queryForList(String sql, Object... object) {
        return getDbPro().find(sql, object);
    }

    /**
     * 通用查询
     */
    public List<Record> queryForList(String sql, Record record) {
        return queryForList(sql, record, null);
    }

    /**
     * 通用查询
     */
    public List<Record> queryForList(String sql, Record record, String groupOrderBy) {
        List<Object> paras = new ArrayList<>();
        sql = this.createQuerySql(sql, groupOrderBy, record, paras, "like");
        return getDbPro().find(sql, paras.toArray());
    }

    /**
     * 通用查询
     */
    public List<Record> queryForListEq(String sql, Record record, String groupOrderBy) {
        List<Object> paras = new ArrayList<>();
        sql = this.createQuerySql(sql, groupOrderBy, record, paras, "=");
        return getDbPro().find(sql, paras.toArray());
    }

    /**
     * 自定义分页查询
     */
    public Grid queryForList(String sql, int pageNumber, int pageSize, Record record, String groupOrderBy) {
        List<Object> paras = new ArrayList<>();
        sql = this.createQuerySql(sql, groupOrderBy, record, paras, "like");
        return getGrid(pageNumber, pageSize, sql, paras.toArray());
    }

    /**
     * 自定义分页查询
     */
    public Grid queryForListEq(String sql, int pageNumber, int pageSize, Record record, String groupOrderBy) {
        List<Object> paras = new ArrayList<>();
        sql = this.createQuerySql(sql, groupOrderBy, record, paras, "=");
        return getGrid(pageNumber, pageSize, sql, paras.toArray());
    }

    /**
     * 自定义分页查询
     */
    public Grid queryForList(int pageNumber, int pageSize, String sql) {
        return getGrid(pageNumber, pageSize, sql);
    }

    /**
     * 自定义分页查询
     */
    public Grid queryForList(int pageNumber, int pageSize, String sql, Object... object) {
        return getGrid(pageNumber, pageSize, sql, object);
    }

    /**
     * 自定义分页查询
     */
    public Grid queryForList(int pageNumber, int pageSize) {
        return getGrid(pageNumber, pageSize, getQuerySql());
    }

    /**
     * 自定义分页查询
     */
    public Grid queryForList(int pageNumber, int pageSize, Record record) {
        List<Object> paras = new ArrayList<>();
        String sql = createQuerySql(getQuerySql(), null, record, paras, "like");
        return getGrid(pageNumber, pageSize, sql, paras.toArray());
    }

    /**
     * 自定义分页查询
     */
    public Grid queryForList(int pageNumber, int pageSize, Record record, String orderBygroupBySql) {
        List<Object> paras = new ArrayList<>();
        String sql = createQuerySql(getQuerySql(), orderBygroupBySql, record, paras, "like");
        return getGrid(pageNumber, pageSize, sql, paras.toArray());
    }

    /**
     * 全等查询
     */
    public Grid queryForListEq(int pageNumber, int pageSize, Record record) {
        List<Object> paras = new ArrayList<>();
        String sql = createQuerySql(getQuerySql(), null, record, paras, "=");
        return getGrid(pageNumber, pageSize, sql, paras.toArray());
    }

    /**
     * 全等查询
     */
    public Grid queryForListEq(int pageNumber, int pageSize, Record record, String orderBygroupBySql) {
        List<Object> paras = new ArrayList<>();
        String sql = createQuerySql(getQuerySql(), orderBygroupBySql, record, paras, "=");
        return getGrid(pageNumber, pageSize, sql, paras.toArray());
    }

    /**
     * 分页,模糊查询
     */
    public Grid queryForList(Grid grid, Record record) {
        List<Object> paras = new ArrayList<>();
        String sql = this.createQuerySql(getQuerySql(), null, record, paras, "like");
        return getGrid(grid.getPageNumber(), grid.getPageSize(), sql, paras.toArray());
    }

    /**
     * 分页查询,分组排序
     */
    public Grid queryForList(Grid grid, String orderBygroupBySql) {
        List<Object> paras = new ArrayList<>();
        String sql = this.createQuerySql(getQuerySql(), orderBygroupBySql, null, paras, "like");
        return getGrid(grid.getPageNumber(), grid.getPageSize(), sql, paras.toArray());
    }

    /**
     * 分页,模糊查询,分组排序
     */
    public Grid queryForList(Grid grid, Record record, String orderBygroupBySql) {
        List<Object> paras = new ArrayList<>();
        String sql = this.createQuerySql(getQuerySql(), orderBygroupBySql, record, paras, "like");
        return getGrid(grid.getPageNumber(), grid.getPageSize(), sql, paras.toArray());
    }

    /**
     * 获取grid对象 网格
     */
    private Grid getGrid(int pageNumber, int pageSize, String sql, Object... paras) {
        SqlPara sqlPara = new SqlPara().setSql(sql);
        for (int i = 0; i < paras.length; i++) {
            sqlPara.addPara(paras[i]);
        }

        Page<Record> page = getDbPro().paginate(pageNumber, pageSize, sqlPara);
        return new Grid(page.getList(), pageNumber, pageSize, page.getTotalRow());
    }

    /**
     * 获取grid对象 网格
     */
    private Grid getGrid(int pageNumber, int pageSize, String sql) {
        SqlPara sqlPara = new SqlPara().setSql(sql);
        Page<Record> page = getDbPro().paginate(pageNumber, pageSize, sqlPara);
        return new Grid(page.getList(), pageNumber, pageSize, page.getTotalRow());
    }

    /**
     * 拼接模糊查询条件
     */
    private String createQuerySql(String sql, String orderByGroupBySql, Record record, List<Object> paras,
                                  String queryType) {
        if (record == null) {
            return orderByGroupBySql == null ? sql : sql + " " + orderByGroupBySql;
        }

        Map<String, Object> columns = record.getColumns();
        Iterator<String> iter = columns.keySet().iterator();
        StringBuffer whereSql = new StringBuffer();

        while (iter.hasNext()) {
            String column = iter.next();
            Object value = columns.get(column);

            if (value != null && value.toString().trim().length() > 0) {
                if (whereSql.length() > 0) {
                    whereSql.append(" and ");
                }
                //用法看用户管理查询功能
                //column=、column<=、column>=
                if (column.endsWith("=")) {
                    whereSql.append(column).append(" ? ");
                    paras.add(value);
                    //column<、column>
                } else if (column.endsWith(">") || column.endsWith("<")) {
                    whereSql.append(column).append(" ? ");
                    paras.add(value);
                    //column like
                } else if (column.toLowerCase().endsWith("like")) {
                    whereSql.append(column).append(" ? ");
                    paras.add("%" + value + "%");
                } else if ("=".equals(queryType)) {
                    whereSql.append(column).append(" =? ");
                    paras.add(value);
                } else {
                    whereSql.append(column).append(" like ? ");
                    paras.add("%" + value + "%");
                }
            }
        }

        if (whereSql.length() > 0) {
            if (sql.toLowerCase().contains("where")) {
                sql += " and " + whereSql.toString();
            } else {
                sql += " where " + whereSql.toString();
            }
        }

        if (orderByGroupBySql != null) {
            sql += " " + orderByGroupBySql;
        }

        return sql;
    }

    /**
     * select * from getTable()
     */
    private String getQuerySql() {
        return "select * from " + getTable() + " ";
    }

    /**
     * 自定义sql查询，sql定义在sql模板文件中
     */
    public Grid queryForListBySqlTemplate(String dbName, Integer pageNumber, Integer pageSize, JSONObject params, String sqlTemplateName, String orderBygroupBySql) {
        DbPro dbPro = Db.use();
        //切换数据源
        if (StrKit.notBlank(dbName)) {
            dbPro = Db.use(dbName);
        }
        SqlPara sqlPara = dbPro.getSqlPara(sqlTemplateName, params);
        // 如果有排序语句，则追加
        if (StrKit.notBlank(orderBygroupBySql)) {
            sqlPara.setSql(sqlPara.getSql() + " " + orderBygroupBySql);
        }
        Page<Record> page = dbPro.paginate(pageNumber, pageSize, sqlPara);
        return new Grid(page.getList(), pageNumber, pageSize, page.getTotalRow());
    }

    /**
     * 自定义sql查询，sql定义在sql模板文件中
     */
    public Grid queryForListBySqlTemplate(Integer pageNumber, Integer pageSize, JSONObject params, String sqlTemplateName, String orderBygroupBySql) {
        return queryForListBySqlTemplate(null, pageNumber, pageSize, params, sqlTemplateName, orderBygroupBySql);
    }

    /**
     * 通用查询
     */
    public Grid queryForListByRecord(String dbConfig, int pageNumber, int pageSize, Record params, String orderBySql, String groupBySql) {
        return getGrid(dbConfig, pageNumber, pageSize, params, orderBySql, groupBySql);
    }

    /**
     * 通用查询
     */
    public Grid queryForListByRecord(int pageNumber, int pageSize, Record params, String orderBySql) {
        return getGrid(null, pageNumber, pageSize, params, orderBySql, null);
    }

    /**
     * 单表分页条件查询，支持多数据源
     */
    private Grid getGrid(String dbConfig, int pageNumber, int pageSize, Record params, String orderBySql, String groupBySql) {
        //拼接sql中的from部分
        StringBuilder from = new StringBuilder("from ");
        from.append(getTable()).append(" where 1=1");
        //这个用来存值不为空的value集合
        List<Object> notNullValues = new ArrayList<>();
        if (params != null && params.getColumnNames().length > 0) {
            //查询条件前置部分集合（字段+匹配符号，如 name like,id = ）
            //也可以直接写全查询条件，这时不需要value,如(id = 1 or parent_id = 1)
            String columnNames[] = params.getColumnNames();
            //查询条件后置部分集合（每个查询条件匹配的值，如"张三",20）
            Object[] columnValues = params.getColumnValues();
            for (int i = 0; i < columnNames.length; i++) {
                String columnName = columnNames[i];
                Object columnValue = columnValues[i];
                if (columnValue != null && StrKit.notBlank(String.valueOf(columnValue))) {
                    //处理不带?号的查询条件，这类查询条件，value一律传"withoutValue"
                    if ("withoutValue".equals(columnValue)) {
                        from.append(" and ").append(columnName);
                    } else {
                        if (columnName.contains("like")) {
                            columnValue = "%" + columnValue + "%";
                        }
                        from.append(" and ").append(columnName).append(" ?");
                        notNullValues.add(columnValue);
                    }
                }
            }
        }
        Object[] notNullValueArr = new Object[notNullValues.size()];
        notNullValues.toArray(notNullValueArr);
        //计数语句
        String totalRowSql = "select count(*) " + from.toString();

        if (StrKit.notBlank(groupBySql)) {
            totalRowSql += " " + groupBySql;
        }
        //查询语句
        String findSql = "select * " + from.toString();
        if (StrKit.notBlank(orderBySql)) {
            findSql += " " + orderBySql;
        }
        Page<Record> page = new Page<Record>();
        if (StrKit.notBlank(dbConfig)) {
            page = Db.use(dbConfig).paginateByFullSql(pageNumber, pageSize, totalRowSql, findSql, notNullValueArr);
        } else {
            page = Db.paginateByFullSql(pageNumber, pageSize, totalRowSql, findSql, notNullValueArr);
        }

        return new Grid(page.getList(), pageNumber, pageSize, page.getTotalRow());
    }
}
