package Client;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import Log.Log;
import static Variables.DeclareVariable.*;
import HealthCareInterfaceApp.HealthCareInterface;
import HealthCareInterfaceApp.HealthCareInterfaceHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.*;

public class Client {

	private static Scanner input; // to get input

	public static void main(String[] args) {
		try {
			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			startClientServer(ncRef);
		} catch (CustomException | Exception e) {
			System.out.println("Please try again!.");
		}
	}

	// Login section. It will handle login of ADMIN and patient
	public static void startClientServer(NamingContextExt ncRef) throws CustomException, Exception {
		try {
			input = new Scanner(System.in);
			System.out.println("------------- Welcome to Health Care Management System -------------");
			System.out.println("Please Enter your ID:\n [For Concurrent Testing enter 'ConcurrentTester']");
			String userID = input.next().trim().toUpperCase();
			if (userID.equalsIgnoreCase("ConcurrentTester")) {
				Log.userLog(userID, " Login attempt for ConcurrentTester(concurrent testing) ");
				initiateConcurrentTrial(ncRef);
			} else {
				Log.userLog(userID, " login attempt");

				switch (checkUserType(userID)) {
				case ACCOUNT_TYPE_PATIENT:
					handlePatientLogin(userID, ncRef);
					break;
				case ACCOUNT_TYPE_ADMIN:
					handleAdminLogin(userID, ncRef);
					break;
				default:
					System.out.println("Please Enter a valid UserID !!!");
					Log.userLog(userID, " UserID is not in correct format");
					Log.deleteLogFile(userID);
					startClientServer(ncRef);
					break;
				}
			}

		} catch (Exception e) {
			System.err.println("Error occur in client server: " + e.getMessage());
			e.printStackTrace(); // throw a stack of error if try block fail
		}
	}

	// print patient login information and pass patientId and serverPort to patient
	private static void handlePatientLogin(String userID, NamingContextExt ncRef) throws IOException, CustomException {
		System.out.println("Patient with an ID of " + userID + " successful Login ");
		Log.userLog(userID, " Patient Login successful");
		patient(ncRef, userID); // no need of userID
	}

	// print admin login information and pass adminId and serverPort to admin
	private static void handleAdminLogin(String userID, NamingContextExt ncRef) throws IOException, CustomException {
		System.out.println("Admin with an ID of (" + userID + "]" + "successful Login");
		Log.userLog(userID, " Admin Login successful");
		admin(ncRef, userID);
	}

	private static void initiateConcurrentTrial(NamingContextExt ncRef) throws Exception {
		System.out.println("Starting of Appointment Scheduler Concurrency Trial ");
		System.out.println("Connecting Montreal Server...");
		String appointmentType = APPOINTMENT_TYPE_PHYSICIAN;
		String appointmentID = "MTLM101024";
		HealthCareInterface servant = HealthCareInterfaceHelper.narrow(ncRef.resolve_str("MTL"));
		System.out
				.println("adding " + appointmentID + " " + appointmentType + " with capacity 4 in Montreal Server...");
		String response = servant.addAppointment(appointmentID, appointmentType, 4);
		System.out.println(response);
		Runnable task1 = () -> {
			String patientID = "MTLP1010";
			System.out.println("Connecting Montreal Server for " + patientID);
			String res = servant.bookAppointment(patientID, appointmentID, appointmentType);
			System.out.println("Booking response for " + patientID + " " + res);
			res = servant.cancelAppointment(patientID, appointmentID);
			System.out.println("Canceling response for " + patientID + " " + res);
		};
		Runnable task2 = () -> {
			String patientID = "MTLP1011";
			System.out.println("Connecting Montreal Server for " + patientID);
			String res = servant.bookAppointment(patientID, appointmentID, appointmentType);
			System.out.println("Booking response for " + patientID + " " + res);
			res = servant.cancelAppointment(patientID, appointmentID);
			System.out.println("Canceling response for " + patientID + " " + res);
		};
		Runnable task3 = () -> {
			String patientID = "MTLP1012";
			System.out.println("Connecting Montreal Server for " + patientID);
			String res = servant.bookAppointment(patientID, appointmentID, appointmentType);
			System.out.println("Booking response for " + patientID + " " + res);
			res = servant.cancelAppointment(patientID, appointmentID);
			System.out.println("Canceling response for " + patientID + " " + res);
		};
		Runnable task4 = () -> {
			String patientID = "MTLP1013";
			System.out.println("Connecting Montreal Server for " + patientID);
			String res = servant.bookAppointment(patientID, appointmentID, appointmentType);
			System.out.println("Booking response for " + patientID + " " + res);
			res = servant.cancelAppointment(patientID, appointmentID);
			System.out.println("Canceling response for " + patientID + " " + res);
		};
		Runnable task5 = () -> {
			String patientID = "MTLP1014";
			System.out.println("Connecting Montreal Server for " + patientID);
			String res = servant.bookAppointment(patientID, appointmentID, appointmentType);
			System.out.println("Booking response for " + patientID + " " + res);
			res = servant.cancelAppointment(patientID, appointmentID);
			System.out.println("Canceling response for " + patientID + " " + res);
		};

		Runnable task6 = () -> {
			String res = servant.removeAppointment(appointmentID, appointmentType);
			System.out.println("removeappointment response for " + appointmentID + " " + res);
		};

		Thread thread1 = new Thread(task1);
		Thread thread2 = new Thread(task2);
		Thread thread3 = new Thread(task3);
		Thread thread4 = new Thread(task4);
		Thread thread5 = new Thread(task5);
		Thread thread6 = new Thread(task6);
		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		thread5.start();
		thread1.join();
		thread2.join();
		thread3.join();
		thread4.join();
		thread5.join();
		System.out.println("Concurrency Test Finished for Book Appointmentt");
		thread6.start();
		thread6.join();
		try {
			startClientServer(ncRef);
		} catch (CustomException | Exception e) {
			e.printStackTrace();
		}
	}

