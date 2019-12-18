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

import java.util.ArrayList;
import java.util.Collection;
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

public class TargetReferenceLabelMarker implements ICustomPaintListener {

	private static final int NO_ALPHA = 255;
	private final List<TargetLabel> identifications = new ArrayList<>();
	private boolean visible = true;
	private final boolean showReferenceId;

	public TargetReferenceLabelMarker() {
		this(false);
	}

	public TargetReferenceLabelMarker(boolean showReferenceId) {
		this.showReferenceId = showReferenceId;
	}

	public TargetReferenceLabelMarker(Collection<? extends ScanTargetReference> references, TargetDisplaySettings settings) {
		this.showReferenceId = false;
		setData(references, settings);
	}

	@Override
	public void paintControl(PaintEvent event) {

		if(visible && !identifications.isEmpty()) {
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

	public boolean isVisible() {

		return visible;
	}

	public void setVisible(boolean visible) {

		this.visible = visible;
	}

	private void paintLabels(GC gc, IAxis xAxis, IAxis yAxis) {

		Transform transform = new Transform(gc.getDevice());
		Font peakFont = null;
		Font scanFont = null;
		Font oldFont = gc.getFont();
		gc.setAlpha(NO_ALPHA);
		try {
			Color activeColor = gc.getDevice().getSystemColor(SWT.COLOR_BLACK);
			Color inactiveColor = gc.getDevice().getSystemColor(SWT.COLOR_GRAY);
			Color idColor = gc.getDevice().getSystemColor(SWT.COLOR_DARK_GRAY);
			gc.setTransform(transform);
			for(TargetLabel reference : identifications) {
				if(reference.isPeakLabel) {
					if(peakFont == null) {
						peakFont = createPeakFont(gc.getDevice());
					}
					gc.setFont(peakFont);
				} else if(reference.isScanLabel) {
					if(scanFont == null) {
						scanFont = createScanFont(gc.getDevice());
					}
					gc.setFont(scanFont);
				} else {
					gc.setFont(oldFont);
				}
				String label = reference.label;
				float x = xAxis.getPixelCoordinate(reference.x);
				float y = yAxis.getPixelCoordinate(reference.y);
				Point labelSize = gc.textExtent(label);
				transform.identity();
				transform.translate(x - labelSize.y / 2, y - 15);
				transform.rotate(-90);
				gc.setTransform(transform);
				if(reference.isActive) {
					gc.setForeground(activeColor);
				} else {
					gc.setForeground(inactiveColor);
				}
				gc.drawText(label, 0, 0, true);
				if(reference.id != null && reference.isActive) {
					gc.setForeground(idColor);
					gc.drawText(reference.id, labelSize.x + labelSize.y / 2, 0, true);
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

	public Predicate<TargetReference> setData(Collection<? extends ScanTargetReference> identifications, TargetDisplaySettings settings) {

		return setData(identifications, settings, always -> true);
	}

	public Predicate<TargetReference> setData(Collection<? extends ScanTargetReference> input, TargetDisplaySettings settings, Predicate<TargetReference> activeFilter) {

		identifications.clear();
		Predicate<TargetReference> createVisibleFilter = ScanTargetReference.createVisibleFilter(settings);
		if(settings != null) {
			Function<IIdentificationTarget, String> stringTransformer = settings.getField().stringTransformer();
			for(ScanTargetReference reference : input) {
				if(createVisibleFilter.test(reference)) {
					IIdentificationTarget target = reference.getBestTarget();
					if(settings.isVisible(reference, target)) {
						String label = stringTransformer.apply(target);
						if(label == null || label.isEmpty()) {
							continue;
						}
						boolean isPeakLabel = ScanTargetReference.TYPE_PEAK.equals(reference.getType());
						boolean isScanLabel = ScanTargetReference.TYPE_SCAN.equals(reference.getType());
						boolean isActive = activeFilter == null || activeFilter.test(reference);
						IScan scan = reference.getScan();
						TargetLabel targetLabel = new TargetLabel(label, showReferenceId ? reference.getName() : null, isPeakLabel, isScanLabel, isActive, scan.getX(), scan.getY());
						identifications.add(targetLabel);
					}
				}
			}
		}
		return createVisibleFilter;
	}

	private static final class TargetLabel {

		private final boolean isPeakLabel;
		private final boolean isScanLabel;
		private final String label;
		private final String id;
		private final boolean isActive;
		private final double x;
		private final double y;

		public TargetLabel(String label, String id, boolean isPeakLabel, boolean isScanLabel, boolean isActive, double x, double y) {
			this.label = label;
			this.id = id;
			this.isPeakLabel = isPeakLabel;
			this.isScanLabel = isScanLabel;
			this.isActive = isActive;
			this.x = x;
			this.y = y;
		}
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
