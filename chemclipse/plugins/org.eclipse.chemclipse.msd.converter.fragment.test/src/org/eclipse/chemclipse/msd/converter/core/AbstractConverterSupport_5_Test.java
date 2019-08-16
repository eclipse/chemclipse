/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.core;

import java.util.List;

import org.eclipse.chemclipse.converter.core.IConverterSupportSetter;
import org.eclipse.chemclipse.converter.core.ISupplier;

public class AbstractConverterSupport_5_Test extends AbstractConverterTestCase {

	private IConverterSupportSetter converterSupport;
	private List<ISupplier> supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		converterSupport = getConverterSupport();
		supplier = converterSupport.getSupplier();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetSupplier_1() {

		assertEquals(5, supplier.size());
	}

	public void testGetSupplier_2() {

		assertEquals("org.eclipse.chemclipse.msd.converter.supplier.agilent", supplier.get(0).getId());
	}

	public void testGetSupplier_3() {

		assertEquals("org.eclipse.chemclipse.msd.converter.supplier.agilent.msd1", supplier.get(1).getId());
	}

	public void testGetSupplier_4() {

		assertEquals("net.openchrom.msd.converter.supplier.cdf", supplier.get(2).getId());
	}

	public void testGetSupplier_5() {

		assertEquals("org.eclipse.chemclipse.msd.converter.supplier.excel", supplier.get(3).getId());
	}

	public void testGetSupplier_6() {

		assertEquals("org.eclipse.chemclipse.msd.converter.supplier.test", supplier.get(4).getId());
	}
}
