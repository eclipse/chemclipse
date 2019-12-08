/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.ScanTargetReference;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetReference;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedChromatogramUI;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtchart.Chart;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.ICustomPaintListener;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.ISeries;

public class PreviewLabelMarker implements ICustomPaintListener {

	private TargetDisplaySettings settings;
	private final List<ScanTargetReference> identifications;
	private Predicate<TargetReference> settingsFilter;
	private Predicate<TargetReference> activeFilter;

	public PreviewLabelMarker(List<ScanTargetReference> identifications) {
		this.identifications = identifications;
	}

	@Override
	public void paintControl(PaintEvent event) {

		if(settings != null) {
			Widget widget = event.widget;
			if(widget instanceof IPlotArea) {
				Chart chart = ((IPlotArea)widget).getChart();
				ISeries<?> series = chart.getSeriesSet().getSeries(ExtendedChromatogramUI.SERIES_ID_CHROMATOGRAM);
				if(series != null) {
					IAxisSet axisSet = chart.getAxisSet();
					paintLabels(event.gc, axisSet.getXAxis(series.getXAxisId()), axisSet.getYAxis(series.getYAxisId()));
				}
			}
		}
	}

	private void paintLabels(GC gc, IAxis xAxis, IAxis yAxis) {

		Function<IIdentificationTarget, String> transformer = settings.getField().stringTransformer();
		Transform transform = new Transform(gc.getDevice());
		Font peakFont = null;
		Font scanFont = null;
		Font oldFont = gc.getFont();
		try {
			Color activeColor = gc.getDevice().getSystemColor(SWT.COLOR_BLACK);
			Color inactiveColor = gc.getDevice().getSystemColor(SWT.COLOR_GRAY);
			Color idColor = gc.getDevice().getSystemColor(SWT.COLOR_DARK_GRAY);
			gc.setTransform(transform);
			for(ScanTargetReference reference : identifications) {
				if(settings.isVisible(reference) && settingsFilter.test(reference)) {
					IIdentificationTarget target = reference.getBestTarget();
					if(settings.isVisible(reference, target)) {
						String label = transformer.apply(target);
						if(label == null || label.isEmpty()) {
							continue;
						}
						if(ScanTargetReference.TYPE_PEAK.equals(reference.getType())) {
							if(peakFont == null) {
								peakFont = createPeakFont(gc.getDevice());
							}
							gc.setFont(peakFont);
						} else if(ScanTargetReference.TYPE_SCAN.equals(reference.getType())) {
							if(scanFont == null) {
								scanFont = createScanFont(gc.getDevice());
							}
							gc.setFont(scanFont);
						} else {
							gc.setFont(oldFont);
						}
						IScan scan = reference.getScan();
						float x = xAxis.getPixelCoordinate(scan.getX());
						float y = yAxis.getPixelCoordinate(scan.getY());
						Point labelSize = gc.textExtent(label);
						transform.identity();
						transform.translate(x - labelSize.y / 2, y - 15);
						transform.rotate(-90);
						gc.setTransform(transform);
						boolean isActive = activeFilter == null || activeFilter.test(reference);
						if(isActive) {
							gc.setForeground(activeColor);
						} else {
							gc.setForeground(inactiveColor);
						}
						gc.drawText(label, 0, 0, true);
						if(isActive) {
							gc.setForeground(idColor);
							gc.drawText(reference.getName(), labelSize.x + labelSize.y / 2, 0, true);
						}
					}
				}
			}
		} finally {
			gc.setTransform(null);
			gc.setFont(oldFont);
			if(peakFont != null) {
				peakFont.dispose();
			}
			if(scanFont != null) {
				scanFont.dispose();
			}
			transform.dispose();
		}
	}

	public void setSettings(TargetDisplaySettings settings, Predicate<TargetReference> settingsFilter, Predicate<TargetReference> activeFilter) {

		this.settings = settings;
		this.settingsFilter = settingsFilter;
		this.activeFilter = activeFilter;
	}

	public static Font createPeakFont(Device device) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String name = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_NAME);
		int height = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE);
		int style = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE);
		return new Font(device, name, height, style);
	}

	public static Font createScanFont(Device device) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String name = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_NAME);
		int height = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE);
		int style = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_STYLE);
		return new Font(device, name, height, style);
	}
}