	private static String getServerPort(String userID) {
		String branchAcronym = userID.substring(0, 3);
		if (branchAcronym.equalsIgnoreCase("MTL")) {
			return branchAcronym;
		} else if (branchAcronym.equalsIgnoreCase("SHE")) {
			return branchAcronym;
		} else if (branchAcronym.equalsIgnoreCase("QUE")) {
			return branchAcronym;
		}
		return "1";
	}

	// check the type of user depends on character A (ADMIN) or P (patient)
	private static int checkUserType(String userID) {
		// Check Invalid user ID length. Length of userID should be exactly 8.
		if (userID.length() != 8) {
			return 0;
		}

		String getUserCity = userID.substring(0, 3).toUpperCase();
		char userTypeChar = userID.charAt(3);

		if (getUserCity.equals("MTL") || getUserCity.equals("QUE") || getUserCity.equals("SHE")) {
			if (userTypeChar == 'P') {
				return ACCOUNT_TYPE_PATIENT;
			} else if (userTypeChar == 'A') {
				return ACCOUNT_TYPE_ADMIN;
			}
		}

		return 0;
	}

	private static void patient(NamingContextExt ncRef, String patientID) throws CustomException, IOException {
		try {
			String serverID = getServerPort(patientID);
			if (serverID.equals("1")) {
				startClientServer(ncRef);
			}
			HealthCareInterface servant = HealthCareInterfaceHelper.narrow(ncRef.resolve_str(serverID));
			boolean isRepeat = true;

			do {
				getMenuOption(ACCOUNT_TYPE_PATIENT); // it will trigger getMenuOption method for patient
				int menuSelection = input.nextInt();
				String appointmentType;
				String appointmentID;
				String serverResponse;

				switch (menuSelection) {
					case PATIENT_BOOK_APPOINTMENT: // invoke bookAppointment method
						appointmentType = getMenuOptionForAppointmentType();
						appointmentID = getAppointmentID();
						Log.userLog(patientID, " booking to " + "Appointment");
						serverResponse = servant.bookAppointment(patientID, appointmentID, appointmentType);
						System.out.println(serverResponse);
						Log.userLog(patientID, "bookAppointment "," appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ",serverResponse);
					break;
					case PATIENT_CANCEL_APPOINTMENT: // invoke cancelAppointment method
						appointmentType = getMenuOptionForAppointmentType();
						appointmentID = getAppointmentID();
						Log.userLog(patientID, " attempting to " + "cancelAppointment");
						serverResponse = servant.cancelAppointment(patientID, appointmentID);
						System.out.println(serverResponse);
						Log.userLog(patientID, "cancelAppointment"," appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ",serverResponse);
					break;
					case PATIENT_GET_APPOINTMENT_SCHEDULE: // invoke patientAppointmentSchedule method
						Log.userLog(patientID, " trying to getAppointmentSchedule");
						serverResponse = servant.getAppointmentSchedule(patientID);
						System.out.println(serverResponse);
						Log.userLog(patientID, " bookAppointment", " null ", serverResponse);
						break;
					case PATIENT_SWAP_APPOINTMENT:
						appointmentType = getMenuOptionForAppointmentType();
						System.out.println("Please Enter the old Appointment you want to replaced: ");
						appointmentID = getAppointmentID();
						System.out.println("Please Enter the New Appointment: ");
						String newAppointmentType = getMenuOptionForAppointmentType();
						String newAppointmentID = getAppointmentID();
						Log.userLog(patientID, " trying to swap Appointment");
						serverResponse = servant.swapAppointment(patientID, newAppointmentID, newAppointmentType,appointmentID, appointmentType);
						System.out.println(serverResponse);
						Log.userLog(patientID, " swap Appointment"," oldAppointmentID: " + appointmentID + " oldAppointmentType: " + appointmentType+ " newAppointmentID: " + newAppointmentID + " newAppointmentType: "+ newAppointmentType + " ",serverResponse);
						break;
					case SHUTDOWN:
						Log.userLog(patientID, "trying ORB to shutdown");
						servant.shutdown();
						Log.userLog(patientID, " shutdown ");
						return;
					case PATIENT_LOGOUT:
						isRepeat = false;
						Log.userLog(patientID, " trying to Logout");
						break;
				}
			} while (isRepeat);
			startClientServer(ncRef);
		} catch (Exception e) {
			System.err.println(
					"Something wrong : You can not perform this operation " + e.getMessage() + " Please try again.");
			Log.userLog(e.getMessage(), "trying to Logout"); // e.printStack();
		}
	}

