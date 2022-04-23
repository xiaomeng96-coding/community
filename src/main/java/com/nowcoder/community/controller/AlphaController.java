package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaServiceImpl;
import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author : zhiHao
 * @since : 2021/9/19
 */
@Controller
@RequestMapping("/alpha")
public class AlphaController {

    private static final Logger logger = LoggerFactory.getLogger(AlphaController.class);

    @Autowired
    private AlphaServiceImpl alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String helloSpringBoot() {
        return "Hello Spring Boot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String selectByAlphaService() {
        return alphaService.selectByAlphaDao();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        // 获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        System.out.println(request.getParameter("code"));

        // 返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try(
            PrintWriter writer = response.getWriter()
            ){
            writer.write("<hl>牛客网</hl>");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GET请求，默认请求为GET请求
    // localhost:8080/community/alpha/students?current=3&limit=20
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println("current: " + current);
        System.out.println("limit: " + limit);
        return "some students";
    }

    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }

    // POST请求
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age) {
        System.out.println("name: " + name);
        System.out.println("age: " + age);
        return "success";
    }

    // 响应HTML数据
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","张三");
        modelAndView.addObject("age","30");
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name", "南京大学");
        model.addAttribute("age", "110");
        return "/demo/view";
    }

    // 响应JSON数据(异步请求)
    // Java对象 -> JSON字符串 -> JS对象
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getMap() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getMaps() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp1 = new HashMap<>();
        emp1.put("name", "张三");
        emp1.put("age", 23);
        emp1.put("salary", 8000.00);
        list.add(emp1);

        Map<String, Object> emp2 = new HashMap<>();
        emp2.put("name", "李四");
        emp2.put("age", 24);
        emp2.put("salary", 9000.00);
        list.add(emp2);
        return list;
    }

    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        cookie.setPath("/community/alpha");
        // 设置cookie的生存时间
        cookie.setMaxAge(60 * 10);
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get cookie";
    }

    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");
        return "set session";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    // ajax示例
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age) {
        logger.info("name[{}] :, age[{}]", name, age);
        return CommunityUtil.convert2JSONString(0, "操作成功");
    }
}
