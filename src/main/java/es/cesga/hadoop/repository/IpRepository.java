package es.cesga.hadoop.repository;

import es.cesga.hadoop.domain.Ip;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Ip entity.
 */
public interface IpRepository extends JpaRepository<Ip,Long> {

    @Query("select ip from Ip ip where ip.user.login = ?#{principal.username}")
    List<Ip> findAllForCurrentUser();

}
