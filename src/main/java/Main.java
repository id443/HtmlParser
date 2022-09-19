import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {
	private static Connection connection;
	private static Statement st;

    public static void main(String[] args) throws IOException {

        String urlMainCIAN = "https://spb.cian.ru/kupit-kvartiru-1-komn-ili-2-komn/";

		List<String> urls = new ArrayList<>();
		urls.add(urlMainCIAN);

		for (int i = 2; i < 10; i++)
			urls.add("https://spb.cian.ru/cat.php?deal_type=sale&engine_version=2&offer_type=flat&p="
					+i+"&region=2&room1=1&room2=1");

		List<Document> docs = new ArrayList<>();

        try {
			for(String url: urls) {
				Document doc = Jsoup.connect(url).get();
				docs.add(doc);
			}
        } catch (IOException e) {
            e.printStackTrace();
        }

		List<Elements> elements = new ArrayList<>();
		List<String> articleList = new ArrayList();
		Map<Integer, String> apartments = new TreeMap<>();

		for(Document document : docs) {
			Elements title = document.getElementsByAttributeValue("data-mark", "OfferTitle");
			Elements price = document.getElementsByAttributeValue("data-mark", "MainPrice");
			List<String> links = document.getElementsByAttribute("href")
					.eachAttr("href")
					.stream()
					.filter(e -> e.contains("flat"))
					.filter(e -> e.contains("spb"))
					.filter(e -> !e.contains("pdf"))
					.filter(e -> !e.contains("cat"))
					.filter(e -> e.contains("sale"))
					.collect(Collectors.toList());
			String[] titles = title.text().split("\\u00B2,");
			String[] prices = price.text().split("\\u20bd ");
			for (int i = 0; i < titles.length && i < prices.length; i++) {
				articleList.add(titles[i] + " " + links.get(i) + " "+ prices[i]);
				apartments.put(Integer.parseInt(prices[i].replace(" ","")
						.replace("\u20bd","")),titles[i] + " " + links.get(i));
			}
		}


//        add(articleList, h2Ref, "https://1prime.ru");
		apartments.forEach((k,v)-> System.out.println(String.format(String.valueOf(k), "") + v));
        
        String filename = "art.doc";
//        write(articleList, filename);
        //writeDb(articleList);
       
        articleList.forEach(System.out::println);
        
    }
//    static public void add(List<String> articleList, Elements h2Ref, String mainUrl) {
//    	articleList.add(mainUrl);
//    	for(Element h2 : h2Ref) {
//            String url = mainUrl + h2.attr("href");
//            String title = h2.text();
//            articleList.add(title + "  " + url);
//        };
//    }
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
//    static void writeDb(List<String> articleList) {
//    	try {
//    		connection = DriverManager.getConnection(adr, user, pas);
//    		st = connection.createStatement();
//    		String sql = "INSERT INTO news (title) VALUES (?)";
//    		PreparedStatement ps = connection.prepareStatement(sql);
//			for (String s : articleList) {
//				ps.setString(1, s);
//				ps.executeUpdate();
//			}
//    	} catch (SQLException sqlEx) {
//    		sqlEx.printStackTrace();
//    	} finally {
//			try {
//				connection.close();
//			} catch (NullPointerException | SQLException ex) {
//				System.out.println("Can't connect to db!");
//			}
//			try {
//				st.close();
//			} catch (NullPointerException | SQLException ignored) {
//			}
//		}
//		}
}