/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.txt.io.ReportWriter2;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.txt.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.txt.settings.ReportSettings2;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class Report2 extends AbstractReport {

	private static final Logger logger = Logger.getLogger(Report2.class);

	@Override
	public IProcessingInfo<File> report(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IChromatogramReportSettings settings, IProgressMonitor monitor) {

		file = SpecificationValidator.validateSpecification(file);
		IProcessingInfo<File> processingInfo = super.validate(file);
		//
		if(!processingInfo.hasErrorMessages()) {
			if(settings instanceof ReportSettings2) {
				ReportSettings2 reportSettings = (ReportSettings2)settings;
				ReportWriter2 chromatogramReport = new ReportWriter2();
				try {
					chromatogramReport.generate(file, append, chromatograms, reportSettings, monitor);
					processingInfo.setProcessingResult(file);
				} catch(IOException e) {
					logger.warn(e);
					processingInfo.addErrorMessage("ChemClipse Chromatogram Report", "The report couldn't be created. An error occured.");
				}
			} else {
				logger.warn("The settings are not of type: " + ReportSettings2.class);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<File> generate(File file, boolean append, IChromatogram<? extends IPeak> chromatogram, IProgressMonitor monitor) {

		List<IChromatogram<? extends IPeak>> chromatograms = getChromatogramList(chromatogram);
		ReportSettings2 settings = PreferenceSupplier.getReportSettings2();
		return report(file, append, chromatograms, settings, monitor);
	}

	@Override
	public IProcessingInfo<File> generate(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IProgressMonitor monitor) {

		ReportSettings2 settings = PreferenceSupplier.getReportSettings2();
		return report(file, append, chromatograms, settings, monitor);
	}
}