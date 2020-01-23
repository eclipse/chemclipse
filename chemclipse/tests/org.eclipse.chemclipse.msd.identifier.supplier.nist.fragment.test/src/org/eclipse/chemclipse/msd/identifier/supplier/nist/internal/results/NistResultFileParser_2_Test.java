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

public class NistResultFileParser_2_Test extends TestCase {

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
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSize_1() {

		assertEquals("Size", 4, compounds.size());
	}

	public void testGetCompound_1() {

		compound = compounds.getCompound(1);
		assertEquals(2, compound.size());
	}

	public void testGetCompound_2() {

		compound = compounds.getCompound(2);
		assertEquals(1, compound.size());
	}

	public void testGetCompound_3() {

		compound = compounds.getCompound(3);
		assertEquals(4, compound.size());
	}

	public void testGetCompound_4() {

		compound = compounds.getCompound(4);
		assertEquals(3, compound.size());
	}

	public void testGetHit_1() {

		compound = compounds.getCompound(1);
		hit = compound.getHit(1);
		assertEquals("9-Tetradecen-1-ol, (E)-", hit.getName());
	}

	public void testGetHit_2() {

		compound = compounds.getCompound(1);
		hit = compound.getHit(2);
		assertEquals("1,2-Benzenediol, 4-(2-amino-1-hydroxypropyl)-", hit.getName());
	}

	public void testGetHit_3() {

		compound = compounds.getCompound(2);
		hit = compound.getHit(1);
		assertEquals("9-Tetradecen-1-ol, (E)-", hit.getName());
	}

	public void testGetHit_4() {

		compound = compounds.getCompound(3);
		hit = compound.getHit(1);
		assertEquals("6-Methyl-6-hepten-4-yn-2-ol", hit.getName());
	}

	public void testGetHit_5() {

		compound = compounds.getCompound(3);
		hit = compound.getHit(4);
		assertEquals("Ethylbenzene", hit.getName());
	}

	public void testGetHit_6() {

		compound = compounds.getCompound(4);
		hit = compound.getHit(1);
		assertEquals("9-Tetradecen-1-ol, (E)-", hit.getName());
	}

	public void testGetHit_7() {

		compound = compounds.getCompound(4);
		hit = compound.getHit(3);
		assertEquals("9-Tetradecen-1-ol, (E)-", hit.getName());
	}
}
