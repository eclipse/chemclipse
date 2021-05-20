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
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ChromatogramWSD;

import junit.framework.TestCase;

public class ChromatogramDataSupport_3_Test extends TestCase {

	private IChromatogramWSD chromatogram = new ChromatogramWSD();

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

		assertEquals("Chromatogram", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.DEFAULT, -1, false));
	}

	public void test2() {

		assertEquals("Master Chromatogram", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.DEFAULT, 0, false));
	}

	public void test3() {

		assertEquals("Referenced Chromatogram (1)", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.DEFAULT, 1, false));
	}

	public void test4() {

		assertEquals("Chromatogram [WSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.DEFAULT, -1, true));
	}

	public void test5() {

		assertEquals("Master Chromatogram [WSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.DEFAULT, 0, true));
	}

	public void test6() {

		assertEquals("Referenced Chromatogram (1) [WSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.DEFAULT, 1, true));
	}

	public void test7() {

		assertEquals("This", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.NAME, -1, false));
	}

	public void test8() {

		assertEquals("This", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.NAME, 0, false));
	}

	public void test9() {

		assertEquals("This", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.NAME, 1, false));
	}

	public void test10() {

		assertEquals("This [WSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.NAME, -1, true));
	}

	public void test11() {

		assertEquals("This [WSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.NAME, 0, true));
	}

	public void test12() {

		assertEquals("This [WSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.NAME, 1, true));
	}

	public void test13() {

		assertEquals("is", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.DATA_NAME, -1, false));
	}

	public void test14() {

		assertEquals("is [WSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.DATA_NAME, -1, true));
	}

	public void test15() {

		assertEquals("a", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.SAMPLE_GROUP, -1, false));
	}

	public void test16() {

		assertEquals("a [WSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.SAMPLE_GROUP, -1, true));
	}

	public void test17() {

		assertEquals("test.", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.SHORT_INFO, -1, false));
	}

	public void test18() {

		assertEquals("test. [WSD]", ChromatogramDataSupport.getReferenceLabel(chromatogram, HeaderField.SHORT_INFO, -1, true));
	}
}
