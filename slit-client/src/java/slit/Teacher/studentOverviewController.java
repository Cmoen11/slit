
package slit.Teacher;

import auth.UserDetails;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import sessionBeans.studentOverviewRemote;
import transferClasses.StudentSubmissionHistory;
import user_details.UserBeanRemote;
import modul.SubmissionBeanRemote;


/**
 *
 * @author TorOle
 */
public class studentOverviewController {
    
    // Formålet her er at vi fra og med de to linjene under skal kunne
    // referer til ListVeiw i Scene Builder med andre navn (etter behov)
    @FXML
    ListView<Label> studentList;
    @FXML 
    ListView<Label> moduleSubmissionListView;
    
    
    // Nedenfor angir vi at bønnene kan referes til med kortere navn, 
    // nærmere bestemt overveiwBean, userOverveiw og submissionDetails
    studentOverviewRemote overviewBean = lookupstudentOverviewBeanRemote();
    UserBeanRemote userOverveiw = lookupUserBeanRemote();
    
    
    // For å ha muligheten til å navigere oss gjennom og sortere ut 
    // fra databasen har vi behov for å bruke ArrayLists, litt som 
    // buffer reader når vi skal lese av / skrive til filer
    ArrayList<UserDetails> students;
    ArrayList<StudentSubmissionHistory> submissionsForSelectedUser;
    
    
    // Henter ut samtlige studenter fra databasen og lagrer objektene
    // (UserDetails objekter) i ArrayListen vi opprettet over. Deretter
    // iterer vi gjennom listen og oppretter en ny label for hvert av 
    // elementene i listen. Teksen på labelet er brukernavnet og antall
    // innleverte moduler den spesifikke brukeren har 
    public void initialize() {
        students = userOverveiw.getAllUsers();
        for(UserDetails user : students) {
            Label l = new Label(user.getUsername() +
                    "(" + countModuleSubmissions(user.getId()) + ")");
            studentList.getItems().add(l);
                
        }
        if (!students.isEmpty()) {
            studentList.getSelectionModel().select(0);
            changeSubmissionList();
        }
        
        // "Lytter" på liste 1. Når man selekterer et element i liste 1
        // Skal liste 2 oppdateres. 
        studentList.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends Label> observable, 
                        Label oldValue, Label newValue) -> {
            changeSubmissionList();
        });
    }
    
    
    // Som navnet tilsier, holder oversikten over hvor mange 
    // modulSubmissions student(ene) har
    private int countModuleSubmissions(int userID) {
        ArrayList<StudentSubmissionHistory> temp;
        temp = lookupSubmissionBeanRemote()
                .getSubmissionHistoryFromUser(userID,
                        Controller.getUser().getCourseID())
                .getHistory();
        
        return temp.size();
    }
    
    
    private void changeSubmissionList() {
      
        moduleSubmissionListView.getItems().clear();
        
        int index = studentList.getSelectionModel().getSelectedIndex();
        UserDetails user = students.get(index);  
        
        submissionsForSelectedUser = lookupSubmissionBeanRemote()
                .getSubmissionHistoryFromUser(user.getId(),
                        Controller.getUser().getCourseID())
                .getHistory();
        
        for (StudentSubmissionHistory sh : submissionsForSelectedUser) {
            Label l = new Label(sh.getModuleName());
            moduleSubmissionListView.getItems().add(l);
        }
    }
    
    
    // Sørger for at controller klassen er koblet sammen med studentOverveiwBean
    private studentOverviewRemote lookupstudentOverviewBeanRemote() {
        try {
            Context c = new InitialContext();
            return (studentOverviewRemote) c.lookup("java:global/slit-bean/studentOverviewBean");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    
    // Sørger for at controller klassen faktisk er koblet sammen med UserBeanRemote
    private UserBeanRemote lookupUserBeanRemote() {
        try {
            Context c = new InitialContext();
            return (UserBeanRemote) c.lookup("java:global/slit-bean/UserBean");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    
    // Muliggjør at vi benytter oss av SubmissionsBeanRemote som vi trenger
    // for å se hvor mange modulSubmissions en student har
    private SubmissionBeanRemote lookupSubmissionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (SubmissionBeanRemote) c.lookup("java:global/slit-bean/SubmissionBean");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
 
    
}
