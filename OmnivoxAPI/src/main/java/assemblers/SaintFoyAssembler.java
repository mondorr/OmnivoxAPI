package assemblers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import courses.CalendarEvent;
import courses.CourseAssignment;
import courses.CourseDocument;

/**
 * This class extends the {@link Assembler} class with its own private methods
 * to facilitate the creation of Course Elements for the Champlain St-Lambert
 * College.
 */
public class SaintFoyAssembler extends Assembler {

	/**
	 * Used to format the {@link Date} Objects using the format MMM d, yyyy for the
	 * {@link CourseDocument} Object
	 */
	private static final SimpleDateFormat documentFormatter = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);

	/**
	 * Used to format the {@link Date} Objects using the format MMM-d, yyyy for the
	 * {@link CourseAssignment} Object
	 */
	private static final SimpleDateFormat assignmentFormatter = new SimpleDateFormat("MMM-d, yyyy", Locale.ENGLISH);

	/**
	 * Used to format the {@link Date} Objects using the format d MMMMMMMM yyyy for
	 * the {@link CalendarEvent} Object
	 */
	private static final SimpleDateFormat calendarEventFormatter = new SimpleDateFormat("d MMMMMMMM yyyy", Locale.ENGLISH);

	@Override
	public CourseDocument[] assembleDocuments(HtmlPage page) {
		List<HtmlElement> documents = page.getByXPath("//*[@class='itemDataGrid' or @class='itemDataGridAltern']");

		String courseName = page.<HtmlElement>getFirstByXPath("//*[@class='TitrePageLigne2']").asText();
		System.out.printf("Getting documents for %s...\n", courseName);

		CourseDocument[] return_array = new CourseDocument[documents.size()];

		int i = 0;
		for (HtmlElement document : documents) {

			String documentName = document.<HtmlElement>getFirstByXPath("./td[2]/div/a").asText();
			String distributed = document.<HtmlElement>getFirstByXPath("./td[3]").asText();
			String view = document.<HtmlElement>getFirstByXPath("./td[4]").asText();

			HtmlElement star = document.getFirstByXPath("./td[1]/img");
			boolean seen = star == null;

			return_array[i++] = formatDocument(courseName, documentName, distributed, view, seen);

		}

		return return_array;
	}

	@Override
	public CourseAssignment[] assembleAssignments(HtmlPage page) {
		List<HtmlElement> assignments = page.getByXPath("//*[@id='tabListeTravEtu']/tbody/tr[@height='30']");

		String courseName = page.<HtmlElement>getFirstByXPath("//*[@class='TitrePageLigne2']").asText();
		System.out.printf("Getting assignments for %s...\n", courseName);

		CourseAssignment[] return_array = new CourseAssignment[assignments.size()];

		int i = 0;
		for (HtmlElement assignment : assignments) {

			String title = assignment.<HtmlElement>getFirstByXPath("./td[2]").asText();
			String distributed = assignment.<HtmlElement>getFirstByXPath("./td[3]").asText();

			HtmlElement check = assignment.<HtmlElement>getFirstByXPath("./td/table/tbody/tr/td[2]/a");
			boolean completed = check != null;

			HtmlElement star = assignment.getFirstByXPath("./td[1]/img");
			boolean seen = star == null;

			return_array[i++] = formatAssignment(courseName, title, distributed, seen, completed);

		}

		return return_array;
	}

	@Override
	public CalendarEvent[] assembleCalendarEvents(HtmlPage page) {

		List<HtmlElement> events = page.getByXPath("//*[@id='tblCalendrierEvenement']/tbody/tr/td/div[4]/div");

		// If the user has the wrong calendar type
		if (events.size() == 0) {
			page = changeCalendar(page);
			events = page.getByXPath("//*[@id='tblCalendrierEvenement']/tbody/tr/td/div[4]/div");
		}

		CalendarEvent[] return_array = new CalendarEvent[events.size()];

		int i = 0;
		for (HtmlElement event : events) {

			String day = event.<HtmlElement>getFirstByXPath("./div/div[2]").asText();
			String month = event.<HtmlElement>getFirstByXPath("./div/div[3]").asText();
			int year = Calendar.getInstance().get(Calendar.YEAR);

			String title = event.<HtmlElement>getFirstByXPath("./div[3]/h3").asText();

			// Checking if it is a course event or general event
			HtmlElement courseNameElement = event.getFirstByXPath("./div[3]/div/span");
			String courseName = courseNameElement == null ? "Not A Course" : courseNameElement.asText();

			// Checking if there is a description
			DomText descriptionDom = event.getFirstByXPath("./div[3]/div/text()");
			String description = descriptionDom == null ? "No Description" : descriptionDom.asText();

			return_array[i++] = formatCalendarEvent(day, month, year, courseName, title, description);

		}

		return return_array;
	}

	/**
	 * Formats the given Strings and parses them into a {@link CourseDocument}
	 * object
	 * 
	 * @param courseName   Name of the course the document belongs to
	 * @param documentName Title of the document
	 * @param distributed  Date distributed
	 * @param view         The title of the document (will be set to link if its
	 *                     empty)
	 * @param seen         If the document is seen or not
	 * 
	 * @return The formatted CourseDocument object
	 */
	private static CourseDocument formatDocument(String courseName, String documentName, String distributed,
			String view, boolean seen) {
		
		
		// Formatting the Strings
		documentName = "a";
		distributed = "b";
		distributed = "c";
		view = "d";

		try {
			return new CourseDocument(courseName, documentName, documentFormatter.parse(distributed), seen, view);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Formats the given Strings and parses them into a {@link CourseAssignment}
	 * object
	 * 
	 * @param courseName     Name of the course the assignment belongs to
	 * @param assignmentName Title of the assignment
	 * @param distributed    Date distributed
	 * @param completed      If the assignment has been submitted
	 * @param seen           If the assignment is seen or not
	 * 
	 * @return The formatted CourseAssignment object
	 */
	private static CourseAssignment formatAssignment(String courseName, String assignmentName, String distributed,
			boolean seen, boolean completed) {

		// Formatting the Strings
		assignmentName = "a";

				/*TODO: CAUSED ERROR assignmentName.replace("\n", " ").replace("\r", "").strip();
		distributed = distributed.replace("\n", " ").replace("\r", "");
		distributed = distributed.substring(0, 13);*/

		try {
			return new CourseAssignment(courseName, assignmentName, assignmentFormatter.parse(distributed), seen,
					completed);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Formats the given Strings into a {@link CalendarEvent} object
	 * 
	 * @param day         The day as a number
	 * @param month       The month as the full month name
	 * @param year        The year as a number
	 * @param courseName  The course name
	 * @param title       The title of the event
	 * @param description The description of the event
	 * 
	 * @return The formatted CalendarEvent object
	 */
	private static CalendarEvent formatCalendarEvent(String day, String month, int year, String courseName, String title, String description) {

		// Formatting Strings
		courseName ="a";
		title = "b";
		description = "c";

		try {
			return new CalendarEvent(courseName, title, calendarEventFormatter.parse(day + " " + month + " " + year),
					description);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

}
