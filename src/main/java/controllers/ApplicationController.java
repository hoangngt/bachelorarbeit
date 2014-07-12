/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;

import OntoBuilder.*;
import code.QueryBean;
import code.QueryBuilder;
import com.google.inject.Singleton;
import com.hp.hpl.jena.ontology.OntModel;

import java.io.IOException;

import models.*;
import ninja.Result;
import ninja.Results;


@Singleton
public class ApplicationController {
  
    public Result SearchProcess(SearchQuery squery) throws Exception {
        QueryBuilder qb = new QueryBuilder(squery);
        OntModel ontModel = OntoBuilder.readModelFromTDB();
    	QueryBean query = new QueryBean(ontModel);  
        return Results.json().render(query.executeQuery(qb));
    }
    public Result index() {

        return Results.html();

    }
    
    public Result updateOnto() {
        return Results.html();
    }
    public Result update() throws IOException {
        System.out.println("Done");
        OntoBuilder.createTDB(this.getClass().getResourceAsStream("/res/onto/ontology.owl"));
        Parser.parse();
        
        return Results.json().render("Fertig.");
    }
    

}
