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
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.ICompound;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.ICompounds;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.IHit;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results.NistResultFileParser;

import junit.framework.TestCase;

public class NistResultFileParser_4_Test extends TestCase {

	private NistResultFileParser nistResultFileParser;
	private File results;
	private ICompounds compounds;
	private ICompound compound;
	private IHit hit;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		nistResultFileParser = new NistResultFileParser();
		results = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_NIST_SRCRESLT_3));
		compounds = nistResultFileParser.getCompounds(results);
		compound = compounds.getCompound(1);
		hit = compound.getHit(3);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetIdentifier_1() {

		assertEquals("PEAK-I-NAME", compound.getIdentfier());
	}

	public void testGetCompoundInLibraryFactor_1() {

		assertEquals("-158", compound.getCompoundInLibraryFactor());
	}

	public void testGetName_1() {

		assertEquals("1-Pentanol, 3-methyl-", hit.getName());
	}

	public void testGetFormula_1() {

		assertEquals("C6H14O", hit.getFormula());
	}

	public void testGetMF_1() {

		assertEquals(75.0f, hit.getMF());
	}

	public void testGetRMF_1() {

		assertEquals(75.9f, hit.getRMF());
	}

	public void testGetProb_1() {

		assertEquals(3.89f, hit.getProb());
	}

	public void testGetCAS_1() {

		assertEquals("589-35-5", hit.getCAS());
	}

	public void testGetMw_1() {

		assertEquals(102, hit.getMw());
	}

	public void testGetLib_1() {

		assertEquals("mainlib", hit.getLib());
	}

	public void testGetId_1() {

		assertEquals(430, hit.getId());
	}
}
