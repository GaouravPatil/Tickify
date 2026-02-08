package com.ticket_tracking_service.controller;



import com.ticket_tracking_service.Repository.UserRepository;
import com.ticket_tracking_service.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    private final UserRepository userRepository;

    public AdminAuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public String adminLogin(@RequestParam String email,
                             @RequestParam String password,
                             HttpServletRequest request) {

        Optional<User> optionalUser =
                userRepository.findByEmailAndRoleAndIsActiveTrue(email, "ADMIN");

        if(optionalUser.isEmpty()){
            return "redirect:/admin-login.html";
        }

        User user = optionalUser.get();

        // ⚠️ replace with real hash check later
        if(!user.getPassword().equals(password)){
            return "redirect:/admin-login.html";
        }

        HttpSession session = request.getSession(true);

        session.setAttribute("role", "ADMIN");
        session.setAttribute("userId", user.getUserId());

        return "redirect:/admin.html";
    }

}

