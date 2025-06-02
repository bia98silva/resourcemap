package com.resourcemap.controller;
import com.resourcemap.model.Donation;
import com.resourcemap.model.Category;
import com.resourcemap.service.DonationService;
import com.resourcemap.service.MatchingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/donations")
public class DonationController {

    private final DonationService donationService;
    private final MatchingService matchingService;

    public DonationController(DonationService donationService, MatchingService matchingService) {
        this.donationService = donationService;
        this.matchingService = matchingService;
    }

    @GetMapping
    public String listDonations(Model model) {
        model.addAttribute("donations", donationService.findAvailableDonations());
        model.addAttribute("categories", Category.values());
        return "donations/list";
    }

    @GetMapping("/create")
    public String createDonationForm(Model model) {
        model.addAttribute("donation", new Donation());
        model.addAttribute("categories", Category.values());
        return "donations/create";
    }

    @PostMapping("/create")
    public String createDonation(@Valid @ModelAttribute Donation donation, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", Category.values());
            return "donations/create";
        }

        donationService.createDonation(donation);
        return "redirect:/donations";
    }

    @GetMapping("/{id}")
    public String viewDonation(@PathVariable Long id, Model model) {
        Donation donation = donationService.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        model.addAttribute("donation", donation);
        model.addAttribute("matches", matchingService.findMatchesByDonation(id));
        return "donations/view";
    }

    @GetMapping("/{id}/edit")
    public String editDonationForm(@PathVariable Long id, Model model) {
        Donation donation = donationService.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        model.addAttribute("donation", donation);
        model.addAttribute("categories", Category.values());
        return "donations/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateDonation(@PathVariable Long id, @Valid @ModelAttribute Donation donation, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", Category.values());
            return "donations/edit";
        }

        donation.setId(id);
        donationService.updateDonation(donation);
        return "redirect:/donations/" + id;
    }
}