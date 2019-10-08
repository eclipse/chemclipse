/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;

import junit.framework.TestCase;

/**
 * Tests getTotalIonSignal(IExcludedIons excludedIons)
 * 
 * @author eselmeister
 */
public class MassSpectrum_20_Test extends TestCase {

	private ScanMSD massSpectrum;
	private Ion ion;
	private IMarkedIons excludedIons;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new ScanMSD();
		ion = new Ion(45.5f, 78500.2f);
		massSpectrum.addIon(ion);
		ion = new Ion(104.1f, 120000.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(32.6f, 890520.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(105.7f, 120000.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(28.2f, 33000.5f);
		massSpectrum.addIon(ion);
		excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		ion = null;
		excludedIons = null;
		super.tearDown();
	}

	public void testGetTotalSignal_1() {

		assertEquals("getTotalSignal", 1242021.9f, massSpectrum.getTotalSignal());
	}

	public void testGetTotalIonSignal_2() {

		excludedIons.add(new MarkedIon(104));
		assertEquals("getTotalSignal", 1122021.5f, massSpectrum.getTotalSignal(excludedIons));
	}

	public void testGetTotalIonSignal_3() {

		excludedIons.add(new MarkedIon(104));
		excludedIons.add(new MarkedIon(86));
		assertEquals("getTotalSignal", 1122021.5f, massSpectrum.getTotalSignal(excludedIons));
	}

	public void testGetTotalIonSignal_4() {

		assertEquals("getTotalSignal", 1242021.9f, massSpectrum.getTotalSignal(excludedIons));
	}

	public void testGetTotalIonSignal_5() {

		assertEquals("getTotalSignal", 1242021.9f, massSpectrum.getTotalSignal(null));
	}

	public void testGetTotalIonSignal_6() {

		excludedIons.add(new MarkedIon(45));
		excludedIons.add(new MarkedIon(104));
		excludedIons.add(new MarkedIon(32));
		excludedIons.add(new MarkedIon(105));
		excludedIons.add(new MarkedIon(28));
		/*
		 * Why do we have a total ion signal here? Because the float ion value
		 * will be rounded Math.round() and so only 104.1 and 28.2 will be
		 * kicked.
		 */
		assertEquals("getTotalSignal", 1089021.0f, massSpectrum.getTotalSignal(excludedIons));
	}
}
