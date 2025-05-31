package com.resourcemap.repository;

import com.resourcemap.model.Match;
import com.resourcemap.model.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByStatus(MatchStatus status);

    @Query("SELECT m FROM Match m WHERE m.need.id = :needId")
    List<Match> findByNeedId(@Param("needId") Long needId);

    @Query("SELECT m FROM Match m WHERE m.donation.id = :donationId")
    List<Match> findByDonationId(@Param("donationId") Long donationId);

    @Query("SELECT m FROM Match m ORDER BY m.compatibilityScore DESC")
    List<Match> findAllOrderByCompatibilityScore();
}