	// Locate RMI registry and lookup for HEALTH_CARE_SYSTEM remoteObject to invokes
	// methods in remote object interface
	private static void admin(NamingContextExt ncRef, String appointmentAdminID) throws CustomException, IOException {
		try {
			String serverID = getServerPort(appointmentAdminID);
			if (serverID.equals("1")) {
				startClientServer(ncRef);
			}
			HealthCareInterface servant = HealthCareInterfaceHelper.narrow(ncRef.resolve_str(serverID));
			boolean isRepeat = true;

			do {
				getMenuOption(ACCOUNT_TYPE_ADMIN); // it will trigger getMenuOption method for admin
				int menuSelection = input.nextInt();

				switch (menuSelection) {
				case ADMIN_ADD_APPOINTMENT: // invoke addAppointment method
				case ADMIN_REMOVE_APPOINTMENT: // invoke removeAppointment method
				case ADMIN_LIST_APPOINTMENT_AVAILABILITY: // invoke listAppointmentAvailability method
					handleAdminAppointment(menuSelection, appointmentAdminID, servant);
					break;
				case ADMIN_BOOK_APPOINTMENT: // invoke bookAppointment method
				case ADMIN_GET_APPOINTMENT_SCHEDULE: // invoke appointmentSchedule method
				case ADMIN_CANCEL_APPOINTMENT: // invoke cancelAppointment method
					handleAdminAction(menuSelection, appointmentAdminID, servant);
					break;
				case ADMIN_LOGOUT:
					isRepeat = false;
					Log.userLog(appointmentAdminID, "trying to Logout");
					break;
				}
			} while (isRepeat);

			startClientServer(ncRef);
		} catch (Exception e) {
			System.err.println("You can not perform this operation: " + e.getMessage());
			Log.userLog(e.getMessage(), "trying to Logout");

		}
	}

	// handle ADMIN appointment
	private static void handleAdminAppointment(int menuSelection, String appointmentAdminID,
			HealthCareInterface servant) throws IOException {
		String action = listOfActionForAdmin(menuSelection);

		String appointmentID;
		String appointmentType;
		Log.userLog(appointmentAdminID, "attempting to " + action);

		switch (menuSelection) {
		case ADMIN_ADD_APPOINTMENT:
			appointmentType = getMenuOptionForAppointmentType();
			appointmentID = getAppointmentID();
			int capacity = getCapacity();
			String serverResponse = servant.addAppointment(appointmentID, appointmentType, capacity);
			System.out.println(serverResponse);
			Log.userLog(appointmentAdminID, action, "appointmentID: " + appointmentID + " appointmentType: "
					+ appointmentType + " appointmentCapacity: " + capacity + " ", serverResponse);
			break;
		case ADMIN_REMOVE_APPOINTMENT:
			appointmentType = getMenuOptionForAppointmentType();
			appointmentID = getAppointmentID();
			serverResponse = servant.removeAppointment(appointmentID, appointmentType); // invoke remove Appointment
																						// method
			System.out.println(serverResponse);
			Log.userLog(appointmentAdminID, action,
					"appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", serverResponse);
			break;
		case ADMIN_LIST_APPOINTMENT_AVAILABILITY:
			appointmentType = getMenuOptionForAppointmentType();
			serverResponse = servant.listAppointmentAvailability(appointmentType); // invoke listAppointmentAvailability
																					// method
			System.out.println(serverResponse);
			Log.userLog(appointmentAdminID, action, "appointmentType: " + appointmentType + " ", serverResponse);
			break;
		}
	}

