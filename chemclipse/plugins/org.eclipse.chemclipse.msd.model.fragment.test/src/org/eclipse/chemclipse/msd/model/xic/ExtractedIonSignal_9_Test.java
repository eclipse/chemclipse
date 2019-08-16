/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

public class ExtractedIonSignal_9_Test extends TestCase {

	private IExtractedIonSignal extractedIonSignal;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		extractedIonSignal = new ExtractedIonSignal(22, 28);
		extractedIonSignal.setAbundance(22, 389.2f);
		extractedIonSignal.setAbundance(23, 298.6f);
		extractedIonSignal.setAbundance(24, 128.2f);
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

		assertEquals(0.0f, extractedIonSignal.getNthHighestIntensity(-1));
	}

	public void testSize_2() {

		assertEquals(0.0f, extractedIonSignal.getNthHighestIntensity(0));
	}

	public void testSize_3() {

		assertEquals(2788.89f, extractedIonSignal.getNthHighestIntensity(1));
	}

	public void testSize_4() {

		assertEquals(829.1f, extractedIonSignal.getNthHighestIntensity(2));
	}

	public void testSize_5() {

		assertEquals(128.2f, extractedIonSignal.getNthHighestIntensity(7));
	}

	public void testSize_6() {

		assertEquals(0.0f, extractedIonSignal.getNthHighestIntensity(8));
	}
}
