import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
	private static final String adr = "jdbc:postgresql://localhost:5432/articles";
	private static final String user = "postgres";
	static final String pas = "123";
	private static Connection connection;
	private static Statement st;
	private static ResultSet res;
	
		
    public static void main(String[] args) throws IOException {
    
    	//String urlNewsSite1 = "https://1prime.ru/state_regulation/";
        String urlNewsSite2 = "https://lenta.ru/rubrics/economics/";
        String urlNewsSite3 = "https://www.gazeta.ru/business/news/";

        Document doc = null;
        Document doc2 = null;
        Document doc3 = null;
        
        try {
//            doc = Jsoup.connect(urlNewsSite1)
//					.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
//					.header("Accept-Encoding", "gzip, deflate, sdch")
//					.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (HTML, like Gecko)")
//					.get();
            doc2 = Jsoup.connect(urlNewsSite2).get();
            doc3 = Jsoup.connect(urlNewsSite3).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
		assert doc != null;
//		Elements h2Ref = doc.getElementsByAttributeValue("class", "rubric-list__article-title");
		assert doc2 != null;
		Elements h2Ref2 = doc2.getElementsByAttributeValue("class", "card-mini _longgrid");
		assert doc3 != null;
		Elements h2Ref3 = doc3.getElementsByAttributeValue("class", "b_ear-title");

        List<String> articleList = new ArrayList();
        
//        add(articleList, h2Ref, "https://1prime.ru");
        add(articleList, h2Ref2, "https://lenta.ru");
        add(articleList, h2Ref3, "https://www.gazeta.ru");
        
        String filename = "art.doc";
        write(articleList, filename);
        //writeDb(articleList);
       
        articleList.forEach(System.out::println);
        
    }
    static public void add(List articleList, Elements h2Ref, String mainUrl) {
    	articleList.add(mainUrl);
    	for(Element h2 : h2Ref) {
            String url = mainUrl + h2.attr("href");
            String title = h2.text();

            articleList.add(title + "  " + url);
        };
    }
    static void write(List<String> articleList, String filename) throws IOException {
    	BufferedWriter bufferedWriter = null;
    	bufferedWriter = new BufferedWriter(new FileWriter(filename));
    	
    	bufferedWriter.write(String.valueOf(LocalDate.now()));
    	bufferedWriter.newLine();
    	for (String s : articleList) {
    		bufferedWriter.write(s);
    		bufferedWriter.newLine();
    	}
    	bufferedWriter.flush();
    	bufferedWriter.close();
    }
    static void writeDb(List<String> articleList) {
    	try {
    		connection = DriverManager.getConnection(adr, user, pas);
    		st = connection.createStatement();
    		String sql = "INSERT INTO news (title) VALUES (?)";
    		PreparedStatement ps = connection.prepareStatement(sql);
			for (String s : articleList) {
				ps.setString(1, s);
				ps.executeUpdate();
			}
    	} catch (SQLException sqlEx) {
    		sqlEx.printStackTrace();
    	} finally {
			try {
				connection.close();
			} catch (NullPointerException | SQLException ex) {
				System.out.println("Can't connect to db!");
			}
			try {
				st.close();
			} catch (NullPointerException | SQLException ignored) {
			}
		}
		}
}