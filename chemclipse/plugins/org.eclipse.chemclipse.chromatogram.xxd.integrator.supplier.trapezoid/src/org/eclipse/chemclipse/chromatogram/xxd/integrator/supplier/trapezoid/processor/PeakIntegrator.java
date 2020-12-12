/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.IBaselineSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.IIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.ISettingStatus;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.PeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.PeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.ISegment;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.Segment;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.SegmentAreaCalculator;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.implementation.IntegrationEntry;
import org.eclipse.chemclipse.model.support.IntegrationConstraint;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.support.IIonPercentages;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.IonPercentages;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This integrator implements the chemstation peak integrator routines.<br/>
 * Each peak will be integrated in 100ms steps.<br/>
 * First, each peak will be integrated.<br/>
 * Then, each integration result will be analyzed if it matches the integration
 * settings.
 * 
 * @author eselmeister
 */
public class PeakIntegrator {

	/*
	 * The FirstDerivative seems to use a correction factor.
	 * Otherwise, the peak areas are too high.
	 */
	private static final String INTEGRATOR_DESCRIPTION = "Trapezoid";
	// private static final String XIC = "XIC =";
	// private static final String TIC = "TIC";
	//
	private static final double CORRECTION_FACTOR_TRAPEZOID = 100.0d; // ChemStation Factor

