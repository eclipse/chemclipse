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

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.exceptions.IonIsNullException;

import junit.framework.TestCase;

/**
 * HashCode test.
 * 
 * @author eselmeister
 */
public class Ion_10_Test extends TestCase {

	private Ion ion1;
	private Ion ion2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ion1 = new Ion(5.2f, 4746.3f);
	}

	@Override
	protected void tearDown() throws Exception {

		ion1 = null;
		ion2 = null;
		super.tearDown();
	}

	public void testConstructor_1() {

		try {
			ion2 = new Ion(ion1);
		} catch(AbundanceLimitExceededException e) {
			assertFalse("AbundanceLimitExceededException should not be thrown here.", true);
		} catch(IonLimitExceededException e) {
			assertFalse("IonLimitExceededException should not be thrown here.", true);
		} catch(IonIsNullException e) {
			assertFalse("IonIsNullException should not be thrown here.", true);
		}
		assertTrue("hashCode", ion1.equals(ion2));
	}

	public void testConstructor_2() {

		try {
			ion2 = new Ion(null);
		} catch(AbundanceLimitExceededException e) {
			assertFalse("AbundanceLimitExceededException should not be thrown here.", true);
		} catch(IonLimitExceededException e) {
			assertFalse("IonLimitExceededException should not be thrown here.", true);
		} catch(IonIsNullException e) {
			assertTrue("IonIsNullException", true);
		}
	}
}
