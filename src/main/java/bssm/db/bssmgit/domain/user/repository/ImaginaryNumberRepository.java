package bssm.db.bssmgit.domain.user.repository;

import bssm.db.bssmgit.domain.user.domain.ImaginaryNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImaginaryNumberRepository extends JpaRepository<ImaginaryNumber, Long> {

    @Query("select i from ImaginaryNumber i where i.reportedUserId = :userId")
    List<ImaginaryNumber> findByReportedUserId(@Param("userId") Long userId);

}
