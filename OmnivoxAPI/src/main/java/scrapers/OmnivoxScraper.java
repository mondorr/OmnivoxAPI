package scrapers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import assemblers.Assembler;

/**
 * This abstract class is used to collect the {@link HtmlPage} object from
 * Omnivox. You need to extend this class and implement its 4 methods.
 * This class' goal is to collect the appropriate pages from Omnivox so that the {@link Assembler} can turn them into usable objects.
 */
public abstract class OmnivoxScraper {

	/**
	 * This String represents the login url of the Omnivox page.
	 *
	 */
	private final String loginUrl;

	/**
	 * This is the webclient used to connect and make requests to
	 */
	private final WebClient client = newClient();

	/**
	 * Represents the HtmlPage of Omnivox's homepage.
	 */
	protected HtmlPage homePage;

	/**
	 * Represents the HtmlPage of Lea's homepage.
	 */
	protected HtmlPage LeaPage;

	/**
	 * The only constructor for the Omnivox Scraper.
	 * 
	 * @param loginUrl The login url for starting the Omnivox Scraper
	 */
	public OmnivoxScraper(String loginUrl) throws IllegalArgumentException {

		// Check if it matches the login pattern
		if (!loginUrl.matches("https:\\/\\/(.+?)\\.omnivox\\.ca\\/intr\\/Module\\/Identification\\/Login\\/Login\\.aspx")) {
			throw new IllegalArgumentException("The login url is invalid it should match this pattern:"
					+ "https:// + [Your Cegep Name] + .omnivox.ca/intr/Module/Identification/Login/Login.aspx");
		}

		this.loginUrl = loginUrl;
	}

	/**
	 * This method needs to get all the documents in the student's account.
	 * 
	 * When called, it should go and find all the HtmlPages from every course containing all the document pages.
	 * It needs to go through the Lea page and return a {@link HtmlPage} for every course.
	 */
	public abstract HtmlPage[] getDocumentPages();

	/**
	 * This method needs to get all the assignments in the student's account.
	 * 
	 * When called, it should go and find all the HtmlPages from every course
	 * containing all the assignment pages.
	 * It needs to go through the Lea page and return an {@link HtmlPage} for every course.
	 */
	public abstract HtmlPage[] getAssignmentPages();

	/**
	 * This method prints the what's new section in the Ommnivox homepage.
	 */

	public abstract void printWhatsNew();

	/**
	 * This method needs to set the Lea Page field to the corresponding field.
	 * 
	 * However, if it is not set, it will not be usable since it will throw a {@link NullPointerException}.
	 */
	public abstract void setLeaPage();

	/**
	 * This final method creates a new instance of a {@link WebClient} with all of
	 * the required parameters to run well.
	 */
	private final static WebClient newClient() {
		// Creates client with options
		WebClient client = new WebClient();
//		client.getOptions().setJavaScriptEnabled(true);
		client.getOptions().setCssEnabled(false);
		client.getOptions().setUseInsecureSSL(true);
		client.getOptions().setDownloadImages(false);
		client.getOptions().setPopupBlockerEnabled(true);
		client.getOptions().setRedirectEnabled(true);
		client.getOptions().setTimeout(10000);
		client.getOptions().setThrowExceptionOnScriptError(false);

		return client;
	}

	/**
	 * This method will log in to the Omnivox page and set the homePage field.
	 */
	public void login(String username, String password) {

		try {
			// Hiding warnings
			java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
			java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

			HtmlPage response = client.getPage(this.loginUrl);
			HtmlForm form = response.getFormByName("formLogin");

			String k = form.getInputByName("k").getValueAttribute();

			URL url = new URL(this.loginUrl);
			WebRequest loginRequest = new WebRequest(url, HttpMethod.POST);

			// Filling form requests
			ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			requestParams.add(new NameValuePair("NoDA", username));
			requestParams.add(new NameValuePair("PasswordEtu", password));
			requestParams.add(new NameValuePair("TypeIdentification", "Etudiant"));
			requestParams.add(new NameValuePair("TypeLogin", "PostSolutionLogin"));
			requestParams.add(new NameValuePair("k", k));
			loginRequest.setRequestParameters(requestParams);

			this.homePage = client.getPage(loginRequest);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Getters
	public HtmlPage getHomePage() {
		return this.homePage;
	}

	public HtmlPage getLeaPage() {
		return this.LeaPage;
	}

	public WebClient getClient() {
		return this.client;
	}

}
