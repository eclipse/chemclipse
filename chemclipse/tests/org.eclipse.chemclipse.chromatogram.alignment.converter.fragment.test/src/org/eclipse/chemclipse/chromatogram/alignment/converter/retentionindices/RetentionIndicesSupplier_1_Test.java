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
package org.eclipse.chemclipse.chromatogram.alignment.converter.retentionindices;

import junit.framework.TestCase;

public class RetentionIndicesSupplier_1_Test extends TestCase {

	private IRetentionIndicesSupplier supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		supplier = new RetentionIndicesSupplier();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSupplier_1() {

		assertEquals("Description", "", supplier.getDescription());
	}
}
