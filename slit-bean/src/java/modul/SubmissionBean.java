/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modul;

import database.Courses;
import database.Modulefeedback;
import database.Modulesubmission;
import database.Users;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Christian
 */
@Stateless
public class SubmissionBean implements SubmissionBeanRemote {
    @PersistenceContext EntityManager em;
    
    @Override
    public List<ModuleSubmissionDetails> getSubmissions(int courseID) {
        int status = 0;
        List <Modulesubmission> subs = em.createNamedQuery("Modulesubmission.findByStatusAndCourse")
                .setParameter("courseID", courseID)
                .setParameter("status", status)
                .getResultList();
        
        List<ModuleSubmissionDetails> output = new ArrayList<>();
        for (Modulesubmission obj : subs) {
            output.add(
                    new ModuleSubmissionDetails(
                        obj.getSubmissionID(), obj.getStatus(), obj.getModuleID().getModuleID(),
                        obj.getUserID().getUserID(), obj.getType(), obj.getContent(),
                            obj.getCreationDate()
                    )
            );
                    
        }
        
        return output;
    }
    /**
     * Assign the selected user to the moduleSubmission
     * @param sub   the moduleSubmission that are to be applied to the user
     * @param userID  the user that are to be asigned to the submission.
     */
    @Override
    public void assignSubmissionToUser(ModuleSubmissionDetails sub, int userID) {
        // adding the user to the moduleSubmission
        Modulefeedback feedback = new Modulefeedback();
        feedback.setFeedbackID(Integer.MAX_VALUE);
        feedback.setContent(null);
        feedback.setSubmissionID(em.find(Modulesubmission.class, sub.getSubmissionID()));
        feedback.setUserID(em.find(Users.class, userID));
        
        em.persist(feedback);
        
        //set the status to 1, directly translated to " under processing".
        Modulesubmission ms = em.find(Modulesubmission.class, sub.getSubmissionID());
        System.out.println("lol");
        ms.setStatus(1);   
    }
    
    /**
     * get all assigned modules that are not processed.
     * @param userID
     * @return 
     */
    @Override
    public ArrayList<ModuleSubmissionDetails> getAssignedModulesForUser(int userID) {
        List<Modulefeedback> feedback;
        
        
        List<Modulesubmission> allAssignedSubmissions = em.createNamedQuery("Modulesubmission.findByStatus")
                .setParameter("status", 1).getResultList();
        
        for (Iterator<Modulesubmission> it = allAssignedSubmissions.iterator(); it.hasNext();) {
            Modulesubmission obj = it.next();
            for (Modulefeedback item : obj.getModulefeedbackCollection())
                if (item.getUserID().getUserID() != userID)
                    it.remove();
        }
        
        ArrayList<ModuleSubmissionDetails> output = new ArrayList<>();
        for (Modulesubmission obj: allAssignedSubmissions) {
            output.add(
                    new ModuleSubmissionDetails(
                        obj.getSubmissionID(), obj.getStatus(), obj.getModuleID().getModuleID(),
                        obj.getUserID().getUserID(), obj.getType(), obj.getContent(),
                            obj.getCreationDate()
                    )
            );
        }
   
        
        return output;
    }
    
    
    
}
