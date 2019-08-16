/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.parser;

import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.TestPathHelper;

import junit.framework.TestCase;

public class MGSparserSuplier_ITest extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_PPW_ppw_L22_142651859102_CLASIC_SPECTRA);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}
}
