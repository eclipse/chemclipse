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
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexMarker;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;

import junit.framework.TestCase;

public class RetentionIndexExtrapolator_1_Test extends TestCase {

	private RetentionIndexExtrapolator extrapolator = new RetentionIndexExtrapolator();
	private RetentionIndexMarker retentionIndexMarker = new RetentionIndexMarker();
	private List<IRetentionIndexEntry> retentionIndexEntries = new ArrayList<>();

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * 7.333 1000.0 100 999 C10 (Decane)
		 * 8.85 1100.0 100 999 C11 (Undecane)
		 * 10.3 1200.0 100 999 C12 (Dodecane)
		 * 11.675 1300.0 100 999 C13 (Tridecane)
		 * 12.983 1400.0 100 999 C14 (Tetradecane)
		 */
		retentionIndexMarker.add(new RetentionIndexEntry(getRetentionTimeMilliseconds(7.333d), 1000.0f, "C10 (Decane)"));
		retentionIndexMarker.add(new RetentionIndexEntry(getRetentionTimeMilliseconds(8.85d), 1100.0f, "C11 (Undecane)"));
		retentionIndexMarker.add(new RetentionIndexEntry(getRetentionTimeMilliseconds(10.3d), 1200.0f, "C12 (Dodecane)"));
		retentionIndexMarker.add(new RetentionIndexEntry(getRetentionTimeMilliseconds(11.675d), 1300.0f, "C13 (Tridecane)"));
		retentionIndexMarker.add(new RetentionIndexEntry(getRetentionTimeMilliseconds(12.983d), 1400.0f, "C14 (Tetradecane)"));
		/*
		 * Extrapolate
		 */
		extrapolator.extrapolateMissingAlkaneRanges(retentionIndexMarker);
		retentionIndexEntries.clear();
		retentionIndexEntries.addAll(retentionIndexMarker);
		Collections.sort(retentionIndexEntries, (e1, e2) -> Integer.compare(e1.getRetentionTime(), e2.getRetentionTime()));
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(94, retentionIndexMarker.size());
	}

	public void test2() {

		IRetentionIndexEntry entry = retentionIndexEntries.get(0);
		assertEquals(getRetentionTimeMilliseconds(1.265d), entry.getRetentionTime());
		assertEquals(600.0f, entry.getRetentionIndex());
		assertEquals("C6 (Hexane) -> Extrapolated", entry.getName());
	}

	public void test3() {

		IRetentionIndexEntry entry = retentionIndexEntries.get(3);
		assertEquals(getRetentionTimeMilliseconds(5.816d), entry.getRetentionTime());
		assertEquals(900.0f, entry.getRetentionIndex());
		assertEquals("C9 (Nonane) -> Extrapolated", entry.getName());
	}

	public void test4() {

		IRetentionIndexEntry entry = retentionIndexEntries.get(4);
		assertEquals(getRetentionTimeMilliseconds(7.333d), entry.getRetentionTime());
		assertEquals(1000.0f, entry.getRetentionIndex());
		assertEquals("C10 (Decane)", entry.getName());
	}

	public void test5() {

		IRetentionIndexEntry entry = retentionIndexEntries.get(8);
		assertEquals(getRetentionTimeMilliseconds(12.983d), entry.getRetentionTime());
		assertEquals(1400.0f, entry.getRetentionIndex());
		assertEquals("C14 (Tetradecane)", entry.getName());
	}

	public void test6() {

		IRetentionIndexEntry entry = retentionIndexEntries.get(9);
		assertEquals(getRetentionTimeMilliseconds(14.291d), entry.getRetentionTime());
		assertEquals(1500.0f, entry.getRetentionIndex());
		assertEquals("C15 (Pentadecane) -> Extrapolated", entry.getName());
	}

	public void test7() {

		IRetentionIndexEntry entry = retentionIndexEntries.get(93);
		assertEquals(getRetentionTimeMilliseconds(124.163d), entry.getRetentionTime());
		assertEquals(9900.0f, entry.getRetentionIndex());
		assertEquals("C99 (Nonanonacontane) -> Extrapolated", entry.getName());
	}

	private int getRetentionTimeMilliseconds(double minutes) {

		return (int)(minutes * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
	}
}