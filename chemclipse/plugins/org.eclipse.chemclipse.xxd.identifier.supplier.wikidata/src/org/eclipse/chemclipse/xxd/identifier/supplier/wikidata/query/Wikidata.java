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

public class Wikidata {

	public static final String ENDPOINT = "https://query.wikidata.org/sparql";
	//
	public static final String PROP = "PREFIX p: <http://www.wikidata.org/prop/>\n";
	public static final String STATEMENT = "PREFIX ps: <http://www.wikidata.org/prop/statement/>\n";
	public static final String WIKIBASE = "PREFIX wikibase: <http://wikiba.se/ontology#>\n";
	public static final String BIGDATA = "PREFIX bd: <http://www.bigdata.com/rdf#>\n";
	public static final String WD = "PREFIX wd: <http://www.wikidata.org/entity/>\n";
	public static final String WDT = "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n";
	public static final String RDFS = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n";
	public static final String SKOS = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n";
	public static final String SCHEMA = "PREFIX schema: <http://schema.org/>\n";
}
