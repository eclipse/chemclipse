/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.classifier.result;

import junit.framework.TestCase;

public class AbstractChromatogramClassifierResult_1_Test extends TestCase {

	private IChromatogramClassifierResult chromatogramClassifierResult;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogramClassifierResult = null;
		super.tearDown();
	}

	public void testGetResultStatus_1() {

		ResultStatus status = ResultStatus.EXCEPTION;
		chromatogramClassifierResult = new TestChromatogramClassifierResult(status, "");
		assertEquals("ResultStatus", ResultStatus.EXCEPTION, chromatogramClassifierResult.getResultStatus());
	}

	public void testGetResultStatus_2() {

		ResultStatus status = ResultStatus.OK;
		chromatogramClassifierResult = new TestChromatogramClassifierResult(status, "");
		assertEquals("ResultStatus", ResultStatus.OK, chromatogramClassifierResult.getResultStatus());
	}

	public void testGetResultStatus_3() {

		ResultStatus status = ResultStatus.UNDEFINED;
		chromatogramClassifierResult = new TestChromatogramClassifierResult(status, "");
		assertEquals("ResultStatus", ResultStatus.UNDEFINED, chromatogramClassifierResult.getResultStatus());
	}

	public void testGetDescription_3() {

		ResultStatus status = ResultStatus.UNDEFINED;
		chromatogramClassifierResult = new TestChromatogramClassifierResult(status, "My test description.");
		assertEquals("Description", "My test description.", chromatogramClassifierResult.getDescription());
	}

	public void testGetDescription_4() {

		ResultStatus status = ResultStatus.UNDEFINED;
		chromatogramClassifierResult = new TestChromatogramClassifierResult(status, "");
		assertEquals("Description", "", chromatogramClassifierResult.getDescription());
	}
}