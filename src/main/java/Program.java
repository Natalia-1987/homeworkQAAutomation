import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) throws IOException {

        Program program = new Program();
//        String consoleRequest = program.readRequest();
        String consoleRequest = args[0];
        String response = program.sendRequest(consoleRequest);
        List<String> filteredResult = program.filterResponse(consoleRequest, response);
        program.writeToFile(filteredResult);
        System.out.println(response);


    }

    public List<String> filterResponse(String consoleRequest, String response) {
        List<String> result = new ArrayList<String>();
        if (response != null) {
            List<String> strings = splitHtml(response);
            for (String line : strings) {
                if (line.toLowerCase().contains(consoleRequest.toLowerCase())) {
                    result.add(line);
                }
            }
        }
        return result;
    }

    private List<String> splitHtml(String response) {
        List<String> lines = new ArrayList<String>();
        String body = response.split("<body")[1].split("</body>")[0];
        String[] rows = body.split("<br/>");
        for (String row : rows) {
            lines.add(row);
        }
        return lines;
    }

    public void writeToFile(List<String> filteredResult) throws IOException {
        File file = new File("result.txt");

        FileWriter fileWriter = new FileWriter(file);
        for (String s : filteredResult) {
            fileWriter.write(s);
            fileWriter.write(System.getProperty("line.separator"));
        }

        //fileWriter.flush();
        fileWriter.close();
    }

    public String readRequest() {
        Scanner scan = new Scanner(System.in);
        return scan.next();
    }

    public String sendRequest(String st) {
        HttpURLConnection connection = null;
        try {
            connection = establishConnection(st);
            InputStream is = connection.getInputStream();
            Scanner scan = new Scanner(is);
            StringBuilder sb = new StringBuilder();
            while (scan.hasNext()) {
                sb.append(scan.next());
            }
            return sb.toString();

        } catch (Exception e) {
            System.out.println("Such request not found on wiki");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return "";
    }

    private HttpURLConnection establishConnection(String requests) {
        HttpURLConnection connection = null;
        try {
            String uri = "https://wikipedia.org/wiki/" + requests;
            System.out.println(uri);
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setRequestProperty("content-encoding", "gzip");
            connection.setRequestProperty("accept-ranges", "bytes");
            connection.setRequestProperty("Content-type", "text/html; charset=UTF-8");
            connection.setUseCaches(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }


}
