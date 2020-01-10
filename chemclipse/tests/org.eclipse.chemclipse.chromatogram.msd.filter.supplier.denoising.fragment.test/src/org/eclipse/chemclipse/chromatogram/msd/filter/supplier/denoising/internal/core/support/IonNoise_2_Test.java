/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.internal.core.support;

import org.eclipse.chemclipse.support.comparator.SortOrder;

import junit.framework.TestCase;

public class IonNoise_2_Test extends TestCase {

	private IonNoise ionNoise1;
	private IonNoise ionNoise2;
	private IonNoise ionNoise3;
	private IonNoiseAbundanceComparator ionNoiseAbundanceComparator;

	@Override
	protected void setUp() throws Exception {

		ionNoise1 = new IonNoise(120, 3889.56f);
		ionNoise2 = new IonNoise(167, 5893.56f);
		ionNoise3 = new IonNoise(140, 28793809.56f);
		ionNoiseAbundanceComparator = new IonNoiseAbundanceComparator(SortOrder.DESC);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testCompareTo_1() {

		assertEquals(0, ionNoiseAbundanceComparator.compare(ionNoise1, ionNoise1));
	}

	public void testCompareTo_2() {

		assertTrue(1 <= ionNoiseAbundanceComparator.compare(ionNoise1, ionNoise2));
	}

	public void testCompareTo_3() {

		assertTrue(-1 >= ionNoiseAbundanceComparator.compare(ionNoise2, ionNoise1));
	}

	public void testCompareTo_4() {

		assertTrue(-1 >= ionNoiseAbundanceComparator.compare(ionNoise3, ionNoise2));
	}

	public void testCompareTo_5() {

		assertTrue(-1 >= ionNoiseAbundanceComparator.compare(ionNoise3, ionNoise1));
	}
}
