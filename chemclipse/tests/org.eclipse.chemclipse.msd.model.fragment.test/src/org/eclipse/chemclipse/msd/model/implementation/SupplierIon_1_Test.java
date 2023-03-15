/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
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

import junit.framework.TestCase;

/**
 * Constructor test.
 * 
 * @author eselmeister
 */
public class SupplierIon_1_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testConstructor_1() {

		try {
			new ScanIon(0.5f);
		} catch(IonLimitExceededException e) {
			assertTrue(false);
		}
	}

	public void testConstructor_2() {

		try {
			new ScanIon(0.4f);
		} catch(IonLimitExceededException e) {
			assertTrue(true);
		}
	}

	public void testConstructor_3() {

		try {
			new ScanIon(65535.0f);
		} catch(IonLimitExceededException e) {
			assertTrue(false);
		}
	}

	public void testConstructor_4() {

		try {
			new ScanIon(65535.1f);
		} catch(IonLimitExceededException e) {
			assertTrue(true);
		}
	}

	public void testConstructor_5() {

		try {
			ScanIon ion = new ScanIon(1.0f);
			ion.setIon(0.4f);
		} catch(IonLimitExceededException e) {
			assertTrue(true);
		}
	}

	public void testConstructor_6() {

		try {
			ScanIon ion = new ScanIon(1.0f);
			ion.setIon(65535.0f);
		} catch(IonLimitExceededException e) {
			assertTrue(false);
		}
	}

	public void testConstructor_7() {

		try {
			ScanIon ion = new ScanIon(1.0f);
			ion.setIon(65535.1f);
		} catch(IonLimitExceededException e) {
			assertTrue(true);
		}
	}

	public void testConstructor_8() {

		try {
			new ScanIon(0.5f, 0.0f);
		} catch(AbundanceLimitExceededException e) {
			assertTrue(true);
		} catch(IonLimitExceededException e) {
			assertTrue(false);
		}
	}

	public void testConstructor_9() {

		try {
			new ScanIon(65535.0f, 0.0f);
		} catch(AbundanceLimitExceededException e) {
			assertTrue(true);
		} catch(IonLimitExceededException e) {
			assertTrue(false);
		}
	}

	public void testConstructor_10() {

		try {
			new ScanIon(65535.1f, 0.0f);
		} catch(AbundanceLimitExceededException e) {
			assertTrue(false);
		} catch(IonLimitExceededException e) {
			assertTrue(true);
		}
	}

	public void testConstructor_11() {

		try {
			new ScanIon(0.4f, 0.0f);
		} catch(AbundanceLimitExceededException e) {
			assertTrue(false);
		} catch(IonLimitExceededException e) {
			assertTrue(true);
		}
	}

	public void testConstructor_12() {

		try {
			new ScanIon(0.5f, -0.1f);
		} catch(AbundanceLimitExceededException e) {
			assertTrue(true);
		} catch(IonLimitExceededException e) {
			assertTrue(false);
		}
	}

	public void testConstructor_13() {

		try {
			ScanIon ion = new ScanIon(0.5f, 0.0f);
			ion.setAbundance(-0.1f);
		} catch(AbundanceLimitExceededException e) {
			assertTrue(true);
		} catch(IonLimitExceededException e) {
			assertTrue(false);
		}
	}

	public void testConstructor_14() {

		try {
			ScanIon ion = new ScanIon(0.5f, 0.1f);
			ion.setIon(0.4f);
		} catch(AbundanceLimitExceededException e) {
			assertTrue(false);
		} catch(IonLimitExceededException e) {
			assertTrue(true);
		}
	}
}
