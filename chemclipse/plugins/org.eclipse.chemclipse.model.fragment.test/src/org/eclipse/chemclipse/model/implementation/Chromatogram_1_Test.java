/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IMeasurementResult;

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

		assertEquals(0, chromatogram.getMeasurementResults().size());
	}

	public void testChromatogram_2() {

		String name = "test1.name";
		String identifier = "test1.identifier";
		String description = "test1.description";
		String result = "Hello World!";
		IMeasurementResult chromatogramResult = new MeasurementResult(name, identifier, description, result);
		assertEquals(0, chromatogram.getMeasurementResults().size());
		chromatogram.addMeasurementResult(chromatogramResult);
		assertEquals(1, chromatogram.getMeasurementResults().size());
		Object object = chromatogram.getMeasurementResult(identifier);
		assertTrue(object instanceof MeasurementResult);
		assertEquals(((MeasurementResult)object).getIdentifier(), identifier);
		assertEquals(((MeasurementResult)object).getDescription(), description);
		assertEquals(((MeasurementResult)object).getResult().toString(), result);
	}
}
