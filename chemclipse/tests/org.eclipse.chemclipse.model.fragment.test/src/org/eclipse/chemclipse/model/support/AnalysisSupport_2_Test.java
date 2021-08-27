/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.exceptions.AnalysisSupportException;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class AnalysisSupport_2_Test extends TestCase {

	private IAnalysisSupport support;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		support = null;
		super.tearDown();
	}

	public void testConstructor_1() {

		try {
			support = new AnalysisSupport(0, 0);
		} catch(AnalysisSupportException e) {
			assertTrue("AnalysisSupportException", true);
		}
	}

	public void testConstructor_2() {

		try {
			support = new AnalysisSupport(-1, -1);
		} catch(AnalysisSupportException e) {
			assertTrue("AnalysisSupportException", true);
		}
	}

	public void testConstructor_3() {

		try {
			support = new AnalysisSupport(3, 3);
		} catch(AnalysisSupportException e) {
			assertTrue("AnalysisSupportException", true);
		}
	}

	public void testConstructor_4() {

		try {
			support = new AnalysisSupport(4, 3);
		} catch(AnalysisSupportException e) {
			assertFalse("AnalysisSupportException", true);
		}
		assertEquals("NumberOfAnalysisSegments", 2, support.getNumberOfAnalysisSegments());
	}
}
