package org.example.web.controllers;

import org.apache.log4j.Logger;
import org.example.app.exceptions.BookShelfLoginException;
import org.example.app.services.LoginService;
import org.example.web.dto.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/login")
public class LoginController {

    private Logger logger = Logger.getLogger(LoginController.class);
    private LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }


    @GetMapping
    public ModelAndView login(Model model) {
        logger.info("GET /login returns 'login_page.html'");
        model.addAttribute("loginForm", new LoginForm());
        return new ModelAndView("login_page");
    }

    @PostMapping(value = "/auth")
    public String authenticate(LoginForm loginForm) throws BookShelfLoginException{
        if (loginService.authenticate(loginForm)){
            logger.info("login OK redirect to book shelf");
            return "redirect:/books/shelf";
        } else {
            logger.info("login FAIL redirect to login form");
            throw new BookShelfLoginException("Invalid username or password!");
        }
    }

    @ExceptionHandler(BookShelfLoginException.class)
    public String handle(Model model, BookShelfLoginException ex){
        model.addAttribute("errorMassage", ex.getMessage());
        return "/errors/404";
    }
}
