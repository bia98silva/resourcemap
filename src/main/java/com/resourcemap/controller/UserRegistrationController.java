package com.resourcemap.controller;

import com.resourcemap.model.User;
import com.resourcemap.model.UserRole;
import com.resourcemap.service.UserService;
import com.resourcemap.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
public class UserRegistrationController {

    private final UserService userService;
    private final ReportService reportService;

    public UserRegistrationController(UserService userService, ReportService reportService) {
        this.userService = userService;
        this.reportService = reportService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model, Locale locale) {
        model.addAttribute("user", new User());
        model.addAttribute("userRoles", UserRole.values());
        model.addAttribute("stats", reportService.getDashboardStatistics());
        model.addAttribute("currentLocale", locale.toString());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

     
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            result.rejectValue("email", "error.user", "Este email já está cadastrado no sistema");
        }

      
        if (result.hasErrors()) {
            model.addAttribute("userRoles", UserRole.values());
            model.addAttribute("stats", reportService.getDashboardStatistics());
            return "auth/register";
        }

        try {
         
            userService.createUser(user);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cadastro realizado com sucesso! Você pode fazer login agora.");
            return "redirect:/login";
        } catch (Exception e) {
            result.rejectValue("email", "error.user", "Erro ao criar usuário: " + e.getMessage());
            model.addAttribute("userRoles", UserRole.values());
            model.addAttribute("stats", reportService.getDashboardStatistics());
            return "auth/register";
        }
    }
}
