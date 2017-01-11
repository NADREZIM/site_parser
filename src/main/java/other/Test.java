package other;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 1/10/17.
 */
public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://www.linkedin.com/company/0001023");
        request.addHeader("User-Agent", "Mozilla/5.0");
        request.addHeader("Accept-Encoding", "gzip, deflate");
        //  request.setConfig(config);
        int tryCount = 0;
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("Response Code : "
                + statusCode);
        if (statusCode == 999) {
            if (tryCount == 10) {
                tryCount = 0;

            }
            System.out.println("Count of request = " + tryCount);
            ++tryCount;
        } else if (statusCode == 200) {
            tryCount = 0;
            String content = null;
            HttpEntity entity = response.getEntity();
            try {
                content = EntityUtils.toString(entity);

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(content);
        }
    }
}