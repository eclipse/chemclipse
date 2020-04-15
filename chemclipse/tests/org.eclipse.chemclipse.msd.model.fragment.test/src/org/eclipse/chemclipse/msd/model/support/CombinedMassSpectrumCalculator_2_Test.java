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
package org.eclipse.chemclipse.msd.model.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;

import junit.framework.TestCase;

public class CombinedMassSpectrumCalculator_2_Test extends TestCase {

	private CombinedMassSpectrumCalculator combinedMassSpectrumCalculator;
	private IMarkedIons excludedIons;
	private List<IIon> ions;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		combinedMassSpectrumCalculator = new CombinedMassSpectrumCalculator();
		ions = new ArrayList<IIon>();
		excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSize_1() {

		combinedMassSpectrumCalculator.addIons(null, null);
		assertEquals("Size", 0, combinedMassSpectrumCalculator.size());
	}

	public void testSize_2() {

		try {
			ions.add(new Ion(56.5f, 500.0f));
			combinedMassSpectrumCalculator.addIons(ions, excludedIons);
			assertEquals("Size", 1, combinedMassSpectrumCalculator.size());
		} catch(AbundanceLimitExceededException e) {
			assertFalse("An AbundanceLimitExceededException should not be thrown here.", false);
		} catch(IonLimitExceededException e) {
			assertFalse("An IonLimitExceededException should not be thrown here.", false);
		}
	}

	public void testSize_3() {

		try {
			ions.add(new Ion(56.5f, 500.0f));
			ions.add(new Ion(80.2f, 700.0f));
			combinedMassSpectrumCalculator.addIons(ions, excludedIons);
			assertEquals("Size", 2, combinedMassSpectrumCalculator.size());
		} catch(AbundanceLimitExceededException e) {
			assertFalse("An AbundanceLimitExceededException should not be thrown here.", false);
		} catch(IonLimitExceededException e) {
			assertFalse("An IonLimitExceededException should not be thrown here.", false);
		}
	}

	public void testSize_4() {

		try {
			ions.add(new Ion(56.5f, 500.0f));
			ions.add(new Ion(80.2f, 700.0f));
			ions.add(new Ion(90.3f, 800.0f));
			combinedMassSpectrumCalculator.addIons(ions, excludedIons);
			assertEquals("Size", 3, combinedMassSpectrumCalculator.size());
		} catch(AbundanceLimitExceededException e) {
			assertFalse("An AbundanceLimitExceededException should not be thrown here.", false);
		} catch(IonLimitExceededException e) {
			assertFalse("An IonLimitExceededException should not be thrown here.", false);
		}
	}

	public void testSize_5() {

		/*
		 * Math round is used to determine the integer value of the mass
		 * fragment.
		 */
		try {
			ions.add(new Ion(56.4f, 500.0f));
			ions.add(new Ion(56.2f, 700.0f));
			ions.add(new Ion(55.9f, 800.0f));
			combinedMassSpectrumCalculator.addIons(ions, excludedIons);
			assertEquals("Size", 1, combinedMassSpectrumCalculator.size());
		} catch(AbundanceLimitExceededException e) {
			assertFalse("An AbundanceLimitExceededException should not be thrown here.", false);
		} catch(IonLimitExceededException e) {
			assertFalse("An IonLimitExceededException should not be thrown here.", false);
		}
	}
}
