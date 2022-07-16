/**index_subtable.html代码模板*/

#sql("index_subtable")
#[[
#@layoutT("${tableComment}")
#define main()
<div class="container-wrap">
	#@formStart()
		#@queryStart('关键词查询')
		   <input type="search" name="keyword" autocomplete="off" class="layui-input" placeholder="搜索关键词" style="padding-left:20px"/>
		   <i class="layui-icon layui-icon-search" style="position: absolute;top:7px;left:2px"></i>
      #@queryEnd()
	#@formEnd()
	
	<!-- 主表数据 -->
	#@table()
	
	<!-- 从表Tab页签-->
	#@subTable([${subTableTitle}])
</div>	
#end

#define js()

<!-- 引入从表js-->
#@subTableJs()

<!-- 主表 -->
<script>
    gridArgs.title='${tableComment}';
    gridArgs.dataId='${primaryKey}';
    gridArgs.deleteUrl='#(path)${actionKey}/delete';
    gridArgs.updateUrl='#(path)${actionKey}/edit/';
    gridArgs.addUrl='#(path)${actionKey}/add';
    gridArgs.heightDiff=340;//调整表格高度
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

<!-- 从表 -->
${subTableScript}

#end

<!-- 监听主表点击事件 --> 
#define layuiFunc()
  //监听行单击事件（双击事件为：rowDouble）
  table.on('row(maingrid)', function(obj){
    var data = obj.data;
    var refId=data.${refId};
    //渲染从表数据
    ${renderSubTable}
     //标注选中样式
    obj.tr.addClass('layui-table-click').siblings().removeClass('layui-table-click');
    
  });
#end
]]#
#end
