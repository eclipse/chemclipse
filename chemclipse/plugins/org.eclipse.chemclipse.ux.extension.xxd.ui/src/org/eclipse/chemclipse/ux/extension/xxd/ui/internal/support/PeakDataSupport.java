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
import java.util.Set;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class PeakDataSupport {

	private DecimalFormat decimalFormatRetentionTime = ValueFormat.getDecimalFormatEnglish("0.0##");
	private IdentificationDataSupport identificationDataSupport = new IdentificationDataSupport();

	public String getPeakLabel(IPeak peak) {

		StringBuilder builder = new StringBuilder();
		if(peak != null) {
			builder.append("Peak");
			builder.append(" | ");
			builder.append("Start RT: ");
			builder.append(decimalFormatRetentionTime.format(peak.getPeakModel().getStartRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
			builder.append(" | ");
			builder.append("Stop RT: ");
			builder.append(decimalFormatRetentionTime.format(peak.getPeakModel().getStopRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
			builder.append(" | ");
			builder.append("Signal: ");
			builder.append((int)peak.getIntegratedArea());
		} else {
			builder.append("No peak has been selected yet.");
		}
		return builder.toString();
	}

	public ILibraryInformation getBestLibraryInformation(Set<IIdentificationTarget> targets) {

		return identificationDataSupport.getBestLibraryInformation(targets);
	}

	public IIdentificationTarget getBestPeakTarget(Set<IIdentificationTarget> targets) {

		return identificationDataSupport.getBestIdentificationTarget(targets);
	}
}
