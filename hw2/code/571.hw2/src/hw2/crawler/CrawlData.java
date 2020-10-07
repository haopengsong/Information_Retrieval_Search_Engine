package hw2.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrawlData {
	
	private int totalPageAttempts;
	
	protected int allofTheUrls;
	
	protected List<UrlSpec> allUrls;
	protected Map<String, Integer> attemptUrl;
	protected Map<String, List<String>> successDownload;
	public CrawlData() {
		attemptUrl = new HashMap<>();
		successDownload = new HashMap<>();
		allUrls = new ArrayList<>();
		
		allofTheUrls = 0;
	}	
}