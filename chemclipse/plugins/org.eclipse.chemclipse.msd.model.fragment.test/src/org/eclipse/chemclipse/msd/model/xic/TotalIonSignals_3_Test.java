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

import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;

import junit.framework.TestCase;

public class TotalIonSignals_3_Test extends TestCase {

	private ITotalScanSignals signals;
	private IChromatogramMSD chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testConstruct_1() {

		signals = new TotalScanSignals(10, chromatogram);
		assertNotNull("getChromatogram", signals.getChromatogram());
	}

	public void testConstruct_2() {

		signals = new TotalScanSignals(10, null);
		assertNull("getChromatogram", signals.getChromatogram());
	}

	public void testConstruct_3() {

		signals = new TotalScanSignals(20, 40, chromatogram);
		assertNotNull("getChromatogram", signals.getChromatogram());
	}

	public void testConstruct_4() {

		signals = new TotalScanSignals(20, 40, null);
		assertNull("getChromatogram", signals.getChromatogram());
	}
}
