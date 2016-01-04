package geo.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Http {
    public static final int DEFAULT_CONNECTION_TIMEOUT = 3;
    public static final int DEFAULT_SO_TIMEOUT = 5;
    private HttpParams httpParams;
    private List<Header> headerList = new ArrayList<Header>();

    private Logger logger = Logger.getLogger(this.getClass());

    public static class Response {
        private HttpResponse httpResponse;
        private byte[] content;

        public Response(HttpResponse httpResponse) throws IOException {
            this.httpResponse = httpResponse;
            content =  IOUtils.toByteArray(httpResponse.getEntity().getContent());
        }

        public int getStatusCode() {
            return httpResponse.getStatusLine().getStatusCode();
        }

        public String getReason() {
            return httpResponse.getStatusLine().getReasonPhrase();
        }

        public int getContentLength() {
            if (content==null) return 0;
            return content.length;
        }

        public byte[] getContent() {
            return content;
        }

        public String getContentString() {
            if (content==null) return "";
            return new String(content);
        }

        public String getContentString(String charset) {
            try { return new String(content, charset); } catch (Exception e) {}
            return "";
        }

        public boolean isOk() {
            return httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        }

        public String getStatusLine() {
            return httpResponse.getStatusLine().toString();
        }

        public Map<String, String> getAllHeaders() {
            Map<String, String> map = new HashMap<String, String>();
            for (Header header : httpResponse.getAllHeaders()) map.put(header.getName(), header.getValue());
            return map;
        }
    }

    public Http() {
        this(DEFAULT_CONNECTION_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    public Http(int connectTimeout, int soTimeout) {
        httpParams = new BasicHttpParams();
        httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout*1000);
        httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeout * 1000);;
    }

    public static Response executeGet(String uri) throws IOException {
        return new Http().get(uri);
    }

    public static Response executeGet(String uri, Map<String,String> params) throws IOException {
        return new Http().get(uri, params);
    }

    public static Response executeGet(String uri, Map<String,String> header, Map<String,String> params) throws IOException {
        Http http = new Http();
        for (String key : header.keySet())
            http.addHeader(key, header.get(key));
        return http.get(uri, params);
    }

    public static Response executePost(String url, Map<String,String> params) throws IOException {
        return new Http().post(url, params, null);
    }
    
    public static Response executePost(String url, Map<String,String> params, String charset) throws IOException {
        return new Http().post(url, params, charset);
    }

    public static Response executePost(String url, byte[] requestBody) throws IOException {
        return new Http().post(url, requestBody);
    }

    public static Response executePostJson(String url, String json) throws IOException {
        return new Http().postJson(url, json);
    }

    public void addHeader(String name, String value) {
        headerList.add(new BasicHeader(name, value));
    }

    public Response get(String uri) throws IOException {
        try {
            return get(new URI(uri));
        } catch (URISyntaxException e) {
            logger.error(e.toString(), e);
            throw new RuntimeException(e.toString());
        }
    }

    public Response get(String uri, Map<String,String> params) throws IOException {
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);
            for (String name : params.keySet()) uriBuilder = uriBuilder.addParameter(name, params.get(name));
            return get(uriBuilder.build());
        } catch (URISyntaxException e) {
            logger.error(e.toString(), e);
            throw new RuntimeException(e.toString());
        }
    }

    private Response get(URI uri) throws IOException {
        return execute(new HttpGet((uri)));
    }

    public Response post(String url, Map<String,String> params, String charset) throws IOException {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        
        if ( params != null ) for (String name : params.keySet()) nvps.add(new BasicNameValuePair(name, params.get(name)));
        
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        if ( charset == null ) httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        else httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));
        return execute(httpPost);
    }

    public Response post(String url, byte[] requestBody) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new ByteArrayEntity(requestBody));

        return execute(httpPost);
    }
    
    public Response post(String url, String requestBody) throws IOException {
        logger.info("postData=[\n" + requestBody + "\n]");
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(requestBody, "utf-8"));
        
        return execute(httpPost);
    }

    public Response postJson(String url, String json) throws IOException {
        addHeader("Content-Type", "application/json; charset=UTF-8");
        return post(url, json);
    }

    private Response execute(HttpRequestBase httpReq) throws IOException {
        addHeader("Accept", "*/*");
        for (Header header : headerList) httpReq.addHeader(header);

        return doExecute(httpReq);
    }

    protected Response doExecute(HttpRequestBase httpReq) throws IOException {
        long time = System.currentTimeMillis();
        try {
            logger.info("REQ: " + httpReq.getMethod() + " " + httpReq.getURI());
            DefaultHttpClient client = new DefaultHttpClient(httpParams);
            HttpResponse httpRes = client.execute(httpReq);
            Response res = new Response(httpRes);
            logger.info("RES: " + res.getStatusCode() + " " + res.getReason() + " contentLen=" + res.getContentLength()
                    + " " + (System.currentTimeMillis()-time) + "ms");
            logger.info("content(max:1000)=\n" + StringUtils.left(res.getContentString(), 10000));
            return res;
        } finally {
            httpReq.releaseConnection();
        }
    }

    public Response put(String url) throws IOException {
        HttpPut put = new HttpPut(url);
        return execute(put);
    }
}
