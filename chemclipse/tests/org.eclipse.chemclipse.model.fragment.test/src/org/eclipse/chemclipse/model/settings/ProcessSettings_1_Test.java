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

import java.io.File;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.implementation.Chromatogram;

import junit.framework.TestCase;

public class ProcessSettings_1_Test extends TestCase {

	private ProcessSettingsTest processSettings = new ProcessSettingsTest();
	private IChromatogram<?> chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new Chromatogram();
		chromatogram.setFile(new File("Chromatogram"));
		chromatogram.putHeaderData("Data Name", "This is");
		chromatogram.putHeaderData("Sample Group", "a group name");
		chromatogram.putHeaderData("Short Info", "and short info demo");
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
		assertEquals("This is.ocb", fileName);
	}

	public void test3() {

		String fileNamePattern = "{chromatogram_samplegroup}{extension}";
		String fileName = processSettings.getFileName(chromatogram, fileNamePattern, ".ocb");
		assertEquals("a group name.ocb", fileName);
	}

	public void test4() {

		String fileNamePattern = "{chromatogram_shortinfo}{extension}";
		String fileName = processSettings.getFileName(chromatogram, fileNamePattern, ".ocb");
		assertEquals("and short info demo.ocb", fileName);
	}

	public void test5() {

		String fileNamePattern = "{chromatogram_dataname} {chromatogram_samplegroup} {chromatogram_shortinfo}{extension}";
		String fileName = processSettings.getFileName(chromatogram, fileNamePattern, ".ocb");
		assertEquals("This is a group name and short info demo.ocb", fileName);
	}
}