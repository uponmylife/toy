package geo.house;

import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
public class Product implements Comparable<Product> {
    private String name;
    private String dong;
    private Date date;
    private Integer price;
    private Long size;
    private String floor;

    public Product(String name, String str) {
        try {
            String[] a = str.split(" ");
            this.name = name;
            dong = a[1];
            date = new SimpleDateFormat("yyMMdd").parse(onlyDigit(a[2]));
            price = Integer.parseInt(onlyDigit(a[4]));
            size = Math.round(Integer.parseInt(a[7].split("/")[0])/3.3);
            floor = a[9];
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String onlyDigit(String str) {
        return str.replaceAll("[^0-9]", "");
    }

    @Override
    public int compareTo(Product p) {
        return (int) (p.getDate().getTime() / 1000 - date.getTime() / 1000);
    }

    @Override
    public String toString() {
        return String.format("%s %1.3g %s %d평 %s",
                new SimpleDateFormat("MM/dd").format(date), price / 10000.0, name, size, floor);
    }
}
