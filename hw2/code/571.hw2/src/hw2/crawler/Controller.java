package hw2.crawler;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;


public class Controller {

	public static void main(String[] args) throws Exception {
        
		String storageFolder = "//Users//haopengsong//crawldata//store";
        String rootFolder = "//Users//haopengsong//crawldata//root";
        String[] crawlDomain = {"https://www.reuters.com/", "http://www.reuters.com", 
        						"https://reuters.com", "http://reuters.com"};
        
        int numberOfCrawlers = 9;
        int maxDepthCrawling = 16;
        int politnessDelay = 150;
        int maxPagestoFetch = 20000;
        
        CrawlConfig config = new CrawlConfig();
       // CrawlConfig config2 = new CrawlConfig();
        
        
        
        config.setCrawlStorageFolder(rootFolder + "//c1");
       // config2.setCrawlStorageFolder(rootFolder + "//c2");
        
        config.setMaxDepthOfCrawling(maxDepthCrawling);
       // config2.setMaxDepthOfCrawling(maxDepthCrawling);
        
        config.setPolitenessDelay(politnessDelay);
       // config2.setPolitenessDelay(politnessDelay-50);
        
        config.setMaxPagesToFetch(maxPagestoFetch);
        
        config.setIncludeBinaryContentInCrawling(true);
        
        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        //PageFetcher pageFetcher2 = new PageFetcher(config2);
        
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
       // CrawlController controller2 = new CrawlController(config, pageFetcher2, robotstxtServer);
        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        
        for(String domain : crawlDomain) {
        	controller.addSeed(domain);
        }
        
        MyCrawler.configure(crawlDomain);
        
        //controller2.addSeed("https://reuters.com/");
        
        //MyCrawler.configure(crawlDomain, storageFolder);
        /*
   		 * Start the crawl. This is a blocking operation, meaning that your code
 		 * will reach the line after this only when crawling is finished.
   		 */
        
        controller.startNonBlocking(MyCrawler.class, numberOfCrawlers);
        
        controller.waitUntilFinish();
        System.out.println("Crawler is finished");
        
        List<Object> crawlsData = controller.getCrawlersLocalData();
        int totalAttemptedPages = 0;
        int totalExtractedUrl = 0;
        
        System.out.println("debug1 : " + crawlsData.size());
        
        StringBuilder job1 = new StringBuilder();
        /*
         * header for output 1
         */
        job1.append("URL, StatusCode \n");
        
        StringBuilder job2 = new StringBuilder();
        /*
         * header for output 2
         */
        job2.append("URL, SIZE, #OUTLINKS, Content-Type \n");
        /*
         * header for output 3
         */
        StringBuilder job3 = new StringBuilder();
        job3.append("URL, DIR \n");
        
        for(Object data : crawlsData) {
        	CrawlData cd = (CrawlData) data;
        	fileBuilder(job3, cd, 3);
        	fileBuilder(job2, cd, 2);
        	fileBuilder(job1, cd, 1);
        	totalExtractedUrl += cd.allUrls.size();
        	totalAttemptedPages += cd.attemptUrl.size();
        	
        }
        
        System.out.println("total attempted : " + totalAttemptedPages);
        System.out.println("total extracted : " + totalExtractedUrl);
        
       	PrintWriter writer = new PrintWriter(new File("fetch_reuters.csv"));
       	PrintWriter writer2 = new PrintWriter(new File("visit_reuters.csv"));
       	PrintWriter writer3 = new PrintWriter(new File("urls_reuters.csv"));
       	
       	writer.write(job1.toString()); 	
       	writer2.write(job2.toString());
       	writer3.write(job3.toString());
       	
       	writer.close();
       	writer2.close();
       	writer3.close();
    }
	
	public static void fileBuilder(StringBuilder sb, CrawlData cd, int id) {
		
		if(id == 1) {
			Map<String, Integer> data1 = cd.attemptUrl;
			for(Map.Entry<String, Integer> entry : data1.entrySet()) {
				sb.append(entry.getKey()+",");
				sb.append(Integer.toString(entry.getValue())+"\n");
			}
		} else if(id == 2) {
			Map<String, List<String>> data2 = cd.successDownload;
			for(Map.Entry<String, List<String>> entry : data2.entrySet()) {
				sb.append(entry.getKey() + ",");
				sb.append(entry.getValue().get(0) + ",");
				sb.append(entry.getValue().get(1) + ",");
				sb.append(entry.getValue().get(2) + "\n");
			}
			
		} else {
			List<UrlSpec> data3 = cd.allUrls;
			for(UrlSpec us : data3) {
				sb.append(us.url + ",");
				sb.append(us.indicator+"\n");
			}
		}
		
	}
}