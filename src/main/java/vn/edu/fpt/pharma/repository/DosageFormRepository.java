package vn.edu.fpt.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.pharma.entity.DosageForm;
import vn.edu.fpt.pharma.entity.Unit;


import java.util.List;
import java.util.Optional;

@Repository
public interface DosageFormRepository extends JpaRepository<DosageForm, Long> {

    Optional<DosageForm> findByDisplayName(String displayName);

    List<DosageForm> findByActiveOrderByDisplayOrder(Boolean active);

    List<DosageForm> findAllByOrderByDisplayOrder();

    @Deprecated
    boolean existsByDisplayName(String displayName);

    // Check if combination of displayName and baseUnit exists
    boolean existsByDisplayNameAndBaseUnit(String displayName, Unit baseUnit);

    // Check if combination exists excluding a specific id (for updates)
    @Query("SELECT CASE WHEN COUNT(df) > 0 THEN true ELSE false END FROM DosageForm df " +
           "WHERE df.displayName = :displayName AND df.baseUnit = :baseUnit AND df.id <> :id")
    boolean existsByDisplayNameAndBaseUnitExcludingId(@Param("displayName") String displayName,
                                                       @Param("baseUnit") Unit baseUnit,
                                                       @Param("id") Long id);

    @Query("SELECT df FROM DosageForm df WHERE df.active = true ORDER BY df.displayOrder")
    List<DosageForm> findAllActiveOrderByDisplayOrder();
}

