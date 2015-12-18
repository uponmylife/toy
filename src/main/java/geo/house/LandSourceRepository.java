package geo.house;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LandSourceRepository extends CrudRepository<LandSource, String> {
    List<LandSource> findAllByUse(Boolean use);
}
