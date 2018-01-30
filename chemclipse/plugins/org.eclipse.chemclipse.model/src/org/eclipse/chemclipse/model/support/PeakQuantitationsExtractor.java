/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public class PeakQuantitationsExtractor {

	private PeakRetentionTimeComparator chromatogramPeakRetentionTimeComparator;

	public PeakQuantitationsExtractor() {
		chromatogramPeakRetentionTimeComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	}

	public PeakQuantitations extract(List<? extends IPeak> peaks) {

		PeakQuantitations peakQuantitations = new PeakQuantitations();
		//
		if(peaks != null) {
			/*
			 * Sort peak list by retention time.
			 */
			peaks = new ArrayList<>(peaks);
			Collections.sort(peaks, chromatogramPeakRetentionTimeComparator);
			//
			Set<String> quantitationColumns = new HashSet<String>();
			for(IPeak peak : peaks) {
				for(IQuantitationEntry quantitationEntry : peak.getQuantitationEntries()) {
					quantitationColumns.add(getIdentifier(quantitationEntry.getName(), quantitationEntry.getConcentrationUnit()));
				}
			}
			/*
			 * Add the titles.
			 */
			peakQuantitations.getTitles().add("RT (Minutes)");
			peakQuantitations.getTitles().add("Area");
			for(String quantitationColumn : quantitationColumns) {
				peakQuantitations.getTitles().add(quantitationColumn);
			}
			/*
			 * Add the concentrations.
			 */
			for(IPeak peak : peaks) {
				PeakQuantitation peakQuantitationEntry = new PeakQuantitation();
				peakQuantitationEntry.setRetentionTime(peak.getPeakModel().getRetentionTimeAtPeakMaximum());
				peakQuantitationEntry.setIntegratedArea(peak.getIntegratedArea());
				for(String quantitationColumn : quantitationColumns) {
					peakQuantitationEntry.getConcentrations().add(getConcentration(peak, quantitationColumn));
				}
				peakQuantitations.getQuantitationEntries().add(peakQuantitationEntry);
			}
		}
		return peakQuantitations;
	}

	private double getConcentration(IPeak peak, String quantitationColumn) {

		double concentration = 0.0d;
		exitloop:
		for(IQuantitationEntry quantitationEntry : peak.getQuantitationEntries()) {
			if(quantitationColumn.equals(getIdentifier(quantitationEntry.getName(), quantitationEntry.getConcentrationUnit()))) {
				concentration = quantitationEntry.getConcentration();
				break exitloop;
			}
		}
		return concentration;
	}

	private String getIdentifier(final String name, String concentrationUnit) {

		return name + " [" + concentrationUnit + "]";
	}
}
