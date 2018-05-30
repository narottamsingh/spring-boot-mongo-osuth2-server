package com.ns.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Narottam Singh
 * @Date 2018-05-30
 */
@Controller
@RequestMapping("/api")
public class ApiController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String sayHello() {
        return "Secure API access Hahahahahah!";
    }

}
