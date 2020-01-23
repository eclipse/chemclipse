/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
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

public class NistResultFileParser_3_Test extends TestCase {

	private NistResultFileParser nistResultFileParser;
	private File results;
	private Compounds compounds;
	private Compound compound;
	private Hit hit;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		nistResultFileParser = new NistResultFileParser();
		results = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_NIST_SRCRESLT_2));
		compounds = nistResultFileParser.getCompounds(results);
		compound = compounds.getCompound(3);
		hit = compound.getHit(3);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetCompoundInLibraryFactor_1() {

		assertEquals("-1558", compound.getCompoundInLibraryFactor());
	}

	public void testGetName_1() {

		assertEquals("Hydrazinecarboxylic acid, phenylmethyl ester", hit.getName());
	}

	public void testGetFormula_1() {

		assertEquals("C8H10N2O2", hit.getFormula());
	}

	public void testGetMF_1() {

		assertEquals(67.1f, hit.getMatchFactor());
	}

	public void testGetRMF_1() {

		assertEquals(68.6f, hit.getReverseMatchFactor());
	}

	public void testGetProb_1() {

		assertEquals(45.98f, hit.getProbability());
	}

	public void testGetCAS_1() {

		assertEquals("5331-43-1", hit.getCAS());
	}

	public void testGetMw_1() {

		assertEquals(166, hit.getMolecularWeight());
	}

	public void testGetLib_1() {

		assertEquals("mainlib", hit.getLib());
	}

	public void testGetId_1() {

		assertEquals(2140, hit.getId());
	}
}
