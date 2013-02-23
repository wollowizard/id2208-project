/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.id2208.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import ontology.MyOntManager;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 *
 * @author Gerard
 */
public class Project {

    final double EXACT = 1.0;
    final double SUBSUMPTION = 0.8;
    final double PLUGIN = 0.6;
    final double STRUCTURAL = 0.5;
    final double NOTMATCHED = 0.0;
    public final double THRESHOLD = 0.5;
    static MyOntManager om;
    static OWLOntologyManager manager;
    static OWLOntology ontology;
    static Reasoner reasoner;
    static HashMap<String, OWLClass> mapName_OWLClass;

    public static void main(String[] args) {
        //C:/Users/Gerard/KTH/period 3/Web Services/Homework/FinalProject/ProjectServices/data
        String ontologyLocation = "file:///C:/Users/Gerard/KTH/period%203/Web%20Services/Homework/FinalProject/id2208-project/data/SUMO.owl";
        //String ontologyLocation = "file:///media/alfredo/SharedData/Programming/NetBeansProjects/id2208/project/data/SUMO.owl";
        om = new MyOntManager();
        manager = om.initializeOntologyManager();
        ontology = om.initializeOntology(manager, ontologyLocation);
        reasoner = om.initializeReasoner(ontology, manager);

        //Load classes from ontology file
        mapName_OWLClass = om.loadClasses(reasoner);



        System.out.println("/n/nMap size = " + mapName_OWLClass.size());

        for (Map.Entry<String, OWLClass> entry : mapName_OWLClass.entrySet()) {
            String string = entry.getKey();
            OWLClass oWLClass = entry.getValue();
            //System.out.println("key=" + string + "  owlClass=" + oWLClass.toString());

        }

        //Test Ancestor
        //String clsName = "City";
        //String clsName = "Hotel";
        String clsName = "Address";
        OWLClass cls = mapName_OWLClass.get(clsName.toLowerCase());
        OWLClass[] superClasses = om.getAncestorClasses(cls, reasoner);
        System.out.println("/nSuperClasses of class: " + clsName);
        for (int i = 0; i < superClasses.length; i++) {
            System.out.println("  " + superClasses[i].toString());
        }
        
        HashMap<String, OWLClass> loadClasses = om.loadClasses(reasoner);
        for (Map.Entry<String, OWLClass> e : loadClasses.entrySet()) {
            if(hasRelationWith(e.getKey(), "Thing")){
                System.out.println (e.getKey()+" hasRelationWith Book ");
            }
            
        }

    }

         
    

    public Project() {
        String ontologyLocation = "file:///C:/Users/Gerard/KTH/period%203/Web%20Services/Homework/FinalProject/id2208-project/data/SUMO.owl";

        om = new MyOntManager();
        manager = om.initializeOntologyManager();
        ontology = om.initializeOntology(manager, ontologyLocation);
        reasoner = om.initializeReasoner(ontology, manager);
        if (reasoner == null) {
            System.out.println("NULLLLLLLLLLLLL");
        }

        //Load classes from ontology file
        mapName_OWLClass = om.loadClasses(reasoner);



        System.out.println("/n/nMap size = " + mapName_OWLClass.size());

        for (Map.Entry<String, OWLClass> entry : mapName_OWLClass.entrySet()) {
            String string = entry.getKey();
            OWLClass oWLClass = entry.getValue();
            //System.out.println("key=" + string + "  owlClass=" + oWLClass.toString());

        }

        

    }

    

    public double findMatching(String output, String input) {
        if (isSameAs(output, input)) {
            return EXACT;
        } else if (isSubClassOf(input, output)) {
            return SUBSUMPTION;
        } else if (isSubClassOf(output, input)) {
            return PLUGIN;
        } else if (hasRelationWith(output, input)) {
            return STRUCTURAL;
        } else {
            return NOTMATCHED;
        }
    }

    static private boolean isSameAs(String output, String input) {
        return output.equals(input);
    }

    static private boolean isSubClassOf(String child, String parent) {
        OWLClass cls1 = mapName_OWLClass.get(child.toLowerCase());
        OWLClass cls2 = mapName_OWLClass.get(parent.toLowerCase());

        if (cls1 == null || cls2 == null) {
            return false;
        }
        return reasoner.isSubClassOf(cls1, cls2);
    }

    static private boolean hasRelationWith(String output, String input) {

        OWLClass cls1 = mapName_OWLClass.get(output.toLowerCase());

        OWLClass cls2 = mapName_OWLClass.get(input.toLowerCase());
        if (cls1 == null || cls2 == null) {
            return false;
        }

        Vector<OWLObjectProperty> objprops = om.findRelationship(cls1, cls2, reasoner);
        //System.out.println("Vector size " + objprops.size());

        for (OWLObjectProperty p : objprops) {
            System.out.println("  : " + p.getURI().getFragment() + "  " + p.getNamedProperty().toString());
        }
        return objprops.size() > 0;
    }
}