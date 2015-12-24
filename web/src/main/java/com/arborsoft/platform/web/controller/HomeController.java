package com.arborsoft.platform.web.controller;

import com.arborsoft.platform.web.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

@Controller
public class HomeController {

    @RequestMapping(
        value = "/",
        method = RequestMethod.GET
    )
    public ModelAndView home(Authentication authentication) throws Exception {
        final User user = (User) authentication.getPrincipal();

        return new ModelAndView("home", new HashMap<String, Object>(){
            {
                put("user", user);
            }
        });
    }

    @RequestMapping(
            value = "/login",
            method = RequestMethod.GET
    )
    public String login() throws Exception {
        return "login";
    }

}
