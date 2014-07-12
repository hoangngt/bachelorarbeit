/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OntoBuilder;

import code.Functions;
import static code.Constants.*;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.tdb.TDBFactory;
import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Connection.*;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 *
 * @author tahoa_000
 */
public class Parser {

    public static void parse() throws IOException {
        Dataset dataset = TDBFactory.createDataset(TDB_DIR) ;
        dataset.begin(ReadWrite.WRITE);             
        Model m = dataset.getNamedModel(TDB_MODEL);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,m);

        //Get content of the Study modules page
        Document ebene1 = Jsoup.connect("https://studip.uni-halle.de/mlu_vv.php?reset_all=TRUE&view=mod")
  			  .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:28.0) Gecko/20100101 Firefox/28.0")
  			  .timeout(30000)
  			  .post();
        // Get Cookies from Stud.IP
        Response res = Jsoup.connect("https://studip.uni-halle.de/mlu_vv.php?reset_all=TRUE&view=mod")
            .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:28.0) Gecko/20100101 Firefox/28.0")
            .method(Method.GET)
            .execute();
        
        Elements studienProgrammen = ebene1.select(".semtree a");
        for (Element studienProgramm : studienProgrammen) {
            String spID = studienProgramm.attr("href").split("=")[1];  // get the ID of Studienprogrammen
            Document ebene2 = Jsoup.connect("https://studip.uni-halle.de/mlu_vv.php?&cmd=show_stm_tree&item_id="+ spID +"_withkids")
  			  .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:28.0) Gecko/20100101 Firefox/28.0")
  			  .timeout(30000)
 			  .cookies(res.cookies())
  			  .referrer("https://studip.uni-halle.de/mlu_vv.php?level=vv&cmd=qs&sset=0")
  			  .post();
            Elements studienModulen = ebene2.select(".table_row_even > font > a"); // parse the list of Studienmodule
            for (Element studienModule : studienModulen) {
                OntClass modul = model.getOntClass( NS + "Module" );
                Property modArt = null;
                String smLink = studienModule.attr("href").split("&")[0];
                System.out.println(smLink);
                Document ebene3 = Jsoup.connect("https://studip.uni-halle.de/" + smLink)
  			  .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:28.0) Gecko/20100101 Firefox/28.0")
  			  .timeout(3000)
 			  .cookies(res.cookies())
  			  .referrer("https://studip.uni-halle.de/mlu_vv.php?level=vv&cmd=qs&sset=0")
  			  .post();
                String modName = ebene3.select("td > b").get(0).text();
                Individual mod = model.createIndividual( NS + modName, modul );     //create new Individual of Class Module
                Elements modContent = ebene3.select("td > br + font");       
                String dozentName = modContent.get(1).text();  // get name of Dozent
                String semester = modContent.get(2).text().split(" ")[0];    //get semester
                // Holen List von Studiengangen, für denen diese Modul verpflichten bzw. wahl ist
                Elements studienGangen = ebene3.select("table table table tbody").get(2).select("tr"); 
                for (int i=1; i<studienGangen.size(); i++) {
                    Elements el = studienGangen.get(i).select("font");
                    String sg ="";
                    switch (el.get(0).html()) {
                        case "Bachelor":        
                            sg = "Bachelor-Stg";
                            break;
                        case "Bachelor (2-Fach)":
                            sg = "Bachelor2Fach";
                            break;
                        case "Master":
                            sg = "Master-Stg";
                            break;
                        case "Master (2-Fach)":
                            sg = "Master2Fach";
                            break;
                        default:
                            sg = "Staatsexamen-Stg";
                            break;
                    }
                    switch (el.get(2).html()) {
                        case "Pflichtmodul":        
                            modArt = model.getProperty(NS + "verpflichtendesAngebotFür");
                            break;
                        case "Wahpflichtmodul":
                            modArt = model.getProperty(NS + "wählbaresAngebotFür");
                            break;                     
                    }
                    OntClass studgang = model.getOntClass( NS + sg );
                    // p1 ist ein Individual von Class Studiengang
                    Individual p1 = model.createIndividual( NS + Functions.umlaut_decode(el.get(1).html()), studgang );
                    p1.addLabel(Functions.umlaut_decode(el.get(1).html()), "de");
                    
                    model.add(mod, modArt, p1);
                }
                // update modName, modInhalt, modZiele for Module
                mod.addLabel(modName,"de");

                Property modLink = model.getProperty(NS + "Link");
                mod.addProperty(modLink, smLink);
 
                Property modSem = model.getProperty(NS + "Semester");
                mod.addProperty(modSem, semester);
                // update dozentName for Module
                OntClass dozent = model.getOntClass(NS+"Dozent");
                Property wirdgelehrt = model.getProperty(NS + "wirdGelehrtDurch");
                Element d = ebene3.select("font a").get(0);
                String uname = d.attr("href");
                uname = uname.substring(uname.length()-5);
                Individual doz = model.createIndividual(NS + uname, dozent);
                doz.addLabel(Functions.umlaut_decode(dozentName), "de");
                model.add(mod, wirdgelehrt, doz);
            }
        }
        model.write(System.out, "RDF/XML", NS);
        dataset.commit();
        dataset.end();
    }
}
