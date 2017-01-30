package siteParser; /**
 * Created by user on 1/3/17.
 */

import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import mailSender.EmailSender;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main extends Thread {

    private static final Logger logger = Logger.getLogger(Main.class);
    static String regexpTerms[] = new String[]{".+[\"][companyName]{11}[\"][:][\"]([a-zA-Z0-9_\\s\\-\\.]+)",
                                               ".+[\"][website]{7}[\"][:][\"]([https]++\\W{3}[a-z]+\\W[a-z0-9]+\\W[a-zA-Z\\/0-9]+)[\"]",
                                               ".+[\"][size]{4}[\"][:][\"]([0123456789\\s[ ][,]]+[-|–][0123456789\\s[ ][,]]+|\\d{2}[,]\\d{3}[+])",
                                               ".+[\"][industry]{8}[\":]{3}([а-яА-Яa-zA-Z\\s]+)[\"]",
                                               ".+[\"][companyType]{11}[\":]{3}([а-яА-Яa-zA-Z\\s]+)[\"]"};

    static String topics[] = new String[]{"name", "webSite", "size", "industry", "type"};
    int numberOfUrl;
    static int currentMainDigit = 10;

    public Main(int numberOfUrl) {
        this.numberOfUrl = numberOfUrl;
    }

    public static void main(String[] args) throws IOException, InterruptedException, WriteException {
        logger.info("start");
        System.out.println("start");
       // Writer.createFile();
        CVSCreateFile.create();
        List<Thread> allThread = new LinkedList<Thread>();
        for (int j = 0; j < 20; j++) {
            ++currentMainDigit;
            for (int i = 1; i <= 1; i++) {
                Thread t = new Main(i * 1000);
                allThread.add(t);
                t.start();
                i=1;
            }
            for (Thread thread : allThread) {
                thread.join();
             }
            allThread.clear();
            logger.info("end");
            System.out.println("end");
            EmailSender emailSender = new EmailSender();
            emailSender.sendMail();
            CVSCreateFile.delete();
        }


      //  logger.info("end");
     //   System.out.println("end");
     //   EmailSender emailSender = new EmailSender();
      //  emailSender.sendMail();
    }

    @Override
    public void run() {

        int tryCount = 0;
        for (int i = numberOfUrl - 999; i <= numberOfUrl; i++) {
            String url = "https://www.linkedin.com/company/000" + currentMainDigit + "00" + i;
            if (i >= 10) {
                url = "https://www.linkedin.com/company/000" + currentMainDigit + "0" + i;
            }
            if (i >= 100) {
                url = "https://www.linkedin.com/company/000" + currentMainDigit + i;
            }
            if (i==1000){
                url = "https://www.linkedin.com/company/000"+ (currentMainDigit+1) +"000";
            }
            logger.info("URL -> " + url);
            System.out.println("URL -> " + url);
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);


            request.addHeader("User-Agent", "Mozilla/5.0");
            request.addHeader("Accept-Encoding", "gzip, deflate");

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

    public static String parseByTopic(int topicIndex, String fullText) throws WriteException, IOException, BiffException {
        String results;
        Pattern patternWebAddress = Pattern.compile(regexpTerms[topicIndex]);
        Matcher matcherWeb = patternWebAddress.matcher(fullText);
        boolean statusWeb = matcherWeb.find();
        if (statusWeb) {
           // Writer.writeToFile(matcherWeb.group(1));
           results = (matcherWeb.group(1));
        } else {
          //  Writer.writeToFile(topics[topicIndex] + " not found");
           results = (topics[topicIndex] + " not found");
        }
        return results;
    }

    public static void patternStringFind(String[] topics, String fullText, String url) throws IOException, WriteException, BiffException {
        synchronized (Object.class) {
           // Writer.writeToFile(url);
            List<String> res = new ArrayList<String>();
            String csvFile = "src/main/java/reportFolder/report.csv";
            FileWriter writer = new FileWriter(csvFile,true);
            for (int i = 0; i < topics.length; i++) {
             res.add(parseByTopic(i,fullText));  //parseByTopic(i,fullText);
            }
            CSVUtils.writeLine(writer,Arrays.asList(res.get(0)+";"+res.get(1)+";"+res.get(2)+";"+res.get(3)+";"+res.get(4)+";"+url));

          //  Writer.columnIndex = 0;
          //  Writer.rowIndex++;
            writer.flush();
            writer.close();
        }
    }
}