/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.internal.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.IIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.ISettingStatus;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.PeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.PeakIntegrationResults;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IMarkedTrace;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.implementation.IntegrationEntry;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.support.IIonPercentages;
import org.eclipse.chemclipse.msd.model.core.support.IonPercentages;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakMaxPeakIntegrator implements IPeakMaxPeakIntegrator {

	@Override
	public IPeakIntegrationResult integrate(IPeak peak, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

		validatePeak(peak);
		validateSettings(peakIntegrationSettings);
		boolean useAreaConstraint = true;
		if(peakIntegrationSettings instanceof PeakIntegrationSettings settings) {
			useAreaConstraint = settings.isUseAreaConstraint();
		}
		ISettingStatus settingStatus;
		PeakIntegrationResult result = null;
		List<IIntegrationEntry> integrationEntries = calculateIntegratedArea(peak, peakIntegrationSettings.getMarkedTraces(), useAreaConstraint);
		peak.setIntegratedArea(integrationEntries, IPeakMaxPeakIntegrator.INTEGRATOR_DESCRIPTION);
		/*
		 * Get the peak area if the peak should be reported.
		 */
		settingStatus = peakIntegrationSettings.getSettingStatus(peak);
		if(settingStatus.report()) {
			double integratedArea = calculateIntegratedArea(integrationEntries);
			result = getPeakIntegrationResult(peak, integratedArea, peakIntegrationSettings);
		}
		return result;
	}

	@Override
	public IPeakIntegrationResults integrate(List<? extends IPeak> peaks, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

		validatePeakList(peaks);
		validateSettings(peakIntegrationSettings);
		IPeakIntegrationResult peakIntegrationResult;
		IPeakIntegrationResult peakIntegrationSumResult = null;
		ISettingStatus settingStatus;
		PeakIntegrationResults peakIntegrationResults = new PeakIntegrationResults();
		int peakNumber = 1;
		/*
		 * Iterate through all peaks and decide if they should be reported.
		 */
		for(IPeak peak : peaks) {
			monitor.subTask("Integrate Peak " + peakNumber++);
			try {
				peakIntegrationResult = integrate(peak, peakIntegrationSettings, monitor);
			} catch(ValueMustNotBeNullException e) {
				/*
				 * Do a break here and integrate nothing. Go on with the next
				 * peak.
				 */
				continue;
			}
			settingStatus = peakIntegrationSettings.getSettingStatus(peak);
			/*
			 * Should the peak be reported or not.
			 */
			if(settingStatus.report()) {
				/*
				 * Should the area sum be taken?
				 */
				if(settingStatus.sumOn()) {
					/*
					 * Create a new object.
					 */
					if(peakIntegrationSumResult == null) {
						peakIntegrationSumResult = peakIntegrationResult;
						resetIntegrationSumResultValues(peakIntegrationSumResult);
					} else {
						peakIntegrationSumResult.setStopRetentionTime(peakIntegrationResult.getStopRetentionTime());
						double integratedArea = peakIntegrationSumResult.getIntegratedArea() + peakIntegrationResult.getIntegratedArea();
						peakIntegrationSumResult.setIntegratedArea(integratedArea);
					}
				} else {
					/*
					 * Add the summed peak result.
					 */
					if(peakIntegrationSumResult != null) {
						peakIntegrationResults.add(peakIntegrationSumResult);
						peakIntegrationSumResult = null;
					}
					peakIntegrationResults.add(peakIntegrationResult);
				}
			}
		}
		/*
		 * Add the summed peak result, if there is some at the end.
		 */
		if(peakIntegrationSumResult != null) {
			peakIntegrationResults.add(peakIntegrationSumResult);
		}
		return peakIntegrationResults;
	}

	/**
	 * Checks that the peak list is not null.
	 * 
	 * @param peaks
	 * @throws ValueMustNotBeNullException
	 */
	private void validatePeakList(List<? extends IPeak> peaks) throws ValueMustNotBeNullException {

		if(peaks == null) {
			throw new ValueMustNotBeNullException("The peak list must not be null.");
		}
	}

	/**
	 * Checks that the peak is not null.
	 * 
	 * @param peak
	 * @throws ValueMustNotBeNullException
	 */
	private void validatePeak(IPeak peak) throws ValueMustNotBeNullException {

		if(peak == null) {
			throw new ValueMustNotBeNullException("The peak instance must not be null.");
		}
		if(peak.getPeakModel() == null) {
			throw new ValueMustNotBeNullException("The peak model must not be null.");
		}
	}

	/**
	 * Checks that the integration settings are not null.
	 * 
	 * @param integrationSettings
	 * @throws ValueMustNotBeNullException
	 */
	private void validateSettings(IIntegrationSettings integrationSettings) throws ValueMustNotBeNullException {

		if(integrationSettings == null) {
			throw new ValueMustNotBeNullException("The integration settings must not be null.");
		}
	}

	/**
	 * Calculates the integrated peak area.
	 * 
	 * @return List<IIntegrationEntry>
	 */
	private List<IIntegrationEntry> calculateIntegratedArea(IPeak peak, IMarkedTraces<IMarkedTrace> markedTraces, boolean useAreaConstraint) {

		List<IIntegrationEntry> integrationEntries = new ArrayList<>();
		IIntegrationEntry integrationEntry;
		//
		IPeakModel peakModel = peak.getPeakModel();
		IScan scan = peakModel.getPeakMaximum();
		double integratedAreaTIC = calculateTICPeakArea(peak, useAreaConstraint);
		Set<Integer> selectedIonsNominal = markedTraces.getTraces();
		/*
		 * Use the selected ions if:<br/> the size is greater 0
		 * (means, ions have been selected)<br/> and<br/> the selected
		 * ions does not contain IIon.TIC_Ion, which means,
		 * the TIC signal should be integrated.
		 */
		if(!selectedIonsNominal.isEmpty() && !selectedIonsNominal.contains(AbstractIon.getIon(IIon.TIC_ION)) && scan instanceof IScanMSD scanMSD) {
			IIonPercentages ionPercentages = new IonPercentages(scanMSD);
			/*
			 * Calculate the percentage integrated area for each selected ion.
			 */
			for(Integer ion : selectedIonsNominal) {
				float correctionFactor = ionPercentages.getPercentage(ion) / IIonPercentages.MAX_PERCENTAGE;
				double integratedArea = integratedAreaTIC * correctionFactor;
				integrationEntry = new IntegrationEntry(ion, integratedArea);
				integrationEntries.add(integrationEntry);
			}
		} else {
			integrationEntry = new IntegrationEntry(integratedAreaTIC);
			integrationEntries.add(integrationEntry);
		}
		return integrationEntries;
	}

	/**
	 * Calculates the TIC (total ion chromatogram) peak area.<br/>
	 * 
	 * @param peak
	 * @return double
	 */
	private double calculateTICPeakArea(IPeak peak, boolean useAreaConstraint) {

		IPeakModel peakModel = peak.getPeakModel();
		/*
		 * Integrates the max scan of the peak model.
		 * There is currently no option to support the settings like "baseline hold".
		 * In case it is needed, please have a look at the Integrator Trapezoid.
		 */
		double integratedArea = peakModel.getPeakMaximum().getTotalSignal();
		/*
		 * We had a check before, that the area must not be < 1.
		 * Especially when handling HPLC-DAD files, it could be lower than 1.
		 * Hence, this check has been revised and set to the constraint, that
		 * no negative areas are allowed.
		 */
		double minArea = useAreaConstraint ? 1.0d : 0.0d;
		if(integratedArea < minArea) {
			integratedArea = 0.0d;
		}
		//
		return integratedArea;
	}

	/**
	 * Returns a valid PeakMaxPeakIntegrationResult
	 */
	private PeakIntegrationResult getPeakIntegrationResult(IPeak peak, double integratedArea, IPeakIntegrationSettings peakIntegrationSettings) {

		/*
		 * Selected ions.
		 */
		IMarkedTraces<IMarkedTrace> selectedIons = peakIntegrationSettings.getMarkedTraces();
		Set<Integer> integratedIons = getIntegratedIons(selectedIons);
		PeakIntegrationResult result = new PeakIntegrationResult();
		result.setIntegratedArea(integratedArea);
		result.setIntegratorType(IPeakMaxPeakIntegrator.INTEGRATOR_DESCRIPTION);
		result.setPeakType(peak.getPeakType().toString());
		/*
		 * Specific values.
		 */
		float purity = 0.0f;
		float sn = 0.0f;
		if(peak instanceof IChromatogramPeakMSD chromatogramPeak) {
			purity = chromatogramPeak.getPurity();
			sn = chromatogramPeak.getSignalToNoiseRatio();
		} else if(peak instanceof IChromatogramPeakCSD chromatogramPeak) {
			sn = chromatogramPeak.getSignalToNoiseRatio();
		} else if(peak instanceof IChromatogramPeakWSD chromatogramPeak) {
			purity = chromatogramPeak.getPurity();
			sn = chromatogramPeak.getSignalToNoiseRatio();
		}
		result.setPurity(purity);
		result.setSN(sn);
		//
		result.setStartRetentionTime(peak.getPeakModel().getStartRetentionTime());
		result.setStopRetentionTime(peak.getPeakModel().getStopRetentionTime());
		result.setTailing(peak.getPeakModel().getTailing());
		result.setWidth(peak.getPeakModel().getWidthByInflectionPoints());
		result.addIntegratedTraces(integratedIons);
		return result;
	}

	private double calculateIntegratedArea(List<IIntegrationEntry> integrationEntries) {

		double result = 0.0d;
		for(IIntegrationEntry integrationEntry : integrationEntries) {
			result += integrationEntry.getIntegratedArea();
		}
		return result;
	}

	/**
	 * Resets the integration sum result values to default values.
	 * 
	 * @param peakIntegrationSumResult
	 */
	private void resetIntegrationSumResultValues(IPeakIntegrationResult peakIntegrationSumResult) {

		/*
		 * Reset some values. A summed peak has no S/N ration, respectively
		 * purity ...
		 */
		peakIntegrationSumResult.setPeakType("Summed integrated area");
		peakIntegrationSumResult.setPurity(0.0f);
		peakIntegrationSumResult.setSN(0.0f);
		peakIntegrationSumResult.setTailing(0.0f);
		peakIntegrationSumResult.setWidth(0);
	}

	/**
	 * Returns a list of the integrated ions.
	 * 
	 * @param markedTraces
	 * @return List<Integer>
	 */
	private Set<Integer> getIntegratedIons(IMarkedTraces<IMarkedTrace> markedTraces) {

		Set<Integer> result;
		if(markedTraces == null) {
			result = new HashSet<>();
		} else {
			result = markedTraces.getTraces();
		}
		return result;
	}
}
