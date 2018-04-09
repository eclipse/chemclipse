/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum;

import junit.framework.TestCase;

public class MassSpectrumComparator_4_Test extends TestCase {

	private IMassSpectrumComparator comparator;
	IMassSpectrumComparisonSupplier supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		comparator = MassSpectrumComparator.getMassSpectrumComparator("org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.distance.cosine");
		supplier = comparator.getMassSpectrumComparisonSupplier();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test_1() {

		assertNotNull(comparator);
	}

	public void test_2() {

		assertNotNull(supplier);
	}

	public void test_3() {

		assertEquals("org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.distance.cosine", supplier.getId());
	}

	public void test_4() {

		assertEquals("Cosine", supplier.getComparatorName());
	}

	public void test_5() {

		assertEquals("This comparator calculates the similarity between two mass spectra with the Cosine value.", supplier.getDescription());
	}

	public void test_6() {

		assertEquals(true, supplier.supportsNominalMS());
	}

	public void test_7() {

		assertEquals(false, supplier.supportsTandemMS());
	}

	public void test_8() {

		assertEquals(false, supplier.supportsHighResolutionMS());
	}
}
