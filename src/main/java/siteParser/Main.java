package siteParser; /**
 * Created by user on 1/3/17.
 */

import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import mailSender.EmailSender;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main extends Thread {

    private static final Logger logger = Logger.getLogger(Main.class);
    static String regexpTerms[] = new String[]{".+[\"][universalName]{13}[\"][:][\"]([a-zA-Z0-9_\\s\\-\\.]+)[\"][,][\"][size]{4}",
                                               ".+[\"][website]{7}[\"][:][\"]([https]++\\W{3}[a-z]+\\W[a-z0-9]+\\W[a-zA-Z\\/0-9]+)[\"]",
                                               ".+[\"][size]{4}[\"][:][\"]([0123456789\\s[ ]]+[-|–][0123456789\\s[ ]]+)",
                                               ".+[\"][industry]{8}[\":]{3}([а-яА-Яa-zA-Z\\s]+)[\"]",
                                               ".+[\"][companyType]{11}[\":]{3}([а-яА-Яa-zA-Z\\s]+)[\"]"};

    static String topics[] = new String[]{"name", "webSite", "size", "industry", "type"};
    int numberOfUrl;

    public Main(int numberOfUrl) {
        this.numberOfUrl = numberOfUrl;
    }

    public static void main(String[] args) throws IOException, InterruptedException, WriteException {
        logger.info("start");
        System.out.println("start");
        Writer.createFile();
        List<Thread> allThread = new LinkedList<Thread>();
        for (int i = 1; i <= 1; i++) {
            Thread t = new Main(i * 10);
            allThread.add(t);
            t.start();
        }
        for (Thread thread : allThread) {
            thread.join();
        }
        logger.info("end");
        System.out.println("end");
        EmailSender emailSender = new EmailSender();
        emailSender.sendMail();
    }

    @Override
    public void run() {
        int tryCount = 0;
        for (int i = numberOfUrl - 9; i <= numberOfUrl; i++) {
            String url = "https://www.linkedin.com/company/000100" + i;
            if (i >= 10) {
                url = "https://www.linkedin.com/company/00010" + i;
            }
            if (i >= 100) {
                url = "https://www.linkedin.com/company/0001" + i;
            }
            logger.info("URL -> " + url);
            System.out.println("URL -> " + url);
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            /*
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                    new AuthScope("193.32.68.156",23456),
                    new UsernamePasswordCredentials("342502", "SDw5qGs4es"));
            CloseableHttpClient client = HttpClients.custom()
                    .setDefaultCredentialsProvider(credsProvider).build();

            HttpHost proxy = new HttpHost("193.32.68.156",23456);

            RequestConfig config = RequestConfig.custom()
                    .setProxy(proxy)
                    .build();

            HttpGet request = new HttpGet(url);
            */

            request.addHeader("User-Agent", "Mozilla/5.0");
            request.addHeader("Accept-Encoding", "gzip, deflate");
            //  request.setConfig(config);
            CloseableHttpResponse response = null;
            try {
                response = client.execute(request);
            } catch (IOException e) {
                logger.error("failed to execute request", e);
            }
            int statusCode = response.getStatusLine().getStatusCode();
            logger.info("Response Code : " + statusCode);
            System.out.println("Response Code : " + statusCode);
            if (statusCode == 999) {
                if (tryCount == 10) {
                    tryCount = 0;
                    continue;
                }
                logger.info("Count of request = " + tryCount);
                System.out.println("Count of request = " + tryCount);
                ++tryCount;
                --i;
            } else if (statusCode == 200) {
                tryCount = 0;
                String content = null;
                HttpEntity entity = response.getEntity();
                try {
                    content = EntityUtils.toString(entity);
                } catch (IOException e) {
                    logger.error("failed to convert response entity to string",e);
                }
                try {
                    patternStringFind(topics, content, url);
                } catch (IOException e) {
                    logger.error("failed to write into file",e);
                } catch (WriteException e) {
                    logger.error("writer configuration exception",e);
                } catch (BiffException e) {
                    logger.error("exception in xls document configuration",e);
                }
            }
            try {
                response.close();
            } catch (IOException e) {
                logger.error("failed to close 'CloseableHttpResponse' ",e);
            }
            try {
                client.close();
            } catch (IOException e) {
                logger.error("failed to close 'CloseableHttpClient' ",e);
            }
        }
    }

    public static void parseByTopic(int topicIndex, String fullText) throws WriteException, IOException, BiffException {

        Pattern patternWebAddress = Pattern.compile(regexpTerms[topicIndex]);
        Matcher matcherWeb = patternWebAddress.matcher(fullText);
        boolean statusWeb = matcherWeb.find();
        if (statusWeb) {
            Writer.writeToFile(matcherWeb.group(1));
        } else {
            Writer.writeToFile(topics[topicIndex] + " not found");
        }
    }

    public static void patternStringFind(String[] topics, String fullText, String url) throws IOException, WriteException, BiffException {
        synchronized (Object.class) {
            Writer.writeToFile(url);
            for (int i = 0; i < topics.length; i++) {
                parseByTopic(i,fullText);
            }
            Writer.columnIndex = 0;
            Writer.rowIndex++;
        }
    }
}