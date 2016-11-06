package slit.Teacher.popups;

import auth.UserDetails;
import com.jfoenix.controls.JFXListView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import modul.ModuleDetails;
import modul.ModuleRemote;
import modul.ModuleSubmissionDetails;
import slit.Teacher.TeacherMain;
import user_details.UserBeanRemote;

/**
 *
 * @author Christian
 */
public class FacilitateController {

    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private WebView selectedBlogPost;
    @FXML private HTMLEditor answerSubmission;
    @FXML private WebView moduleDesc;
    @FXML private JFXListView<?> allBlogPosts;
    @FXML private JFXListView<?> moduleLearningGoals;
    @FXML private WebView moduleSubmission; 
    @FXML private Text studentName;
    
    static ModuleSubmissionDetails submission;
    ModuleDetails moduleInfo;
    UserDetails user;
    
    @FXML
    void initialize() {
        
        // add the submission text.
        WebEngine webEngine = moduleSubmission.getEngine();
        webEngine.loadContent(submission.getContent());
        
        // getting the submitter object
        user = lookupUserBeanRemote().getUserByID(submission.getUserID());
        studentName.setText(user.getFirstname() + " " + user.getLastname());
        
        // setting the moduleInfo
        moduleInfo = lookupModuleBeanRemote().getModuleByID(submission.getModuleID());
        WebEngine moduleDescEngine = moduleDesc.getEngine();
        moduleDescEngine.loadContent("<h1>"+moduleInfo.getName()+"</h1> <br>" +
                moduleInfo.getDescription());
        
        
    }
    public void displayPopup(ModuleSubmissionDetails submission) throws IOException {
        
        FacilitateController.submission = submission;
        
        Parent root = TeacherMain.getRoot();
        
        Stage primaryStage = new Stage();
        Parent root2 = FXMLLoader.load(FacilitateController.class.getResource("FacilitateModuleSubmission.fxml"));
        primaryStage.setScene(new Scene(root2));
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.initOwner(root.getScene().getWindow());
        primaryStage.setTitle("Modulgodkjenning");
        primaryStage.showAndWait();
    }

    private UserBeanRemote lookupUserBeanRemote() {
        try {
            Context c = new InitialContext();
            return (UserBeanRemote) c.lookup("java:comp/env/UserBean");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private ModuleRemote lookupModuleBeanRemote() {
        try {
            Context c = new InitialContext();
            return (ModuleRemote) c.lookup("java:comp/env/ModuleBean");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    
}