/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.support.Fonts;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.marker.LabelMarker;

public class IdentificationLabelMarker extends LabelMarker {

	private Font font = DisplayUtils.getDisplay().getSystemFont();
	private TargetExtendedComparator comparator = new TargetExtendedComparator(SortOrder.DESC);

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
			for(IPeak peak : peaks) {
				ILibraryInformation libraryInformation = IIdentificationTarget.getBestLibraryInformation(peak.getTargets(), comparator);
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
			for(IScan scan : scans) {
				/*
				 * Get the best target.
				 */
				ILibraryInformation libraryInformation = IIdentificationTarget.getBestLibraryInformation(scan.getTargets(), comparator);
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
