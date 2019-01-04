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
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.implementation.IntegrationEntryMSD;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.msd.model.implementation.PeakIon;
import org.eclipse.chemclipse.msd.model.implementation.PeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;

import junit.framework.TestCase;

public class ReferencePeakMSDTestCase extends TestCase {

	private IPeakMSD referencePeakMSD_TIC_1; // 1x Concentration
	private IPeakMSD referencePeakMSD_TIC_2; // 5x Concentration
	private IPeakMSD referencePeakMSD_TIC_3; // 10x Concentration
	private IPeakMSD referencePeakMSD_TIC_X; // 3x Concentration
	//
	private IPeakMSD referencePeakMSD_XIC_1; // 8x Concentration
	private IPeakMSD referencePeakMSD_XIC_2; // 9x Concentration
	private IPeakMSD referencePeakMSD_XIC_3; // 6x Concentration
	private IPeakMSD referencePeakMSD_XIC_X; // 2x Concentration
	//
	private IPeakModelMSD peakModel;
	private IPeakMassSpectrum peakMaximum;
	private IPeakIon peakIon;
	private TreeMap<Double, Float> fragmentValues;
	private IPeakIntensityValues intensityValues;
	private TreeMap<Integer, Float> scanValues;
	private float startBackgroundAbundance = 0.0f;
	private float stopBackgroundAbundance = 0.0f;
	//
	private double baseArea = 750220.0d;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		referencePeakMSD_TIC_1 = createPeak(1, false);
		referencePeakMSD_TIC_2 = createPeak(5, false);
		referencePeakMSD_TIC_3 = createPeak(10, false);
		referencePeakMSD_TIC_X = createPeak(3, false);
		//
		referencePeakMSD_XIC_1 = createPeak(8, true);
		referencePeakMSD_XIC_2 = createPeak(9, true);
		referencePeakMSD_XIC_3 = createPeak(6, true);
		referencePeakMSD_XIC_X = createPeak(2, true);
	}

	private IPeakMSD createPeak(int scale, boolean xic) throws Exception {

		// ----------------------PeakMaximum
		peakMaximum = new PeakMassSpectrum();
		fragmentValues = new TreeMap<Double, Float>();
		fragmentValues.put(104.0d, 2300.0f);
		fragmentValues.put(103.0d, 580.0f);
		fragmentValues.put(51.0d, 260.0f);
		fragmentValues.put(50.0d, 480.0f);
		fragmentValues.put(78.0d, 236.0f);
		fragmentValues.put(77.0d, 25.0f);
		fragmentValues.put(74.0d, 380.0f);
		fragmentValues.put(105.0d, 970.0f);
		for(Entry<Double, Float> entry : fragmentValues.entrySet()) {
			peakIon = new PeakIon(entry.getKey(), entry.getValue() * scale);
			peakMaximum.addIon(peakIon);
		}
		// ----------------------PeakMaximum
		// ----------------------IntensityValues
		intensityValues = new PeakIntensityValues();
		/*
		 * Add Peak (1500) to 16 (15500)
		 */
		scanValues = new TreeMap<Integer, Float>();
		scanValues.put(1500, 0.0f);
		scanValues.put(2500, 5.0f);
		scanValues.put(3500, 10.0f);
		scanValues.put(4500, 15.0f);
		scanValues.put(5500, 20.0f);
		scanValues.put(6500, 30.0f);
		scanValues.put(7500, 46.0f);
		scanValues.put(8500, 82.0f);
		scanValues.put(9500, 100.0f);
		scanValues.put(10500, 86.0f);
		scanValues.put(11500, 64.0f);
		scanValues.put(12500, 43.0f);
		scanValues.put(13500, 30.0f);
		scanValues.put(14500, 15.0f);
		scanValues.put(15500, 4.0f);
		for(Entry<Integer, Float> entry : scanValues.entrySet()) {
			intensityValues.addIntensityValue(entry.getKey(), entry.getValue());
		}
		// ----------------------IntensityValues
		peakModel = new PeakModelMSD(peakMaximum, intensityValues, startBackgroundAbundance, stopBackgroundAbundance);
		IPeakMSD referencePeakMSD;
		if(xic) {
			/*
			 * XIC integration entries
			 */
			referencePeakMSD = new PeakMSD(peakModel, "XIC");
			List<IIntegrationEntry> integrationEntries = new ArrayList<IIntegrationEntry>();
			float totalSignal = referencePeakMSD.getPeakModel().getPeakMassSpectrum().getTotalSignal();
			for(IIon ion : referencePeakMSD.getPeakModel().getPeakMassSpectrum().getIons()) {
				double percentageIonIntensity = (1.0d / totalSignal) * ion.getAbundance();
				IIntegrationEntry integrationEntry = new IntegrationEntryMSD(ion.getIon(), baseArea * scale * percentageIonIntensity);
				integrationEntries.add(integrationEntry);
			}
			referencePeakMSD.setIntegratedArea(integrationEntries, "Test Integrator XIC");
		} else {
			/*
			 * TIC integration entries
			 */
			referencePeakMSD = new PeakMSD(peakModel, "TIC");
			List<IIntegrationEntry> integrationEntries = new ArrayList<IIntegrationEntry>();
			IIntegrationEntry integrationEntry = new IntegrationEntryMSD(AbstractIon.TIC_ION, baseArea * scale);
			integrationEntries.add(integrationEntry);
			referencePeakMSD.setIntegratedArea(integrationEntries, "Test Integrator TIC");
		}
		//
		return referencePeakMSD;
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		peakIon = null;
		peakMaximum = null;
		peakModel = null;
		intensityValues = null;
		fragmentValues = null;
		//
		referencePeakMSD_TIC_1 = null;
		referencePeakMSD_TIC_2 = null;
		referencePeakMSD_TIC_3 = null;
	}

	/*
	 * 1x Concentration (Integration Entries TIC)
	 */
	public IPeakMSD getReferencePeakMSD_TIC_1() {

		return referencePeakMSD_TIC_1;
	}

	/*
	 * 5x Concentration (Integration Entries TIC)
	 */
	public IPeakMSD getReferencePeakMSD_TIC_2() {

		return referencePeakMSD_TIC_2;
	}

	/*
	 * 10x Concentration (Integration Entries TIC)
	 */
	public IPeakMSD getReferencePeakMSD_TIC_3() {

		return referencePeakMSD_TIC_3;
	}

	/*
	 * 3x Concentration (Integration Entries TIC)
	 */
	public IPeakMSD getReferencePeakMSD_TIC_X() {

		return referencePeakMSD_TIC_X;
	}

	/*
	 * 8x Concentration (Integration Entries XIC)
	 */
	public IPeakMSD getReferencePeakMSD_XIC_1() {

		return referencePeakMSD_XIC_1;
	}

	/*
	 * 9x Concentration (Integration Entries XIC)
	 */
	public IPeakMSD getReferencePeakMSD_XIC_2() {

		return referencePeakMSD_XIC_2;
	}

	/*
	 * 6x Concentration (Integration Entries XIC)
	 */
	public IPeakMSD getReferencePeakMSD_XIC_3() {

		return referencePeakMSD_XIC_3;
	}

	/*
	 * 2x Concentration (Integration Entries XIC)
	 */
	public IPeakMSD getReferencePeakMSD_XIC_X() {

		return referencePeakMSD_XIC_X;
	}
}
