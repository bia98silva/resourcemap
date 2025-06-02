package com.resourcemap.service;

import com.resourcemap.model.*;
import com.resourcemap.repository.NeedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NeedService {

    private final NeedRepository needRepository;

    public NeedService(NeedRepository needRepository) {
        this.needRepository = needRepository;
    }

    public Need createNeed(Need need) {
        if (need.getCreatedAt() == null) {
            need.setCreatedAt(LocalDateTime.now());
        }
        if (need.getStatus() == null) {
            need.setStatus(NeedStatus.ACTIVE);
        }
        return needRepository.save(need);
    }

    public List<Need> findAll() {
        return needRepository.findAll();
    }

    public List<Need> findActiveNeeds() {
        return needRepository.findByStatus(NeedStatus.ACTIVE);
    }

    public List<Need> findByCategory(Category category) {
        return needRepository.findByCategory(category);
    }

    public List<Need> findByPriority(Priority priority) {
        return needRepository.findByPriority(priority);
    }

    public List<Need> findByLocation(String location) {
        return needRepository.findByLocationContainingIgnoreCase(location);
    }

    public Optional<Need> findById(Long id) {
        return needRepository.findById(id);
    }

    public List<Need> findUrgentNeeds() {
        return needRepository.findActiveNeedsByPriority();
    }

 
    public Need updateNeed(Need need) {
        System.out.println("=== SERVICE UPDATE ===");
        System.out.println("ID: " + need.getId());
        System.out.println("Título: " + need.getTitle());
        System.out.println("Categoria: " + need.getCategory());
        System.out.println("Prioridade: " + need.getPriority());

        if (need.getId() == null) {
            throw new RuntimeException("ID da necessidade não pode ser nulo para atualização");
        }

      
        if (!needRepository.existsById(need.getId())) {
            throw new RuntimeException("Necessidade com ID " + need.getId() + " não encontrada");
        }

      
        Need savedNeed = needRepository.save(need);

        System.out.println("Necessidade salva com sucesso: " + savedNeed.getId());
        return savedNeed;
    }

    public void deleteNeed(Long id) {
        if (needRepository.existsById(id)) {
            needRepository.deleteById(id);
        } else {
            throw new RuntimeException("Necessidade não encontrada para exclusão");
        }
    }

    public Need markAsFulfilled(Long id) {
        Need need = needRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Necessidade não encontrada"));
        need.setStatus(NeedStatus.FULFILLED);
        return needRepository.save(need);
    }

    public Need markAsPartiallyFulfilled(Long id) {
        Need need = needRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Necessidade não encontrada"));
        need.setStatus(NeedStatus.PARTIALLY_FULFILLED);
        return needRepository.save(need);
    }

    public Need cancelNeed(Long id) {
        Need need = needRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Necessidade não encontrada"));
        need.setStatus(NeedStatus.CANCELLED);
        return needRepository.save(need);
    }

    public void markAsExpired() {
        List<Need> activeNeeds = needRepository.findByStatus(NeedStatus.ACTIVE);
        LocalDateTime now = LocalDateTime.now();

        for (Need need : activeNeeds) {
            if (need.getDeadlineDate() != null && need.getDeadlineDate().isBefore(now)) {
                need.setStatus(NeedStatus.EXPIRED);
                needRepository.save(need);
            }
        }
    }

    public long countActiveNeeds() {
        return needRepository.findByStatus(NeedStatus.ACTIVE).size();
    }

    public long countFulfilledNeeds() {
        return needRepository.findByStatus(NeedStatus.FULFILLED).size();
    }
}
