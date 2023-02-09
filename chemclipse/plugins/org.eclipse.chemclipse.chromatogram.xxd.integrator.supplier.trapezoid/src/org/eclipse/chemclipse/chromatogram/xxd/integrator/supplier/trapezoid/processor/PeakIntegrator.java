/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
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

import java.text.MessageFormat;
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
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.Segment;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.support.SegmentAreaCalculator;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IMarkedTrace;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.implementation.IntegrationEntry;
import org.eclipse.chemclipse.model.support.IntegrationConstraint;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.support.IIonPercentages;
import org.eclipse.chemclipse.msd.model.core.support.IonPercentages;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.support.l10n.TranslationSupport;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IPeakModelWSD;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.support.WavelengthPercentages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.translation.TranslationService;

/**
 * This integrator implements the chemstation peak integrator routines.<br/>
 * Each peak will be integrated in 100ms steps.<br/>
 * First, each peak will be integrated.<br/>
 * Then, each integration result will be analyzed if it matches the integration
 * settings.
 * 
 * @author Philip Wenig
 */
public class PeakIntegrator {

	/*
	 * The FirstDerivative seems to use a correction factor.
	 * Otherwise, the peak areas are too high.
	 */
	private static TranslationService translationService = TranslationSupport.getTranslationService();
	private static final String INTEGRATOR_DESCRIPTION = translationService.translate("%Trapezoid", Activator.getContributorURI());
	//
	private static final double CORRECTION_FACTOR_TRAPEZOID = 100.0d; // ChemStation Factor