	public IPeakIntegrationResult integrate(IPeak peak, PeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

		validatePeak(peak);
		validateSettings(peakIntegrationSettings);
		/*
		 * The background shall be not included normally.
		 */
		boolean includeBackground = peakIntegrationSettings.isIncludeBackground();
		ISettingStatus settingStatus;
		PeakIntegrationResult result = null;
		IBaselineSupport baselineSupport = peakIntegrationSettings.getBaselineSupport();
		//
		List<? extends IIntegrationEntry> integrationEntries = calculateIntegratedArea(peak, baselineSupport, peakIntegrationSettings.getSelectedIons(), includeBackground);
		peak.setIntegratedArea(integrationEntries, INTEGRATOR_DESCRIPTION);
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

	public IPeakIntegrationResults integrate(List<? extends IPeak> peaks, PeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException {

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
			peakIntegrationSumResult = null;
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
		if(peak instanceof IPeakMSD) {
			/*
			 * MSD
			 */
			IPeakMSD peakMSD = (IPeakMSD)peak;
			if(peakMSD.getPeakModel() == null) {
				throw new ValueMustNotBeNullException("The peak model must not be null.");
			}
		} else if(peak instanceof IPeakCSD) {
			/*
			 * CSD
			 */
			IPeakCSD peakFID = (IPeakCSD)peak;
			if(peakFID.getPeakModel() == null) {
				throw new ValueMustNotBeNullException("The peak model must not be null.");
			}
		} else if(peak instanceof IPeakWSD) {
			/*
			 * WSD
			 */
			IPeakWSD peakWSD = (IPeakWSD)peak;
			if(peakWSD.getPeakModel() == null) {
				throw new ValueMustNotBeNullException("The peak model must not be null.");
			}
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
	 * Calculates the integrated peak area.<br/>
	 * A baseline correction will be also calculated if necessary.
	 * 
	 * @return List<IIntegrationEntry>
	 */
	private List<IIntegrationEntry> calculateIntegratedArea(IPeak peak, IBaselineSupport baselineSupport, IMarkedIons selectedIons, boolean includeBackground) {

		List<IIntegrationEntry> integrationEntries = new ArrayList<IIntegrationEntry>();
		if(peak instanceof IPeakMSD) {
			/*
			 * MSD
			 */
			IPeakMSD peakMSD = (IPeakMSD)peak;
			IPeakModelMSD peakModel = peakMSD.getPeakModel();
			IPeakMassSpectrum massSpectrum = peakModel.getPeakMassSpectrum();
			double integratedAreaTIC = calculateTICPeakArea(peak, baselineSupport, includeBackground);
			/*
			 * Use the selected ions if:<br/> the size is greater 0
			 * (means, ions have been selected)<br/> and<br/> the selected
			 * ions does not contain IIon.TIC_Ion, which means,
			 * the TIC signal should be integrated.
			 */
			IIntegrationEntry integrationEntry;
			if(selectedIons.size() > 0 && !selectedIons.getIonsNominal().contains(AbstractIon.getIon(IIon.TIC_ION))) {
				Set<Integer> ions = selectedIons.getIonsNominal();
				IIonPercentages ionPercentages = new IonPercentages(massSpectrum);
				/*
				 * Calculate the percentage integrated area for each selected ion.
				 */
				for(Integer ion : ions) {
					float correctionFactor = ionPercentages.getPercentage(ion) / IIonPercentages.MAX_PERCENTAGE;
					double integratedArea = integratedAreaTIC * correctionFactor;
					integrationEntry = new IntegrationEntry(ion, integratedArea);
					integrationEntries.add(integrationEntry);
				}
			} else {
				integrationEntry = new IntegrationEntry(ISignal.TOTAL_INTENSITY, integratedAreaTIC);
				integrationEntries.add(integrationEntry);
			}
			//
		} else if(peak instanceof IPeakCSD) {
			/*
			 * FID
			 */
			double integratedAreaTIC = calculateTICPeakArea(peak, baselineSupport, includeBackground);
			IIntegrationEntry integrationEntry = new IntegrationEntry(integratedAreaTIC);
			integrationEntries.add(integrationEntry);
		} else if(peak instanceof IPeakWSD) {
			/*
			 * WSD
			 */
			double integratedAreaTIC = calculateTICPeakArea(peak, baselineSupport, includeBackground);
			IIntegrationEntry integrationEntry = new IntegrationEntry(integratedAreaTIC);
			integrationEntries.add(integrationEntry);
		}
		//
		return integrationEntries;
	}

	/**
	 * Calculates the TIC (total ion chromatogram) peak area.<br/>
	 * If only selected ions should be integrated, use the tic signal.<br/>
	 * Then add only fractions of the integrated area (depending on the
	 * percentage part of the ions in the mass spectrum).
	 * 
	 * @param peak
	 * @return double
	 */
	private double calculateTICPeakArea(IPeak peak, IBaselineSupport baselineSupport, boolean includeBackground) {

		double integratedArea = 0.0d;
		List<Integer> retentionTimes;
		IPeakModel peakModel;
		//
		if(peak instanceof IPeakMSD) {
			peakModel = ((IPeakMSD)peak).getPeakModel();
			retentionTimes = peakModel.getRetentionTimes();
		} else if(peak instanceof IPeakCSD) {
			peakModel = ((IPeakCSD)peak).getPeakModel();
			retentionTimes = peakModel.getRetentionTimes();
		} else if(peak instanceof IPeakWSD) {
			peakModel = ((IPeakWSD)peak).getPeakModel();
			retentionTimes = peakModel.getRetentionTimes();
		} else {
			return integratedArea;
		}
		/*
		 * Integrates each scan of the peak model.<br/>
		 */
		for(int i = 0; i < retentionTimes.size() - 1; i++) {
			/*
			 * Why do we use stopRetentionTime -1 here?<br/> Ok, it is just 1
			 * millisecond, but if we do not correct the stop retention time,
			 * the areas would overlap in 1 ms.
			 */
			int startRetentionTime = retentionTimes.get(i);
			int stopRetentionTime = retentionTimes.get(i + 1) - 1;
			float peakAbundanceStart = peakModel.getPeakAbundance(startRetentionTime);
			float peakAbundanceStop = peakModel.getPeakAbundance(stopRetentionTime);
			//
			integratedArea += calculatePeakArea(peak, startRetentionTime, stopRetentionTime, peakAbundanceStart, peakAbundanceStop);
			/*
			 * If the peak should be integrated as it is, skip the baseline
			 * correction. It is implemented in the method
			 * calculateBaselineCorrectedPeakArea.
			 * IMPORTANT NOTE:
			 * The include background shall bet not used normally.
			 */
			if(includeBackground) {
				integratedArea += calculateBaselineCorrectedPeakArea(peak, baselineSupport, startRetentionTime, stopRetentionTime);
			}
		}
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
	 * Returns the calculated peak area of the given segment.
	 * 
	 * @param peak
	 * @param startRetentionTime
	 * @param stopRetentionTime
	 * @return double
	 */
	private double calculatePeakArea(IPeak peak, int startRetentionTime, int stopRetentionTime, float peakAbundanceStart, float peakAbundanceStop) {

		IPoint psp1, psp2; // PeakSignalPoint
		IPoint pbp1, pbp2; // PeakBaselinePoint
		ISegment segment;
		double integratedArea = 0.0f;
		/*
		 * Calculate the area of the peak in the given retention time
		 * segment.<br/> Use the FirstDerivative
		 * (IFirstDerivativePeakIntegrator.INTEGRATION_STEPS). Why do we use 0.0f at
		 * pbp1 and pbp2?<br/> This method should only calculate the absolute
		 * peak area. Any other correction will be calculated in
		 * calculateBaselineCorrectedPeakArea.
		 */
		psp1 = new Point(startRetentionTime, peakAbundanceStart);
		psp2 = new Point(stopRetentionTime, peakAbundanceStop);
		pbp1 = new Point(startRetentionTime, 0.0f);
		pbp2 = new Point(stopRetentionTime, 0.0f);
		segment = new Segment(pbp1, pbp2, psp1, psp2);
		integratedArea = SegmentAreaCalculator.calculateSegmentArea(segment) / CORRECTION_FACTOR_TRAPEZOID;
		return integratedArea;
	}

	/**
	 * Apply a correction depending on the baseline of the given segment.<br/>
	 * The correction will be applied only if the peak allows it (its constraint
	 * is not LEAVE_PEAK_AS_IT_IS).
	 * 
	 * @param peak
	 * @param baselineSupport
	 * @param startRetentionTime
	 * @param stopRetentionTime
	 * @return double
	 */
	private double calculateBaselineCorrectedPeakArea(IPeak peak, IBaselineSupport baselineSupport, int startRetentionTime, int stopRetentionTime) {

		/*
		 * Take care of the peak baseline and the chromatogram baseline. Be
		 * aware! If the chromatogram baseline is higher than the peak baseline
		 * + the peak abundance, reset the chromatogram baseline to the summed
		 * peak baseline and peak abundance.
		 */
		IPoint cbp1, cbp2; // ChromatogramBaselinePoint
		IPoint pbp1, pbp2; // PeakBaselinePoint
		ISegment segment;
		IPeakModel peakModel;
		double integratedArea = 0.0f;
		//
		if(peak instanceof IPeakMSD) {
			IPeakMSD peakMSD = (IPeakMSD)peak;
			peakModel = peakMSD.getPeakModel();
		} else if(peak instanceof IPeakCSD) {
			IPeakCSD peakFID = (IPeakCSD)peak;
			peakModel = peakFID.getPeakModel();
		} else {
			return integratedArea;
		}
		float chromatogramBaselineStart = 0.0f;
		float chromatogramBaselineStop = 0.0f;
		/*
		 * Calculate no correction (means return 0.0f) if the peak should be
		 * left as it is.<br/> Use the FirstDerivative
		 * (IFirstDerivativePeakIntegrator.INTEGRATION_STEPS).
		 */
		if(!peak.getIntegrationConstraints().hasIntegrationConstraint(IntegrationConstraint.LEAVE_PEAK_AS_IT_IS)) {
			chromatogramBaselineStart = validateChromatogramBaseline(peakModel, baselineSupport, startRetentionTime);
			chromatogramBaselineStop = validateChromatogramBaseline(peakModel, baselineSupport, stopRetentionTime);
			/*
			 * Calculate the area, that needs to be corrected.
			 */
			cbp1 = new Point(startRetentionTime, chromatogramBaselineStart);
			cbp2 = new Point(stopRetentionTime, chromatogramBaselineStop);
			pbp1 = new Point(startRetentionTime, peakModel.getBackgroundAbundance(startRetentionTime));
			pbp2 = new Point(stopRetentionTime, peakModel.getBackgroundAbundance(stopRetentionTime));
			segment = new Segment(cbp1, cbp2, pbp1, pbp2);
			integratedArea = SegmentAreaCalculator.calculateSegmentArea(segment) / CORRECTION_FACTOR_TRAPEZOID;
		}
		return integratedArea;
	}

	/**
	 * Check if the chromatogram baseline is higher than the absolute peak
	 * height.<br/>
	 * If yes, reset it to the absolute peak height, otherwise too much area
	 * would be subtracted.
	 * 
	 * @param peakModel
	 * @param baselineSupport
	 * @param retentionTime
	 */
	private float validateChromatogramBaseline(IPeakModel peakModel, IBaselineSupport baselineSupport, int retentionTime) {

		float totalPeakHeight = 0.0f;
		float chromatogramBaseline = 0.0f;
		totalPeakHeight = peakModel.getBackgroundAbundance(retentionTime) + peakModel.getPeakAbundance(retentionTime);
		chromatogramBaseline = baselineSupport.getBackgroundAbundance(retentionTime);
		if(chromatogramBaseline > totalPeakHeight) {
			chromatogramBaseline = totalPeakHeight;
		}
		return chromatogramBaseline;
	}

	/**
	 * Returns a valid FirstDerivativePeakIntegrationResult
	 */
	private PeakIntegrationResult getPeakIntegrationResult(IPeak peak, double integratedArea, IPeakIntegrationSettings peakIntegrationSettings) {

		/*
		 * Selected ions.
		 */
		IMarkedIons selectedIons = peakIntegrationSettings.getSelectedIons();
		Set<Integer> integratedIons = getIntegratedIons(selectedIons);
		PeakIntegrationResult result = new PeakIntegrationResult();
		result.setIntegratedArea(integratedArea);
		result.setIntegratorType(INTEGRATOR_DESCRIPTION);
		result.setPeakType(peak.getPeakType().toString());
		/*
		 * Chromatogram Peak
		 * TODO optimize
		 */
		if(peak instanceof IChromatogramPeakMSD) {
			IChromatogramPeakMSD chromatogramPeak = (IChromatogramPeakMSD)peak;
			result.setPurity(chromatogramPeak.getPurity());
			result.setSN(chromatogramPeak.getSignalToNoiseRatio());
		} else if(peak instanceof IChromatogramPeakCSD) {
			IChromatogramPeakCSD chromatogramPeak = (IChromatogramPeakCSD)peak;
			result.setSN(chromatogramPeak.getSignalToNoiseRatio());
		}
		/*
		 * Peak MSD / FID
		 */
		if(peak instanceof IPeakMSD) {
			IPeakMSD peakMSD = (IPeakMSD)peak;
			result.setStartRetentionTime(peakMSD.getPeakModel().getStartRetentionTime());
			result.setStopRetentionTime(peakMSD.getPeakModel().getStopRetentionTime());
			result.setTailing(peakMSD.getPeakModel().getTailing());
			result.setWidth(peakMSD.getPeakModel().getWidthByInflectionPoints());
			result.addIntegratedIons(integratedIons);
		} else if(peak instanceof IPeakCSD) {
			IPeakCSD peakFID = (IPeakCSD)peak;
			result.setStartRetentionTime(peakFID.getPeakModel().getStartRetentionTime());
			result.setStopRetentionTime(peakFID.getPeakModel().getStopRetentionTime());
			result.setTailing(peakFID.getPeakModel().getTailing());
			result.setWidth(peakFID.getPeakModel().getWidthByInflectionPoints());
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

	private double calculateIntegratedArea(List<? extends IIntegrationEntry> integrationEntries) {

		double result = 0.0d;
		for(IIntegrationEntry integrationEntry : integrationEntries) {
			result += integrationEntry.getIntegratedArea();
		}
		return result;
	}
}
