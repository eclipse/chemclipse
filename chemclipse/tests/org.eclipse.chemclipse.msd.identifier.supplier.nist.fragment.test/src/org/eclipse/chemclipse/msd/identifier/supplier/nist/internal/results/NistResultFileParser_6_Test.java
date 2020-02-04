/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
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

import junit.framework.TestCase;

public class NistResultFileParser_6_Test extends TestCase {

	private NistResultFileParser nistResultFileParser;
	private File results;
	private Compounds compounds;
	private Compound compound;
	private Hit hit;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		nistResultFileParser = new NistResultFileParser();
		results = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_NIST_SRCRESLT_4));
		compounds = nistResultFileParser.getCompounds(results);
		compound = compounds.getCompound(2);
		hit = compound.getHit(2);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetIdentifier_1() {

		assertEquals("ID-2", compound.getIdentfier());
	}

	public void testGetCompoundInLibraryFactor_1() {

		assertEquals("300", compound.getCompoundInLibraryFactor());
	}

	public void testGetName_1() {

		assertEquals("Ethane, 1,2-diethoxy-", hit.getName());
	}

	public void testGetFormula_1() {

		assertEquals("C6H14O2", hit.getFormula());
	}

	public void testGetMF_1() {

		assertEquals(82.1f, hit.getMatchFactor());
	}

	public void testGetRMF_1() {

		assertEquals(82.7f, hit.getReverseMatchFactor());
	}

	public void testGetProb_1() {

		assertEquals(12.82f, hit.getProbability());
	}

	public void testGetCAS_1() {

		assertEquals("629-14-1", hit.getCAS());
	}

	public void testGetMw_1() {

		assertEquals(118, hit.getMolecularWeight());
	}

	public void testGetLib_1() {

		assertEquals("mainlib", hit.getLib());
	}

	public void testGetId_1() {

		assertEquals(32113, hit.getId());
	}

	public void testGetRI_1() {

		assertEquals(750, hit.getRetentionIndex());
	}
}
