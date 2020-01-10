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
package org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramCSD;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;

public class AbstractChromatogramFilter_1_Test extends TestCase {

	private IChromatogramFilterCSD filter;
	private IChromatogramSelectionCSD chromatogramSelection;
	private IChromatogramCSD chromatogram;
	private IChromatogramFilterSettings chromatogramFilterSettings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testConstructor_1() {

		chromatogramSelection = null;
		chromatogramFilterSettings = null;
		filter = new TestChromatogramFilter();
		filter.applyFilter(chromatogramSelection, chromatogramFilterSettings, new NullProgressMonitor());
	}

	public void testConstructor_2() {

		chromatogram = new ChromatogramCSD();
		try {
			chromatogramSelection = new ChromatogramSelectionCSD(chromatogram);
		} catch(ChromatogramIsNullException e) {
			assertTrue("ChromatogramIsNullException", false);
		}
		chromatogramFilterSettings = null;
		filter = new TestChromatogramFilter();
		filter.applyFilter(chromatogramSelection, chromatogramFilterSettings, new NullProgressMonitor());
	}
}
