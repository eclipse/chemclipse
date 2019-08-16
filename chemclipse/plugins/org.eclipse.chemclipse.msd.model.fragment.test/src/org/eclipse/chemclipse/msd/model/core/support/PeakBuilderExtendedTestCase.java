/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.msd.model.core.IPeakIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakIon;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;

public class PeakBuilderExtendedTestCase extends PeakBuilderTestCase {

	protected ITotalScanSignals totalIonSignals;
	protected IPeakMassSpectrum peakMassSpectrum;
	private IPeakIon peakIon;
	private TreeMap<Float, Float> peakMassSpectrumValues;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		totalIonSignals = new TotalScanSignals(2, 16);
		totalIonSignals.add(new TotalScanSignal(1500, 0, 145.487406362466f));
		totalIonSignals.add(new TotalScanSignal(2500, 0, 399.763031554856f));
		totalIonSignals.add(new TotalScanSignal(3500, 0, 654.038716607066f));
		totalIonSignals.add(new TotalScanSignal(4500, 0, 908.314326834502f));
		totalIonSignals.add(new TotalScanSignal(5500, 0, 1162.58993706194f));
		totalIonSignals.add(new TotalScanSignal(6500, 0, 1671.14115751681f));
		totalIonSignals.add(new TotalScanSignal(7500, 0, 2484.82317010442f));
		totalIonSignals.add(new TotalScanSignal(8500, 0, 4315.60769842655f));
		totalIonSignals.add(new TotalScanSignal(9500, 0, 5231f));
		totalIonSignals.add(new TotalScanSignal(10500, 0, 4519.0280519239f));
		totalIonSignals.add(new TotalScanSignal(11500, 0, 3400.21532202832f));
		totalIonSignals.add(new TotalScanSignal(12500, 0, 2332.2579236876f));
		totalIonSignals.add(new TotalScanSignal(13500, 0, 1671.14115751681f));
		totalIonSignals.add(new TotalScanSignal(14500, 0, 908.314326834502f));
		totalIonSignals.add(new TotalScanSignal(15500, 0, 145.487406362466f));
		peakMassSpectrumValues = new TreeMap<Float, Float>();
		peakMassSpectrumValues.put(104.0f, 2300.0f);
		peakMassSpectrumValues.put(103.0f, 580.0f);
		peakMassSpectrumValues.put(51.0f, 260.0f);
		peakMassSpectrumValues.put(50.0f, 480.0f);
		peakMassSpectrumValues.put(78.0f, 236.0f);
		peakMassSpectrumValues.put(77.0f, 25.0f);
		peakMassSpectrumValues.put(74.0f, 380.0f);
		peakMassSpectrumValues.put(105.0f, 970.0f);
		peakMassSpectrum = new PeakMassSpectrum();
		for(Entry<Float, Float> entry : peakMassSpectrumValues.entrySet()) {
			peakIon = new PeakIon(entry.getKey(), entry.getValue());
			peakMassSpectrum.addIon(peakIon);
		}
	}

	@Override
	protected void tearDown() throws Exception {

		totalIonSignals = null;
		peakMassSpectrum = null;
		peakIon = null;
		peakMassSpectrumValues = null;
		super.tearDown();
	}
}
