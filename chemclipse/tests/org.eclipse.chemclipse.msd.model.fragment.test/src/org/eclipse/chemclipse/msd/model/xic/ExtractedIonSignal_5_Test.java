/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
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

import junit.framework.TestCase;

public class ExtractedIonSignal_5_Test extends TestCase {

	private IExtractedIonSignal extractedIonSignal;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		extractedIonSignal = new ExtractedIonSignal(18, 28);
		extractedIonSignal.setAbundance(18, 5689.8f);
		extractedIonSignal.setAbundance(19, 829.83f);
		extractedIonSignal.setAbundance(20, 893.2f);
		extractedIonSignal.setAbundance(21, 113.9f);
		extractedIonSignal.setAbundance(22, 389.2f);
		extractedIonSignal.setAbundance(23, 298.6f);
		extractedIonSignal.setAbundance(24, 8938.2f);
		extractedIonSignal.setAbundance(25, 192.1f);
		extractedIonSignal.setAbundance(26, 2788.89f);
		extractedIonSignal.setAbundance(27, 829.1f);
		extractedIonSignal.setAbundance(28, 568.89f);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSize_1() {

		assertEquals(11, extractedIonSignal.getNumberOfIonValues());
	}

	public void testSize_2() {

		assertEquals(21531.71f, extractedIonSignal.getTotalSignal());
	}

	public void testSize_3() {

		assertEquals(113.9f, extractedIonSignal.getMinIntensity());
	}

	public void testSize_4() {

		assertEquals(8938.2f, extractedIonSignal.getMaxIntensity());
	}

	public void testSize_5() {

		assertEquals(829.1f, extractedIonSignal.getMedianIntensity());
	}

	public void testSize_6() {

		assertEquals(24, extractedIonSignal.getIonMaxIntensity());
	}
}
