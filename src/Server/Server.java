package Server;

import Log.Log;
import implementInterface.ImplementHealthCareInterface;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import static Variables.DeclareVariable.*;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import HealthCareInterfaceApp.HealthCareInterface;
import HealthCareInterfaceApp.HealthCareInterfaceHelper;

public class Server{

    private final String serverID;
    private String serverName;
    private int serverUdpPort;                                                                                    // Different server udp port
    private ImplementHealthCareInterface object;

    public static void main(String[] args) throws Exception {
    	
    	Runnable server1 = () -> {
    		try {
    			Server SherbrookeServer = new Server("SHE", args);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	};
    	
    	Runnable server2 = () -> {
    		try {
    			Server QuebecServer = new Server("QUE", args);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	};
    	
    	Runnable server3 = () -> {
    		try {
    			Server MontrealServer = new Server("MTL", args);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	};
    	Thread thread1 = new Thread(server1);
        thread1.start();
        Thread thread2 = new Thread(server2);
        thread2.start();
        Thread thread3 = new Thread(server3);
        thread3.start();
    }

    // Constructor
    public Server(String serverID, String[] args) throws Exception {
    	   this.serverID = serverID;
           switch (serverID) {
               case "MTL":
                   serverName = MONTREAL_APPOINTMENT_SERVER;
                   serverUdpPort = SERVER_PORT_MONTREAL;
                   break;
               case "QUE":
                   serverName = QUEBEC_APPOINTMENT_SERVER;
                   serverUdpPort = SERVER_PORT_QUEBEC;
                   break;
               case "SHE":
                   serverName = SHERBROOKE_APPOINTMENT_SERVER;
                   serverUdpPort = SERVER_PORT_SHERBROOKE;
                   break;
               default:
                   throw new IllegalArgumentException("Invalid server ID: " + serverID);    
           }
           try {
               ORB orb = ORB.init(args, null);
               POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
               rootpoa.the_POAManager().activate();

               ImplementHealthCareInterface servant = new ImplementHealthCareInterface(serverID, serverName);
               servant.setORB(orb);

               org.omg.CORBA.Object ref = rootpoa.servant_to_reference(servant);
               HealthCareInterface href = HealthCareInterfaceHelper.narrow(ref);

               org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
               NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

               NameComponent[] path = ncRef.to_name(serverID);
               ncRef.rebind(path, href);

               System.out.println(serverName + " Server is Up & Running././././");
               Log.serverLog(serverID, " Server is Up & Running././././");

               startUDPServer(servant);

               orb.run();
           } catch (Exception e) {
               handleException(e);
           }

           System.out.println(serverName + " Server disconnecting");
           Log.serverLog(serverID, " Server disconnecting");
    }


    private void startUDPServer(ImplementHealthCareInterface servant) {
    	Runnable task = () -> {
    		listenForRequest(servant, getServerUDPPort(serverID), getServerName(serverID), serverID);
    	};
    	Thread thread = new Thread(task);
    	thread.start();
    }

    private String getServerName(String serverID) {
    	switch (serverID) {
        	case "MTL":
        		return MONTREAL_APPOINTMENT_SERVER;
        	case "QUE":
        		return QUEBEC_APPOINTMENT_SERVER;
        	case "SHE":
        		return SHERBROOKE_APPOINTMENT_SERVER;
        	default:
        		return "";
    	}
    }

    private int getServerUDPPort(String serverID) {
    	switch (serverID) {
        	case "MTL":
        		return SERVER_PORT_MONTREAL;
        	case "QUE":
        		return SERVER_PORT_QUEBEC;
        	case "SHE":
        		return SERVER_PORT_SHERBROOKE;
        	default:
        		return 0;
    	}
    }

    private void handleException(Exception e) throws IOException {
    	e.printStackTrace(System.out);
    	Log.serverLog(serverID, "Exception: " + e);
    }
    
    private static void listenForRequest(ImplementHealthCareInterface obj, int serverUdpPort, String serverName, String serverID) {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(serverUdpPort);
            System.out.println(serverName + " UDP Server Started at port " + aSocket.getLocalPort() + " ............");
            Log.serverLog(serverID, " UDP Server Started at port " + aSocket.getLocalPort());

            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);

                String requestContent = new String(request.getData(), 0, request.getLength());
                String[] parts = requestContent.split(";");
                String method = parts[0];
                String patientID = parts[1];
                String appointmentType = parts[2];
                String appointmentID = parts[3];

                String logMessage = " UDP request received " + method + " ";
                String response = "";

                switch (method.toLowerCase()) {
                    case "removeappointment":
                        logMessage += " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ";
                        response = obj.removeAppointmentUDP(appointmentID, appointmentType, patientID);
                        break;
                    case "listappointmentavailability":
                        logMessage += " appointmentType: " + appointmentType + " ";
                        response = obj.listAppointmentAvailabilityUDP(appointmentType);
                        break;
                    case "bookappointment":
                        logMessage += " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ";
                        response = obj.bookAppointment(patientID, appointmentID, appointmentType);
                        break;
                    case "cancelappointment":
                        logMessage += " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ";
                        response = obj.cancelAppointment(patientID, appointmentID);
                        break;
                    default:
                        response = "Invalid method";
                        break;
                }

                String logReplyMessage = " UDP reply sent " + method + " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ";
                byte[] sendData = (response + ";").getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, sendData.length, request.getAddress(), request.getPort());
                aSocket.send(reply);
                Log.serverLog(serverID, patientID, logReplyMessage, response);
            }
        } catch (SocketException e) {
            System.err.println("SocketException: " + e);
            e.printStackTrace(System.out);
        } catch (IOException e) {
            System.err.println("IOException: " + e);
            e.printStackTrace(System.out);
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }

