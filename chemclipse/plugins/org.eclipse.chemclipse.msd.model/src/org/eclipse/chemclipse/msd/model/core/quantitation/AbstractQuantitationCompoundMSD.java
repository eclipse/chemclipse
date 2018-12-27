/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.ConcentrationResponseEntries;
import org.eclipse.chemclipse.model.quantitation.ConcentrationResponseEntry;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntries;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignals;
import org.eclipse.chemclipse.model.quantitation.IRetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.model.quantitation.QuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.QuantitationSignals;
import org.eclipse.chemclipse.model.quantitation.RetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.RetentionTimeWindow;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;

public abstract class AbstractQuantitationCompoundMSD implements IQuantitationCompoundMSD {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8760040551533353962L;
	//
	private String name = ""; // Styrene
	private String chemicalClass = ""; // SB (Styrene-Butadiene)
	private IRetentionTimeWindow retentionTimeWindow;
	private IRetentionIndexWindow retentionIndexWindow;
	private String concentrationUnit;
	private IQuantitationSignals quantitationSignalsMSD;
	private IConcentrationResponseEntries concentrationResponseEntriesMSD;
	private boolean useTIC;
	private CalibrationMethod calibrationMethod;
	private boolean useCrossZero;

	/**
	 * Name, e.g. Styrene<br/>
	 * <br/>
	 * Concentration unit, e.g.:<br/>
	 * "ng/ml"<br/>
	 * "µg/ml"<br/>
	 * "ppm"<br/>
	 * "mg/ml"<br/>
	 * <br/>
	 * Retention Time in milliseconds
	 * 
	 * @param name
	 * @param concentrationUnit
	 * @param retentionTime
	 */
	public AbstractQuantitationCompoundMSD(String name, String concentrationUnit, int retentionTime) {
		this.name = name;
		this.concentrationUnit = concentrationUnit;
		retentionTimeWindow = new RetentionTimeWindow();
		retentionTimeWindow.setRetentionTime(retentionTime);
		retentionIndexWindow = new RetentionIndexWindow();
		quantitationSignalsMSD = new QuantitationSignals();
		concentrationResponseEntriesMSD = new ConcentrationResponseEntries();
		useTIC = true;
		calibrationMethod = CalibrationMethod.LINEAR;
		useCrossZero = true;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String getChemicalClass() {

		return chemicalClass;
	}

	@Override
	public void setChemicalClass(String chemicalClass) {

		if(chemicalClass != null) {
			this.chemicalClass = chemicalClass;
		}
	}

	@Override
	public IRetentionTimeWindow getRetentionTimeWindow() {

		return retentionTimeWindow;
	}

	@Override
	public IRetentionIndexWindow getRetentionIndexWindow() {

		return retentionIndexWindow;
	}

	@Override
	public String getConcentrationUnit() {

		return concentrationUnit;
	}

	@Override
	public boolean isUseTIC() {

		return useTIC;
	}

	@Override
	public void setUseTIC(boolean useTIC) {

		this.useTIC = useTIC;
	}

	@Override
	public IQuantitationSignals getQuantitationSignalsMSD() {

		return quantitationSignalsMSD;
	}

	@Override
	public void updateQuantitationSignalsMSD(IQuantitationSignals quantitationSignalsMSD) {

		if(quantitationSignalsMSD != null) {
			this.quantitationSignalsMSD = quantitationSignalsMSD;
		}
	}

	@Override
	public IConcentrationResponseEntries getConcentrationResponseEntriesMSD() {

		return concentrationResponseEntriesMSD;
	}

	@Override
	public void updateConcentrationResponseEntries(IConcentrationResponseEntries concentrationResponseEntriesMSD) {

		if(concentrationResponseEntriesMSD != null) {
			this.concentrationResponseEntriesMSD = concentrationResponseEntriesMSD;
		}
	}

	@Override
	public void calculateQuantitationSignalsAndConcentrationResponseEntries(List<IQuantitationPeakMSD> quantitationPeaks) {

		/*
		 * Create a new table only, if there is at least 1 peak stored.
		 */
		if(quantitationPeaks.size() > 0) {
			/*
			 * Delete the current lists.
			 */
			quantitationSignalsMSD.clear();
			concentrationResponseEntriesMSD.clear();
			/*
			 * Extract the values for the lists.
			 */
			if(isUseTIC()) {
				createSignalTablesTIC(quantitationPeaks);
			} else {
				createSignalTablesXIC(quantitationPeaks);
			}
		}
	}

	/**
	 * Create the quantitation signals and concentration response entries from TIC signal.
	 * 
	 * @param quantitationPeaks
	 */
	private void createSignalTablesTIC(List<IQuantitationPeakMSD> quantitationPeaks) {

		boolean firstPeak = true;
		for(IQuantitationPeakMSD quantitationPeakMSD : quantitationPeaks) {
			double concentration = quantitationPeakMSD.getConcentration();
			IPeakMSD peak = quantitationPeakMSD.getReferencePeakMSD();
			/*
			 * The integrated area needs to use the TIC mode. Otherwise, wrong areas/response values would be calculated.
			 */
			IntegrationQuantitationSupport integrationQuantitationSupport = new IntegrationQuantitationSupport(peak);
			if(integrationQuantitationSupport.validateTIC()) {
				//
				double ion = AbstractIon.TIC_ION;
				double response = integrationQuantitationSupport.getIntegrationArea(ion);
				//
				if(firstPeak) {
					IQuantitationSignal quantitationSignal = new QuantitationSignal(ion, IQuantitationSignal.ABSOLUTE_RESPONSE);
					quantitationSignalsMSD.add(quantitationSignal);
				}
				//
				IConcentrationResponseEntry concentrationResponseEntry = new ConcentrationResponseEntry(ion, concentration, response);
				concentrationResponseEntriesMSD.add(concentrationResponseEntry);
				/*
				 * The first peak has been evaluated.
				 */
				firstPeak = false;
			}
		}
	}

	/**
	 * Create the quantitation signals and concentration response entries from XIC signal.
	 * 
	 * @param quantitationPeaks
	 */
	private void createSignalTablesXIC(List<IQuantitationPeakMSD> quantitationPeaks) {

		boolean firstPeak = true;
		for(IQuantitationPeakMSD quantitationPeakMSD : quantitationPeaks) {
			double concentration = quantitationPeakMSD.getConcentration();
			IPeakMSD peak = quantitationPeakMSD.getReferencePeakMSD();
			/*
			 * The integrated area needs to use the TIC mode. Otherwise, wrong areas/response values would be calculated.
			 */
			IntegrationQuantitationSupport integrationQuantitationSupport = new IntegrationQuantitationSupport(peak);
			//
			IScanMSD massSpectrum = peak.getExtractedMassSpectrum();
			IExtractedIonSignal extractedIonSignal = massSpectrum.getExtractedIonSignal();
			List<Double> selectedQuantitationIons = getSelectedQunatitationIons(extractedIonSignal);
			//
			if(integrationQuantitationSupport.validateXIC(selectedQuantitationIons)) {
				//
				for(double ion : selectedQuantitationIons) {
					float abundance = extractedIonSignal.getAbundance((int)ion);
					float totalSignalMassSpectrum = extractedIonSignal.getTotalSignal();
					/*
					 * See, e.g. percentage ion abundance
					 * MassSpectrum (m/z - abundance - percentage abundance):
					 * 50 - 1000 - 0.11...
					 * 103 - 2000 - 0.22...
					 * 104 - 5000 - 0.55...
					 * 105 - 1000 - 0.11...
					 * = 9000 (totalSignalMassSpectrum)
					 */
					float percentageIonAbundance = (float)((1.0d / totalSignalMassSpectrum) * abundance);
					/*
					 * Each peak has a mass spectrum. Only the first mass spectrum will be used.
					 */
					if(firstPeak) {
						IQuantitationSignal quantitationSignal = new QuantitationSignal(ion, percentageIonAbundance);
						quantitationSignalsMSD.add(quantitationSignal);
					}
					/*
					 * The real response is calculated by the total response and the percentage of the ion abundance.
					 */
					double response = integrationQuantitationSupport.getIntegrationArea(ion);
					IConcentrationResponseEntry concentrationResponseEntry;
					if(integrationQuantitationSupport.isTheTotalSignalIntegrated()) {
						/*
						 * TIC
						 */
						concentrationResponseEntry = new ConcentrationResponseEntry(ion, concentration, response * percentageIonAbundance);
					} else {
						/*
						 * XIC
						 */
						concentrationResponseEntry = new ConcentrationResponseEntry(ion, concentration, response);
					}
					concentrationResponseEntriesMSD.add(concentrationResponseEntry);
				}
				/*
				 * The first peak has been evaluated.
				 */
				firstPeak = false;
			}
		}
	}

	private List<Double> getSelectedQunatitationIons(IExtractedIonSignal extractedIonSignal) {

		List<Double> selectedQunatitationIons = new ArrayList<Double>();
		int startIon = extractedIonSignal.getStartIon();
		int stopIon = extractedIonSignal.getStopIon();
		for(int ion = startIon; ion <= stopIon; ion++) {
			float abundance = extractedIonSignal.getAbundance(ion);
			if(abundance > 0) {
				selectedQunatitationIons.add((double)ion);
			}
		}
		return selectedQunatitationIons;
	}

	@Override
	public CalibrationMethod getCalibrationMethod() {

		return calibrationMethod;
	}

	@Override
	public void setCalibrationMethod(CalibrationMethod calibrationMethod) {

		this.calibrationMethod = calibrationMethod;
	}

	@Override
	public boolean isCrossZero() {

		return useCrossZero;
	}

	@Override
	public void setUseCrossZero(boolean useCrossZero) {

		this.useCrossZero = useCrossZero;
	}

	@Override
	public void updateQuantitationCompound(IQuantitationCompoundMSD quantitationCompoundMSD) {

		if(quantitationCompoundMSD != null) {
			this.name = quantitationCompoundMSD.getName();
			this.chemicalClass = quantitationCompoundMSD.getChemicalClass();
			this.concentrationUnit = quantitationCompoundMSD.getConcentrationUnit();
			this.useTIC = quantitationCompoundMSD.isUseTIC();
			this.calibrationMethod = quantitationCompoundMSD.getCalibrationMethod();
			this.useCrossZero = quantitationCompoundMSD.isCrossZero();
		}
	}
}
