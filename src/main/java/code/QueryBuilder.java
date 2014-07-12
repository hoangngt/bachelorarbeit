package code;
import models.*;
import static code.Constants.*;
public class QueryBuilder {

	private SearchQuery query;

	public QueryBuilder(SearchQuery query) {
		this.query=query;
            
	}

	/**
	 * Build the SPARQL query based off of the values passed in on creation of
	 * the class
	 * 
	 * @return SPARQL Query in the form of a String
	 */
	public String getSPARQLQuery() {
		StringBuilder builder = new StringBuilder();
		builder.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "PREFIX uni: <"+NS+"> \n");
		builder.append("SELECT ?mod \n");
		builder.append("WHERE {\n");
		builder.append("    {?mod rdf:type uni:Module ; \n");
		builder.append("        rdfs:label ?modName. ");
                if (!"".equals(query.modName))	{
                    builder.append("    FILTER regex(?modName, '");
                    builder.append(query.modName);
                    builder.append("', 'i'). \n");
                }
                builder.append("    } \n");		
        if (!"alle".equals(query.sem)) {
                builder.append("    {?mod uni:Semester '");
                builder.append(query.sem);
                builder.append("'}. \n");
        }

        switch (query.modArt) {
            case "wahl":    
                builder.append("    {?mod uni:wählbaresAngebotFür ?wahlSG.");
                builder.append("     ?wahlSG rdfs:label ?wahlName. \n");
        	builder.append("        FILTER regex(?wahlName, '");
        	builder.append(query.studienGang);
        	builder.append("', 'i')} \n");
                break;
            case "pflicht":
                builder.append("    {?mod uni:verpflichtendesAngebotFür ?verpSG.");
                builder.append("    ?verpSG rdfs:label ?verpName. \n");
        	builder.append("        FILTER regex(?verpName, '");
        	builder.append(query.studienGang);
        	builder.append("', 'i')} \n");
            case "alle": break;
        }
        
        if (!"".equals(query.dozent))	{
        	builder.append("    {?mod uni:wirdGelehrtDurch ?dozent. ?dozent rdfs:label ?dozName. \n");
        	builder.append("        FILTER regex(?dozName, '");
        	builder.append(query.dozent);
        	builder.append("', 'i')} \n");
        }
        builder.append("} \n");
        builder.append("GROUP BY ?mod");
        System.out.println(builder.toString());
	return builder.toString();
	}
}