 // pre-requisite records which is required to perform test scenarios
    private void addFewRecords() {
        switch (serverID) {
            case "MTL":
            	object.addNewAppointment("MTLA050324", APPOINTMENT_TYPE_PHYSICIAN, 2);             // Add same appointmentDi for this and below record if MTLP2345 patient try to book appointmentDI MTLA050324 which is exist in both physician and surgon
            	object.addNewAppointment("MTLA050324", APPOINTMENT_TYPE_DENTAL, 2);
            	object.addNewAppointment("MTLA050324", APPOINTMENT_TYPE_PHYSICIAN, 2);
            	object.addNewAppointment("MTLA060324", APPOINTMENT_TYPE_PHYSICIAN, 2);
            	object.addNewAppointment("MTLE050324", APPOINTMENT_TYPE_SURGEON, 1);
            	object.addNewAppointment("MTLM060324", APPOINTMENT_TYPE_DENTAL, 12);
            	object.addNewAppointment("MTLA070324", APPOINTMENT_TYPE_SURGEON, 8);
            	object.addNewPatientID("MTLP1090");
            	object.addNewPatientID("MTLP1121");
                break;
            case "QUE":
            	object.addNewAppointment("QUEM100624", APPOINTMENT_TYPE_PHYSICIAN, 5);
            	object.addNewAppointment("QUEM090624", APPOINTMENT_TYPE_PHYSICIAN, 5);
            	object.addNewAppointment("QUEM080624", APPOINTMENT_TYPE_SURGEON, 5);
            	object.addNewAppointment("QUEM080624", APPOINTMENT_TYPE_DENTAL, 18);
            	object.addNewPatientID("QUEP0821");
            	object.addNewPatientID("QUEP1020");
                break;
            case "SHE":
            	object.addNewAppointment("SHEE080624", APPOINTMENT_TYPE_PHYSICIAN, 1);
            	object.addNewAppointment("SHEA070624", APPOINTMENT_TYPE_PHYSICIAN, 2);
            	object.addNewAppointment("SHEA050624", APPOINTMENT_TYPE_DENTAL, 20);
            	object.addNewAppointment("SHEM090624", APPOINTMENT_TYPE_SURGEON, 5);
            	object.addNewPatientID("SHEP1001");
            	object.addNewPatientID("SHEP2001");
                break;
        }
    }
}
