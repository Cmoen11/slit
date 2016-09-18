
package slit.Teacher;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import modul.ModulRemote;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
/**
 *
 * @author Christian
 */
public class Controller {
    
    private ArrayList<String> learningGoals_list = new ArrayList<>();
    
    @FXML Label name;
    @FXML TextArea moduleDesc;
    @FXML TextField moduleName;
    @FXML TextField learningGoal_input;
    @FXML ListView learning_goals_view;
    static String username;
    public void changeName() {
        name.setText("Velkommen, " + Controller.username);
    }
    
    public void createModule() {
        try {
        lookupModulRemote().createModule(moduleDesc.getText(), moduleName.getText());
        }catch(Exception e) {
            System.out.println(e);
        }
    }
    
    private ModulRemote lookupModulRemote() {
        try {
            Context c = new InitialContext();
            return (ModulRemote) c.lookup("java:comp/env/Modul");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    public void addLearningGoal() {
        learningGoals_list.add(learningGoal_input.getText());
        ObservableList<String> observableList = FXCollections.observableList(learningGoals_list);
        learning_goals_view.setItems(observableList);
        
    }
    
}
