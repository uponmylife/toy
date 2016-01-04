package geo.house;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.replaceAll;
import static java.util.Collections.sort;

@RestController
public class HouseController {
    @Autowired
    private HouseRepository houseRepository;
    @Autowired
    private HouseSourceRepository houseSourceRepository;

    @RequestMapping(value = "/house", produces = MediaType.TEXT_PLAIN_VALUE)
    public String list() {
        return list(true);
    }

    @RequestMapping(value = "/house/{use}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String list(@PathVariable Boolean use) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);
        List<House> houses = houseSourceRepository.findAllByUse(use).stream()
                .map(s -> createProduct(s.getName(), s.getUrl()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        sort(houses);
        houses.forEach(out::println);
        try {
            houses.forEach(houseRepository::save);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    private List<House> createProduct(String productName, String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select(".product_info");
            return elements.stream()
                    .map(Element::text)
                    .map(text -> text.replace("회원인증 ", "").replace("현장 ", ""))
                    .map(text -> new House(productName, text))
                    .filter(h -> h.getSize() > 30)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }
}
