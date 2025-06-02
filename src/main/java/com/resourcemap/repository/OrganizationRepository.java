package com.resourcemap.repository;

import com.resourcemap.model.Organization;
import com.resourcemap.model.OrganizationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    List<Organization> findByType(OrganizationType type);
    List<Organization> findByLocationContainingIgnoreCase(String location);
    List<Organization> findByNameContainingIgnoreCase(String name);
}