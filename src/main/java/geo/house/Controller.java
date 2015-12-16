package geo.house;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.MediaType;
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
    private Map<String, String> interests = new HashMap<>();

    @PostConstruct
    public void init() {
//        interests.put("상현자이2003", "http://m2.land.naver.com/article/complexDetail.nhn?tab=article&hscpNo=9125&rletTypeCd=A01&dongCnt=12&cortarNo=4146510700&tradTpCd=A1&detailFlag=table&spc=114&sort=date&order=desc");
//        interests.put("죽전힐스테이2004", "http://m2.land.naver.com/article/complexDetail.nhn?tab=article&hscpNo=8627&rletTypeCd=A01&dongCnt=23&cortarNo=4146510200&tradTpCd=A1&detailFlag=table&spc=111&sort=date&order=desc");
        interests.put("죽전아이파크1998", "http://m2.land.naver.com/article/complexDetail.nhn?tab=article&hscpNo=2534&rletTypeCd=A01&dongCnt=0&cortarNo=4146510200&tradTpCd=A1&detailFlag=table&spc=108&sort=date&order=desc");
        interests.put("신봉자이1차2003", "http://m2.land.naver.com/article/complexDetail.nhn?tab=article&hscpNo=8246&rletTypeCd=A01&dongCnt=24&cortarNo=4146510500&tradTpCd=A1&detailFlag=table&spc=110&sort=date&order=desc");
        interests.put("신봉자이2차2005", "http://m2.land.naver.com/article/complexDetail.nhn?tab=article&hscpNo=10065&rletTypeCd=A01&dongCnt=22&cortarNo=4146510500&tradTpCd=A1&detailFlag=table&spc=109&sort=date&order=desc");
        interests.put("동백어울림2006", "http://m2.land.naver.com/article/complexDetail.nhn?tab=article&hscpNo=17644&rletTypeCd=A01&dongCnt=6&cortarNo=4146311500&tradTpCd=A1&detailFlag=table&spc=109&sort=date&order=desc");
        interests.put("흥덕스위첸2009", "http://m2.land.naver.com/article/complexDetail.nhn?tab=article&hscpNo=27656&rletTypeCd=A01&dongCnt=0&cortarNo=4146311100&tradTpCd=A1&detailFlag=table&spc=116&sort=date&order=desc");
        interests.put("죽전대림2003", "http://m2.land.naver.com/article/complexDetail.nhn?tab=article&hscpNo=8773&rletTypeCd=A01&dongCnt=2&cortarNo=4146510200&tradTpCd=A1&detailFlag=table&spc=106&sort=date&order=desc");
    }

    @RequestMapping(value = "/house", produces = MediaType.TEXT_PLAIN_VALUE)
    public String todayProducts() {
        List<Product> list = interests.keySet().stream()
                .map(k -> createProduct(k, interests.get(k))).flatMap(List::stream).collect(Collectors.toList());
        sort(list);
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);
        list.forEach(out::println);
        return stringWriter.toString();
    }

    private List<Product> createProduct(String productName, String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select(".product_info");
            return elements.stream().map(Element::text).map(text -> new Product(productName, text)).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }
}
