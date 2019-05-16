/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
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
import java.io.IOException;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.report.settings.IChromatogramReportSettings;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.internal.support.SpecificationValidator;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.io.ReportWriter1;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.settings.ReportSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class Report1 extends AbstractReport {

	private static final Logger logger = Logger.getLogger(Report1.class);

	@Override
	public IProcessingInfo<File> report(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IChromatogramReportSettings settings, IProgressMonitor monitor) {

		file = SpecificationValidator.validateSpecification(file);
		IProcessingInfo<File> processingInfo = super.validate(file);
		//
		if(!processingInfo.hasErrorMessages()) {
			if(settings instanceof ReportSettings) {
				ReportSettings reportSettings = (ReportSettings)settings;
				ReportWriter1 chromatogramReport = new ReportWriter1();
				try {
					chromatogramReport.generate(file, append, chromatograms, reportSettings, monitor);
					processingInfo.setProcessingResult(file);
				} catch(IOException e) {
					logger.warn(e);
					processingInfo.addErrorMessage("ChemClipse Chromatogram Report", "The report couldn't be created. An error occured.");
				}
			} else {
				logger.warn("The settings are not of type: " + ReportSettings.class);
			}
		}
		//
		return processingInfo;
	}
}
