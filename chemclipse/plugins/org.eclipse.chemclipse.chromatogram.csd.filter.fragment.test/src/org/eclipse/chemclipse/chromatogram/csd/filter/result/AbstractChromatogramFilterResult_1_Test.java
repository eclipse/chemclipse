/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.filter.result;

import junit.framework.TestCase;

import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;

public class AbstractChromatogramFilterResult_1_Test extends TestCase {

	private IChromatogramFilterResult chromatogramFilterResult;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogramFilterResult = null;
		super.tearDown();
	}

	public void testGetResultStatus_1() {

		ResultStatus status = ResultStatus.EXCEPTION;
		chromatogramFilterResult = new TestChromatogramFilterResult(status, "");
		assertEquals("ResultStatus", ResultStatus.EXCEPTION, chromatogramFilterResult.getResultStatus());
	}

	public void testGetResultStatus_2() {

		ResultStatus status = ResultStatus.OK;
		chromatogramFilterResult = new TestChromatogramFilterResult(status, "");
		assertEquals("ResultStatus", ResultStatus.OK, chromatogramFilterResult.getResultStatus());
	}

	public void testGetResultStatus_3() {

		ResultStatus status = ResultStatus.UNDEFINED;
		chromatogramFilterResult = new TestChromatogramFilterResult(status, "");
		assertEquals("ResultStatus", ResultStatus.UNDEFINED, chromatogramFilterResult.getResultStatus());
	}

	public void testGetDescription_3() {

		ResultStatus status = ResultStatus.UNDEFINED;
		chromatogramFilterResult = new TestChromatogramFilterResult(status, "My test description.");
		assertEquals("Description", "My test description.", chromatogramFilterResult.getDescription());
	}

	public void testGetDescription_4() {

		ResultStatus status = ResultStatus.UNDEFINED;
		chromatogramFilterResult = new TestChromatogramFilterResult(status, "");
		assertEquals("Description", "", chromatogramFilterResult.getDescription());
	}
}
