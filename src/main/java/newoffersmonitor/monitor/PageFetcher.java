package newoffersmonitor.monitor;

import newoffersmonitor.configurations.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class PageFetcher {

    Document fetchPage(Page page) throws IOException {
        return Jsoup
                .connect(page.getUrl())
                .get();
    }

}
