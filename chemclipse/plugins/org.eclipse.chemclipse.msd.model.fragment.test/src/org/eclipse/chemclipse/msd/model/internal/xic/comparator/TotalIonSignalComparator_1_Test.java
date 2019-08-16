/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.internal.xic.comparator;

import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignalComparator;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;

import junit.framework.TestCase;

public class TotalIonSignalComparator_1_Test extends TestCase {

	private ITotalScanSignal totalIonSignal1;
	private ITotalScanSignal totalIonSignal2;
	private TotalScanSignalComparator comparator;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		comparator = new TotalScanSignalComparator();
	}

	@Override
	protected void tearDown() throws Exception {

		totalIonSignal1 = null;
		totalIonSignal2 = null;
		super.tearDown();
	}

	public void testComaparator_1() {

		totalIonSignal1 = new TotalScanSignal(0, 0.0f, 0.0f);
		totalIonSignal2 = new TotalScanSignal(0, 0.0f, 0.0f);
		assertEquals(0, comparator.compare(totalIonSignal1, totalIonSignal2));
	}

	public void testComaparator_2() {

		totalIonSignal1 = null;
		totalIonSignal2 = new TotalScanSignal(0, 0.0f, 0.0f);
		assertEquals(0, comparator.compare(totalIonSignal1, totalIonSignal2));
	}

	public void testComaparator_3() {

		totalIonSignal1 = new TotalScanSignal(0, 0.0f, 0.0f);
		totalIonSignal2 = null;
		assertEquals(0, comparator.compare(totalIonSignal1, totalIonSignal2));
	}

	public void testComaparator_4() {

		totalIonSignal1 = new TotalScanSignal(0, 0.0f, 0.0f);
		totalIonSignal2 = new TotalScanSignal(0, 0.0f, 1.0f);
		assertEquals(-1, comparator.compare(totalIonSignal1, totalIonSignal2));
	}

	public void testComaparator_5() {

		totalIonSignal1 = new TotalScanSignal(0, 0.0f, 1.0f);
		totalIonSignal2 = new TotalScanSignal(0, 0.0f, 0.0f);
		assertEquals(1, comparator.compare(totalIonSignal1, totalIonSignal2));
	}
}
