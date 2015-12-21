package geo.util;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class RestTemplateFactory {
    private static RestTemplate restTemplate;
    static {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnPerRoute(25)
                .setMaxConnTotal(1000)
                .setConnectionTimeToLive(5, TimeUnit.MINUTES)
                .build();
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("utf-8")));
    }

    public static RestTemplate getInstance() {
        return restTemplate;
    }
}
