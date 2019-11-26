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
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.support.Fonts;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.marker.LabelMarker;

public class IdentificationLabelMarker extends LabelMarker {

	private final Font font = DisplayUtils.getDisplay().getSystemFont();
	private static final TargetExtendedComparator COMPARATOR = new TargetExtendedComparator(SortOrder.DESC);

	/**
	 * Peak or Scan can be null. If null, it won't be processed.
	 * 
	 * @param baseChart
	 * @param indexSeries
	 * @param peaks
	 * @param scans
	 */
	public IdentificationLabelMarker(BaseChart baseChart, int indexSeries, List<? extends ITargetSupplier> identifications, Font font, TargetDisplaySettings settings) {
		super(baseChart);
		List<String> labels = new ArrayList<String>();
		for(ITargetSupplier identification : identifications) {
			String label = "";
			IIdentificationTarget target = IIdentificationTarget.getBestIdentificationTarget(identification.getTargets(), COMPARATOR);
			if(target != null && settings.isVisible(target)) {
				ILibraryInformation libraryInformation = target.getLibraryInformation();
				if(libraryInformation != null) {
					switch(settings.getField()) {
						case CAS:
							label = libraryInformation.getCasNumber();
							break;
						case CLASSIFICATION:
							label = joinStrings(libraryInformation.getClassifier());
							break;
						case NAME:
							label = libraryInformation.getName();
							break;
						case FORMULA:
							label = libraryInformation.getFormula();
							break;
						case SYNONYMS:
							label = joinStrings(libraryInformation.getSynonyms());
							break;
						default:
							break;
					}
					label = libraryInformation.getName();
				}
			}
			labels.add(label);
		}
		setLabels(labels, indexSeries, SWT.VERTICAL);
	}

	private String joinStrings(Collection<String> classifier) {

		StringBuilder builder = new StringBuilder();
		for(String c : classifier) {
			if(builder.length() > 0) {
				builder.append("; ");
			}
			builder.append(c);
		}
		return builder.toString();
	}

	@Override
	public void paintControl(PaintEvent e) {

		Font currentFont = e.gc.getFont();
		e.gc.setFont(font);
		super.paintControl(e);
		e.gc.setFont(currentFont);
	}

	public static Font getPeakFont(Display display) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String name = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_NAME);
		int height = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE);
		int style = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE);
		return Fonts.getCachedFont(display, name, height, style);
	}

	public static Font getScanFont(Display display) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String name = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_NAME);
		int height = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE);
		int style = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_STYLE);
		return Fonts.getCachedFont(display, name, height, style);
	}
}
