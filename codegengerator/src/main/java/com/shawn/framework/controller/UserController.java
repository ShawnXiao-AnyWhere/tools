package com.shawn.framework.controller;

import com.shawn.framework.domain.User;
import com.shawn.framework.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Auto Generator
 * @date 2017-10-11 13:45:05
 */
@Controller
@RequestMapping("/framework/users")
public class UserController {

  @Autowired
  private UserService userService;


  @RequestMapping(value = "/index.html", method = RequestMethod.GET)
  public ModelAndView index() {
    ModelAndView mav = new ModelAndView();
    mav.setViewName("framework/user");
    return mav;
  }
}
