package com.resourcemap.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "match_seq")
    @SequenceGenerator(name = "match_seq", sequenceName = "match_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "need_id")
    private Need need;

    @ManyToOne
    @JoinColumn(name = "donation_id")
    private Donation donation;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    private Integer matchedQuantity;

    private Double compatibilityScore;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(length = 1000)
    private String notes;

    public Match() {
        this.createdAt = LocalDateTime.now();
        this.status = MatchStatus.PENDING;
    }

    public Match(Need need, Donation donation, Integer quantity, Double score) {
        this();
        this.need = need;
        this.donation = donation;
        this.matchedQuantity = quantity;
        this.compatibilityScore = score;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Need getNeed() {
        return need;
    }

    public Donation getDonation() {
        return donation;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public Integer getMatchedQuantity() {
        return matchedQuantity;
    }

    public Double getCompatibilityScore() {
        return compatibilityScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public String getNotes() {
        return notes;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setNeed(Need need) {
        this.need = need;
    }

    public void setDonation(Donation donation) {
        this.donation = donation;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public void setMatchedQuantity(Integer matchedQuantity) {
        this.matchedQuantity = matchedQuantity;
    }

    public void setCompatibilityScore(Double compatibilityScore) {
        this.compatibilityScore = compatibilityScore;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}