	private static void handleAdminAction(int menuSelection, String appointmentAdminID, HealthCareInterface servant) throws IOException {
		String patientID;
		String appointmentType;
		String appointmentID;
		String action = getActionForAdminAction(menuSelection);
		Log.userLog(appointmentAdminID, "attempting to " + action);

		switch (menuSelection) {
		case ADMIN_BOOK_APPOINTMENT:
			patientID = askForPatientIDFromAdmin(appointmentAdminID.substring(0, 3));
			appointmentType = getMenuOptionForAppointmentType();
			appointmentID = getAppointmentID();
			String serverResponse = servant.bookAppointment(patientID, appointmentID, appointmentType); // invoke
																										// bookAppointment
																										// method
			System.out.println(serverResponse);
			Log.userLog(appointmentAdminID, action, "patientID: " + patientID + " appointmentID: " + appointmentID
					+ " appointmentType: " + appointmentType + " ", serverResponse);
			break;
		case ADMIN_GET_APPOINTMENT_SCHEDULE:
			patientID = askForPatientIDFromAdmin(appointmentAdminID.substring(0, 3));
			serverResponse = servant.getAppointmentSchedule(patientID); // invoke getAppointmentSchedule method
			System.out.println(serverResponse);
			Log.userLog(appointmentAdminID, action, "patientID: " + patientID + " ", serverResponse);
			break;
		case ADMIN_CANCEL_APPOINTMENT:
			patientID = askForPatientIDFromAdmin(appointmentAdminID.substring(0, 3));
			appointmentType = getMenuOptionForAppointmentType();
			appointmentID = getAppointmentID();
			serverResponse = servant.cancelAppointment(patientID, appointmentID); // invoke cancelAppointment method
			System.out.println(serverResponse);
			Log.userLog(appointmentAdminID, action, "patientID: " + patientID + " appointmentID: " + appointmentID
					+ " appointmentType: " + appointmentType + " ", serverResponse);
			break;
		 case ADMIN_SWAP_APPOINTMENT:
			 patientID = askForPatientIDFromAdmin(appointmentAdminID.substring(0, 3));
             appointmentType = getMenuOptionForAppointmentType();
             System.out.println("Please Enter the OLD appointment to be swapped");
 			 appointmentID = getAppointmentID();
             System.out.println("Please Enter the NEW appointment to be swapped");
             String newAppointmentype = getMenuOptionForAppointmentType();
             String newAppointmentID = getAppointmentID();
             Log.userLog(appointmentAdminID, " attempting to swapappointment");
             serverResponse = servant.swapAppointment(patientID, newAppointmentID, newAppointmentype, appointmentID, appointmentType);
             System.out.println(serverResponse);
             Log.userLog(appointmentAdminID, " swapappointment", " patientID: " + patientID + " oldAppointmentID: " + appointmentID + " OldAppointmentType: " + appointmentType + " newAppointmentID: " + newAppointmentID + " newAppointmentype: " + newAppointmentype + " ", serverResponse);
             break;
         case ADMIN_SHUTDOWN:
        	 Log.userLog(appointmentAdminID, " attempting ORB shutdown");
             servant.shutdown();
             Log.userLog(appointmentAdminID, " shutdown");
             return;	
		}
	}

	private static String askForPatientIDFromAdmin(String cities) {
		try (Scanner scanner = new Scanner(System.in)) {
			String userID;
			do {
				System.out.println("Please enter a patientID (Within " + cities + " Server):");
				userID = scanner.next().trim().toUpperCase();
			} while (checkUserType(userID) != ACCOUNT_TYPE_PATIENT || !userID.substring(0, 3).equals(cities));
			return userID;
		}
	}

