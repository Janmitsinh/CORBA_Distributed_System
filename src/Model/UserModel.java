package Model;

import static Variables.DeclareVariable.*;

// Managing and manipulating the user data
public class UserModel {

    private String userID;
    private String userType;   // type of user ADMIN/patient
    private String userServer;

    // Constructor
    public UserModel(String userID){
        this.userID= userID;
        this.userType= locateUserType();
        this.userServer= locateUserServer();
    }

    // locate user city
    private String locateUserServer() {
        String getUserFromCity = extractUser(userID);
        String userServer;

        String userCity = getUserFromCity.toUpperCase();
        if ("MTL".equals(userCity)) {
            userServer = USER_MONTREAL_SERVER;
        } else if ("SHER".equals(userCity)) {
            userServer = USER_SHERBROOK_SERVER;
        } else {
            userServer = USER_QUEBEC_SERVER;
        }

        return userServer;
    }

    private String extractUser(String userID) {
        return userID.substring(0, 3);
    }

    // find user type
    private String locateUserType(){
        if (isAdmin(userID)) {
            return USER_ADMIN;
        } else {
            return USER_PATIENT;
        }
    }

    // checking ADMIN
    private boolean isAdmin(String userID) {
        return userID.substring(3, 4).equalsIgnoreCase("A");
    }

    @Override
    public String toString(){
        return getUserType() + "(" + getUserID() + ") on " + getUserServer() + " Server.";
    }

    public String getUserID() { return userID; }
    public String getUserType() { return userType; }
    public String getUserServer() { return userServer; }
}
