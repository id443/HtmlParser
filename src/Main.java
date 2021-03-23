import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Article> articleList = new ArrayList<Article>();
        Document doc = null;
        try {
            doc = Jsoup.connect("https://1prime.ru/state_regulation/").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements h2Ref = doc.getElementsByAttributeValue("class", "rubric-list__article-title");


        for(Element h2: h2Ref) {
            String url = h2.attr("href");
            String title = h2.child(0).text();

            articleList.add(new Article(url, title));
        };
        articleList.forEach(System.out::println);
    }
}

class Article{
    private String url;
    private String name;

    @Override
    public String toString() {
        return "Article " + name + "url "+ url;
    }

    public Article(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }
}