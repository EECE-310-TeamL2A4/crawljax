package com.crawljax.examples;

import com.crawljax.browser.EmbeddedBrowser.BrowserType;
import com.crawljax.core.CrawljaxController;
import com.crawljax.core.configuration.BrowserConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration.CrawljaxConfigurationBuilder;
import com.crawljax.core.configuration.InputSpecification;
import com.crawljax.core.configuration.PopUpCancel;

public class Crawltest
{
	//private static final String URL = "http://gkh501.com/download.html";
	//private static final String URL = "http://www.w3schools.com/js/tryit.asp?filename=tryjs_alert";
	private static final String URL = "http://courses.ece.ubc.ca/315/luisindex.html";

	private static final String ALL_ANCHORS = "a";
	private static final String LANGUAGE_TOOLS = "Language Tools";

	private static final String HEADER_XPATH = "//DIV[@id='guser']";

	private static final int MAX_CRAWL_DEPTH = 1;
	private static final int MAX_STATES = 10;

	/**
	 * Entry point
	 */
	public static void main(String[] args) {
		System.setProperty("webdriver.firefox.bin","C:\\Program Files (x86)\\Mozilla Firefox\\Firefox.exe");
		
		CrawljaxConfigurationBuilder builder = CrawljaxConfiguration.builderFor(URL);
		
		BrowserConfiguration browserConfig = new BrowserConfiguration(BrowserType.firefox);
		
		builder.setBrowserConfig(browserConfig);
		
		//PopUpCancel configuration  = new PopUpCancel();
		
		
		
		// limit the crawling scope
		builder.setMaximumStates(MAX_STATES);
		builder.setMaximumDepth(MAX_CRAWL_DEPTH);

		builder.crawlRules().setInputSpec(getInputSpecification());

		CrawljaxController crawljax = new CrawljaxController(builder.build());
		crawljax.run();
		
		
		
	}
	
	private static InputSpecification getInputSpecification() {
		InputSpecification input = new InputSpecification();

		// enter "Crawljax" in the search field
		input.field("masthead-search-term").setValue("cat");
		return input;
	}

}