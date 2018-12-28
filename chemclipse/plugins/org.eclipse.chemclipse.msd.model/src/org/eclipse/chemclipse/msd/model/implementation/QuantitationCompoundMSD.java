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
package org.eclipse.chemclipse.msd.model.implementation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.quantitation.AbstractQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.ConcentrationResponseEntry;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.QuantitationSignal;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IntegrationQuantitationSupport;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;

public class QuantitationCompoundMSD extends AbstractQuantitationCompound<IPeakMSD> implements IQuantitationCompound<IPeakMSD> {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 2376307650470986106L;

	public QuantitationCompoundMSD(String name, String concentrationUnit, int retentionTime) {
		super(name, concentrationUnit, retentionTime);
	}

	@Override
	public void createSignalTablesTIC(List<IQuantitationPeak<IPeakMSD>> quantitationPeaks) {

		boolean firstPeak = true;
		for(IQuantitationPeak<IPeakMSD> quantitationPeakMSD : quantitationPeaks) {
			double concentration = quantitationPeakMSD.getConcentration();
			IPeakMSD peak = quantitationPeakMSD.getReferencePeak();
			/*
			 * The integrated area needs to use the TIC mode. Otherwise, wrong areas/response values would be calculated.
			 */
			IntegrationQuantitationSupport integrationQuantitationSupport = new IntegrationQuantitationSupport(peak);
			if(integrationQuantitationSupport.validateTIC()) {
				//
				double ion = IQuantitationSignal.TIC_SIGNAL;
				double response = integrationQuantitationSupport.getIntegrationArea(ion);
				//
				if(firstPeak) {
					IQuantitationSignal quantitationSignal = new QuantitationSignal(ion, IQuantitationSignal.ABSOLUTE_RESPONSE);
					getQuantitationSignals().add(quantitationSignal);
				}
				//
				IConcentrationResponseEntry concentrationResponseEntry = new ConcentrationResponseEntry(ion, concentration, response);
				getConcentrationResponseEntries().add(concentrationResponseEntry);
				/*
				 * The first peak has been evaluated.
				 */
				firstPeak = false;
			}
		}
	}

	@Override
	public void createSignalTablesXIC(List<IQuantitationPeak<IPeakMSD>> quantitationPeaks) {

		boolean firstPeak = true;
		for(IQuantitationPeak<IPeakMSD> quantitationPeakMSD : quantitationPeaks) {
			double concentration = quantitationPeakMSD.getConcentration();
			IPeakMSD peak = quantitationPeakMSD.getReferencePeak();
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
						getQuantitationSignals().add(quantitationSignal);
					}
					/*
					 * The real response is calculated by the total response and the percentage of the ion abundance.
					 */
					double response = integrationQuantitationSupport.getIntegrationArea(ion);
					IConcentrationResponseEntry concentrationResponseEntry;
					if(integrationQuantitationSupport.isTotalSignalIntegrated()) {
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
					//
					getConcentrationResponseEntries().add(concentrationResponseEntry);
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
}
