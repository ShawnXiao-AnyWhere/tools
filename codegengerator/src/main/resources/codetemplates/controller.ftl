package ${packageName};

import ${className};
import ${basePackageName}.service.${classSimpleName}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author ${author}
 * @date ${date}
 */
@Controller
@RequestMapping("/${module}/${classSimpleName?lower_case}s")
public class ${classSimpleName}Controller {

  @Autowired
  private ${classSimpleName}Service ${classSimpleName?lower_case}Service;


  @RequestMapping(value = "/index.html", method = RequestMethod.GET)
  public ModelAndView index() {
    ModelAndView mav = new ModelAndView();
    mav.setViewName("${module}/${classSimpleName?lower_case}");
    return mav;
  }
}
