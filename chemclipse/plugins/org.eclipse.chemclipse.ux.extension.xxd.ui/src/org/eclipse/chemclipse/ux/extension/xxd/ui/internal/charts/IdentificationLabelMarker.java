/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.support.Fonts;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PeakDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.marker.LabelMarker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;

public class IdentificationLabelMarker extends LabelMarker {

	private Font font = DisplayUtils.getDisplay().getSystemFont();

	/**
	 * Peak or Scan can be null. If null, it won't be processed.
	 * 
	 * @param baseChart
	 * @param indexSeries
	 * @param peaks
	 * @param scans
	 */
	public IdentificationLabelMarker(BaseChart baseChart, int indexSeries, List<IPeak> peaks, List<IScan> scans) {
		super(baseChart);
		//
		List<String> labels = new ArrayList<String>();
		labels.addAll(getPeakLabels(peaks));
		labels.addAll(getScanLabels(scans));
		setLabels(labels, indexSeries, SWT.VERTICAL);
	}

	@Override
	public void paintControl(PaintEvent e) {

		Font currentFont = e.gc.getFont();
		e.gc.setFont(font);
		super.paintControl(e);
		e.gc.setFont(currentFont);
	}

	private List<String> getPeakLabels(List<IPeak> peaks) {

		List<String> labels = new ArrayList<String>();
		//
		if(peaks != null) {
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			String name = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_NAME);
			int height = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE);
			int style = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE);
			font = Fonts.getFont(name, height, style);
			//
			PeakDataSupport peakDataSupport = new PeakDataSupport();
			for(IPeak peak : peaks) {
				ILibraryInformation libraryInformation = peakDataSupport.getBestLibraryInformation(peak.getTargets());
				String substance = "";
				if(libraryInformation != null) {
					substance = libraryInformation.getName();
				}
				labels.add(substance);
			}
		}
		//
		return labels;
	}

	private List<String> getScanLabels(List<IScan> scans) {

		List<String> labels = new ArrayList<String>();
		//
		if(scans != null) {
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			String name = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_NAME);
			int height = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE);
			int style = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_STYLE);
			font = Fonts.getFont(name, height, style);
			//
			ScanDataSupport scanDataSupport = new ScanDataSupport();
			for(IScan scan : scans) {
				/*
				 * Get the best target.
				 */
				ILibraryInformation libraryInformation = null;
				if(scan instanceof IScanMSD) {
					IScanMSD scanMSD = (IScanMSD)scan;
					libraryInformation = scanDataSupport.getBestLibraryInformation(scanMSD);
				} else if(scan instanceof IScanCSD) {
					IScanCSD scanCSD = (IScanCSD)scan;
					libraryInformation = scanDataSupport.getBestLibraryInformation(scanCSD);
				} else if(scan instanceof IScanWSD) {
					IScanWSD scanWSD = (IScanWSD)scan;
					libraryInformation = scanDataSupport.getBestLibraryInformation(scanWSD);
				}
				//
				String substance = "";
				if(libraryInformation != null) {
					substance = libraryInformation.getName();
				}
				labels.add(substance);
			}
		}
		//
		return labels;
	}
}
