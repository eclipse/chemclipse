/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.InternalStandard;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.util.ValueParserSupport;

public class PeakQuantifierSupport {

	private static final Logger logger = Logger.getLogger(PeakQuantifierSupport.class);
	//
	private static final String MARKER_COMPENSATION = "x";
	private static final String PATTERN_DECIMAL_FORMAT = "0.#####";

	/**
	 * Parses e.g. 20 mg/g x1.0.
	 * May return null if concentration is not valid.
	 * 
	 * @param name
	 * @param value
	 * @return {@link IInternalStandard}a
	 */
	public static IInternalStandard getInternalStandard(String name, String value) {

		IInternalStandard internalStandard = null;
		//
		if(name != null && value != null) {
			if(!name.isBlank()) {
				String[] parts = value.trim().split(" ");
				if(parts.length >= 2) {
					ValueParserSupport valueParserSupport = new ValueParserSupport();
					double concentration = valueParserSupport.parseDouble(parts, 0, 0);
					String unit = valueParserSupport.parseString(parts, 1, "");
					String compensation = valueParserSupport.parseString(parts, 2, ""); // x10.0
					double compensationFactor = 1.0d;
					if(compensation.startsWith(MARKER_COMPENSATION)) {
						try {
							compensationFactor = Double.parseDouble(compensation.replace(MARKER_COMPENSATION, ""));
						} catch(Exception e) {
							logger.warn(e);
						}
					}
					//
					if(concentration > 0 && !unit.isBlank()) {
						internalStandard = new InternalStandard(name, concentration, unit, compensationFactor);
					}
				}
			}
		}
		//
		return internalStandard;
	}

	/**
	 * Returns e.g.:
	 * 20 mg/g x1.0
	 * or
	 * 20 mg/g [*4] - if the peak contains 4 internal standards
	 * 
	 * @param object
	 * @return String
	 */
	public static String getInternalStandardConcentrations(Object object) {

		String concentrations = "";
		if(object instanceof IPeak peak) {
			List<IInternalStandard> internalStandards = new ArrayList<>(peak.getInternalStandards());
			if(!internalStandards.isEmpty()) {
				/*
				 * Sort internal standards by name
				 */
				Collections.sort(internalStandards, (s1, s2) -> s1.getName().compareTo(s2.getName()));
				IInternalStandard internalStandard = internalStandards.get(0);
				//
				StringBuilder builder = new StringBuilder();
				builder.append(ValueFormat.getDecimalFormatEnglish(PATTERN_DECIMAL_FORMAT).format(internalStandard.getConcentration()));
				builder.append(" ");
				builder.append(internalStandard.getConcentrationUnit());
				builder.append(" ");
				builder.append(MARKER_COMPENSATION);
				builder.append(ValueFormat.getDecimalFormatEnglish(PATTERN_DECIMAL_FORMAT).format(internalStandard.getCompensationFactor()));
				//
				int size = internalStandards.size();
				if(size > 1) {
					builder.append(" [*");
					builder.append(size);
					builder.append("]");
				}
				//
				concentrations = builder.toString();
			}
		}
		//
		return concentrations;
	}

	/**
	 * Returns e.g.:
	 * 20 mg/g
	 * or
	 * 20 mg/g [*4] - if the peak contains 4 quantitation results
	 * 
	 * @param object
	 * @return String
	 */
	public static String getPeakConcentrations(Object object) {

		String concentrations = "";
		if(object instanceof IPeak peak) {
			List<IQuantitationEntry> quantitationEntries = new ArrayList<>(peak.getQuantitationEntries());
			if(!quantitationEntries.isEmpty()) {
				/*
				 * Sort concentrations by name
				 */
				Collections.sort(quantitationEntries, (q1, q2) -> q1.getName().compareTo(q2.getName()));
				IQuantitationEntry quantitationEntry = quantitationEntries.get(0);
				//
				StringBuilder builder = new StringBuilder();
				builder.append(ValueFormat.getDecimalFormatEnglish(PATTERN_DECIMAL_FORMAT).format(quantitationEntry.getConcentration()));
				builder.append(" ");
				builder.append(quantitationEntry.getConcentrationUnit());
				int size = quantitationEntries.size();
				if(size > 1) {
					builder.append(" [*");
					builder.append(size);
					builder.append("]");
				}
				//
				concentrations = builder.toString();
			}
		}
		//
		return concentrations;
	}
}