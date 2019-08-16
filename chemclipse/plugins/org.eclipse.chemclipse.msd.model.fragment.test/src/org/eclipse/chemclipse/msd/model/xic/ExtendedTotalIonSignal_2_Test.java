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
package org.eclipse.chemclipse.msd.model.xic;

import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;

import junit.framework.TestCase;

/**
 * Test methods of ExtendedTotalIonSignal.
 * 
 * @author eselmeister
 */
public class ExtendedTotalIonSignal_2_Test extends TestCase {

	private ITotalScanSignal totalIonSignal;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		totalIonSignal = new TotalScanSignal(1524, 0.0f, -346.0f);
	}

	@Override
	protected void tearDown() throws Exception {

		totalIonSignal = null;
		super.tearDown();
	}

	public void testMakeDeepCopy_1() {

		ITotalScanSignal totalIonSignal2 = totalIonSignal.makeDeepCopy();
		assertNotSame(totalIonSignal2, totalIonSignal);
		assertEquals("retention time", totalIonSignal.getRetentionTime(), totalIonSignal2.getRetentionTime());
		assertEquals("retention index", totalIonSignal.getRetentionIndex(), totalIonSignal2.getRetentionIndex());
		assertEquals("total signal", totalIonSignal.getTotalSignal(), totalIonSignal2.getTotalSignal());
	}
}
