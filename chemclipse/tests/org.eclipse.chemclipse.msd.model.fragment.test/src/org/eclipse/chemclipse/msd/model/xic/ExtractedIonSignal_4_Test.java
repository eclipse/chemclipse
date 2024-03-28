/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.implementation.Ion;

import junit.framework.TestCase;

/**
 * Tests the class ExtractedIonSignal.
 * 
 * @author eselmeister
 */
public class ExtractedIonSignal_4_Test extends TestCase {

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
	 * Test setup with start and stop ion = 1 and abundance 1000.
	 */
	public void testSetup_1() {

		extractedIonSignal = new ExtractedIonSignal(1, 1);
		IIon ion = new Ion(1.0f, 1000.0f);
		extractedIonSignal.setAbundance(ion);
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(0));
		assertEquals("Abundance", 1000.0f, extractedIonSignal.getAbundance(1));
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(2));
	}

	/**
	 * Test setup with start and stop ion = 1 and abundance 1000.
	 */
	public void testSetup_2() {

		extractedIonSignal = new ExtractedIonSignal(1, 1);
		IIon ion = new Ion(1.0f, 1000.0f);
		extractedIonSignal.setAbundance(ion);
		extractedIonSignal.setAbundance(0, 2586.4f, true);
		extractedIonSignal.setAbundance(1, 2586.4f, true);
		extractedIonSignal.setAbundance(2, 2586.4f, true);
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(0));
		assertEquals("Abundance", 2586.4f, extractedIonSignal.getAbundance(1));
		assertEquals("Abundance", 0.0f, extractedIonSignal.getAbundance(2));
	}
}
