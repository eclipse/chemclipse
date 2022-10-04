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

public class ProcessSettings_3_Test extends TestCase {

	private ProcessSettingsTest processSettings = new ProcessSettingsTest();
	private IChromatogram<?> chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new Chromatogram();
		chromatogram.putHeaderData("Data Name", "Py-GC/MS");
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		String fileNamePattern = "{chromatogram_dataname}{extension}";
		String fileName = processSettings.getFileName(chromatogram, fileNamePattern, ".ocb");
		assertEquals("Py-GC-MS.ocb", fileName); // Replace OS specific path chars by a '-'.
	}
}