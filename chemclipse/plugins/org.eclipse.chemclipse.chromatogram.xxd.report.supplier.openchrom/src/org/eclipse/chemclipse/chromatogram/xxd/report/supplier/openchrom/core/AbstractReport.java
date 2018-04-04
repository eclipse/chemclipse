/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.report.chromatogram.AbstractChromatogramReportGenerator;
import org.eclipse.chemclipse.chromatogram.xxd.report.processing.ChromatogramReportProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.report.processing.IChromatogramReportProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.report.settings.IChromatogramReportSettings;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.settings.IReportSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractReport extends AbstractChromatogramReportGenerator {

	public IChromatogramReportProcessingInfo report(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IReportSettings chromatogramReportSettings, IProgressMonitor monitor) {

		IChromatogramReportProcessingInfo processingInfo = new ChromatogramReportProcessingInfo();
		processingInfo.addErrorMessage("ChemClipse Chromatogram Report", "Please override this method");
		return processingInfo;
	}

	@Override
	public IChromatogramReportProcessingInfo generate(File file, boolean append, IChromatogram<? extends IPeak> chromatogram, IChromatogramReportSettings chromatogramReportSettings, IProgressMonitor monitor) {

		List<IChromatogram<? extends IPeak>> chromatograms = getChromatogramList(chromatogram);
		IReportSettings settings = getSettings(chromatogramReportSettings);
		return report(file, append, chromatograms, settings, monitor);
	}

	@Override
	public IChromatogramReportProcessingInfo generate(File file, boolean append, IChromatogram<? extends IPeak> chromatogram, IProgressMonitor monitor) {

		List<IChromatogram<? extends IPeak>> chromatograms = getChromatogramList(chromatogram);
		IReportSettings settings = getSettings(null);
		return report(file, append, chromatograms, settings, monitor);
	}

	@Override
	public IChromatogramReportProcessingInfo generate(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IChromatogramReportSettings chromatogramReportSettings, IProgressMonitor monitor) {

		IReportSettings settings = getSettings(chromatogramReportSettings);
		return report(file, append, chromatograms, settings, monitor);
	}

	@Override
	public IChromatogramReportProcessingInfo generate(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IProgressMonitor monitor) {

		IReportSettings settings = getSettings(null);
		return report(file, append, chromatograms, settings, monitor);
	}

	private List<IChromatogram<? extends IPeak>> getChromatogramList(IChromatogram<? extends IPeak> chromatogram) {

		List<IChromatogram<? extends IPeak>> chromatograms = new ArrayList<IChromatogram<? extends IPeak>>();
		chromatograms.add(chromatogram);
		return chromatograms;
	}

	private IReportSettings getSettings(IChromatogramReportSettings chromatogramReportSettings) {

		if(chromatogramReportSettings == null) {
			return PreferenceSupplier.getChromatogramReportSettings();
		}
		if(chromatogramReportSettings instanceof IReportSettings) {
			return (IReportSettings)chromatogramReportSettings;
		} else {
			return PreferenceSupplier.getChromatogramReportSettings();
		}
	}
}
