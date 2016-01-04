package geo.house;

import com.google.gson.Gson;
import geo.util.Http;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class LandController {
    @Autowired
    private LandSourceRepository landSourceRepository;
    @Autowired
    private LandRepository landRepository;



    @RequestMapping(value = "/land", produces = MediaType.TEXT_PLAIN_VALUE)
    public String list() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);
        landRepository.findByOrderByNameAscDateDesc().forEach(out::println);
        return stringWriter.toString();
    }

    @RequestMapping(value = "/landStore/{year}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String store(@PathVariable Integer year) {
        landSourceRepository.findAllByUse(true).stream()
                .map(s -> getLands(s.getName(), year, s.getPostData()))
                .forEach(lands -> lands.forEach(land -> {
                    try {
                        landRepository.save(land);
                    } catch (Exception e) {
                        System.err.println("land=" + land);
                        e.printStackTrace();
                    }
                }));
        return "ok";
    }

    private List<Land> getLands(String name, Integer year, String postData) {
        String url = "http://rt.molit.go.kr/srh/getMonthListAjax.do";
        List<Land> lands = new ArrayList<>();
        try {
            Map<String, List<Map>> map = post(url, postData.replace("{YEAR}", year.toString()));
            for (int i=1; i<=12; i++) {
                for (Map m : map.get("month" + i + "List")) {
                    lands.add(new Land(name, year, i, m));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lands;
    }


    private Map<String, List<Map>> post(String url, String postData) throws IOException {
        Http http = new Http();
        http.addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.toString());
        Http.Response response = http.post(url, postData.getBytes());
        return (Map) ((List) new Gson().fromJson(response.getContentString(), Map.class).get("jsonList")).get(0);
    }
}
