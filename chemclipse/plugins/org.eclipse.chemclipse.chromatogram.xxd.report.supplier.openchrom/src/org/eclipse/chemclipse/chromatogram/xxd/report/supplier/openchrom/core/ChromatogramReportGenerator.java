/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.chromatogram.xxd.report.chromatogram.AbstractChromatogramReportGenerator;
import org.eclipse.chemclipse.chromatogram.xxd.report.processing.ChromatogramReportProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.report.processing.IChromatogramReportProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.report.settings.IChromatogramReportSettings;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.internal.support.SpecificationValidator;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.io.ChromatogramReport;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.settings.IChemClipseChromatogramReportSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public class ChromatogramReportGenerator extends AbstractChromatogramReportGenerator {

	private static final Logger logger = Logger.getLogger(ChromatogramReportGenerator.class);

	public ChromatogramReportGenerator() {
	}

	@Override
	public IChromatogramReportProcessingInfo generate(File file, boolean append, IChromatogram chromatogram, IChromatogramReportSettings chromatogramReportSettings, IProgressMonitor monitor) {

		List<IChromatogram> chromatograms = getChromatogramList(chromatogram);
		IChemClipseChromatogramReportSettings settings = getSettings(chromatogramReportSettings);
		return report(file, append, chromatograms, settings, monitor);
	}

	@Override
	public IChromatogramReportProcessingInfo generate(File file, boolean append, IChromatogram chromatogram, IProgressMonitor monitor) {

		List<IChromatogram> chromatograms = getChromatogramList(chromatogram);
		IChemClipseChromatogramReportSettings settings = getSettings(null);
		return report(file, append, chromatograms, settings, monitor);
	}

	@Override
	public IChromatogramReportProcessingInfo generate(File file, boolean append, List<IChromatogram> chromatograms, IChromatogramReportSettings chromatogramReportSettings, IProgressMonitor monitor) {

		IChemClipseChromatogramReportSettings settings = getSettings(chromatogramReportSettings);
		return report(file, append, chromatograms, settings, monitor);
	}

	@Override
	public IChromatogramReportProcessingInfo generate(File file, boolean append, List<IChromatogram> chromatograms, IProgressMonitor monitor) {

		IChemClipseChromatogramReportSettings settings = getSettings(null);
		return report(file, append, chromatograms, settings, monitor);
	}

	private IChromatogramReportProcessingInfo report(File file, boolean append, List<IChromatogram> chromatograms, IChemClipseChromatogramReportSettings chromatogramReportSettings, IProgressMonitor monitor) {

		IChromatogramReportProcessingInfo processingInfo = new ChromatogramReportProcessingInfo();
		/*
		 * Validate the file.
		 */
		file = SpecificationValidator.validateSpecification(file);
		IProcessingInfo processingInfoValidate = super.validate(file);
		/*
		 * Don't process if errors have occurred.
		 */
		if(processingInfoValidate.hasErrorMessages()) {
			processingInfo.addMessages(processingInfoValidate);
		} else {
			ChromatogramReport chromatogramReport = new ChromatogramReport();
			try {
				chromatogramReport.generate(file, append, chromatograms, chromatogramReportSettings, monitor);
				processingInfo.setFile(file);
			} catch(IOException e) {
				logger.warn(e);
				processingInfo.addErrorMessage("ChemClipse Chromatogram Report", "The report couldn't be created. An error occured.");
			}
		}
		return processingInfo;
	}

	private List<IChromatogram> getChromatogramList(IChromatogram chromatogram) {

		List<IChromatogram> chromatograms = new ArrayList<IChromatogram>();
		chromatograms.add(chromatogram);
		return chromatograms;
	}

	private IChemClipseChromatogramReportSettings getSettings(IChromatogramReportSettings chromatogramReportSettings) {

		if(chromatogramReportSettings == null) {
			return PreferenceSupplier.getChromatogramReportSettings();
		}
		if(chromatogramReportSettings instanceof IChemClipseChromatogramReportSettings) {
			return (IChemClipseChromatogramReportSettings)chromatogramReportSettings;
		} else {
			return PreferenceSupplier.getChromatogramReportSettings();
		}
	}
}
