/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts;

import java.io.File;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.ReferencesLabel;

import junit.framework.TestCase;

public class ChromatogramDataSupport_2_Test extends TestCase {

	private IChromatogramMSD chromatogram = new ChromatogramMSD();

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		setData(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	private void setData(IChromatogram<?> chromatogram) {

		chromatogram.setFile(new File("This"));
		chromatogram.setDataName("is");
		chromatogram.setSampleGroup("a");
		chromatogram.setShortInfo("test.");
	}

	public void test1() {

		assertEquals("Chromatogram", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.DEFAULT, -1, false));
	}

	public void test2() {

		assertEquals("Master Chromatogram", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.DEFAULT, 0, false));
	}

	public void test3() {

		assertEquals("Referenced Chromatogram (1)", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.DEFAULT, 1, false));
	}

	public void test4() {

		assertEquals("Chromatogram [MSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.DEFAULT, -1, true));
	}

	public void test5() {

		assertEquals("Master Chromatogram [MSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.DEFAULT, 0, true));
	}

	public void test6() {

		assertEquals("Referenced Chromatogram (1) [MSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.DEFAULT, 1, true));
	}

	public void test7() {

		assertEquals("This", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.NAME, -1, false));
	}

	public void test8() {

		assertEquals("This", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.NAME, 0, false));
	}

	public void test9() {

		assertEquals("This", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.NAME, 1, false));
	}

	public void test10() {

		assertEquals("This [MSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.NAME, -1, true));
	}

	public void test11() {

		assertEquals("This [MSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.NAME, 0, true));
	}

	public void test12() {

		assertEquals("This [MSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.NAME, 1, true));
	}

	public void test13() {

		assertEquals("is", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.DATA_NAME, -1, false));
	}

	public void test14() {

		assertEquals("is [MSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.DATA_NAME, -1, true));
	}

	public void test15() {

		assertEquals("a", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.SAMPLE_GROUP, -1, false));
	}

	public void test16() {

		assertEquals("a [MSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.SAMPLE_GROUP, -1, true));
	}

	public void test17() {

		assertEquals("test.", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.SHORT_INFO, -1, false));
	}

	public void test18() {

		assertEquals("test. [MSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, ReferencesLabel.SHORT_INFO, -1, true));
	}
}
