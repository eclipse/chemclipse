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
package org.eclipse.chemclipse.msd.model.xic;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;

import junit.framework.TestCase;

/**
 * Tests the class ExtractedIonSignal.
 * 
 * @author eselmeister
 */
public class ExtractedIonSignal_1_Test extends TestCase {

	private IExtractedIonSignal extractedIonSignal;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	/**
	 * Test setup with start and stop ion = 0.
	 */
	public void testSetup_1() {

		extractedIonSignal = new ExtractedIonSignal(0, 0);
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(-1));
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(0));
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(1));
	}

	/**
	 * Test setup with start and stop ion = 1.
	 */
	public void testSetup_2() {

		extractedIonSignal = new ExtractedIonSignal(0, 0);
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(0));
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(1));
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(2));
	}

	/**
	 * Test setup with start and stop ion = 1 and abundance 1000.
	 */
	public void testSetup_3() {

		extractedIonSignal = new ExtractedIonSignal(1, 1);
		Ion ion;
		try {
			ion = new Ion(1.0f, 1000.0f);
			extractedIonSignal.setAbundance(ion);
		} catch(AbundanceLimitExceededException e) {
			assertTrue("AbundanceLimitExceededException", false);
		} catch(IonLimitExceededException e) {
			assertTrue("IonLimitExceededException", false);
		}
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(0));
		assertEquals("Abundance", 1000.0f, extractedIonSignal.getAbundance(1));
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(2));
	}

	/**
	 * Try to set negative values.
	 */
	public void testSetup_4() {

		extractedIonSignal = new ExtractedIonSignal(-1, 0);
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(0));
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(1));
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(2));
	}

	/**
	 * Try to set negative values.
	 */
	public void testSetup_5() {

		extractedIonSignal = new ExtractedIonSignal(0, -1);
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(0));
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(1));
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(2));
	}

	/**
	 * Try to set negative values.
	 */
	public void testSetup_6() {

		extractedIonSignal = new ExtractedIonSignal(-2, -1);
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(0));
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(1));
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(2));
	}

	/**
	 * Test setup with start and stop ion 10-11<br/>
	 * ion = 10 and abundance 1000<br/>
	 * ion = 11 and abundance 2000
	 */
	public void testSetup_7() {

		extractedIonSignal = new ExtractedIonSignal(10, 11);
		Ion ion;
		try {
			ion = new Ion(10.0f, 1000.0f);
			extractedIonSignal.setAbundance(ion);
			ion = new Ion(11.0f, 2000.0f);
			extractedIonSignal.setAbundance(ion);
		} catch(AbundanceLimitExceededException e) {
			assertTrue("AbundanceLimitExceededException", false);
		} catch(IonLimitExceededException e) {
			assertTrue("IonLimitExceededException", false);
		}
		assertEquals("Abundance", 1000.0f, extractedIonSignal.getAbundance(10));
		assertEquals("Abundance", 2000.0f, extractedIonSignal.getAbundance(11));
	}

	/**
	 * Test setup with start and stop ion 10<br/>
	 * ion = 10 and abundance 1000<br/>
	 */
	public void testSetup_8() {

		extractedIonSignal = new ExtractedIonSignal(10, 11);
		Ion ion;
		try {
			ion = new Ion(10.0f, 1000.0f);
			extractedIonSignal.setAbundance(ion);
		} catch(AbundanceLimitExceededException e) {
			assertTrue("AbundanceLimitExceededException", false);
		} catch(IonLimitExceededException e) {
			assertTrue("IonLimitExceededException", false);
		}
		assertEquals("Abundance", 1000.0f, extractedIonSignal.getAbundance(10));
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(11));
	}

	/**
	 * Test setup with start and stop ion 10-11<br/>
	 * ion = 10 and abundance 1000<br/>
	 * ion = 11 and abundance 2000 ion = 10 and abundance 1000 ion = 10 and
	 * abundance 1000 ion = 11 and abundance 2000
	 */
	public void testSetup_9() {

		extractedIonSignal = new ExtractedIonSignal(10, 11);
		Ion ion;
		try {
			ion = new Ion(10.0f, 1000.0f);
			extractedIonSignal.setAbundance(ion);
			extractedIonSignal.setAbundance(ion);
			extractedIonSignal.setAbundance(ion);
			ion = new Ion(11.0f, 2000.0f);
			extractedIonSignal.setAbundance(ion);
			extractedIonSignal.setAbundance(ion);
		} catch(AbundanceLimitExceededException e) {
			assertTrue("AbundanceLimitExceededException", false);
		} catch(IonLimitExceededException e) {
			assertTrue("IonLimitExceededException", false);
		}
		assertEquals("Abundance", 3000.0f, extractedIonSignal.getAbundance(10));
		assertEquals("Abundance", 4000.0f, extractedIonSignal.getAbundance(11));
	}

	/**
	 * Test setup with start and stop ion 10.4<br/>
	 * ion = 10 and abundance 1000
	 */
	public void testSetup_10() {

		extractedIonSignal = new ExtractedIonSignal(10.4f, 10.4f);
		Ion ion;
		try {
			ion = new Ion(10.4f, 1000.0f);
			extractedIonSignal.setAbundance(ion);
		} catch(AbundanceLimitExceededException e) {
			assertTrue("AbundanceLimitExceededException", false);
		} catch(IonLimitExceededException e) {
			assertTrue("IonLimitExceededException", false);
		}
		assertEquals("Abundance", 1000.0f, extractedIonSignal.getAbundance(10));
	}

	/**
	 * Test setup with start and stop ion 10.5<br/>
	 * ion = 11 and abundance 1000
	 */
	public void testSetup_11() {

		extractedIonSignal = new ExtractedIonSignal(10.5f, 10.5f);
		Ion ion;
		try {
			ion = new Ion(10.5f, 1000.0f);
			extractedIonSignal.setAbundance(ion);
		} catch(AbundanceLimitExceededException e) {
			assertTrue("AbundanceLimitExceededException", false);
		} catch(IonLimitExceededException e) {
			assertTrue("IonLimitExceededException", false);
		}
		assertEquals("Abundance", 1000.0f, extractedIonSignal.getAbundance(11));
	}
}
