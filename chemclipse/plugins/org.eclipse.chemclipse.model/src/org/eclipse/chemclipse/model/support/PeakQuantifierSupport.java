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
package org.eclipse.chemclipse.model.support;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class PeakQuantifierSupport {

	/**
	 * Returns e.g.:
	 * 20 mg/g
	 * 
	 * @param object
	 * @return String
	 */
	public static String getInternalStandardConcentration(Object object) {

		return getInternalStandardConcentrations(object, false);
	}

	/**
	 * Returns e.g.:
	 * 20 mg/g
	 * or
	 * 20 mg/g [*4] - if the peak contains 4 internal standards
	 * 
	 * @param object
	 * @return String
	 */
	public static String getInternalStandardConcentrations(Object object) {

		return getInternalStandardConcentrations(object, true);
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

		if(object instanceof IPeak peak) {
			DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0000");
			List<IQuantitationEntry> quantitationEntries = new ArrayList<>(peak.getQuantitationEntries());
			if(!quantitationEntries.isEmpty()) {
				Collections.sort(quantitationEntries, (q1, q2) -> q1.getName().compareTo(q2.getName()));
				IQuantitationEntry quantitationEntry = quantitationEntries.get(0);
				StringBuilder builder = new StringBuilder();
				//
				builder.append(decimalFormat.format(quantitationEntry.getConcentration()));
				builder.append(" ");
				builder.append(quantitationEntry.getConcentrationUnit());
				int size = quantitationEntries.size();
				if(size > 1) {
					builder.append(" [*");
					builder.append(size);
					builder.append(" ]");
				}
				//
				return builder.toString();
			}
		}
		//
		return "";
	}

	private static String getInternalStandardConcentrations(Object object, boolean showSizeMarker) {

		if(object instanceof IPeak peak) {
			List<IInternalStandard> internalStandards = new ArrayList<>(peak.getInternalStandards());
			if(!internalStandards.isEmpty()) {
				Collections.sort(internalStandards, (s1, s2) -> s1.getName().compareTo(s2.getName()));
				IInternalStandard internalStandard = internalStandards.get(0);
				StringBuilder builder = new StringBuilder();
				//
				builder.append(internalStandard.getConcentration());
				builder.append(" ");
				builder.append(internalStandard.getConcentrationUnit());
				//
				if(showSizeMarker) {
					int size = internalStandards.size();
					if(size > 1) {
						builder.append(" [*");
						builder.append(size);
						builder.append(" ]");
					}
				}
				//
				return builder.toString();
			}
		}
		//
		return "";
	}
}