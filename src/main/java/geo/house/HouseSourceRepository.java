package geo.house;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HouseSourceRepository extends CrudRepository<HouseSource, String> {
    List<HouseSource> findAllByUse(Boolean use);
}
