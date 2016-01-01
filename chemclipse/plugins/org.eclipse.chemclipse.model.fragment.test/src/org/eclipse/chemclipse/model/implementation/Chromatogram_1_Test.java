/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.IChromatogramResult;

import junit.framework.TestCase;

public class Chromatogram_1_Test extends TestCase {

	private Chromatogram chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new Chromatogram();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void testChromatogram_1() {

		assertEquals(0, chromatogram.getChromatogramResults().size());
	}

	public void testChromatogram_2() {

		String identifier = "test1.identifier";
		String description = "test1.description";
		String result = "Hello World!";
		IChromatogramResult chromatogramResult = new ChromatogramResult(identifier, description, result);
		assertEquals(0, chromatogram.getChromatogramResults().size());
		chromatogram.addChromatogramResult(chromatogramResult);
		assertEquals(1, chromatogram.getChromatogramResults().size());
		Object object = chromatogram.getChromatogramResult(identifier);
		assertTrue(object instanceof ChromatogramResult);
		assertEquals(((ChromatogramResult)object).getIdentifier(), identifier);
		assertEquals(((ChromatogramResult)object).getDescription(), description);
		assertEquals(((ChromatogramResult)object).getResult().toString(), result);
	}
}
