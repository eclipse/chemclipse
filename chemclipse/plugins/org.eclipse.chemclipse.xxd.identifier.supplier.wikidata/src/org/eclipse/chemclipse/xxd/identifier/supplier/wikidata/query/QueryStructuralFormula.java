/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.identifier.supplier.wikidata.query;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

public class QueryStructuralFormula {

	public static String fromName(String name) {

		String select = Wikidata.PROP + Wikidata.RDFS + Wikidata.SKOS + Wikidata.WIKIBASE + Wikidata.BIGDATA + Wikidata.WDT + //
				"SELECT ?item ?itemLabel ?pic\n" + //
				"WHERE {\n" + //
				"  {\n" + //
				"    ?item rdfs:label \"" + name + "\"@en.\n" + //
				"  }\n" + //
				"  UNION\n" + //
				"  {\n" + //
				"    ?item skos:altLabel \"" + name + "\"@en.\n" + //
				"  }\n" + //
				"  ?item wdt:P117 ?pic\n" + //
				"  SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\". }\n" + //
				"}";
		return query(select);
	}

	public static String fromCAS(String cas) {

		String select = Wikidata.PROP + Wikidata.STATEMENT + Wikidata.WIKIBASE + Wikidata.BIGDATA + Wikidata.WDT + //
				"SELECT ?item ?itemLabel ?pic WHERE {\n" + //
				"  ?item p:P231 ?_cas.\n" + //
				"  ?_cas (ps:P231) \"" + cas + "\".\n" + //
				"  ?item wdt:P117 ?pic\n" + //
				"  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\" }\n" //
				+ "}";
		return query(select);
	}

	public static String fromSMILES(String smiles) {

		String select = Wikidata.PROP + Wikidata.STATEMENT + Wikidata.WIKIBASE + Wikidata.BIGDATA + Wikidata.WDT + //
				"SELECT ?item ?itemLabel ?pic WHERE {\n" + //
				"  ?item p:P233 ?_smiles.\n" + //
				"  ?_smiles (ps:P233) \"" + smiles + "\".\n" + //
				"  ?item wdt:P117 ?pic\n" + //
				"  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\" }\n" //
				+ "}";
		return query(select);
	}

	public static String fromInChI(String inchi) {

		String select = Wikidata.PROP + Wikidata.STATEMENT + Wikidata.WIKIBASE + Wikidata.BIGDATA + Wikidata.WDT + //
				"SELECT ?item ?itemLabel ?pic WHERE {\n" + //
				"  ?item p:P234 ?_inchi.\n" + //
				"  ?_inchi (ps:P234) \"" + inchi + "\".\n" + //
				"  ?item wdt:P117 ?pic\n" + //
				"  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\" }\n" //
				+ "}";
		return query(select);
	}

	public static String fromInChIKey(String inchiKey) {

		String select = Wikidata.PROP + Wikidata.STATEMENT + Wikidata.WIKIBASE + Wikidata.BIGDATA + Wikidata.WDT + //
				"SELECT ?item ?itemLabel ?pic WHERE {\n" + //
				"  ?item p:P235 ?_inchiKey.\n" + //
				"  ?_inchiKey (ps:P235) \"" + inchiKey + "\".\n" + //
				"  ?item wdt:P117 ?pic\n" + //
				"  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\" }\n" //
				+ "}";
		return query(select);
	}

	private QueryStructuralFormula() {

	}

	private static String query(String queryString) {

		Query query = QueryFactory.create(queryString);
		try (QueryExecution qexec = QueryExecutionFactory.sparqlService(Wikidata.ENDPOINT, query)) {
			ResultSet results = qexec.execSelect();
			while(results.hasNext()) {
				QuerySolution solution = results.next();
				RDFNode pic = solution.get("pic");
				return pic.toString();
			}
		}
		return null;
	}
}
