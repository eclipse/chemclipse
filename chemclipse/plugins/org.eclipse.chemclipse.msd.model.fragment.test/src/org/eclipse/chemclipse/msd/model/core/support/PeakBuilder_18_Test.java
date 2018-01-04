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

import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.PeakBuilderMSD;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

/**
 * Test the peak exceptions.
 * 
 * @author eselmeister
 */
public class PeakBuilder_18_Test extends PeakBuilderTestCase {

	private IMarkedIons excludedIons;
	private ITotalScanSignals totalIonSignals;
	private ITotalScanSignal signal;
	private IPoint p1, p2;
	private LinearEquation backgroundEquation;
	private IPeakMassSpectrum peakMassSpectrum;
	private ITotalScanSignalExtractor totalIonSignalExtractor;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
		totalIonSignals = totalIonSignalExtractor.getTotalScanSignals(2, 16);
		excludedIons = new MarkedIons();
		signal = totalIonSignals.getTotalScanSignal(2);
		p1 = new Point(signal.getRetentionTime(), signal.getTotalSignal());
		signal = totalIonSignals.getTotalScanSignal(16);
		p2 = new Point(signal.getRetentionTime(), signal.getTotalSignal());
		backgroundEquation = Equations.createLinearEquation(p1, p2);
		peakMassSpectrum = PeakBuilderMSD.getPeakMassSpectrum(chromatogram, totalIonSignals, backgroundEquation, excludedIons);
	}

	@Override
	protected void tearDown() throws Exception {

		excludedIons = null;
		totalIonSignals = null;
		signal = null;
		p1 = null;
		p2 = null;
		backgroundEquation = null;
		peakMassSpectrum = null;
		super.tearDown();
	}

	public void testGetPeakMassSpectrum_1() {

		assertNotNull(peakMassSpectrum);
	}

	public void testGetPeakMassSpectrum_2() {

		assertEquals("Ions", 11, peakMassSpectrum.getIons().size());
	}
}
