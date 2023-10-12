/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import junit.framework.TestCase;

public class ComparisonResult_1_Test extends TestCase {

	private IComparisonResult comparisonResult = new ComparisonResult(80.0f);

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertNotNull(comparisonResult.getRatingSupplier());
	}

	public void test2() {

		assertNotNull(ComparisonResult.COMPARISON_RESULT_NO_MATCH);
	}

	public void test3() {

		IComparisonResult comparisonResult = ComparisonResult.COMPARISON_RESULT_NO_MATCH;
		assertEquals(0.0f, comparisonResult.getMatchFactor());
		assertFalse(comparisonResult.isMatch());
		//
		comparisonResult.setMatch(true);
		assertEquals(0.0f, comparisonResult.getMatchFactor());
		assertFalse(comparisonResult.isMatch());
	}

	public void test4() {

		assertNotNull(ComparisonResult.COMPARISON_RESULT_BEST_MATCH);
	}

	public void test5() {

		IComparisonResult comparisonResult = ComparisonResult.COMPARISON_RESULT_BEST_MATCH;
		assertEquals(100.0f, comparisonResult.getMatchFactor());
		assertTrue(comparisonResult.isMatch());
		//
		comparisonResult.setPenalty(20.0f);
		comparisonResult.setMatch(false);
		assertEquals(100.0f, comparisonResult.getMatchFactor());
		assertTrue(comparisonResult.isMatch());
	}
}