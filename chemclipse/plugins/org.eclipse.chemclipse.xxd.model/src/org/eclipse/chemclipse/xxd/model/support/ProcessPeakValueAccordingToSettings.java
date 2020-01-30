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

public class ProcessPeakValueAccordingToSettings {

	/**
	 * Applies selected filter criterion and proceeds with deleting or disabling
	 * the selected peaks according to selected filter option.
	 * <br>
	 * The selected filter criterion of type {@link ProcessPeaksByValueFilterSelectionCriterion}
	 * can have the value of smaller than minimum, bigger than maximum or not within range.
	 * <br>
	 * The selected filter treatment option of type {@link ValueFilterTreatmentOption}
	 * defines if the peak(s) will be deleted or disabled.
	 * <br><br>
	 * Can be used to process following peak values:
	 * <p><ul>
	 * <li>area
	 * <li>area%
	 * <li>leading
	 * <li>tailing
	 * <li>width
	 * </ul><p>
	 * 
	 * @param peakValue
	 * @param localSettings
	 * @param listener
	 * @param peak
	 */
	public static <X extends IPeak> void applySelectedOptions(double peakValue, ProcessPeaksByAreaFilterLocalSettings localSettings, CRUDListener<X, IPeakModel> listener, X peak) {

		switch (localSettings.getSelectionCriterion()) {
		case AREA_LESS_THAN_MINIMUM:
			if(Double.compare(peakValue, localSettings.getLowerLimit())<0) {
				deleteOrDisablePeak(listener, localSettings, peak);
			}
			break;
		case AREA_GREATER_THAN_MAXIMUM:
			if(Double.compare(peakValue, localSettings.getUpperLimit())>0) {
				deleteOrDisablePeak(listener, localSettings, peak);
			}
			break;
		case AREA_NOT_WITHIN_RANGE:
			if(Double.compare(peakValue, localSettings.getLowerLimit())<0 || Double.compare(peakValue, localSettings.getUpperLimit())>0) {
				deleteOrDisablePeak(listener, localSettings, peak);
			}
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Selection Criterion!");
		}
	}

	private static <X extends IPeak> void deleteOrDisablePeak(CRUDListener<X, IPeakModel> listener, ProcessPeaksByAreaFilterLocalSettings localSettings, X peak) {

		switch (localSettings.getTreatmentOption()) {
		case DELETE_PEAK:
			listener.delete(peak);
			break;
		case DEACTIVATE_PEAK:
			peak.setActiveForAnalysis(false);
			listener.updated(peak);
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}
}
