/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.support;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.xxd.model.filter.peaks.ProcessPeaksByAsymmetricalPeakShapeFilterSettings;

public class ProcessPeaksByAsymmetricalPeakShapeFilterUtils {

	public static <X extends IPeak> void checkShapeAndFilterPeak(ProcessPeaksByAsymmetricalPeakShapeFilterSettings settings, CRUDListener<X, IPeakModel> listener, X peak) {

		switch (settings.getFilterSelectionCriterion()){
		case LEADING_GREATER_THAN_LIMIT:
			if(Double.compare(peak.getPeakModel().getLeading(), settings.getLeadingValue())>0) {
				deleteOrDisablePeak(listener, settings, peak);
			}
			break;
		case TAILING_GREATER_THAN_LIMIT:
			if(Double.compare(peak.getPeakModel().getTailing(), settings.getTailingValue())>0) {
				deleteOrDisablePeak(listener, settings, peak);
			}
			break;
		case PEAK_ASYMMETRY_FACTOR_GREATER_THAN_LIMIT:
			double  peakAsymmetryFactor = peak.getPeakModel().getTailing()/peak.getPeakModel().getLeading();
			if(Double.compare(peakAsymmetryFactor, settings.getPeakAsymmetryFactor())>0) {
				deleteOrDisablePeak(listener, settings, peak);
			}
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Selection Criterion!");
		}
	}

	private static <X extends IPeak> void deleteOrDisablePeak(CRUDListener<X, IPeakModel> listener, ProcessPeaksByAsymmetricalPeakShapeFilterSettings settings, X peak) {

		switch (settings.getFilterTreatmentOption()) {
		case DELETE_PEAK:
			listener.delete(peak);
			break;
		case DISABLE_PEAK:
			peak.setActiveForAnalysis(false);
			listener.updated(peak);
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}
}