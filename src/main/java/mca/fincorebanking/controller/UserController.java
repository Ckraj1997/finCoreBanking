package mca.fincorebanking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import mca.fincorebanking.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
    @SuppressWarnings("unused")
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

}
