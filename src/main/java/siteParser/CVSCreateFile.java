package siteParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 1/16/17.
 */
public class CVSCreateFile {

    public static void create() throws IOException {
        String csvFile = "src/main/java/reportFolder/report.csv";
        FileWriter writer = new FileWriter(csvFile,true);
        List<String> topics = new ArrayList<String>();
        topics.add("COMPANY NAME");
        topics.add("WEB SITE");
        topics.add("SIZE");
        topics.add("INDUSTRY");
        topics.add("TYPE");
        topics.add("LINK ADDRESS");
        CSVUtils.writeLine(writer,topics);
        writer.flush();
        writer.close();
    }

    public static void delete(){
        File file = new File("src/main/java/reportFolder/report.csv");
        if(file.delete()){
            System.out.println("src/main/java/reportFolder/report.csv файл удален");
        }else System.out.println("Файла src/main/java/reportFolder/report.csv не обнаружено");
    }
}
