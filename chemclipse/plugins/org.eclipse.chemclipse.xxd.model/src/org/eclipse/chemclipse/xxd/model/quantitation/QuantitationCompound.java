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
package org.eclipse.chemclipse.xxd.model.quantitation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.quantitation.AbstractQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.IResponseSignal;
import org.eclipse.chemclipse.model.quantitation.QuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.QuantitationSupport;
import org.eclipse.chemclipse.model.quantitation.ResponseSignal;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;

public class QuantitationCompound extends AbstractQuantitationCompound implements IQuantitationCompound {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 2376307650470986106L;

	public QuantitationCompound(String name, String concentrationUnit, int retentionTime) {
		super(name, concentrationUnit, retentionTime);
	}

	@Override
	public void calculateSignalTablesFromPeaks() {

		List<IQuantitationPeak> quantitationPeaks = getQuantitationPeaks();
		if(quantitationPeaks.size() > 0) {
			/*
			 * Extract the signals and response values
			 * from the stored peaks.
			 */
			getQuantitationSignals().clear();
			getResponseSignals().clear();
			//
			if(isUseTIC()) {
				createTablesTIC(quantitationPeaks);
			} else {
				createTablesXIC(quantitationPeaks);
			}
		}
	}

	private void createTablesTIC(List<IQuantitationPeak> quantitationPeaks) {

		boolean firstPeak = true;
		for(IQuantitationPeak quantitationPeak : quantitationPeaks) {
			double concentration = quantitationPeak.getConcentration();
			IPeak peak = quantitationPeak.getReferencePeak();
			/*
			 * The integrated area needs to use the TIC mode. Otherwise, wrong areas/response values would be calculated.
			 */
			QuantitationSupport integrationQuantitationSupport = new QuantitationSupport(peak);
			if(integrationQuantitationSupport.validateTIC()) {
				//
				double ion = ISignal.TOTAL_INTENSITY;
				double response = integrationQuantitationSupport.getIntegrationArea(ion);
				//
				if(firstPeak) {
					IQuantitationSignal quantitationSignal = new QuantitationSignal(ion, IQuantitationSignal.ABSOLUTE_RELATIVE_RESPONSE);
					getQuantitationSignals().add(quantitationSignal);
				}
				//
				IResponseSignal concentrationResponseEntry = new ResponseSignal(ion, concentration, response);
				getResponseSignals().add(concentrationResponseEntry);
				/*
				 * The first peak has been evaluated.
				 */
				firstPeak = false;
			}
		}
	}

	private void createTablesXIC(List<IQuantitationPeak> quantitationPeaks) {

		boolean firstPeak = true;
		for(IQuantitationPeak quantitationPeak : quantitationPeaks) {
			double concentration = quantitationPeak.getConcentration();
			IPeak peak = quantitationPeak.getReferencePeak();
			if(peak instanceof IPeakMSD) {
				IPeakMSD peakMSD = (IPeakMSD)peak;
				/*
				 * The integrated area needs to use the TIC mode. Otherwise, wrong areas/response values would be calculated.
				 */
				QuantitationSupport integrationQuantitationSupport = new QuantitationSupport(peak);
				//
				IScanMSD massSpectrum = peakMSD.getExtractedMassSpectrum();
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
						IResponseSignal concentrationResponseEntry;
						if(integrationQuantitationSupport.isTotalSignalIntegrated()) {
							/*
							 * TIC
							 */
							concentrationResponseEntry = new ResponseSignal(ion, concentration, response * percentageIonAbundance);
						} else {
							/*
							 * XIC
							 */
							concentrationResponseEntry = new ResponseSignal(ion, concentration, response);
						}
						//
						getResponseSignals().add(concentrationResponseEntry);
					}
					/*
					 * The first peak has been evaluated.
					 */
					firstPeak = false;
				}
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
