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
package org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results;

import java.io.File;

import org.eclipse.chemclipse.msd.identifier.supplier.nist.TestPathHelper;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.ICompounds;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.NistResultFileParser;

import junit.framework.TestCase;

public class NistResultFileParser_1_Test extends TestCase {

	private NistResultFileParser nistResultFileParser;
	private File results;
	private ICompounds compounds;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		nistResultFileParser = new NistResultFileParser();
		results = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_NIST_SRCRESLT_1));
		compounds = nistResultFileParser.getCompounds(results);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testCompounds_1() {

		assertNotNull(compounds);
	}

	public void testSize_1() {

		assertEquals("Size", 213, compounds.size());
	}
}
