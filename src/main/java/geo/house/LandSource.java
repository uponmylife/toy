package geo.house;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class LandSource {
    @Id
    private String name;
    @Column(length = 2000)
    private String postData;
    private Boolean use;
}
