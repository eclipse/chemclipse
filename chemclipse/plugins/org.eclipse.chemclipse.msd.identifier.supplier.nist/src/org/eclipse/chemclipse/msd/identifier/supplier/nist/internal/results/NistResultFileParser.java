/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.logging.core.Logger;

public class NistResultFileParser {

	private static final Logger logger = Logger.getLogger(NistResultFileParser.class);
	private static final String DELIMITER = "\n";
	private static final String COMPOUND_PATTERN = "(Unknown:.*)(Compound in Library Factor)( = )(.*?\\n)((Hit.*\\n)*)";
	private static final String IDENTIFIER_PATTERN = "(Unknown:)(\\s*)(.*)(\\s*)";
	private static final String HIT_PATTERN = "(Hit.*?)(\\n)";
	private static final String NAME_FORMULA_PATTERN = "(<<)(.*?)(>>;<<)(.*?)(>>)";
	private static final String MATCH_FACTOR_PATTERN = "(MF:\\s*)(.*?)(;\\s*RMF:\\s*)(.*?)(;\\s*Prob:\\s*)(.*?)(;)";
	private static final String CAS_PATTERN = "(CAS:\\s*)(.*?)(;)";
	private static final String LIB_PATTERN = "(Mw:\\s*)(.*)(;\\s*Lib:.*<<)(.*)(>>;\\s*Id:\\s*)(.*)(\\.)";
	//
	private static final String DEFAULT_ID = "-1";
	private static final String DEFAULT_RI = "0";
	private static final String RI_MARKER = "; RI:";
	//
	private Pattern compoundPattern;
	private Pattern identifierPattern;
	private Pattern hitPattern;
	private Pattern nameFormulaPattern;
	private Pattern matchFactorPattern;
	private Pattern casPattern;
	private Pattern libPattern;

	public NistResultFileParser() {

		compoundPattern = Pattern.compile(COMPOUND_PATTERN);
		identifierPattern = Pattern.compile(IDENTIFIER_PATTERN);
		hitPattern = Pattern.compile(HIT_PATTERN);
		nameFormulaPattern = Pattern.compile(NAME_FORMULA_PATTERN);
		matchFactorPattern = Pattern.compile(MATCH_FACTOR_PATTERN);
		casPattern = Pattern.compile(CAS_PATTERN);
		libPattern = Pattern.compile(LIB_PATTERN);
	}

	/**
	 * Extracts the compounds of the result file (SRCRESLT.TXT) or
	 * (NISTLOG.TXT).
	 * 
	 * @param resultFile
	 * @return {@link Compounds}
	 */
	public Compounds getCompounds(File results) {

		return extractCompounds(getResultFileContent(results));
	}

	/**
	 * Get the content of the result file as a string. The lines are delimited
	 * by the given DELIMITER ("\n").
	 */
	private String getResultFileContent(File results) {

		String content = "";
		/*
		 * Read the file as ISO-8859-1 (latin 1) to avoid crappy chars.
		 */
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(results), "ISO8859_1"))) {
			String line;
			StringBuilder builder = new StringBuilder();
			while((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(DELIMITER);
			}
			content = builder.toString();
		} catch(IOException e) {
			logger.warn(e);
		}
		//
		return content;
	}

	/**
	 * Extracts the compounds.
	 * 
	 * @param input
	 * @return {@link Compounds}
	 */
	private Compounds extractCompounds(String input) {

		Compounds compounds = new Compounds();
		/*
		 * Find the compounds.
		 */
		Matcher matcher = compoundPattern.matcher(input);
		while(matcher.find()) {
			/*
			 * Each compound could have more than one hit.
			 */
			Compound compound = new Compound();
			extractAndAddIdentifier(matcher.group(1), compound); // ID of the peak
			extractAndAddInLibFactor(matcher.group(4), compound); // InLib factor
			extractAndAddHits(matcher.group(5), compound); // Hits
			compounds.add(compound);
		}
		//
		return compounds;
	}

	private void extractAndAddIdentifier(String input, Compound compound) {

		/*
		 * Find the hits.
		 */
		Matcher matcher = identifierPattern.matcher(input);
		while(matcher.find()) {
			String identifier = matcher.group(3).trim();
			compound.setIdentifier(identifier);
		}
	}

	private void extractAndAddInLibFactor(String input, Compound compound) {

		if(input != null) {
			compound.setCompoundInLibraryFactor(input.replace("\n", ""));
		}
	}

	/**
	 * Extracts and adds the hits to the given compound.
	 * 
	 * @param input
	 * @param compound
	 */
	private void extractAndAddHits(String input, Compound compound) {

		Hit hit;
		/*
		 * Find the hits.
		 */
		Matcher matcher = hitPattern.matcher(input);
		while(matcher.find()) {
			hit = extractHit(matcher.group(1));
			compound.add(hit);
		}
	}

	/**
	 * Extracts and adds the hit to the given compound.
	 * 
	 * @param content
	 * @param compound
	 */
	private Hit extractHit(String input) {

		Hit hit = new Hit();
		addNameAndFormula(input, hit);
		addMatchFactor(input, hit);
		addCAS(input, hit);
		addLib(input, hit);
		return hit;
	}

	/**
	 * Adds the name and formula.
	 * 
	 * @param input
	 * @param hit
	 */
	private void addNameAndFormula(String input, Hit hit) {

		Matcher matcher = nameFormulaPattern.matcher(input);
		if(matcher.find()) {
			hit.setName(matcher.group(2));
			hit.setFormula(matcher.group(4));
		}
	}

	/**
	 * Adds the match, reverse match factor and the probability.
	 * 
	 * @param input
	 * @param hit
	 */
	private void addMatchFactor(String input, Hit hit) {

		Matcher matcher = matchFactorPattern.matcher(input);
		if(matcher.find()) {
			hit.setMatchFactor(Integer.valueOf(matcher.group(2)) / 10.0f); // cause it is 1000 based in the results file
			hit.setReverseMatchFactor(Integer.valueOf(matcher.group(4)) / 10.0f); // cause it is 1000 based in the results file
			hit.setProbability(Float.valueOf(matcher.group(6)));
		}
	}

	/**
	 * Adds the cas number.
	 * 
	 * @param input
	 * @param hit
	 */
	private void addCAS(String input, Hit hit) {

		Matcher matcher = casPattern.matcher(input);
		if(matcher.find()) {
			hit.setCAS(matcher.group(2));
		}
	}

	/**
	 * Set the mw, lib, id and ri.
	 * 
	 * @param input
	 * @param hit
	 */
	private void addLib(String input, Hit hit) {

		Matcher matcher = libPattern.matcher(input);
		if(matcher.find()) {
			hit.setMolecularWeight(Integer.valueOf(matcher.group(2)));
			hit.setLib(matcher.group(4));
			String value = matcher.group(6); // NIST17 -> "4178; RI: 0"
			//
			String id = DEFAULT_ID;
			String ri = DEFAULT_RI;
			//
			if(value.contains(RI_MARKER)) {
				String[] values = value.split(RI_MARKER);
				id = (values.length > 0) ? values[0].trim() : DEFAULT_ID;
				ri = (values.length > 1) ? values[1].trim() : DEFAULT_RI;
			} else {
				id = value;
			}
			/*
			 * ID
			 */
			try {
				hit.setId(Integer.valueOf(id));
			} catch(NumberFormatException e) {
				logger.warn(e);
			}
			/*
			 * RI
			 */
			try {
				hit.setRetentionIndex(Integer.valueOf(ri));
			} catch(NumberFormatException e) {
				logger.warn(e);
			}
		}
	}
}
