/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.model.core.IChromatogramResult;
import org.eclipse.chemclipse.model.implementation.ChromatogramResult;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

import junit.framework.TestCase;

public class ChromatogramResult_1_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IChromatogramResult chromatogramResult;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void testChromatogramResult_1() {

		assertEquals(0, chromatogram.getChromatogramResults().size());
	}

	public void testChromatogramResult_2() {

		chromatogramResult = new ChromatogramResult("result1.id", "Description 1", "TestObject 1");
		chromatogram.addChromatogramResult(chromatogramResult);
		assertEquals(1, chromatogram.getChromatogramResults().size());
	}

	public void testChromatogramResult_3() {

		chromatogramResult = new ChromatogramResult("result1.id", "Description 1", "TestObject 1");
		chromatogram.addChromatogramResult(chromatogramResult);
		chromatogramResult = new ChromatogramResult("result2.id", "Description 2", "TestObject 2");
		chromatogram.addChromatogramResult(chromatogramResult);
		assertEquals(2, chromatogram.getChromatogramResults().size());
	}

	public void testChromatogramResult_4() {

		chromatogramResult = new ChromatogramResult("result1.id", "Description 1", "TestObject 1");
		chromatogram.addChromatogramResult(chromatogramResult);
		chromatogramResult = new ChromatogramResult("result1.id", "Description 2", "TestObject 2");
		chromatogram.addChromatogramResult(chromatogramResult);
		assertEquals(1, chromatogram.getChromatogramResults().size());
		chromatogramResult = chromatogram.getChromatogramResult("result1.id");
		assertEquals("Description 2", chromatogramResult.getDescription());
		assertEquals("TestObject 2", chromatogramResult.getResult());
	}

	public void testChromatogramResult_5() {

		chromatogramResult = new ChromatogramResult("result1.id", "Description 1", "TestObject 1");
		chromatogram.addChromatogramResult(chromatogramResult);
		chromatogramResult = new ChromatogramResult("result2.id", "Description 2", "TestObject 2");
		chromatogram.addChromatogramResult(chromatogramResult);
		assertEquals(2, chromatogram.getChromatogramResults().size());
		chromatogram.deleteChromatogramResult("result2.id");
		assertEquals(1, chromatogram.getChromatogramResults().size());
		chromatogramResult = chromatogram.getChromatogramResult("result1.id");
		assertEquals("Description 1", chromatogramResult.getDescription());
		assertEquals("TestObject 1", chromatogramResult.getResult());
	}

	public void testChromatogramResult_6() {

		chromatogramResult = new ChromatogramResult("result1.id", "Description 1", "TestObject 1");
		chromatogram.addChromatogramResult(chromatogramResult);
		chromatogramResult = new ChromatogramResult("result2.id", "Description 2", "TestObject 2");
		chromatogram.addChromatogramResult(chromatogramResult);
		assertEquals(2, chromatogram.getChromatogramResults().size());
		chromatogram.removeAllChromatogramResults();
		assertEquals(0, chromatogram.getChromatogramResults().size());
	}

	public void testChromatogramResult_7() {

		chromatogram.addChromatogramResult(null);
		assertEquals(0, chromatogram.getChromatogramResults().size());
	}

	public void testChromatogramResult_8() {

		chromatogramResult = new ChromatogramResult("result1.id", "Description 1", "TestObject 1");
		chromatogram.addChromatogramResult(chromatogramResult);
		chromatogramResult = chromatogram.getChromatogramResult(null);
		assertNull(chromatogramResult);
	}

	public void testChromatogramResult_9() {

		chromatogramResult = new ChromatogramResult("result1.id", "Description 1", "TestObject 1");
		chromatogram.addChromatogramResult(chromatogramResult);
		chromatogram.deleteChromatogramResult(null);
		assertEquals(1, chromatogram.getChromatogramResults().size());
	}
}
