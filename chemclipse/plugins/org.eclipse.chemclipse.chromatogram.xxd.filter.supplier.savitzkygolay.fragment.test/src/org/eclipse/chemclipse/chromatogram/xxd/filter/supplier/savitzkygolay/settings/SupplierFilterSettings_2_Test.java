/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings;

import junit.framework.TestCase;

public class SupplierFilterSettings_2_Test extends TestCase {

	private FilterSettings settings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		settings = new FilterSettings();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		settings.setDerivative(5); // Other than 0 not supported
		assertEquals(0, settings.getDerivative());
	}

	public void test2() {

		settings.setOrder(3);
		assertEquals(3, settings.getOrder());
	}

	public void test3() {

		settings.setWidth(7);
		assertEquals(7, settings.getWidth());
	}
}
