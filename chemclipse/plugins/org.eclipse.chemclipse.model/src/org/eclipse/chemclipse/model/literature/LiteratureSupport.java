/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.literature;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LiteratureSupport {

	public static final String LINE_DELIMITER = "\n";
	public static final String ENTTRY_DELIMITER = "  -";
	//
	private static final Map<String, String> RIS_IDENTIFIER_MAP = new HashMap<>();
	private static final String RIS_IDENTIFIER = "TY  -";
	private static final String RIS_END_RECORD = "ER";
	private static final String RIS_UNKNOWN = "Unknown";
	//
	static {
		RIS_IDENTIFIER_MAP.put("TY", "Reference");
		RIS_IDENTIFIER_MAP.put("A1", "Primary Authors");
		RIS_IDENTIFIER_MAP.put("A2", "Secondary Authors");
		RIS_IDENTIFIER_MAP.put("A3", "Tertiary Authors");
		RIS_IDENTIFIER_MAP.put("A4", "Subsidiary Authors");
		RIS_IDENTIFIER_MAP.put("AB", "Abstract");
		RIS_IDENTIFIER_MAP.put("AD", "Author Address");
		RIS_IDENTIFIER_MAP.put("AN", "Accession Number");
		RIS_IDENTIFIER_MAP.put("AU", "Author");
		RIS_IDENTIFIER_MAP.put("AV", "Location in Archives");
		RIS_IDENTIFIER_MAP.put("BT", "References");
		RIS_IDENTIFIER_MAP.put("C1", "Custom 1");
		RIS_IDENTIFIER_MAP.put("C2", "Custom 2");
		RIS_IDENTIFIER_MAP.put("C3", "Custom 3");
		RIS_IDENTIFIER_MAP.put("C4", "Custom 4");
		RIS_IDENTIFIER_MAP.put("C5", "Custom 5");
		RIS_IDENTIFIER_MAP.put("C6", "Custom 6");
		RIS_IDENTIFIER_MAP.put("C7", "Custom 7");
		RIS_IDENTIFIER_MAP.put("C8", "Custom 8");
		RIS_IDENTIFIER_MAP.put("CA", "Caption");
		RIS_IDENTIFIER_MAP.put("CN", "Call Number");
		RIS_IDENTIFIER_MAP.put("CP", "Characters");
		RIS_IDENTIFIER_MAP.put("CT", "Title Unpublished Reference");
		RIS_IDENTIFIER_MAP.put("CY", "Place Published");
		RIS_IDENTIFIER_MAP.put("DA", "Date");
		RIS_IDENTIFIER_MAP.put("DB", "Name of Database");
		RIS_IDENTIFIER_MAP.put("DO", "DOI");
		RIS_IDENTIFIER_MAP.put("DP", "Database Provider");
		RIS_IDENTIFIER_MAP.put("ED", "Editor");
		RIS_IDENTIFIER_MAP.put("EP", "End Page");
		RIS_IDENTIFIER_MAP.put("ET", "Edition");
		RIS_IDENTIFIER_MAP.put("ID", "Reference ID");
		RIS_IDENTIFIER_MAP.put("IS", "Issue Number");
		RIS_IDENTIFIER_MAP.put("J1", "Periodical Name");
		RIS_IDENTIFIER_MAP.put("J2", "Alternate Title");
		RIS_IDENTIFIER_MAP.put("JA", "Periodical Name");
		RIS_IDENTIFIER_MAP.put("JF", "Journal/Periodical");
		RIS_IDENTIFIER_MAP.put("JO", "Journal/Periodical");
		RIS_IDENTIFIER_MAP.put("KW", "Keywords");
		RIS_IDENTIFIER_MAP.put("L1", "Link to PDF");
		RIS_IDENTIFIER_MAP.put("L2", "Link to Fulltext");
		RIS_IDENTIFIER_MAP.put("L3", "Related Records");
		RIS_IDENTIFIER_MAP.put("L4", "Images");
		RIS_IDENTIFIER_MAP.put("LA", "Language");
		RIS_IDENTIFIER_MAP.put("LB", "Label");
		RIS_IDENTIFIER_MAP.put("LK", "Website Link");
		RIS_IDENTIFIER_MAP.put("M1", "Number");
		RIS_IDENTIFIER_MAP.put("M2", "Miscellaneous");
		RIS_IDENTIFIER_MAP.put("M3", "Type of Work");
		RIS_IDENTIFIER_MAP.put("N1", "Notes");
		RIS_IDENTIFIER_MAP.put("N2", "Abstract");
		RIS_IDENTIFIER_MAP.put("NV", "Number of Volumes");
		RIS_IDENTIFIER_MAP.put("OP", "Original Publication");
		RIS_IDENTIFIER_MAP.put("PB", "Publisher");
		RIS_IDENTIFIER_MAP.put("PP", "Publishing Place");
		RIS_IDENTIFIER_MAP.put("PY", "Publication Year");
		RIS_IDENTIFIER_MAP.put("RI", "Reviewed Item");
		RIS_IDENTIFIER_MAP.put("RN", "Research Notes");
		RIS_IDENTIFIER_MAP.put("RP", "Reprint Edition");
		RIS_IDENTIFIER_MAP.put("SE", "Section");
		RIS_IDENTIFIER_MAP.put("SN", "ISBN/ISSN");
		RIS_IDENTIFIER_MAP.put("SP", "Start Page");
		RIS_IDENTIFIER_MAP.put("ST", "Short Title");
		RIS_IDENTIFIER_MAP.put("T1", "Primary Title");
		RIS_IDENTIFIER_MAP.put("T2", "Secondary Title");
		RIS_IDENTIFIER_MAP.put("T3", "Tertiary Title");
		RIS_IDENTIFIER_MAP.put("TA", "Translated Author");
		RIS_IDENTIFIER_MAP.put("TI", "Title");
		RIS_IDENTIFIER_MAP.put("TT", "Translated Title");
		RIS_IDENTIFIER_MAP.put("U1", "User 1");
		RIS_IDENTIFIER_MAP.put("U2", "User 2");
		RIS_IDENTIFIER_MAP.put("U3", "User 3");
		RIS_IDENTIFIER_MAP.put("U4", "User 4");
		RIS_IDENTIFIER_MAP.put("U5", "User 5");
		RIS_IDENTIFIER_MAP.put("UR", "URL");
		RIS_IDENTIFIER_MAP.put("VL", "Volume Number");
		RIS_IDENTIFIER_MAP.put("VO", "Published Standard Number");
		RIS_IDENTIFIER_MAP.put("Y1", "Primary Date");
		RIS_IDENTIFIER_MAP.put("Y2", "Access Date");
		RIS_IDENTIFIER_MAP.put("ER", "End of Reference");
	}
	//
	private static final Pattern PATTERN_TITLE = Pattern.compile("(TI\\s+-\\s+|Title: )(.*?)(\n|[A-Z][A-Z])");
	private static final Pattern PATTERN_DOI = Pattern.compile("(https://doi.org/)(.*?)(\\s+)");
	private static final String PATTERN_KEY_RIS = "([A-Z])([A-Z]|[0-9])(  -)";

	/**
	 * https://en.wikipedia.org/wiki/RIS_(file_format)
	 */
	public static boolean isFormatRIS(String content) {

		if(content != null) {
			return content.startsWith(RIS_IDENTIFIER);
		}
		//
		return false;
	}

	public static String getFormattedRIS(String content, boolean mapDescriptions) {

		if(content != null) {
			String part = content.replaceAll(PATTERN_KEY_RIS, "\n$1$2$3");
			String[] values = part.split(LINE_DELIMITER);
			StringBuilder builder = new StringBuilder();
			for(String value : values) {
				String line = value.trim();
				if(!line.isEmpty()) {
					/*
					 * Map e.g. "TI" to "Title"
					 */
					if(mapDescriptions) {
						String[] entries = line.split(ENTTRY_DELIMITER);
						if(entries.length == 2) {
							String identifier = entries[0].trim();
							if(!isEndOfReferenceRIS(identifier)) {
								String key = LiteratureSupport.getMappedIdentifierRIS(identifier);
								String data = entries[1].trim();
								line = key + ": " + data;
							}
						}
					}
					//
					builder.append(line);
					builder.append(LINE_DELIMITER);
				}
			}
			return builder.toString();
		}
		//
		return "";
	}

	public static boolean isEndOfReferenceRIS(String identifier) {

		if(identifier != null) {
			return RIS_END_RECORD.equals(identifier);
		}
		//
		return false;
	}

	public static String getMappedIdentifierRIS(String identifier) {

		if(identifier != null) {
			return RIS_IDENTIFIER_MAP.getOrDefault(identifier, RIS_UNKNOWN);
		}
		//
		return RIS_UNKNOWN;
	}

	public static String getTitle(String content) {

		String title = "";
		if(content != null) {
			if(!content.isEmpty()) {
				Matcher matcher = PATTERN_TITLE.matcher(content);
				if(matcher.find()) {
					title = matcher.group(2).trim();
				}
			}
		}
		//
		return title;
	}

	/**
	 * This method tries to extract the URL of the first matched
	 * DOI (digital object identifier) according to the specification
	 * of the DOI Foundation:
	 * https://www.doi.org
	 */
	public static String getContainedDOI(String content) {

		String url = "";
		if(content != null) {
			if(!content.isEmpty()) {
				Matcher matcher = PATTERN_DOI.matcher(content);
				if(matcher.find()) {
					url = matcher.group().trim();
				}
			}
		}
		//
		return url;
	}
}