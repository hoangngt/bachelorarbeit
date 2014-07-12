package code;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;
import java.util.ArrayList;
import java.util.List;

import models.*;

public class QueryBean {

        private OntModel model;


	public QueryBean(OntModel ontModel) {
            model = ontModel;
	}

	public List<Modul> executeQuery(QueryBuilder builder) {
		List<Modul> result ;
		QueryExecution qe ;
                
            	// Create a new query
		String queryString = builder.getSPARQLQuery();

		Query query = QueryFactory
				.create(queryString);

		// Execute the query and obtain results
		qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();
                System.out.println(qe);
		result = buildQueryResult(results);

	
		if (qe != null) {
			qe.close();
		}

		return result;
	}

	/**
	 * Convert the Result Set of results into a HTML string for display
	 * 
	 * @param results
	 *            The results of the query to display
	 * @return HTML containing the results
	 */
	private List<Modul> buildQueryResult(ResultSet results) {
		List<Modul> output = new ArrayList<Modul>();
		
		while (results.hasNext()) {
			QuerySolution sol = results.next();
                        Resource modul = sol.getResource("mod");
                        String uri = modul.getURI();
                        Individual ind = model.getIndividual(uri);
                        Modul ret = new Modul(ind, model);			
			output.add(ret);		
		}
		return output;
	}
}
