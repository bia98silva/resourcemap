package com.resourcemap.controller;

import com.resourcemap.model.Need;
import com.resourcemap.model.Category;
import com.resourcemap.model.Priority;
import com.resourcemap.model.NeedStatus;
import com.resourcemap.service.NeedService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/needs")
public class NeedController {

    private final NeedService needService;

    public NeedController(NeedService needService) {
        this.needService = needService;
    }

    @GetMapping
    public String listNeeds(Model model) {
        model.addAttribute("needs", needService.findActiveNeeds());
        model.addAttribute("categories", Category.values());
        model.addAttribute("priorities", Priority.values());
        return "needs/list";
    }

    @GetMapping("/create")
    public String createNeedForm(Model model) {
        model.addAttribute("need", new Need());
        model.addAttribute("categories", Category.values());
        model.addAttribute("priorities", Priority.values());
        return "needs/create";
    }

    @PostMapping("/create")
    public String createNeed(@ModelAttribute Need need, RedirectAttributes redirectAttributes) {
        try {
            needService.createNeed(need);
            redirectAttributes.addFlashAttribute("successMessage", "Necessidade criada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar necessidade: " + e.getMessage());
        }
        return "redirect:/needs";
    }

    @GetMapping("/{id}")
    public String viewNeed(@PathVariable Long id, Model model) {
        Need need = needService.findById(id)
                .orElseThrow(() -> new RuntimeException("Necessidade não encontrada"));
        model.addAttribute("need", need);
        return "needs/view";
    }

    @GetMapping("/{id}/edit")
    public String editNeedForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Need need = needService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Necessidade não encontrada"));
            model.addAttribute("need", need);
            model.addAttribute("categories", Category.values());
            model.addAttribute("priorities", Priority.values());
            return "needs/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao carregar necessidade: " + e.getMessage());
            return "redirect:/needs";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateNeed(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam(required = false) String description,
                             @RequestParam String category,
                             @RequestParam String priority,
                             @RequestParam String location,
                             @RequestParam Integer quantity,
                             @RequestParam(required = false) String unit,
                             RedirectAttributes redirectAttributes) {
        try {
            Need need = needService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Necessidade não encontrada"));

         
            need.setTitle(title);
            need.setDescription(description);
            need.setCategory(Category.valueOf(category));
            need.setPriority(Priority.valueOf(priority));
            need.setLocation(location);
            need.setQuantity(quantity);
            need.setUnit(unit);

         
            needService.updateNeed(need);

            redirectAttributes.addFlashAttribute("successMessage", "Necessidade atualizada com sucesso!");
            return "redirect:/needs/" + id;

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Valores inválidos fornecidos: " + e.getMessage());
            return "redirect:/needs/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar necessidade: " + e.getMessage());
            return "redirect:/needs/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteNeed(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            needService.deleteNeed(id);
            redirectAttributes.addFlashAttribute("successMessage", "Necessidade excluída com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao excluir necessidade: " + e.getMessage());
        }
        return "redirect:/needs";
    }

    @PostMapping("/{id}/fulfill")
    public String fulfillNeed(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            needService.markAsFulfilled(id);
            redirectAttributes.addFlashAttribute("successMessage", "Necessidade marcada como cumprida!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar necessidade: " + e.getMessage());
        }
        return "redirect:/needs/" + id;
    }
}
