package geo.house;

import geo.util.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Data
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
    @Transient
    private String plan = "";

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
        return p.getName().compareTo(name) * 10000 +
                (int) (p.getDate().getTime() / (1000 * 60 * 60) - date.getTime() / (1000 * 60 * 60));
    }

    @Override
    public String toString() {
        String plus = isSameDay(date, new Date()) ? "+" : "";
        return plus + String.format("%s %1.3g %s %d평 %s",
                new SimpleDateFormat("MM/dd").format(date), price / 10000.0, name, size, floor);
    }

    public String toString2() {
        String plus = isSameDay(date, new Date()) ? "+" : "";
        return plus + String.format("%s %1.3g %s %s%s %s",
                new SimpleDateFormat("MM/dd").format(date), price / 10000.0, name, dong, plan, floor);
    }


    private boolean isSameDay(Date d1, Date d2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(d1).equals(df.format(d2));
    }
}
