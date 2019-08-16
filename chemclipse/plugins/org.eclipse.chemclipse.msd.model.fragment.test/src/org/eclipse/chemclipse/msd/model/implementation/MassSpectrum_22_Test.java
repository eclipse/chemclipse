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
package org.eclipse.chemclipse.msd.model.implementation;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;

import junit.framework.TestCase;

/**
 * Tests getTotalIonSignal(IExcludedIons excludedIons)
 * 
 * @author eselmeister
 */
public class MassSpectrum_22_Test extends TestCase {

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
		excludedIons = new MarkedIons();
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

	public void testGetTotalIonSignal_2() throws AbundanceLimitExceededException, IonLimitExceededException {

		excludedIons.add(new MarkedIon(104));
		excludedIons.add(new MarkedIon(28));
		massSpectrum.removeIons(excludedIons);
		assertEquals("getTotalSignal", 1089021.0f, massSpectrum.getTotalSignal());
		assertEquals("NumberOfIons", 3, massSpectrum.getNumberOfIons());
		assertEquals("TotalSignal", 78500.2f, massSpectrum.getIon(46).getAbundance());
		assertEquals("TotalSignal", 890520.4f, massSpectrum.getIon(33).getAbundance());
		assertEquals("TotalSignal", 120000.4f, massSpectrum.getIon(106).getAbundance());
	}

	public void testGetTotalIonSignal_3() throws AbundanceLimitExceededException, IonLimitExceededException {

		massSpectrum.removeIons(excludedIons);
		assertEquals("getTotalSignal", 1242021.9f, massSpectrum.getTotalSignal());
		assertEquals("NumberOfIons", 5, massSpectrum.getNumberOfIons());
		assertEquals("TotalSignal", 78500.2f, massSpectrum.getIon(46).getAbundance());
		assertEquals("TotalSignal", 890520.4f, massSpectrum.getIon(33).getAbundance());
		assertEquals("TotalSignal", 120000.4f, massSpectrum.getIon(106).getAbundance());
	}

	public void testGetTotalIonSignal_4() throws AbundanceLimitExceededException, IonLimitExceededException {

		Set<Integer> ions = new HashSet<Integer>();
		ions.add(104);
		ions.add(28);
		massSpectrum.removeIons(ions);
		assertEquals("getTotalSignal", 1089021.0f, massSpectrum.getTotalSignal());
		assertEquals("NumberOfIons", 3, massSpectrum.getNumberOfIons());
		assertEquals("TotalSignal", 78500.2f, massSpectrum.getIon(46).getAbundance());
		assertEquals("TotalSignal", 890520.4f, massSpectrum.getIon(33).getAbundance());
		assertEquals("TotalSignal", 120000.4f, massSpectrum.getIon(106).getAbundance());
	}

	public void testGetTotalIonSignal_5() throws AbundanceLimitExceededException, IonLimitExceededException {

		massSpectrum.removeIon(104);
		massSpectrum.removeIon(28);
		assertEquals("getTotalSignal", 1089021.0f, massSpectrum.getTotalSignal());
		assertEquals("NumberOfIons", 3, massSpectrum.getNumberOfIons());
		assertEquals("TotalSignal", 78500.2f, massSpectrum.getIon(46).getAbundance());
		assertEquals("TotalSignal", 890520.4f, massSpectrum.getIon(33).getAbundance());
		assertEquals("TotalSignal", 120000.4f, massSpectrum.getIon(106).getAbundance());
	}

	public void testGetTotalIonSignal_6() throws AbundanceLimitExceededException, IonLimitExceededException {

		excludedIons.add(new MarkedIon(104));
		excludedIons.add(new MarkedIon(28));
		excludedIons.add(new MarkedIon(46));
		excludedIons.add(new MarkedIon(33));
		excludedIons.add(new MarkedIon(106));
		massSpectrum.removeIons(excludedIons);
		assertEquals("getTotalSignal", 0.0f, massSpectrum.getTotalSignal());
		assertEquals("NumberOfIons", 0, massSpectrum.getNumberOfIons());
	}
}
