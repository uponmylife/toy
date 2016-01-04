package geo.house;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HouseRepository extends CrudRepository<House, String> {
    List<House> findAllByDong(String dong);
}
