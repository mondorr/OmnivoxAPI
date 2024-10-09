package main;

import assemblers.Assembler;
import assemblers.SaintFoyAssembler;
import scrapers.SaintFoyScraper;
import scrapers.OmnivoxScraper;
import students.Student;
import students.StudentManager;
import students.StudentPrinter;

public class Main {

	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.out.println("Usage: java Main [StudentNumber] [Password]");
			System.out.println("Or: java -cp OmnivoxAPI-0.0.1-SNAPSHOT.jar Main [CegepName] [StudentNumber] [Password]");
			System.exit(0);
		}

		OmnivoxScraper scraper = new SaintFoyScraper();
		Assembler assembler = new SaintFoyAssembler();

		Student student = new Student();
		StudentManager manager = new StudentManager(scraper, assembler, student);
		StudentPrinter printer = new StudentPrinter(student);

		System.out.println("Logging in...");

		String studentNumber = args[0];
		String password = args[1];
		
		manager.login(studentNumber, password);
		
		// Getting and printing documents
		manager.getDocuments();
		printer.printDocuments();

		// Getting and printing assignments
		manager.getAssignments();
		printer.printAssignments();

		// Getting and printing calendar events
		manager.getCalendarEvents();
		printer.printCalendarEvents();

		// Print what's new
		scraper.printWhatsNew();
	}

}
