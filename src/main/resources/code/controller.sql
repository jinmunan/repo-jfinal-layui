/**Controller.java代码模板*/
#sql("Controller")
#[[
package ${controllerPackage};

import com.jfinal.aop.Inject;
import com.jfinal.core.Path;
import com.jfinal.plugin.activerecord.Record;

import com.qinhailin.common.base.BaseController;
import com.qinhailin.common.vo.Grid;
import ${importModel};
import ${servicePackage}.${modelName}Service;

/**
 * ${tableComment}
 * @author ${author}
 * @date ${date}
 */
@Path("${actionKey}")
public class ${modelName}Controller extends BaseController {

   @Inject
   ${modelName}Service service;

  	public void index(){
    	render("index.html");
  	}
    
   public void list() {
       Record record=getAllParamsToRecord();
	   int pageNumber=getParaToInt("pageNumber", 1);
	   int pageSize=getParaToInt("pageSize", 10);
	   Grid g=service.page(pageNumber, pageSize,record);
	   renderJson(g);
	}
    
   public void add() {
    	render("add.html");
   }

   public void save() {
    	${modelName} entity=getBean(${modelName}.class);
    	entity.setId(createUUID());
    	entity.save();
    	setAttr("${lowercaseModelName}", entity);
    	render("add.html");
   }

   public void edit() {
      setAttr("${lowercaseModelName}", service.findById(getPara(0)));
      render("edit.html");
   }

   public void update() {
      ${modelName} entity=getBean(${modelName}.class);
      entity.update();
      setAttr("${lowercaseModelName}", entity);
      render("edit.html");
   }

   public void delete() {
      service.deleteByIds(getIds());
      renderJson(suc());
   }

}
]]#
#end