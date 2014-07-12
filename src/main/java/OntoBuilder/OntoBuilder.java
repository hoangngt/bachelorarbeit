/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OntoBuilder;

import static code.Constants.*;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import java.io.InputStream;

/**
 *
 * @author tahoa_000
 */
public class OntoBuilder {
    public static void createTDB(InputStream in) {
        Dataset dataset = TDBFactory.createDataset(TDB_DIR) ;
        dataset.begin(ReadWrite.WRITE); 
        dataset.removeNamedModel(TDB_MODEL);
        Model m = dataset.getNamedModel(TDB_MODEL);
        m.read(in, NS);
        m.close();
        dataset.commit();
        dataset.end();
        System.out.println("Saved");
    }
    public static OntModel readModelFromTDB() {
        Dataset dataset = TDBFactory.createDataset(TDB_DIR) ;
        dataset.begin(ReadWrite.READ);             
        Model m = dataset.getNamedModel(TDB_MODEL);
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,m);
        dataset.end();
        return ontModel;
    }

}
