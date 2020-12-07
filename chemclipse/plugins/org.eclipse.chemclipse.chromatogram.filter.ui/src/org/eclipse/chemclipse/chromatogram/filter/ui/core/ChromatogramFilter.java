/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.ui.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.numeric.services.IMaximaDetectorService;
import org.eclipse.chemclipse.numeric.services.IMaximaDetectorSettings;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("rawtypes")
public class ChromatogramFilter extends AbstractChromatogramFilter {

	@SuppressWarnings("unchecked")
	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			Shell shell = DisplayUtils.getShell();
			if(shell != null) {
				detectScanMaxima(shell, chromatogramSelection);
			} else {
				DisplayUtils.getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {

						/*
						 * Create a new shell and set
						 * the size to 0 cause only the wizard
						 * will be shown.
						 */
						Shell shell = new Shell();
						shell.setSize(0, 0);
						shell.open();
						//
						detectScanMaxima(shell, chromatogramSelection);
						shell.close();
					}
				});
			}
			processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "The scan maxima detection was successful."));
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramFilterSettings filterSettings = new FilterSettings();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private void detectScanMaxima(Shell shell, IChromatogramSelection chromatogramSelection) {

		ChromatogramFilterDialog dialog = new ChromatogramFilterDialog(shell);
		if(IDialogConstants.OK_ID == dialog.open()) {
			IMaximaDetectorService maximaDetectorService = dialog.getMaximaDetectorService();
			if(maximaDetectorService != null) {
				/*
				 * Extract the selection.
				 */
				IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
				int startScan = chromatogramSelection.getStartScan();
				int stopScan = chromatogramSelection.getStopScan();
				int size = stopScan - startScan + 1;
				//
				Map<Double, IScan> scanMap = new HashMap<>();
				double[] xValues = new double[size];
				double[] yValues = new double[size];
				int j = 0;
				for(int i = startScan; i <= stopScan; i++) {
					IScan scan = chromatogram.getScan(i);
					xValues[j] = scan.getX();
					yValues[j] = scan.getY();
					scanMap.put(scan.getX(), scan);
					j++;
				}
				/*
				 * Detect the maxima.
				 * TODO - service settings could be displayed dynamically via JsonAnnotations in the dialog page.
				 */
				IMaximaDetectorSettings maximaDetectorSettings = maximaDetectorService.getSettings();
				double[] maxima = maximaDetectorService.calculate(xValues, yValues, maximaDetectorSettings);
				for(double maximum : maxima) {
					IScan scan = scanMap.get(maximum);
					if(scan != null) {
						ILibraryInformation libraryInformation = new LibraryInformation();
						libraryInformation.setName("M");
						ComparisonResult comparisonResult = ComparisonResult.createBestMatchComparisonResult();
						IIdentificationTarget identificationTarget = new IdentificationTarget(libraryInformation, comparisonResult);
						scan.getTargets().add(identificationTarget);
					}
				}
			}
		}
	}
}
