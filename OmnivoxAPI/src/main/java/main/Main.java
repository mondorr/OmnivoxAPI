package main;

import assemblers.Assembler;
import assemblers.ChamplainAssembler;
import scrapers.ChamplainScraper;
import scrapers.OmnivoxScraper;
import students.Student;
import students.StudentManager;
import students.StudentPrinter;

public class Main {

	public static void main(String[] args) {
		
		if (args.length != 3) {
			System.out.println("Usage: java Main [CegepName] [StudentNumber] [Password]");
			System.out.println("Or: java -cp OmnivoxAPI-0.0.1-SNAPSHOT.jar Main [CegepName] [StudentNumber] [Password]");
			System.exit(0);
		}

		OmnivoxScraper scraper = null;
		Assembler assembler = null;

		String cegepName = args[0];

        if (cegepName.equalsIgnoreCase("champlain")) {
            scraper = new ChamplainScraper();
            assembler = new ChamplainAssembler();
        } else {
            System.out.println("This program only supports Champlain College at the moment.");
            System.exit(0);
        }

		Student student = new Student();
		StudentManager manager = new StudentManager(scraper, assembler, student);
		StudentPrinter printer = new StudentPrinter(student);

		System.out.println("Logging in...");

		String studentNumber = args[1];
		String password = args[2];
		
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
