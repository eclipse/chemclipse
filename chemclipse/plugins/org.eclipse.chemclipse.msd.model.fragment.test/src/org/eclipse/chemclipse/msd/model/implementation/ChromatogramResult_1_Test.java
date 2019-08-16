/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.implementation.MeasurementResult;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

import junit.framework.TestCase;

public class ChromatogramResult_1_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IMeasurementResult chromatogramResult;

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

		assertEquals(0, chromatogram.getMeasurementResults().size());
	}

	public void testChromatogramResult_2() {

		chromatogramResult = new MeasurementResult("name", "result1.id", "Description 1", "TestObject 1");
		chromatogram.addMeasurementResult(chromatogramResult);
		assertEquals(1, chromatogram.getMeasurementResults().size());
	}

	public void testChromatogramResult_3() {

		chromatogramResult = new MeasurementResult("name", "result1.id", "Description 1", "TestObject 1");
		chromatogram.addMeasurementResult(chromatogramResult);
		chromatogramResult = new MeasurementResult("name", "result2.id", "Description 2", "TestObject 2");
		chromatogram.addMeasurementResult(chromatogramResult);
		assertEquals(2, chromatogram.getMeasurementResults().size());
	}

	public void testChromatogramResult_4() {

		chromatogramResult = new MeasurementResult("name", "result1.id", "Description 1", "TestObject 1");
		chromatogram.addMeasurementResult(chromatogramResult);
		chromatogramResult = new MeasurementResult("name", "result1.id", "Description 2", "TestObject 2");
		chromatogram.addMeasurementResult(chromatogramResult);
		assertEquals(1, chromatogram.getMeasurementResults().size());
		chromatogramResult = chromatogram.getMeasurementResult("result1.id");
		assertEquals("Description 2", chromatogramResult.getDescription());
		assertEquals("TestObject 2", chromatogramResult.getResult());
	}

	public void testChromatogramResult_5() {

		chromatogramResult = new MeasurementResult("name", "result1.id", "Description 1", "TestObject 1");
		chromatogram.addMeasurementResult(chromatogramResult);
		chromatogramResult = new MeasurementResult("name", "result2.id", "Description 2", "TestObject 2");
		chromatogram.addMeasurementResult(chromatogramResult);
		assertEquals(2, chromatogram.getMeasurementResults().size());
		chromatogram.deleteMeasurementResult("result2.id");
		assertEquals(1, chromatogram.getMeasurementResults().size());
		chromatogramResult = chromatogram.getMeasurementResult("result1.id");
		assertEquals("Description 1", chromatogramResult.getDescription());
		assertEquals("TestObject 1", chromatogramResult.getResult());
	}

	public void testChromatogramResult_6() {

		chromatogramResult = new MeasurementResult("name", "result1.id", "Description 1", "TestObject 1");
		chromatogram.addMeasurementResult(chromatogramResult);
		chromatogramResult = new MeasurementResult("name", "result2.id", "Description 2", "TestObject 2");
		chromatogram.addMeasurementResult(chromatogramResult);
		assertEquals(2, chromatogram.getMeasurementResults().size());
		chromatogram.removeAllMeasurementResults();
		assertEquals(0, chromatogram.getMeasurementResults().size());
	}

	public void testChromatogramResult_7() {

		chromatogram.addMeasurementResult(null);
		assertEquals(0, chromatogram.getMeasurementResults().size());
	}

	public void testChromatogramResult_8() {

		chromatogramResult = new MeasurementResult("name", "result1.id", "Description 1", "TestObject 1");
		chromatogram.addMeasurementResult(chromatogramResult);
		chromatogramResult = chromatogram.getMeasurementResult((String)null);
		assertNull(chromatogramResult);
	}

	public void testChromatogramResult_9() {

		chromatogramResult = new MeasurementResult("name", "result1.id", "Description 1", "TestObject 1");
		chromatogram.addMeasurementResult(chromatogramResult);
		chromatogram.deleteMeasurementResult(null);
		assertEquals(1, chromatogram.getMeasurementResults().size());
	}
}
