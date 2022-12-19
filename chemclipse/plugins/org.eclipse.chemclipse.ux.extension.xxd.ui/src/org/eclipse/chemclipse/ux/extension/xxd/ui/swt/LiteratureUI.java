/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

/*
 * Description of the RIS format.
 * https://en.wikipedia.org/wiki/RIS_(file_format)
 */
public class LiteratureUI extends StyledText {

	private static final String LINE_DELIMITER = OperatingSystemUtils.getLineDelimiter();
	private static final String RIS_IDENTIFIER = "TY  -";
	private static final String RIS_END_RECORD = "ER";
	private static final String KEY_RIS = "([A-Z])([A-Z]|[0-9])(  -)";
	//
	private Map<String, String> identifierMap = new HashMap<>();

	public LiteratureUI(Composite parent, int style) {

		super(parent, style);
		initialize();
	}

	@Override
	public void setText(String text) {

		if(isFormatRIS(text)) {
			formatRIS(text);
		} else {
			super.setText(text);
		}
	}

	private void formatRIS(String text) {

		StringBuilder builder = new StringBuilder();
		List<StyleRange> styleRanges = new ArrayList<>();
		//
		String content = text.replaceAll(KEY_RIS, "\n$1$2$3");
		String[] values = content.split("\n");
		int offset = 0;
		for(String value : values) {
			String[] entries = value.split("  -");
			if(entries.length == 2) {
				String identifier = entries[0].trim();
				if(!RIS_END_RECORD.equals(identifier)) {
					String key = identifierMap.getOrDefault(identifier, "Unknown");
					String data = entries[1].trim();
					offset = append(builder, styleRanges, offset, key, data);
				}
			}
		}
		//
		super.setText(builder.toString());
		super.setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
	}

	private int append(StringBuilder builder, List<StyleRange> styleRanges, int offset, String key, String value) {

		builder.append(key);
		builder.append(": ");
		builder.append(value);
		builder.append(LINE_DELIMITER);
		//
		StyleRange styleRange = new StyleRange();
		styleRange.start = offset;
		styleRange.length = key.length();
		styleRange.fontStyle = SWT.BOLD;
		styleRange.foreground = Colors.DARK_RED;
		styleRanges.add(styleRange);
		//
		return offset + key.length() + 2 + value.length() + LINE_DELIMITER.length();
	}

	private boolean isFormatRIS(String text) {

		return text.trim().startsWith(RIS_IDENTIFIER);
	}

	private void initialize() {

		identifierMap.put("TY", "Reference");
		identifierMap.put("A1", "Primary Authors");
		identifierMap.put("A2", "Secondary Authors");
		identifierMap.put("A3", "Tertiary Authors");
		identifierMap.put("A4", "Subsidiary Authors");
		identifierMap.put("AB", "Abstract");
		identifierMap.put("AD", "Author Address");
		identifierMap.put("AN", "Accession Number");
		identifierMap.put("AU", "Author");
		identifierMap.put("AV", "Location in Archives");
		identifierMap.put("BT", "References");
		identifierMap.put("C1", "Custom 1");
		identifierMap.put("C2", "Custom 2");
		identifierMap.put("C3", "Custom 3");
		identifierMap.put("C4", "Custom 4");
		identifierMap.put("C5", "Custom 5");
		identifierMap.put("C6", "Custom 6");
		identifierMap.put("C7", "Custom 7");
		identifierMap.put("C8", "Custom 8");
		identifierMap.put("CA", "Caption");
		identifierMap.put("CN", "Call Number");
		identifierMap.put("CP", "Characters");
		identifierMap.put("CT", "Title Unpublished Reference");
		identifierMap.put("CY", "Place Published");
		identifierMap.put("DA", "Date");
		identifierMap.put("DB", "Name of Database");
		identifierMap.put("DO", "DOI");
		identifierMap.put("DP", "Database Provider");
		identifierMap.put("ED", "Editor");
		identifierMap.put("EP", "End Page");
		identifierMap.put("ET", "Edition");
		identifierMap.put("ID", "Reference ID");
		identifierMap.put("IS", "Issue Number");
		identifierMap.put("J1", "Periodical Name");
		identifierMap.put("J2", "Alternate Title");
		identifierMap.put("JA", "Periodical Name");
		identifierMap.put("JF", "Journal/Periodical");
		identifierMap.put("JO", "Journal/Periodical");
		identifierMap.put("KW", "Keywords");
		identifierMap.put("L1", "Link to PDF");
		identifierMap.put("L2", "Link to Fulltext");
		identifierMap.put("L3", "Related Records");
		identifierMap.put("L4", "Images");
		identifierMap.put("LA", "Language");
		identifierMap.put("LB", "Label");
		identifierMap.put("LK", "Website Link");
		identifierMap.put("M1", "Number");
		identifierMap.put("M2", "Miscellaneous");
		identifierMap.put("M3", "Type of Work");
		identifierMap.put("N1", "Notes");
		identifierMap.put("N2", "Abstract");
		identifierMap.put("NV", "Number of Volumes");
		identifierMap.put("OP", "Original Publication");
		identifierMap.put("PB", "Publisher");
		identifierMap.put("PP", "Publishing Place");
		identifierMap.put("PY", "Publication year");
		identifierMap.put("RI", "Reviewed Item");
		identifierMap.put("RN", "Research Notes");
		identifierMap.put("RP", "Reprint Edition");
		identifierMap.put("SE", "Section");
		identifierMap.put("SN", "ISBN/ISSN");
		identifierMap.put("SP", "Start Page");
		identifierMap.put("ST", "Short Title");
		identifierMap.put("T1", "Primary Title");
		identifierMap.put("T2", "Secondary Title");
		identifierMap.put("T3", "Tertiary Title");
		identifierMap.put("TA", "Translated Author");
		identifierMap.put("TI", "Title");
		identifierMap.put("TT", "Translated Title");
		identifierMap.put("U1", "User 1");
		identifierMap.put("U2", "User 2");
		identifierMap.put("U3", "User 3");
		identifierMap.put("U4", "User 4");
		identifierMap.put("U5", "User 5");
		identifierMap.put("UR", "URL");
		identifierMap.put("VL", "Volume number");
		identifierMap.put("VO", "Published Standard Number");
		identifierMap.put("Y1", "Primary Date");
		identifierMap.put("Y2", "Access Date ");
		identifierMap.put("ER", "End of Reference");
	}
}