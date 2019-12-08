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
 * Christoph Läubrich - make configurable by TargetDisplaySettings
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.support.Fonts;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetReference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.marker.LabelMarker;

public class IdentificationLabelMarker extends LabelMarker {

	private final Font font = DisplayUtils.getDisplay().getSystemFont();

	/**
	 * Peak or Scan can be null. If null, it won't be processed.
	 * 
	 * @param baseChart
	 * @param indexSeries
	 * @param peaks
	 * @param scans
	 */
	public IdentificationLabelMarker(BaseChart baseChart, int indexSeries, List<? extends TargetReference> identifications, Font font, TargetDisplaySettings settings) {
		super(baseChart);
		List<String> labels = new ArrayList<String>();
		Function<IIdentificationTarget, String> transformer = settings.getField().stringTransformer();
		for(TargetReference identification : identifications) {
			String label = null;
			if(settings.isVisible(identification)) {
				IIdentificationTarget target = identification.getBestTarget();
				if(settings.isVisible(identification, target)) {
					label = transformer.apply(target);
				}
			}
			labels.add(label);
		}
		setLabels(labels, indexSeries, SWT.VERTICAL);
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
