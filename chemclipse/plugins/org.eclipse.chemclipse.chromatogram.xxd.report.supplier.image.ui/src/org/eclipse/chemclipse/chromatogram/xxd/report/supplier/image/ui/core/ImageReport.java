/*******************************************************************************
 * Copyright (c) 2023, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.core;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.report.chromatogram.AbstractChromatogramReportGenerator;
import org.eclipse.chemclipse.chromatogram.xxd.report.settings.IChromatogramReportSettings;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.settings.IChromatogramImageReportSettings;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.settings.ImageReportSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

public class ImageReport extends AbstractChromatogramReportGenerator {

	@Override
	public IProcessingInfo<?> generate(File file, boolean append, IChromatogram chromatogram, IChromatogramReportSettings chromatogramReportSettings, IProgressMonitor monitor) {

		IProcessingInfo<File> processingInfo = super.validate(file);

		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramReportSettings instanceof IChromatogramImageReportSettings imageReportSettings) {
				ImageRunnableGeneric imageRunnable = new ImageRunnableGeneric(file, chromatogram, imageReportSettings);
				Display.getDefault().syncExec(imageRunnable);
				processingInfo.setProcessingResult(file);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> generate(File file, boolean append, IChromatogram chromatogram, IProgressMonitor monitor) {

		ImageReportSettings settings = PreferenceSupplier.getReportSettings();
		return generate(file, append, chromatogram, settings, monitor);
	}

	@Override
	public IProcessingInfo<?> generate(File file, boolean append, List<IChromatogram> chromatograms, IChromatogramReportSettings chromatogramReportSettings, IProgressMonitor monitor) {

		IProcessingInfo<File> processingInfo = super.validate(file);
		processingInfo.addErrorMessage("Image Report", "Only accepts single chromatograms at the moment");
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> generate(File file, boolean append, List<IChromatogram> chromatograms, IProgressMonitor monitor) {

		IProcessingInfo<File> processingInfo = super.validate(file);
		processingInfo.addErrorMessage("Image Report", "Only accepts single chromatograms at the moment");
		return processingInfo;
	}
}
