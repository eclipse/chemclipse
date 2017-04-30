/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.normalizer.settings;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.normalizer.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.normalizer.settings.SupplierFilterSettings;

import junit.framework.TestCase;

public class NormalizerFilterSettings_1_Test extends TestCase {

	private ISupplierFilterSettings normalizerFilterSettings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		normalizerFilterSettings = new SupplierFilterSettings();
	}

	@Override
	protected void tearDown() throws Exception {

		normalizerFilterSettings = null;
		super.tearDown();
	}

	public void testGetNormalizationBase_1() {

		assertEquals("NormalizationBase", ISupplierFilterSettings.DEFAULT_NORMALIZATION_BASE, normalizerFilterSettings.getNormalizationBase());
	}

	public void testGetNormalizationBase_2() {

		normalizerFilterSettings.setNormalizationBase(5.7f);
		assertEquals("NormalizationBase", 5.7f, normalizerFilterSettings.getNormalizationBase());
	}

	public void testGetNormalizationBase_3() {

		normalizerFilterSettings.setNormalizationBase(129234.2f);
		assertEquals("NormalizationBase", 129234.2f, normalizerFilterSettings.getNormalizationBase());
	}

	public void testGetNormalizationBase_4() {

		normalizerFilterSettings.setNormalizationBase(0.0f);
		assertEquals("NormalizationBase", ISupplierFilterSettings.DEFAULT_NORMALIZATION_BASE, normalizerFilterSettings.getNormalizationBase());
	}

	public void testGetNormalizationBase_5() {

		normalizerFilterSettings.setNormalizationBase(-1.0f);
		assertEquals("NormalizationBase", ISupplierFilterSettings.DEFAULT_NORMALIZATION_BASE, normalizerFilterSettings.getNormalizationBase());
	}

	public void testGetNormalizationBase_6() {

		normalizerFilterSettings.setNormalizationBase(Float.NaN);
		assertEquals("NormalizationBase", ISupplierFilterSettings.DEFAULT_NORMALIZATION_BASE, normalizerFilterSettings.getNormalizationBase());
	}

	public void testGetNormalizationBase_7() {

		normalizerFilterSettings.setNormalizationBase(Float.POSITIVE_INFINITY);
		assertEquals("NormalizationBase", ISupplierFilterSettings.DEFAULT_NORMALIZATION_BASE, normalizerFilterSettings.getNormalizationBase());
	}

	public void testGetNormalizationBase_8() {

		normalizerFilterSettings.setNormalizationBase(Float.NEGATIVE_INFINITY);
		assertEquals("NormalizationBase", ISupplierFilterSettings.DEFAULT_NORMALIZATION_BASE, normalizerFilterSettings.getNormalizationBase());
	}
}
