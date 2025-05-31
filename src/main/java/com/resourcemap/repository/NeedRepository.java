package com.resourcemap.repository;

import com.resourcemap.model.Need;
import com.resourcemap.model.Category;
import com.resourcemap.model.Priority;
import com.resourcemap.model.NeedStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NeedRepository extends JpaRepository<Need, Long> {
    List<Need> findByStatus(NeedStatus status);
    List<Need> findByCategory(Category category);
    List<Need> findByPriority(Priority priority);
    List<Need> findByLocationContainingIgnoreCase(String location);

    @Query("SELECT n FROM Need n WHERE n.status = 'ACTIVE' ORDER BY n.priority DESC, n.createdAt ASC")
    List<Need> findActiveNeedsByPriority();

    @Query("SELECT n FROM Need n WHERE n.category = :category AND n.status = 'ACTIVE'")
    List<Need> findActiveNeedsByCategory(@Param("category") Category category);
}