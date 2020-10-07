package hw2.crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {
	/*
	 * 1. collects URLs the crawler attempts to fetch along with each statusCode associated with the URL (done)
	 * 		-- store everything to the attemptUrl map...
	 * 
	 */

	private final static Pattern filter = Pattern.compile(".*(\\.(css|js|mp3|wav|mp4|zip|rar|gz|avi|mov|rm|wmv" + "|7z|3gp"
			+ "|ico|ics|jar|mid|midi|mjs|mpeg|ppt|pptx|rtf|xls|xml" 
			+ "|rss|json|aac|abw|arc|azw|bz|csh|csv|epub))$");

	private final static Pattern docFilter = Pattern.compile(".*(\\.(doc|docx|pdf|webp|svg" + "|bmp|gif|jpe?g|png|tiff?))$");
	private static File storageFolder;
	private static String[] crawlDomains;
	
	public static void configure(String[] domains) {
		crawlDomains = domains;
	}

	public CrawlData cd;

	public MyCrawler() {
		cd = new CrawlData();
	}

	public CrawlData retrievalCrawlData() {
		return cd;
	}


	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {

		String href = url.getURL();

		//# content
		if(filter.matcher(href).matches()) {
			return false;
		}
		
		// domain, not duplicate
		if(!cd.attemptUrl.containsKey(href)) {
			for(String domain : crawlDomains) {
				if(href.startsWith(domain)) {
					return true;
				}
			}
		}

		//**********should it consider url outside of the reuters domain?


		//check if the URL has already been visited
		return false;
	}

	@Override
	public void visit(Page page) {

		String url = page.getWebURL().getURL().toLowerCase().replaceAll(",", "-");

		String filesize = "";
		String[] contentType = null;

		Set<WebURL> links = null;

		// 1 non-binary files downloaded
		System.out.println("URL: " + url);
		if (page.getParseData() instanceof HtmlParseData) {
			//System.out.println("size : "  + size);
			filesize = Integer.toString(page.getContentData().length);
			contentType = page.getContentType().split(";");
			//visit html page
			if(contentType[0].trim().equals("text/html")) {
				HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();	
				links = htmlParseData.getOutgoingUrls();
				List<String> job2Data = new ArrayList<>();
				//4.3
				cd.allofTheUrls += links.size();
				for(WebURL wu : links) {
					UrlDirection(cd, wu);
				}
				job2Data.add(filesize);
				job2Data.add(Integer.toString(links.size()));
				job2Data.add(contentType[0]);
				cd.successDownload.put(url, job2Data);
				return;
			}	
		}


		// 2 binary files downloaded
		//check if the url is legal
//		if(!docFilter.matcher(url).matches()) {
//			return;
//		}


		filesize = Integer.toString(page.getContentData().length);
		contentType = page.getContentType().split(";");
		System.out.println("found binary data :" + contentType[0]);

		BinaryParseData BinaryParseData = (BinaryParseData) page.getParseData();	
		links = BinaryParseData.getOutgoingUrls();
		List<String> job2DataBin = new ArrayList<>();
		//4.3
		cd.allofTheUrls += links.size();
		for(WebURL wu : links) {
			UrlDirection(cd, wu);
		}
		job2DataBin.add(filesize);
		job2DataBin.add(Integer.toString(links.size()));
		job2DataBin.add(contentType[0]);
		cd.successDownload.put(url, job2DataBin);


	}


	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		String fetchUrl = webUrl.getURL();
		cd.attemptUrl.put(fetchUrl, statusCode);
	}

	@Override
	public Object getMyLocalData() {
		return cd;
	}
	
	/*
	 * function to filter the content type
	 */
//	public boolean correctContentType(String url) {
//		
//	}

	public void UrlDirection(CrawlData cd, WebURL url) {
		String href = url.getURL().toLowerCase().replaceAll(",", "-");
		UrlSpec us = new UrlSpec();
		us.setUrl(href);
		if   (href.startsWith("http://www.reuters.com") || 
				href.startsWith("https://www.reuters.com") || 
				href.startsWith("https://reuters.com") 	|| 
				href.startsWith("http://reuters.com")) {
			us.setDirection("OK");
		} else {
			us.setDirection("N_OK");
		}
		cd.allUrls.add(us);
	}



}