	public IPeakIntegrationResult integrate(IPeak peak, PeakIntegrationSettings peakIntegrationSettings) throws ValueMustNotBeNullException {

		validatePeak(peak);
		validateSettings(peakIntegrationSettings);
		/*
		 * The background shall be not included normally.
		 */
		boolean includeBackground = peakIntegrationSettings.isIncludeBackground();
		boolean useAreaConstraint = peakIntegrationSettings.isUseAreaConstraint();
		ISettingStatus settingStatus;
		PeakIntegrationResult result = null;
		IBaselineSupport baselineSupport = peakIntegrationSettings.getBaselineSupport();
		//
		List<? extends IIntegrationEntry> integrationEntries = calculateIntegratedArea(peak, baselineSupport, peakIntegrationSettings.getMarkedTraces(), includeBackground, useAreaConstraint);
		peak.setIntegratedArea(integrationEntries, INTEGRATOR_DESCRIPTION);
		/*
		 * Get the peak area if the peak should be reported.
		 */
		settingStatus = peakIntegrationSettings.getSettingStatus(peak);
		if(settingStatus.report()) {
			double integratedArea = calculateIntegratedArea(integrationEntries);
			result = getPeakIntegrationResult(peak, integratedArea, peakIntegrationSettings);
		}
		//
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
			monitor.subTask(MessageFormat.format(translationService.translate("%IntegratePeakNumber", Activator.getContributorURI()), peakNumber++));
			try {
				peakIntegrationResult = integrate(peak, peakIntegrationSettings);
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
		if(peak instanceof IPeakMSD peakMSD) {
			/*
			 * MSD
			 */
			if(peakMSD.getPeakModel() == null) {
				throw new ValueMustNotBeNullException("The peak model must not be null.");
			}
		} else if(peak instanceof IPeakCSD peakCSD) {
			/*
			 * CSD
			 */
			if(peakCSD.getPeakModel() == null) {
				throw new ValueMustNotBeNullException("The peak model must not be null.");
			}
		} else if(peak instanceof IPeakWSD peakWSD) {
			/*
			 * WSD
			 */
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
	private List<IIntegrationEntry> calculateIntegratedArea(IPeak peak, IBaselineSupport baselineSupport, IMarkedTraces<IMarkedTrace> markedTraces, boolean includeBackground, boolean useAreaConstraint) {

		List<IIntegrationEntry> integrationEntries = new ArrayList<>();
		if(peak instanceof IPeakMSD peakMSD) {
			/*
			 * MSD
			 */
			IPeakModelMSD peakModel = peakMSD.getPeakModel();
			IPeakMassSpectrum massSpectrum = peakModel.getPeakMassSpectrum();
			double integratedAreaTIC = calculateTICPeakArea(peak, baselineSupport, includeBackground, useAreaConstraint);
			//
			IIntegrationEntry integrationEntry;
			if(!markedTraces.isEmpty() && !markedTraces.getTraces().contains(IMarkedTrace.TOTAL_SIGNAL_AS_INT)) {
				Set<Integer> ions = markedTraces.getTraces();
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
		} else if(peak instanceof IPeakCSD) {
			/*
			 * FID
			 */
			double integratedAreaTIC = calculateTICPeakArea(peak, baselineSupport, includeBackground, useAreaConstraint);
			IIntegrationEntry integrationEntry = new IntegrationEntry(integratedAreaTIC);
			integrationEntries.add(integrationEntry);
		} else if(peak instanceof IPeakWSD peakWSD) {
			/*
			 * WSD
			 */
			IPeakModelWSD peakModel = peakWSD.getPeakModel();
			double integratedAreaTIC = calculateTICPeakArea(peak, baselineSupport, includeBackground, useAreaConstraint);
			//
			IScan scan = peakModel.getPeakMaximum();
			IIntegrationEntry integrationEntry;
			if(scan instanceof IScanWSD scanWSD) {
				if(!markedTraces.isEmpty() && !markedTraces.getTraces().contains(IMarkedTrace.TOTAL_SIGNAL_AS_INT)) {
					Set<Integer> wavelengths = markedTraces.getTraces();
					WavelengthPercentages wavelengthPercentages = new WavelengthPercentages(scanWSD);
					/*
					 * Calculate the percentage integrated area for each selected ion.
					 */
					for(Integer wavelength : wavelengths) {
						float correctionFactor = wavelengthPercentages.getPercentage(wavelength) / WavelengthPercentages.MAX_PERCENTAGE;
						double integratedArea = integratedAreaTIC * correctionFactor;
						integrationEntry = new IntegrationEntry(wavelength, integratedArea);
						integrationEntries.add(integrationEntry);
					}
				} else {
					integrationEntry = new IntegrationEntry(ISignal.TOTAL_INTENSITY, integratedAreaTIC);
					integrationEntries.add(integrationEntry);
				}
			}
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
	private double calculateTICPeakArea(IPeak peak, IBaselineSupport baselineSupport, boolean includeBackground, boolean useAreaConstraint) {

		double integratedArea = 0.0d;
		List<Integer> retentionTimes;
		IPeakModel peakModel;
		//
		if(peak instanceof IPeakMSD peakMSD) {
			peakModel = peakMSD.getPeakModel();
			retentionTimes = peakModel.getRetentionTimes();
		} else if(peak instanceof IPeakCSD peakCSD) {
			peakModel = peakCSD.getPeakModel();
			retentionTimes = peakModel.getRetentionTimes();
		} else if(peak instanceof IPeakWSD peakWSD) {
			peakModel = peakWSD.getPeakModel();
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
			integratedArea += calculatePeakArea(startRetentionTime, stopRetentionTime, peakAbundanceStart, peakAbundanceStop);
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
	 * Calculate the area of the peak in the given retention time
	 * segment.<br/>
	 * 
	 * This method should only calculate the absolute
	 * peak area. Any other correction will be calculated in
	 * calculateBaselineCorrectedPeakArea.
	 * 
	 * @param startRetentionTime
	 * @param stopRetentionTime
	 * @return double
	 */
	private double calculatePeakArea(int startRetentionTime, int stopRetentionTime, float peakAbundanceStart, float peakAbundanceStop) {

		// PeakSignalPoint
		Point psp1 = new Point(startRetentionTime, peakAbundanceStart);
		Point psp2 = new Point(stopRetentionTime, peakAbundanceStop);
		// PeakBaselinePoint
		Point pbp1 = new Point(startRetentionTime, 0.0f);
		Point pbp2 = new Point(stopRetentionTime, 0.0f);
		Segment segment = new Segment(pbp1, pbp2, psp1, psp2);
		return SegmentAreaCalculator.calculateSegmentArea(segment) / CORRECTION_FACTOR_TRAPEZOID;
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
		double integratedArea = 0d;
		//
		IPeakModel peakModel;
		if(peak instanceof IPeakMSD peakMSD) {
			peakModel = peakMSD.getPeakModel();
		} else if(peak instanceof IPeakCSD peakCSD) {
			peakModel = peakCSD.getPeakModel();
		} else {
			return integratedArea;
		}
		/*
		 * Calculate no correction (means return 0.0f) if the peak should be
		 * left as it is.
		 */
		if(!peak.getIntegrationConstraints().hasIntegrationConstraint(IntegrationConstraint.LEAVE_PEAK_AS_IT_IS)) {
			float chromatogramBaselineStart = validateChromatogramBaseline(peakModel, baselineSupport, startRetentionTime);
			float chromatogramBaselineStop = validateChromatogramBaseline(peakModel, baselineSupport, stopRetentionTime);
			/*
			 * Calculate the area, that needs to be corrected.
			 */
			// ChromatogramBaselinePoint
			Point cbp1 = new Point(startRetentionTime, chromatogramBaselineStart);
			Point cbp2 = new Point(stopRetentionTime, chromatogramBaselineStop);
			// PeakBaselinePoint
			Point pbp1 = new Point(startRetentionTime, peakModel.getBackgroundAbundance(startRetentionTime));
			Point pbp2 = new Point(stopRetentionTime, peakModel.getBackgroundAbundance(stopRetentionTime));
			Segment segment = new Segment(cbp1, cbp2, pbp1, pbp2);
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

		float totalPeakHeight = peakModel.getBackgroundAbundance(retentionTime) + peakModel.getPeakAbundance(retentionTime);
		float chromatogramBaseline = baselineSupport.getBackgroundAbundance(retentionTime);
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
		IMarkedTraces<IMarkedTrace> markedTraces = peakIntegrationSettings.getMarkedTraces();
		Set<Integer> markedTraceSet = getMarkedTraceSet(markedTraces);
		PeakIntegrationResult result = new PeakIntegrationResult();
		result.setIntegratedArea(integratedArea);
		result.setIntegratorType(INTEGRATOR_DESCRIPTION);
		result.setPeakType(peak.getPeakType().toString());
		/*
		 * Chromatogram Peak
		 */
		if(peak instanceof IChromatogramPeakMSD chromatogramPeak) {
			result.setPurity(chromatogramPeak.getPurity());
			result.setSN(chromatogramPeak.getSignalToNoiseRatio());
		} else if(peak instanceof IChromatogramPeakCSD chromatogramPeak) {
			result.setSN(chromatogramPeak.getSignalToNoiseRatio());
		} else if(peak instanceof IChromatogramPeakWSD chromatogramPeak) {
			result.setSN(chromatogramPeak.getSignalToNoiseRatio());
		}
		/*
		 * Peak MSD / CSD / WSD
		 */
		if(peak instanceof IPeakMSD peakMSD) {
			result.setStartRetentionTime(peakMSD.getPeakModel().getStartRetentionTime());
			result.setStopRetentionTime(peakMSD.getPeakModel().getStopRetentionTime());
			result.setTailing(peakMSD.getPeakModel().getTailing());
			result.setWidth(peakMSD.getPeakModel().getWidthByInflectionPoints());
			result.addIntegratedTraces(markedTraceSet);
		} else if(peak instanceof IPeakCSD peakCSD) {
			result.setStartRetentionTime(peakCSD.getPeakModel().getStartRetentionTime());
			result.setStopRetentionTime(peakCSD.getPeakModel().getStopRetentionTime());
			result.setTailing(peakCSD.getPeakModel().getTailing());
			result.setWidth(peakCSD.getPeakModel().getWidthByInflectionPoints());
		} else if(peak instanceof IPeakWSD peakWSD) {
			result.setStartRetentionTime(peakWSD.getPeakModel().getStartRetentionTime());
			result.setStopRetentionTime(peakWSD.getPeakModel().getStopRetentionTime());
			result.setTailing(peakWSD.getPeakModel().getTailing());
			result.setWidth(peakWSD.getPeakModel().getWidthByInflectionPoints());
			result.addIntegratedTraces(markedTraceSet);
		}
		//
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
		peakIntegrationSumResult.setPeakType(translationService.translate("%SummedIntegratedArea", Activator.getContributorURI()));
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
	private Set<Integer> getMarkedTraceSet(IMarkedTraces<IMarkedTrace> markedTraces) {

		Set<Integer> result;
		if(markedTraces == null) {
			result = new HashSet<>();
		} else {
			result = markedTraces.getTraces();
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