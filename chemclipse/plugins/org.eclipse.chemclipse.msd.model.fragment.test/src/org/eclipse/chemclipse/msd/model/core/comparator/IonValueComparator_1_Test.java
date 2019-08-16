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
package org.eclipse.chemclipse.msd.model.core.comparator;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.comparator.IonValueComparator;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;

import junit.framework.TestCase;

public class IonValueComparator_1_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testAbundanceComparator_1() {

		try {
			IIon ion1 = new Ion(25.5f, 5000.5f);
			IIon ion2 = new Ion(25.5f, 5000.5f);
			IonValueComparator comparator = new IonValueComparator();
			assertEquals("Comparator", 0, comparator.compare(ion1, ion2));
		} catch(AbundanceLimitExceededException e) {
			assertTrue("AbundanceLimitExceededException should not occur.", false);
		} catch(IonLimitExceededException e) {
			assertTrue("IonLimitExceededException should not occur.", false);
		}
	}

	public void testAbundanceComparator_2() {

		try {
			IIon ion1 = null;
			IIon ion2 = new Ion(25.5f, 5000.5f);
			IonValueComparator comparator = new IonValueComparator();
			assertEquals("Comparator", 0, comparator.compare(ion1, ion2));
		} catch(AbundanceLimitExceededException e) {
			assertTrue("AbundanceLimitExceededException should not occur.", false);
		} catch(IonLimitExceededException e) {
			assertTrue("IonLimitExceededException should not occur.", false);
		} catch(NullPointerException e) {
			assertTrue(true);
		}
	}

	public void testAbundanceComparator_3() {

		try {
			IIon ion1 = new Ion(25.5f, 5000.5f);
			IIon ion2 = null;
			IonValueComparator comparator = new IonValueComparator();
			assertEquals("Comparator", 0, comparator.compare(ion1, ion2));
		} catch(AbundanceLimitExceededException e) {
			assertTrue("AbundanceLimitExceededException should not occur.", false);
		} catch(IonLimitExceededException e) {
			assertTrue("IonLimitExceededException should not occur.", false);
		} catch(NullPointerException e) {
			assertTrue(true);
		}
	}

	public void testAbundanceComparator_4() {

		try {
			IIon ion1 = new Ion(24.5f, 5000.5f);
			IIon ion2 = new Ion(25.5f, 5000.5f);
			IonValueComparator comparator = new IonValueComparator();
			assertEquals("Comparator", -1, comparator.compare(ion1, ion2));
		} catch(AbundanceLimitExceededException e) {
			assertTrue("AbundanceLimitExceededException should not occur.", false);
		} catch(IonLimitExceededException e) {
			assertTrue("IonLimitExceededException should not occur.", false);
		}
	}

	public void testAbundanceComparator_5() {

		try {
			IIon ion1 = new Ion(25.5f, 5000.5f);
			IIon ion2 = new Ion(24.5f, 4000.5f);
			IonValueComparator comparator = new IonValueComparator();
			assertEquals("Comparator", 1, comparator.compare(ion1, ion2));
		} catch(AbundanceLimitExceededException e) {
			assertTrue("AbundanceLimitExceededException should not occur.", false);
		} catch(IonLimitExceededException e) {
			assertTrue("IonLimitExceededException should not occur.", false);
		}
	}
}
