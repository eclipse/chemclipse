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
package org.eclipse.chemclipse.chromatogram.filter.impl;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.impl.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsHeaderTransfer;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilterHeaderTransfer extends AbstractChromatogramFilter implements IChromatogramFilter {

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo = validate(chromatogramSelection, chromatogramFilterSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramFilterSettings instanceof FilterSettingsHeaderTransfer filterSettings) {
				/*
				 * Settings
				 */
				IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
				HeaderField headerFieldSource = filterSettings.getHeaderFieldSource();
				String dataSoure = getHeaderField(chromatogram, headerFieldSource);
				//
				HeaderField headerFieldSink = filterSettings.getHeaderFieldSink();
				for(IChromatogram<?> chromatogramReference : chromatogram.getReferencedChromatograms()) {
					setHeaderField(chromatogramReference, headerFieldSink, dataSoure);
				}
			}
		}
		//
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		FilterSettingsHeaderTransfer filterSettings = PreferenceSupplier.getFilterSettingsHeaderTransfer();
		return applyFilter(chromatogramSelection, filterSettings, monitor);
	}

	private String getHeaderField(IChromatogram<?> chromatogram, HeaderField headerField) {

		String data;
		switch(headerField) {
			case NAME:
				data = chromatogram.getName();
				break;
			case DATA_NAME:
				data = chromatogram.getDataName();
				break;
			case MISC_INFO:
				data = chromatogram.getMiscInfo();
				break;
			case SAMPLE_GROUP:
				data = chromatogram.getSampleGroup();
				break;
			case SAMPLE_NAME:
				data = chromatogram.getSampleName();
				break;
			case SHORT_INFO:
				data = chromatogram.getShortInfo();
				break;
			case TAGS:
				data = chromatogram.getTags();
				break;
			default:
				data = "";
				break;
		}
		//
		return data;
	}

	private void setHeaderField(IChromatogram<?> chromatogram, HeaderField headerField, String data) {

		switch(headerField) {
			case NAME:
				chromatogram.setFile(new File(data));
				break;
			case DATA_NAME:
				chromatogram.setDataName(data);
				break;
			case MISC_INFO:
				chromatogram.setMiscInfo(data);
				break;
			case SAMPLE_GROUP:
				chromatogram.setSampleGroup(data);
				break;
			case SAMPLE_NAME:
				chromatogram.setSampleName(data);
				break;
			case SHORT_INFO:
				chromatogram.setShortInfo(data);
				break;
			case TAGS:
				chromatogram.setTags(data);
				break;
			default:
				break;
		}
	}
}