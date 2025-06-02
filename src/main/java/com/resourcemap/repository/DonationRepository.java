package com.resourcemap.repository;

import com.resourcemap.model.Donation;
import com.resourcemap.model.Category;
import com.resourcemap.model.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByStatus(DonationStatus status);
    List<Donation> findByCategory(Category category);
    List<Donation> findByLocationContainingIgnoreCase(String location);

    @Query("SELECT d FROM Donation d WHERE d.status = 'AVAILABLE' ORDER BY d.createdAt DESC")
    List<Donation> findAvailableDonations();

    @Query("SELECT d FROM Donation d WHERE d.category = :category AND d.status = 'AVAILABLE'")
    List<Donation> findAvailableDonationsByCategory(@Param("category") Category category);
}