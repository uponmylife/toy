package geo.house;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
public class RoyalDong {
    @Id
    private Integer dong;
    private String plan;
}
