package geo.house;

import geo.util.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
public class Land {
    @Id
    private String id;
    private String name;
    private String date;
    private Integer price;
    private Integer floor;

    public Land(String name, Integer year, Integer month, Map map) {
        this.name = name;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt((String) map.get("DEAL_DD")));
        this.date = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        this.price = Integer.parseInt(StringUtil.onlyDigit((String) map.get("SUM_AMT")));
        this.floor = Integer.parseInt((String) map.get("APTFNO"));
        this.id =  DigestUtils.md5Hex(name + date + floor);
    }

    @Override
    public String toString() {
        return String.format("%s %s %1.3g %2d", name, date, price/10000.0, floor);
    }
}
