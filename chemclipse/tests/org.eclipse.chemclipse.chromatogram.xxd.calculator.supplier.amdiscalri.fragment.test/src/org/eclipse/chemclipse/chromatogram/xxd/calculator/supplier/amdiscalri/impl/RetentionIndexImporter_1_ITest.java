/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.RetentionIndexImporterSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;

public class RetentionIndexImporter_1_ITest extends RetentionIndexImporterTestCase {

	public void test1a() {

		RetentionIndexImporterSettings settings = getRetentionIndexImporterSettings();
		//
		IChromatogram<? extends IPeak> chromatogram = getChromatogram(CHROMATOGRAM_1);
		apply(chromatogram, settings);
		//
		assertEquals(5, chromatogram.getSeparationColumnIndices().size());
		assertEquals(5, chromatogram.getReferencedChromatograms().get(0).getSeparationColumnIndices().size());
	}

	public void test1b() {

		RetentionIndexImporterSettings settings = getRetentionIndexImporterSettings();
		settings.setCaseSensitive(false);
		//
		IChromatogram<? extends IPeak> chromatogram = getChromatogram(CHROMATOGRAM_1);
		apply(chromatogram, settings);
		//
		assertEquals(5, chromatogram.getSeparationColumnIndices().size());
		assertEquals(5, chromatogram.getReferencedChromatograms().get(0).getSeparationColumnIndices().size());
	}

	public void test1c() {

		RetentionIndexImporterSettings settings = getRetentionIndexImporterSettings();
		settings.setFileNamePattern("");
		//
		IChromatogram<? extends IPeak> chromatogram = getChromatogram(CHROMATOGRAM_1);
		apply(chromatogram, settings);
		//
		assertEquals(5, chromatogram.getSeparationColumnIndices().size());
		assertEquals(5, chromatogram.getReferencedChromatograms().get(0).getSeparationColumnIndices().size());
	}

	public void test1d() {

		RetentionIndexImporterSettings settings = getRetentionIndexImporterSettings();
		settings.setFileNamePattern("{chromatogram}");
		//
		IChromatogram<? extends IPeak> chromatogram = getChromatogram(CHROMATOGRAM_1);
		apply(chromatogram, settings);
		//
		assertEquals(10, chromatogram.getSeparationColumnIndices().size());
		assertEquals(10, chromatogram.getReferencedChromatograms().get(0).getSeparationColumnIndices().size());
	}

	public void test1e() {

		RetentionIndexImporterSettings settings = getRetentionIndexImporterSettings();
		settings.setFileNamePattern("heptane C7 - tridecane C13");
		//
		IChromatogram<? extends IPeak> chromatogram = getChromatogram(CHROMATOGRAM_1);
		apply(chromatogram, settings);
		//
		assertEquals(7, chromatogram.getSeparationColumnIndices().size());
		assertEquals(7, chromatogram.getReferencedChromatograms().get(0).getSeparationColumnIndices().size());
	}

	public void test1f() {

		RetentionIndexImporterSettings settings = getRetentionIndexImporterSettings();
		settings.setFileNamePattern("(.*)(C7)(.*)(C13)");
		//
		IChromatogram<? extends IPeak> chromatogram = getChromatogram(CHROMATOGRAM_1);
		apply(chromatogram, settings);
		//
		assertEquals(7, chromatogram.getSeparationColumnIndices().size());
		assertEquals(7, chromatogram.getReferencedChromatograms().get(0).getSeparationColumnIndices().size());
	}

	public void test2a() {

		RetentionIndexImporterSettings settings = getRetentionIndexImporterSettings();
		settings.setProcessReferenceChromatograms(false);
		//
		IChromatogram<? extends IPeak> chromatogram = getChromatogram(CHROMATOGRAM_1);
		apply(chromatogram, settings);
		//
		assertEquals(5, chromatogram.getSeparationColumnIndices().size());
		assertEquals(0, chromatogram.getReferencedChromatograms().get(0).getSeparationColumnIndices().size());
	}

	public void test2d() {

		RetentionIndexImporterSettings settings = getRetentionIndexImporterSettings();
		settings.setProcessReferenceChromatograms(false);
		settings.setFileNamePattern("{chromatogram}");
		//
		IChromatogram<? extends IPeak> chromatogram = getChromatogram(CHROMATOGRAM_1);
		apply(chromatogram, settings);
		//
		assertEquals(10, chromatogram.getSeparationColumnIndices().size());
		assertEquals(0, chromatogram.getReferencedChromatograms().get(0).getSeparationColumnIndices().size());
	}
}