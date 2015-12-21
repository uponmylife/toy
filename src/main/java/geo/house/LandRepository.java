package geo.house;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LandRepository extends CrudRepository<Land, String> {
    List<Land> findByOrderByNameAscDateDesc();
}