	private static String getActionForAdminAction(int menuSelection) {
		if (menuSelection == ADMIN_BOOK_APPOINTMENT) {
			return "bookAppointment";
		} else if (menuSelection == ADMIN_GET_APPOINTMENT_SCHEDULE) {
			return "getBookingSchedule";
		} else if (menuSelection == ADMIN_CANCEL_APPOINTMENT) {
			return "cancelAppointment";
		} else {
			return "";
		}
	}

	private static String listOfActionForAdmin(int menuSelection) {
		if (menuSelection == ADMIN_ADD_APPOINTMENT) {
			return "addAppointment";
		} else if (menuSelection == ADMIN_REMOVE_APPOINTMENT) {
			return "removeAppointment";
		} else if (menuSelection == ADMIN_LIST_APPOINTMENT_AVAILABILITY) {
			return "listAppointmentAvailability";
		} else {
			return "";
		}
	}

	// Menu of three appointment
	private static String getMenuOptionForAppointmentType() {
	    Scanner scanner = new Scanner(System.in);
	    System.out.println("-----------------------------------------");
	    System.out.println("Please pick an option below:");
	    System.out.println("[Option 1] Physician");
	    System.out.println("[Option 2] Surgeon");
	    System.out.println("[Option 3] Dental");
	    System.out.println();
	    System.out.println("Please enter corresponding number from the menu above");

	    try {
	        int userInput = scanner.nextInt();
	        if (userInput == 1) {
	            return APPOINTMENT_TYPE_PHYSICIAN;
	        } else if (userInput == 2) {
	            return APPOINTMENT_TYPE_SURGEON;
	        } else if (userInput == 3) {
	            return APPOINTMENT_TYPE_DENTAL;
	        } else {
	            System.out.println("Invalid option. Please enter a number between 1 and 3.");
	            // Recursive call in case of invalid input
	            return getMenuOptionForAppointmentType();
	        }
	    } catch (InputMismatchException e) {
	        System.out.println("Invalid input. Please enter a number.");
	        scanner.nextLine(); // Consume the invalid input
	        // Recursive call in case of invalid input
	        return getMenuOptionForAppointmentType();
	    }
	}


	// get appointmentID from user
	private static String getAppointmentID() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("-----------------------------------------");
		System.out.println("Please enter the appointmentID (combination of city, time slot, and appointment data)");
		String appointmentID = scanner.next().trim().toUpperCase();

		if (isValidAppointmentID(appointmentID)) {
			return appointmentID;
		} else {
			System.out.println("Invalid appointmentID format. Please try again.");
			return getAppointmentID(); // Recursively call getAppointmentID until a valid appointmentID is entered
		}
	}

	// validate the AppointmentID
	private static boolean isValidAppointmentID(String appointmentID) {
		if (appointmentID.length() != 10) {
			return false;
		}
		String cityCode = appointmentID.substring(0, 3).toUpperCase();
		char timeSlot = appointmentID.charAt(3);
		return (cityCode.equals("MTL") || cityCode.equals("SHE") || cityCode.equals("QUE"))
				&& (timeSlot == 'M' || timeSlot == 'A' || timeSlot == 'E');
	}

	// ask for capacity of particular appointment
	private static int getCapacity() {
		System.out.println("-----------------------------------------");
		System.out.println("Please input the appointment booking capacity:");
		return input.nextInt();
	}

	private static void getMenuOption(int userType) {
		System.out.println("-----------------------------------------");
		System.out.println("Please pick an option below:");
		if (userType == ACCOUNT_TYPE_PATIENT) {
			System.out.println("[Option 0] ShutDown");
			System.out.println("[Option 1] Book Appointment");
			System.out.println("[Option 2] Get Appointment Schedule");
			System.out.println("[Option 3] Cancel Appointment");
			System.out.println("[Option 4] Swap Appointment");
			System.out.println("[Option 5] Logout");
			System.out.println();
			System.out.println("Please enter corresponding number from the menu above");
		} else if (userType == ACCOUNT_TYPE_ADMIN) {
			System.out.println("[Option 0] ShutDown");
			System.out.println("[Option 1] Add Appointment");
			System.out.println("[Option 2] Remove Appointment");
			System.out.println("[Option 3] List Appointment Availability");
			System.out.println("[Option 4] Book Appointment for Patient");
			System.out.println("[Option 5] Get Appointment Schedule for Patient");
			System.out.println("[Option 6] Cancel Appointment for Patient");
			System.out.println("[Option 7] Swap Appointment");
			System.out.println("[Option 8] Logout");
			System.out.println();
			System.out.println("Please enter corresponding number from the menu above");
		}
	}
}
