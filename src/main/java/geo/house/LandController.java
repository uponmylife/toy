package geo.house;

import com.google.gson.Gson;
import geo.util.Http;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@RestController
public class LandController {
    @Autowired
    private LandSourceRepository landSourceRepository;

    @RequestMapping(value = "/land", produces = MediaType.TEXT_PLAIN_VALUE)
    public String list() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);
//        landSourceRepository.findAllByUse(true).stream()
//                .map(s -> post(s.getUrl(), s.ge))

        return stringWriter.toString();
    }

//    private List<Land> getLands(String url, String data) throws IOException {
//        Map<String, String> map = post(url, data);
//    }

    private Map<String, String> post(String url, String data) throws IOException {
        Http http = new Http();
        http.addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.toString());
        Http.Response response = http.post(url, data.getBytes());
        return new Gson().fromJson(response.getContentString(), Map.class);
    }

//    public static void main(String[] args) throws IOException {
//        String url = "http://rt.molit.go.kr/rtSearch.do?cmd=getAptTradeMonthListAjax";
//        String data = "menuGubun=A&srhType=LOC&houseType=1&gugunCode=41465&selDanji=51197&srhYear=2015&srhArea=83.28";
//        Http http = new Http();
//        http.addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.toString());
//
//        Http.Response response = http.post(url, data.getBytes());
//        Map map = (Map) ((List) new Gson().fromJson(response.getContentString(), Map.class).get("jsonList")).get(0);
//        for (int i=1; i<=12; i++) {
//            List list = (List) map.get("month" + i + "List");
//
//            System.out.println(list);
//        }
//    }
}
