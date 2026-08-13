package com.mingji.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** 站点密码页面。 */
@Controller
public class AccessController {

    @GetMapping("/access")
    public String access() {
        return "access";
    }
}
