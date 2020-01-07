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
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;

import junit.framework.TestCase;

/**
 * Exception test.
 * 
 * @author eselmeister
 */
public class Ion_6_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testException_1() {

		try {
			@SuppressWarnings("unused")
			Ion ion = new Ion(IIon.TIC_ION, -1.0f);
		} catch(AbundanceLimitExceededException e) {
			assertTrue(true);
		} catch(IonLimitExceededException e) {
			assertTrue(false);
		}
	}

	public void testException_2() {

		try {
			Ion ion = new Ion(IIon.TIC_ION, 1.0f);
			ion.setAbundance(-1.0f);
		} catch(AbundanceLimitExceededException e) {
			assertTrue(true);
		} catch(IonLimitExceededException e) {
			assertTrue(false);
		}
	}

	public void testException_3() {

		try {
			@SuppressWarnings("unused")
			Ion ion = new Ion(-1.0f, 1.0f);
		} catch(AbundanceLimitExceededException e) {
			assertTrue(false);
		} catch(IonLimitExceededException e) {
			assertTrue(true);
		}
	}

	public void testException_4() {

		try {
			Ion ion = new Ion(1.0f, 1.0f);
			ion.setIon(-1.0f);
		} catch(AbundanceLimitExceededException e) {
			assertTrue(false);
		} catch(IonLimitExceededException e) {
			assertTrue(true);
		}
	}
}
