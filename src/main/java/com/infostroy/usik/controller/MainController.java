package com.infostroy.usik.controller;

import javax.servlet.http.HttpServletRequest;

import com.infostroy.usik.modal.entity.Student;
import com.infostroy.usik.modal.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This class is for handling major requests such as '/', '/ login', '/ logout'.
 *
 * @author N.Usik
 */
@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private StudentService service;

    /**
     * Home page redirect request.
     *
     * <p>Checks if the user is authorized, if not, then sends to authorization, otherwise, direct to the main page.</p>
     *
     * @param request http servlet request
     * @param model   modal for redirect and store username
     */
    @RequestMapping("/")
    public String index(HttpServletRequest request, Model model) {
        String username = (String) request.getSession().getAttribute("username");

        logger.info("Request to index, Student name in session: {}", username);

        if (username == null || username.isEmpty()) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        model.addAttribute("students", service.listAll());

        return "home";
    }

    /**
     * Login page redirect request.
     */
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String showLoginPage() {
        logger.info("Request GET to login, show login page");
        return "login";
    }

    /**
     * User Authorization request.
     *
     * <p>Checks if a user already exists with that name, if so,
     * then reports an error, in the other case creates a new user.</p>
     *
     * @param request  http servlet request
     * @param username username logged user
     */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String doLogin(HttpServletRequest request, @RequestParam(defaultValue = "") String username) {
        username = username.trim();

        logger.info("Request POST to login Student name: {}", username);

        if (username.isEmpty()) {
            return "login";
        }

        if (!service.findByName(username).isPresent()) {
            service.save(new Student(username));
        } else {
            request.getSession().setAttribute("error", "This user is already logged in!");
            return "login";
        }

        request.getSession().setAttribute("username", username);

        return "redirect:/";
    }


    /**
     * Logout request.
     *
     * <p>Checks if the user is authorized, if not, then sends to authorization, otherwise,
     * delete user and sends to authorization.</p>
     *
     * @param request  http servlet request
     * @param username username logged user
     */
    @RequestMapping(path = "/logout")
    public String logout(HttpServletRequest request, @RequestParam(defaultValue = "") String username) {
        request.getSession(true).invalidate();

        logger.info("Request to logout, delete Student: {}", username);

        if (service.findByName(username).isPresent()) {
            service.delete(service.findByName(username).get().getId());
        }

        return "redirect:/login";
    }

}