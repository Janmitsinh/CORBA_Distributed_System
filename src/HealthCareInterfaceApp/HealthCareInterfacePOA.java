package HealthCareInterfaceApp;


/**
* HealthCareInterfaceApp/HealthCareInterfacePOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Health_Management_Interface.idl
* Tuesday, 27 February, 2024 1:07:54 PM EST
*/

public abstract class HealthCareInterfacePOA extends org.omg.PortableServer.Servant
 implements HealthCareInterfaceApp.HealthCareInterfaceOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("addAppointment", new java.lang.Integer (0));
    _methods.put ("removeAppointment", new java.lang.Integer (1));
    _methods.put ("listAppointmentAvailability", new java.lang.Integer (2));
    _methods.put ("bookAppointment", new java.lang.Integer (3));
    _methods.put ("getAppointmentSchedule", new java.lang.Integer (4));
    _methods.put ("cancelAppointment", new java.lang.Integer (5));
    _methods.put ("swapAppointment", new java.lang.Integer (6));
    _methods.put ("shutdown", new java.lang.Integer (7));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {

  // Define addAppointment method
       case 0:  // HealthCareInterfaceApp/HealthCareInterface/addAppointment
       {
         String appointmentID = in.read_string ();
         String appointmentType = in.read_string ();
         int capacity = in.read_long ();
         String $result = null;
         $result = this.addAppointment (appointmentID, appointmentType, capacity);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  // Define removeAppointment method
       case 1:  // HealthCareInterfaceApp/HealthCareInterface/removeAppointment
       {
         String appointmentID = in.read_string ();
         String appointmentType = in.read_string ();
         String $result = null;
         $result = this.removeAppointment (appointmentID, appointmentType);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  // Define listAppointmentAvailabilty method
       case 2:  // HealthCareInterfaceApp/HealthCareInterface/listAppointmentAvailability
       {
         String appointmentType = in.read_string ();
         String $result = null;
         $result = this.listAppointmentAvailability (appointmentType);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  // Define bookAppointment method
       case 3:  // HealthCareInterfaceApp/HealthCareInterface/bookAppointment
       {
         String patientID = in.read_string ();
         String appointmentID = in.read_string ();
         String appointmentType = in.read_string ();
         String $result = null;
         $result = this.bookAppointment (patientID, appointmentID, appointmentType);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  // Define getAppointmentSchedule method
       case 4:  // HealthCareInterfaceApp/HealthCareInterface/getAppointmentSchedule
       {
         String patientID = in.read_string ();
         String $result = null;
         $result = this.getAppointmentSchedule (patientID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  // Define cancelAppointment method
       case 5:  // HealthCareInterfaceApp/HealthCareInterface/cancelAppointment
       {
         String patientID = in.read_string ();
         String appointmentID = in.read_string ();
         String $result = null;
         $result = this.cancelAppointment (patientID, appointmentID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  // Define swap Appointment method
       case 6:  // HealthCareInterfaceApp/HealthCareInterface/swapAppointment
       {
         String patientID = in.read_string ();
         String newAppointmentID = in.read_string ();
         String newAppointmentType = in.read_string ();
         String oldAppointmentID = in.read_string ();
         String oldAppointmentType = in.read_string ();
         String $result = null;
         $result = this.swapAppointment (patientID, newAppointmentID, newAppointmentType, oldAppointmentID, oldAppointmentType);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 7:  // HealthCareInterfaceApp/HealthCareInterface/shutdown
       {
         this.shutdown ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:HealthCareInterfaceApp/HealthCareInterface:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public HealthCareInterface _this() 
  {
    return HealthCareInterfaceHelper.narrow(
    super._this_object());
  }

  public HealthCareInterface _this(org.omg.CORBA.ORB orb) 
  {
    return HealthCareInterfaceHelper.narrow(
    super._this_object(orb));
  }


} // class HealthCareInterfacePOA
