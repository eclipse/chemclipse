/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class ChromatogramSupport {

	private static DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");

	private ChromatogramSupport() {
	}

	public static String getChromatogramLabel(IChromatogram chromatogram) {

		StringBuilder builder = new StringBuilder();
		if(chromatogram != null) {
			builder.append("Chromatogram: ");
			builder.append(chromatogram.getName());
		} else {
			builder.append("No chromatogram has been selected yet.");
		}
		return builder.toString();
	}

	public static String getChromatogramSelectionLabel(IChromatogramSelection chromatogramSelection) {

		StringBuilder builder = new StringBuilder();
		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram != null) {
				builder.append("Chromatogram: ");
				builder.append(chromatogram.getName());
				builder.append(" | ");
				builder.append("RT: ");
				builder.append(decimalFormat.format(chromatogramSelection.getStartRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
				builder.append(" - ");
				builder.append(decimalFormat.format(chromatogramSelection.getStopRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
			}
		} else {
			builder.append("No chromatogram has been selected yet.");
		}
		return builder.toString();
	}
}
