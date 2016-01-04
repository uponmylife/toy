package geo.house;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class RoyalDongController {
    @Autowired
    private RoyalDongRepository royalDongRepository;
    @Autowired
    private HouseRepository houseRepository;

    @RequestMapping(value = "/house/dong", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getRoyalDongHouses() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);

        List<House> houses = new ArrayList<>();
        royalDongRepository.findAll()
                .forEach(r -> houseRepository.findAllByDong(r.getDong() + "동").forEach(h -> {
                    h.setPlan(r.getPlan());
                    houses.add(h);
                }));
        Collections.sort(houses);
        houses.forEach(h -> out.println(h.toString2()));

        return stringWriter.toString();
    }

}
