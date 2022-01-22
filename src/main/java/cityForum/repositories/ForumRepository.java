package cityForum.repositories;

import cityForum.entity.CityForum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ForumRepository extends JpaRepository<CityForum,Long> {
    CityForum findByCity(String city);
}
