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

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.PathResolver;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexFileOption;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.RetentionIndexImporterSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.implementation.Chromatogram;
import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore
public class RetentionIndexImporterTestCase extends TestCase {

	public static final String CHROMATOGRAM_1 = "data/retentionindex/importer/01/chromatogram1.cdf";

	@SuppressWarnings({"unchecked"})
	public IChromatogram<? extends IPeak> getChromatogram(String relativePath) {

		IChromatogram<? extends IPeak> chromatogram = new MyChromatogram();
		chromatogram.setFile(new File(PathResolver.getAbsolutePath(relativePath)));
		Chromatogram chromatogramReference = new Chromatogram();
		chromatogram.addReferencedChromatogram(chromatogramReference);
		//
		return chromatogram;
	}

	public RetentionIndexImporterSettings getRetentionIndexImporterSettings() {

		RetentionIndexImporterSettings settings = new RetentionIndexImporterSettings();
		settings.setRetentionIndexFileOption(RetentionIndexFileOption.CAL);
		settings.setFileNamePattern("");
		settings.setCaseSensitive(true);
		settings.setProcessReferenceChromatograms(true);
		//
		return settings;
	}

	public void apply(IChromatogram<? extends IPeak> chromatogram, RetentionIndexImporterSettings settings) {

		RetentionIndexImporter retentionIndexImporter = new RetentionIndexImporter();
		retentionIndexImporter.apply(chromatogram, settings);
	}

	private class MyChromatogram extends Chromatogram {

		private static final long serialVersionUID = 688258405302662443L;

		@Override
		public String getName() {

			/*
			 * Strip .cdf extension.
			 */
			String name = getFile().getName();
			return name.substring(0, name.length() - 4);
		}
	}
}