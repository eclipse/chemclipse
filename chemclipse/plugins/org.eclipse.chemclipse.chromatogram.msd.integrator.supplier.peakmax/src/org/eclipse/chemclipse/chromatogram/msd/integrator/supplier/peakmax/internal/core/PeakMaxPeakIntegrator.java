/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.internal.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.support.IIonPercentages;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.IonPercentages;
import org.eclipse.chemclipse.msd.model.implementation.IntegrationEntryMSD;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.IBaselineSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.IIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.ISettingStatus;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.PeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.PeakIntegrationResults;

public class PeakMaxPeakIntegrator implements IPeakMaxPeakIntegrator {

	@Override
	public IPeakIntegrationResult integrate(IPeakMSD peak, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

		validatePeak(peak);
		validateSettings(peakIntegrationSettings);
		ISettingStatus settingStatus;
		PeakIntegrationResult result = null;
		IBaselineSupport baselineSupport = peakIntegrationSettings.getBaselineSupport();
		List<IIntegrationEntryMSD> integrationEntries = calculateIntegratedArea(peak, baselineSupport, peakIntegrationSettings.getSelectedIons());
		addPeakAreaAndDescription(peak, peakIntegrationSettings, integrationEntries);
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
	public IPeakIntegrationResults integrate(List<IPeakMSD> peaks, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

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
		for(IPeakMSD peak : peaks) {
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
			peakIntegrationSumResult = null;
		}
		return peakIntegrationResults;
	}

	// ------------------------------------------------private methods
	/**
	 * Checks that the peak list is not null.
	 * 
	 * @param peaks
	 * @throws ValueMustNotBeNullException
	 */
	private void validatePeakList(List<IPeakMSD> peaks) throws ValueMustNotBeNullException {

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
	private void validatePeak(IPeakMSD peak) throws ValueMustNotBeNullException {

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
	 * Adds the peak area and description.
	 * 
	 * @param peak
	 * @param peakIntegrationSettings
	 * @param integratedArea
	 */
	private void addPeakAreaAndDescription(IPeakMSD peak, IPeakIntegrationSettings peakIntegrationSettings, List<IIntegrationEntryMSD> integrationEntries) {

		/*
		 * Set the peak area and its description independently of the report.
		 */
		String integratorDescription;
		IMarkedIons selectedIons = peakIntegrationSettings.getSelectedIons();
		Set<Integer> selectedIonsNominal = selectedIons.getIonsNominal();
		/*
		 * Add a appropriate description detector description to the peak.
		 */
		StringBuilder builder = new StringBuilder();
		if(selectedIonsNominal.size() > 0 && !selectedIonsNominal.contains(IIon.TIC_ION)) {
			builder.append(IPeakMaxPeakIntegrator.DESCRIPTION);
			builder.append(":");
			builder.append(" ");
			builder.append(IPeakMaxPeakIntegrator.XIC);
			/*
			 * Get the ion list and sort it.
			 */
			List<Integer> integratedIons = new ArrayList<Integer>(getIntegratedIons(selectedIons));
			Collections.sort(integratedIons);
			for(Integer ion : integratedIons) {
				builder.append(" ");
				builder.append(ion);
			}
		} else {
			builder.append(IPeakMaxPeakIntegrator.DESCRIPTION);
			builder.append(":");
			builder.append(" ");
			builder.append(IPeakMaxPeakIntegrator.TIC);
		}
		integratorDescription = builder.toString();
		/*
		 * Set the integration results.
		 */
		peak.setIntegratedArea(integrationEntries, integratorDescription);
	}

	/**
	 * Calculates the integrated peak area.
	 * 
	 * @return List<IIntegrationEntry>
	 */
	private List<IIntegrationEntryMSD> calculateIntegratedArea(IPeakMSD peak, IBaselineSupport baselineSupport, IMarkedIons selectedIons) {

		List<IIntegrationEntryMSD> integrationEntries = new ArrayList<IIntegrationEntryMSD>();
		IIntegrationEntryMSD integrationEntry;
		//
		IPeakModelMSD peakModel = peak.getPeakModel();
		IPeakMassSpectrum massSpectrum = peakModel.getPeakMassSpectrum();
		double integratedAreaTIC = calculateTICPeakArea(peak, baselineSupport);
		Set<Integer> selectedIonsNominal = selectedIons.getIonsNominal();
		/*
		 * Use the selected ions if:<br/> the size is greater 0
		 * (means, ions have been selected)<br/> and<br/> the selected
		 * ions does not contain IIon.TIC_Ion, which means,
		 * the TIC signal should be integrated.
		 */
		if(selectedIonsNominal.size() > 0 && !selectedIonsNominal.contains(IIon.TIC_ION)) {
			IIonPercentages ionPercentages = new IonPercentages(massSpectrum);
			/*
			 * Calculate the percentage integrated area for each selected ion.
			 */
			for(Integer ion : selectedIonsNominal) {
				float correctionFactor = ionPercentages.getPercentage(ion) / IIonPercentages.MAX_PERCENTAGE;
				double integratedArea = integratedAreaTIC * correctionFactor;
				integrationEntry = new IntegrationEntryMSD(ion, integratedArea);
				integrationEntries.add(integrationEntry);
			}
		} else {
			integrationEntry = new IntegrationEntryMSD(AbstractIon.TIC_ION, integratedAreaTIC);
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
	private double calculateTICPeakArea(IPeakMSD peak, IBaselineSupport baselineSupport) {

		double integratedArea = 0.0d;
		IPeakModelMSD peakModel = peak.getPeakModel();
		/*
		 * Integrates the max scan of the peak model.
		 * There is currently no option to support the settings like "baseline hold".
		 * In case it is needed, please have a look at the Integrator Trapezoid.
		 */
		integratedArea = peakModel.getPeakMassSpectrum().getTotalSignal();
		/*
		 * The calculations are not as precise as they could be.<br/> If the
		 * area is lower than 1 it could be assumed, that the area is 0.
		 */
		if(integratedArea < 1.0d) {
			integratedArea = 0.0d;
		}
		return integratedArea;
	}

	/**
	 * Returns a valid PeakMaxPeakIntegrationResult
	 */
	private PeakIntegrationResult getPeakIntegrationResult(IPeakMSD peak, double integratedArea, IPeakIntegrationSettings peakIntegrationSettings) {

		/*
		 * Selected ions.
		 */
		IMarkedIons selectedIons = peakIntegrationSettings.getSelectedIons();
		Set<Integer> integratedIons = getIntegratedIons(selectedIons);
		PeakIntegrationResult result = new PeakIntegrationResult();
		result.setIntegratedArea(integratedArea);
		result.setIntegratorType(IPeakMaxPeakIntegrator.DESCRIPTION);
		result.setPeakType(peak.getPeakType().toString());
		/*
		 * TODO optimize
		 */
		if(peak instanceof IChromatogramPeakMSD) {
			IChromatogramPeakMSD chromatogramPeak = (IChromatogramPeakMSD)peak;
			result.setPurity(chromatogramPeak.getPurity());
			result.setSN(chromatogramPeak.getSignalToNoiseRatio());
		}
		result.setStartRetentionTime(peak.getPeakModel().getStartRetentionTime());
		result.setStopRetentionTime(peak.getPeakModel().getStopRetentionTime());
		result.setTailing(peak.getPeakModel().getTailing());
		result.setWidth(peak.getPeakModel().getWidthByInflectionPoints());
		result.addIntegratedIons(integratedIons);
		return result;
	}

	private double calculateIntegratedArea(List<IIntegrationEntryMSD> integrationEntries) {

		double result = 0.0d;
		for(IIntegrationEntryMSD integrationEntry : integrationEntries) {
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
	 * @param selectedIons
	 * @return List<Integer>
	 */
	private Set<Integer> getIntegratedIons(IMarkedIons selectedIons) {

		Set<Integer> result;
		if(selectedIons == null) {
			result = new HashSet<Integer>();
		} else {
			result = selectedIons.getIonsNominal();
		}
		return result;
	}
	// ------------------------------------------private methods
}
