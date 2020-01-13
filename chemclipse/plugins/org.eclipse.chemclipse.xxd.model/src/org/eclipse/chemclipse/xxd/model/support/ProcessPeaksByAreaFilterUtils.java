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

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.xxd.model.filter.peaks.ProcessPeaksByIntegratedAreaFilterSettings;
import org.eclipse.chemclipse.xxd.model.filter.peaks.ProcessPeaksByPercentageAreaFilterSettings;

public class ProcessPeaksByAreaFilterUtils {

	public static double[] getIntegratedAreaLimits(ProcessPeaksByIntegratedAreaFilterSettings configuration) {

		if(configuration.getMinimumAreaValue()>configuration.getMaximumAreaValue()) {
			throw new IllegalArgumentException("Please check the values of the integrated area limits!");
		}
		return new double[] {configuration.getMinimumAreaValue(), configuration.getMaximumAreaValue()};
	}

	public static double[] getPercentageAreaLimits(ProcessPeaksByPercentageAreaFilterSettings configuration) {

		if(configuration.getMinimumPercentageAreaValue()>configuration.getMaximumPercentageAreaValue()) {
			throw new IllegalArgumentException("Please check the values of the percentage area limits!");
		}
		return new double[] {configuration.getMinimumPercentageAreaValue(), configuration.getMaximumPercentageAreaValue()};
	}

	public static <X extends IPeak> double calculateIntegratedAreaCompareValue(X peak, Collection<X> read) {

		return peak.getIntegratedArea();
	}

	public static <X extends IPeak> double calculatePercentageAreaCompareValue(X peak, Collection<X> read) {

		return (100 / calculateAreaSum(read)) * peak.getIntegratedArea();
	}

	private static <X extends IPeak> double calculateAreaSum(Collection<X> read) {

		double areaSum = 0;
		for(X peak : read) {
			areaSum = areaSum + peak.getIntegratedArea();
		}
		return areaSum;
	}

	public static void parseLocalSettings(ProcessPeaksByAreaFilterLocalSettings localSettings) {

		if(localSettings.getPercentageAreaFilterSettings()!=null) {
			localSettings.setAreaLimits(ProcessPeaksByAreaFilterUtils.getPercentageAreaLimits(localSettings.getPercentageAreaFilterSettings()));
			localSettings.setSelectionCriterion(localSettings.getPercentageAreaFilterSettings().getFilterSelectionCriterion());
			localSettings.setTreatmentOption(localSettings.getPercentageAreaFilterSettings().getFilterTreatmentOption());
		} else {
			localSettings.setAreaLimits(ProcessPeaksByAreaFilterUtils.getIntegratedAreaLimits(localSettings.getIntegratedAreaFilterSettings()));
			localSettings.setSelectionCriterion(localSettings.getIntegratedAreaFilterSettings().getFilterSelectionCriterion());
			localSettings.setTreatmentOption(localSettings.getIntegratedAreaFilterSettings().getFilterTreatmentOption());
		}
	}

	public static <X extends IPeak> void checkAreaAndFilterPeak(double area, ProcessPeaksByAreaFilterLocalSettings localSettings, CRUDListener<X, IPeakModel> listener, X peak) {

		switch (localSettings.getSelectionCriterion()) {
		case AREA_LESS_THAN_MINIMUM:
			if(Double.compare(area, localSettings.getAreaLimits()[0])<0) {
				deleteOrDisablePeak(listener, localSettings, peak);
			}
			break;
		case AREA_GREATER_THAN_MAXIMUM:
			if(Double.compare(area, localSettings.getAreaLimits()[1])>0) {
				deleteOrDisablePeak(listener, localSettings, peak);
			}
			break;
		case AREA_NOT_WITHIN_RANGE:
			if(Double.compare(area, localSettings.getAreaLimits()[0])<0 || Double.compare(area, localSettings.getAreaLimits()[1])>0) {
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
		case DISABLE_PEAK:
			peak.setActiveForAnalysis(false);
			listener.updated(peak);
			break;
		default:
			throw new IllegalArgumentException("Unsupported Peak Filter Treatment Option!");
		}
	}
}
