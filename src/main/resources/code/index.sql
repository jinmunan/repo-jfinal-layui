/**index.html代码模板*/
#sql("index")
#[[
#@layoutT('${tableComment}')
#define main()
<div class="container-wrap">
   #@formStart()
      #@queryStart('关键词查询')
   <input type="search" name="keyword" autocomplete="off" class="layui-input" placeholder="搜索关键词" style="padding-left:20px"/>
   <i class="layui-icon layui-icon-search" style="position: absolute;top:7px;left:2px"></i>
      #@queryEnd()
   #@formEnd()

   #@table()
</div>
#end

#define js()
<!-- 分页表格 -->
<script>
    gridArgs.title='${tableComment}';
    gridArgs.dataId='${primaryKey}';
    gridArgs.deleteUrl='#(path)${actionKey}/delete';
    gridArgs.updateUrl='#(path)${actionKey}/edit/';
    gridArgs.addUrl='#(path)${actionKey}/add';
    gridArgs.gridDivId ='maingrid';
    initGrid({id : 'maingrid'
        ,elem : '#maingrid'
        ,toolbar:'#table_toolbar'//开启头部工具栏，并为其绑定左侧模板
        ,cellMinWidth: 100
        ,cols : [ [
${tableCols}
            {fixed:'right',width : 180,align : 'left',toolbar : '#bar_maingrid'}
            ] ]
        ,url:"#(path)${actionKey}/list"
        ,searchForm : 'searchForm'
    });

</script>
#end

]]#

#end