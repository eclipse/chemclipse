/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.txt.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.report.settings.IChromatogramReportSettings;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.txt.internal.support.SpecificationValidator;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.txt.io.ReportWriter3;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.txt.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.txt.settings.ReportSettings3;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class Report3 extends AbstractReport {

	private static final Logger logger = Logger.getLogger(Report3.class);

	@Override
	public IProcessingInfo<File> report(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IChromatogramReportSettings settings, IProgressMonitor monitor) {

		file = SpecificationValidator.validateSpecification(file);
		IProcessingInfo<File> processingInfo = super.validate(file);
		//
		if(!processingInfo.hasErrorMessages()) {
			if(settings instanceof ReportSettings3 reportSettings) {
				try {
					ReportWriter3 chromatogramReport = new ReportWriter3();
					chromatogramReport.generate(file, append, chromatograms, reportSettings, monitor);
					processingInfo.setProcessingResult(file);
				} catch(IOException e) {
					logger.warn(e);
					processingInfo.addErrorMessage("ChemClipse Quantitation Report", "The report couldn't be created. An error occured.");
				}
			} else {
				logger.warn("The settings are not of type: " + ReportSettings3.class);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<File> generate(File file, boolean append, IChromatogram<? extends IPeak> chromatogram, IProgressMonitor monitor) {

		List<IChromatogram<? extends IPeak>> chromatograms = getChromatogramList(chromatogram);
		ReportSettings3 settings = PreferenceSupplier.getReportSettings3();
		return report(file, append, chromatograms, settings, monitor);
	}

	@Override
	public IProcessingInfo<File> generate(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IProgressMonitor monitor) {

		ReportSettings3 settings = PreferenceSupplier.getReportSettings3();
		return report(file, append, chromatograms, settings, monitor);
	}
}