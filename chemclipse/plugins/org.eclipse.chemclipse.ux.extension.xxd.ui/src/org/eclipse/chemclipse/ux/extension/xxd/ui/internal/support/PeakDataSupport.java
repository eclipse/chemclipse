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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class PeakDataSupport {

	private DecimalFormat decimalFormatRetentionTime = ValueFormat.getDecimalFormatEnglish("0.0##");
	private TargetExtendedComparator targetExtendedComparator;

	public PeakDataSupport() {
		targetExtendedComparator = new TargetExtendedComparator(SortOrder.DESC);
	}

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

	public ILibraryInformation getBestLibraryInformation(List<IPeakTarget> targets) {

		ILibraryInformation libraryInformation = null;
		IPeakTarget peakTarget = getBestPeakTarget(targets);
		if(peakTarget != null) {
			libraryInformation = peakTarget.getLibraryInformation();
		}
		return libraryInformation;
	}

	public IPeakTarget getBestPeakTarget(List<IPeakTarget> targets) {

		IPeakTarget peakTarget = null;
		List<IPeakTarget> peakTargets = new ArrayList<IPeakTarget>(targets);
		Collections.sort(peakTargets, targetExtendedComparator);
		if(targets.size() >= 1) {
			peakTarget = targets.get(0);
		}
		return peakTarget;
	}
}
