/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;


/**
 *
 * @author hoangngt
 */
import static code.Constants.*;
import code.QueryBean;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import java.util.ArrayList;
public class Modul {
    private String modName;
    private String fakName;
    private ArrayList<String> verpSG;
    private ArrayList<String> wahlSG;
    
    private String modLink;
    private String dozent;
    private String uri;
    private Individual ind;
    public Modul(Individual ind, OntModel model) {
        modName = ind.getLabel(null);
        System.out.println(modName);
        dozent = null;
        Property vp = model.getProperty(NS, "verpflichtendesAngebotFür");
        Property wp = model.getProperty(NS, "wählbaresAngebotFür");
        Property wirdgelehrt = model.getProperty(NS, "wirdGelehrtDurch");
        Property link = model.getProperty(NS, "Link");
//        Property inhalt = model.getProperty(NS, "Inhalt");
//        modInhalt = ind.getPropertyValue(inhalt).toString();
        NodeIterator it = ind.listPropertyValues(vp);
        verpSG = new ArrayList<String>();
        wahlSG = new ArrayList<String>();
        dozent = null;
        while (it.hasNext()) {
            RDFNode i = it.next();
            Individual temp = model.getIndividual(i.asResource().getURI());
            verpSG.add(temp.getLabel("de"));
        }
        it = ind.listPropertyValues(wp);
        while (it.hasNext()) {
            RDFNode i = it.next();
            Individual temp = model.getIndividual(i.asResource().getURI());
            wahlSG.add(temp.getLabel("de"));
        }
        it = ind.listPropertyValues(wirdgelehrt);
        while (it.hasNext()) {
            RDFNode i = it.next();
            Individual temp = model.getIndividual(i.asResource().getURI());
            dozent = temp.getLabel("de");
        }
        modLink = ind.getPropertyValue(link).toString();
        fakName = "Hello";
    }
    public String getModName() {
        return modName;
    }
    public String getFakName(){
        return fakName;
    }
    public ArrayList<String> getWahlSG() {
        return wahlSG;
    }
    public ArrayList<String> getVerpSG() {
        return verpSG;
    }
    public String getLink(){
        return modLink;
    }
    public String getDozent(){
        return dozent;
    }
}
