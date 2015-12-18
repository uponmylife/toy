package geo.house;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.sort;

@RestController
public class Controller {
    @Autowired
    private HouseRepository houseRepository;
    @Autowired
    private HouseSourceRepository houseSourceRepository;

    private Map<String, String> interests = new HashMap<>();

    @PostConstruct
    public void init() {
        houseSourceRepository.findAllByUse(true).forEach(s -> interests.put(s.getName(), s.getUrl()));
    }

    @RequestMapping(value = "/house", produces = MediaType.TEXT_PLAIN_VALUE)
    public String todayProducts() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);
        List<House> houses = getHouses();
        houses.forEach(out::println);
        try { houses.forEach(houseRepository::save); } catch (Exception e) { e.printStackTrace(); }
        return stringWriter.toString();
    }

    private List<House> getHouses() {
        List<House> list = interests.keySet().stream()
                .map(k -> createProduct(k, interests.get(k))).flatMap(List::stream).collect(Collectors.toList());
        sort(list);
        return list;
    }

    private List<House> createProduct(String productName, String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select(".product_info");
            return elements.stream()
                    .map(Element::text)
                    .map(text -> text.replace("회원인증 ", ""))
                    .map(text -> new House(productName, text))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }
}
