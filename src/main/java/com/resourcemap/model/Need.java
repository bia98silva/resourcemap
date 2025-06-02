package com.resourcemap.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "needs")
public class Need {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "need_seq")
    @SequenceGenerator(name = "need_seq", sequenceName = "need_sequence", allocationSize = 1)
    private Long id;

    @NotBlank
    private String title;

    @Column(length = 2000)
    private String description;

    @NotBlank
    private String location;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private NeedStatus status;

    @Min(1)
    private Integer quantity;

    private String unit;

    @Column(name = "deadline_date")
    private LocalDateTime deadlineDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToMany(mappedBy = "need")
    private List<Match> matches;

    public Need() {
        this.createdAt = LocalDateTime.now();
        this.status = NeedStatus.ACTIVE;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Category getCategory() {
        return category;
    }

    public Priority getPriority() {
        return priority;
    }

    public NeedStatus getStatus() {
        return status;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getCreator() {
        return creator;
    }

    public Organization getOrganization() {
        return organization;
    }

    public List<Match> getMatches() {
        return matches;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setStatus(NeedStatus status) {
        this.status = status;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
}