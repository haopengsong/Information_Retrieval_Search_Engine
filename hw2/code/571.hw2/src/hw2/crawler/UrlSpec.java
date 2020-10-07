package hw2.crawler;

public class UrlSpec {
	
	protected String indicator;
	protected String url;
	
	public UrlSpec() {
		
	}
	
	public UrlSpec(String indi, String url) {
		this.indicator = indi;
		this.url = url;
	}
	
	public void setDirection(String dir) {
		this.indicator = dir;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
}
