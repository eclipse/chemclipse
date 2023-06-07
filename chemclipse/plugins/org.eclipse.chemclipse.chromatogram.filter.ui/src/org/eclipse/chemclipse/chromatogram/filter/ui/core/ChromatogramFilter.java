/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.filter.settings.MaxDetectorFilterSettings;
import org.eclipse.chemclipse.chromatogram.filter.ui.l10n.Messages;
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
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

public class ChromatogramFilter extends AbstractChromatogramFilter {

	private static final String IDENTIFIER = Messages.scanMaximaDetectorUI;

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		//
		final MaxDetectorFilterSettings filterSettings;
		if(chromatogramFilterSettings instanceof MaxDetectorFilterSettings maxDetectorFilterSettings) {
			filterSettings = maxDetectorFilterSettings;
		} else {
			filterSettings = PreferenceSupplier.getMaxDetectorFilterSettings();
			processingInfo.addWarnMessage(IDENTIFIER, NLS.bind(Messages.settingsNotOfType, MaxDetectorFilterSettings.class));
		}
		//
		if(!processingInfo.hasErrorMessages()) {
			Shell shell = DisplayUtils.getShell();
			if(shell != null) {
				detectScanMaxima(shell, chromatogramSelection, filterSettings);
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
						detectScanMaxima(shell, chromatogramSelection, filterSettings);
						shell.close();
					}
				});
			}
			processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, Messages.scanMaximaDetectionSuccessful));
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramFilterSettings filterSettings = new MaxDetectorFilterSettings();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private void detectScanMaxima(Shell shell, IChromatogramSelection<?,?> chromatogramSelection, MaxDetectorFilterSettings filterSettings) {

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
				//
				List<IScan> scans = extractScanMarker(maximaDetectorService, scanMap, xValues, yValues);
				scans = sortMarker(scans, filterSettings);
				markScans(scans, filterSettings);
			}
		}
	}

	private List<IScan> extractScanMarker(IMaximaDetectorService maximaDetectorService, Map<Double, IScan> scanMap, double[] xValues, double[] yValues) {

		/*
		 * Detect the maxima.
		 * TODO - service settings could be displayed dynamically via JsonAnnotations in the dialog page.
		 */
		IMaximaDetectorSettings maximaDetectorSettings = maximaDetectorService.getSettings();
		double[] positionsX = maximaDetectorService.calculate(xValues, yValues, maximaDetectorSettings);
		//
		List<IScan> scans = new ArrayList<>();
		for(double positionX : positionsX) {
			IScan scan = scanMap.get(positionX);
			if(scan != null) {
				scans.add(scan);
			}
		}
		//
		return scans;
	}

	private List<IScan> sortMarker(List<IScan> scans, MaxDetectorFilterSettings filterSettings) {

		int count = filterSettings.getCount();
		if(count > 0) {
			/*
			 * Filter maximia/minima
			 */
			if(filterSettings.isDetectMinima()) {
				Collections.sort(scans, (s1, s2) -> Float.compare(s1.getTotalSignal(), s2.getTotalSignal()));
			} else {
				Collections.sort(scans, (s1, s2) -> Float.compare(s2.getTotalSignal(), s1.getTotalSignal()));
			}
			//
			int toIndex = scans.size() > count ? count : scans.size();
			return scans.subList(0, toIndex);
		} else {
			/*
			 * Mark all
			 */
			return scans;
		}
	}

	private void markScans(List<IScan> scans, MaxDetectorFilterSettings filterSettings) {

		for(IScan scan : scans) {
			if(scan != null) {
				ILibraryInformation libraryInformation = new LibraryInformation();
				libraryInformation.setName(filterSettings.getTargetName());
				float matchFactor = filterSettings.getMatchFactor();
				ComparisonResult comparisonResult = new ComparisonResult(matchFactor, 0.0f, 0.0f, 0.0f);
				IIdentificationTarget identificationTarget = new IdentificationTarget(libraryInformation, comparisonResult);
				identificationTarget.setIdentifier(IDENTIFIER);
				scan.getTargets().add(identificationTarget);
			}
		}
	}
}
