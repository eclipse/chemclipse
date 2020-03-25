/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.impl;


import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.impl.settings.FilterSettingsAdjust;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.matrix.ExtractedMatrix;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ChromatogramFilterZeroValueRemoval extends AbstractChromatogramFilterMSD {
	
	private static final Logger logger = Logger.getLogger(ChromatogramFilterZeroValueRemoval.class);

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {
		
		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettingsAdjust) {
				/*
				 * No settings needed yet.
				 */
				// FilterSettingsAdjust filterSettings = (FilterSettingsAdjust)chromatogramFilterSettings;
				if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
					IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
					ExtractedMatrix extract = new ExtractedMatrix(chromatogramSelectionMSD);
					float[][] matrix = extract.getMatrix();
					int numberIons = extract.getNumberOfIons();
					int numberScans = extract.getNumberOfScans();
					
					for(int ionIndex = 0; ionIndex < numberIons; ionIndex++) {
						int startIndex = 0;
						
						// pre-loop to prevent iteration from first scan
						for(int scanIndex = 0; scanIndex < numberScans; scanIndex++) {
							if(matrix[scanIndex][ionIndex] != 0.0) {
								startIndex = scanIndex;
								break;
							}	
						}
						
						// main loop 
						for(int scanIndex = startIndex; scanIndex < numberScans; scanIndex++) {
							if(matrix[scanIndex][ionIndex] == 0.0) {
							} else {
								if(scanIndex == startIndex + 1) {
									startIndex = scanIndex;
								} else {
									matrix = linearInterpolation(matrix, startIndex, scanIndex, ionIndex ); 
									startIndex = scanIndex;
								}
							}
						}
					}
					extract.updateSignal(matrix, numberScans, numberIons);
				}
				//
				processingInfo.setProcessingResult(new ChromatogramFilterResult(ResultStatus.OK, "Chromatogram Filter Adjust applied"));
			}
		}
		//
		return processingInfo;
	}
	
	float[][] linearInterpolation(float[][] matrix, int startColumn, int stopColumn, int row ){
		
		float startSignal = matrix[startColumn][row];
		float stopSignal = matrix[stopColumn][row];
		for(int index = 1; index < (stopColumn - startColumn); index++ ) {
			matrix[startColumn + index][row] = (stopSignal - startSignal) / (stopColumn - startColumn) * index + startSignal;
		}
		return (matrix);
	}
	

	@Override
	public IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsAdjust filterSettings = PreferenceSupplier.getFilterSettingsAdjust();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}
	
}


