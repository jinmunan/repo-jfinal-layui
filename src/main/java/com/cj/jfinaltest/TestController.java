package com.cj.jfinaltest;

import com.jfinal.core.Controller;
import com.jfinal.core.Path;

/**
 * @version 1.0
 * @author： jinmunan
 * @date： 2022-07-14 21:16
 */
@Path("/abc")
public class TestController extends Controller {
    public void index(){
        renderText("hello jfinal");
    }
}
