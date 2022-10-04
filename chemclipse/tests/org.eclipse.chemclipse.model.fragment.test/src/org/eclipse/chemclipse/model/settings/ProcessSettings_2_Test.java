/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.settings;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.implementation.Chromatogram;

import junit.framework.TestCase;

public class ProcessSettings_2_Test extends TestCase {

	private ProcessSettingsTest processSettings = new ProcessSettingsTest();
	private IChromatogram<?> chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new Chromatogram();
		chromatogram.setFile(null);
		chromatogram.putHeaderData("Data Name", null);
		chromatogram.putHeaderData("Sample Group", null);
		chromatogram.putHeaderData("Short Info", null);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		String fileNamePattern = "{chromatogram_name}{extension}";
		String fileName = processSettings.getFileName(chromatogram, fileNamePattern, ".ocb");
		assertEquals("Chromatogram.ocb", fileName);
	}

	public void test2() {

		String fileNamePattern = "{chromatogram_dataname}{extension}";
		String fileName = processSettings.getFileName(chromatogram, fileNamePattern, ".ocb");
		assertEquals("DataName.ocb", fileName);
	}

	public void test3() {

		String fileNamePattern = "{chromatogram_samplegroup}{extension}";
		String fileName = processSettings.getFileName(chromatogram, fileNamePattern, ".ocb");
		assertEquals("SampleGroup.ocb", fileName);
	}

	public void test4() {

		String fileNamePattern = "{chromatogram_shortinfo}{extension}";
		String fileName = processSettings.getFileName(chromatogram, fileNamePattern, ".ocb");
		assertEquals("ShortInfo.ocb", fileName);
	}

	public void test5() {

		String fileNamePattern = "{chromatogram_dataname} {chromatogram_samplegroup} {chromatogram_shortinfo}{extension}";
		String fileName = processSettings.getFileName(chromatogram, fileNamePattern, ".ocb");
		assertEquals("DataName SampleGroup ShortInfo.ocb", fileName);
	}
}