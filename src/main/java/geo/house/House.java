package geo.house;

import geo.util.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class House implements Comparable<House> {
    @Id
    private String id;
    private String name;
    private String dong;
    private Date date;
    private Integer price;
    private Long size;
    private String floor;

    public House(String name, String str) {
        try {
            String[] a = str.split(" ");
            this.name = name;
            dong = a[1];
            date = new SimpleDateFormat("yyMMdd").parse(StringUtil.onlyDigit(a[2]));
            price = Integer.parseInt(StringUtil.onlyDigit(a[4]));
            size = Math.round(Integer.parseInt(a[7].split("/")[0]) / 3.3);
            floor = a[9];
            id = DigestUtils.md5Hex(new SimpleDateFormat("yyyyMMdd").format(date) + name + dong + floor);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(House p) {
        return (int) (p.getDate().getTime() / 1000 - date.getTime() / 1000);
    }

    @Override
    public String toString() {
        return String.format("%s %1.3g %s %d평 %s",
                new SimpleDateFormat("MM/dd").format(date), price / 10000.0, name, size, floor);
    }
}
