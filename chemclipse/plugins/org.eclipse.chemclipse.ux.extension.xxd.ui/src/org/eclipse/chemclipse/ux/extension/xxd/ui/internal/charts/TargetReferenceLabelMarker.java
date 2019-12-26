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
import java.util.Collections;
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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtchart.Chart;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.ICustomPaintListener;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.ISeries;

public class TargetReferenceLabelMarker implements ICustomPaintListener {

	private static final boolean DEBUG = false;
	private static final int OFFSET = 15;
	private static final int NO_ALPHA = 255;
	private final List<TargetLabel> identifications = new ArrayList<>();
	private boolean visible = true;
	private final boolean showReferenceId;
	private int rotation;
	private int detectionDepth;

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
			Rectangle clipping = gc.getClipping();
			TargetLabel lastReference = null;
			if(DEBUG) {
				System.out.println("---------------------- start label rendering -----------------------------");
			}
			int collisions = 0;
			for(TargetLabel reference : identifications) {
				int x = xAxis.getPixelCoordinate(reference.x);
				int y = yAxis.getPixelCoordinate(reference.y);
				if(!clipping.contains(x, y)) {
					continue;
				}
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
				reference.bounds = new LabelBounds(gc, reference);
				String label = reference.label;
				int h = reference.bounds.height;
				transform.identity();
				transform.translate(x, y - OFFSET);
				transform.rotate(-rotation);
				transform.translate(0, -h / 2);
				reference.bounds.setTransform(transform);
				if(reference.isActive) {
					gc.setForeground(activeColor);
					gc.setBackground(activeColor);
				} else {
					gc.setForeground(inactiveColor);
					gc.setBackground(inactiveColor);
				}
				if(detectionDepth > 0) {
					if(lastReference != null && lastReference.bounds != null) {
						if(lastReference.bounds.getCx() > reference.bounds.getCx() || lastReference.bounds.intersects(reference.bounds)) {
							collisions++;
							if(DEBUG) {
								System.out.println("label " + label + " intersects with previous label " + lastReference.label);
							}
							// first guess is to move the label up
							transform.identity();
							int yoffset = (int)Math.rint(2 * OFFSET + lastReference.bounds.getHeight(rotation) + (reference.bounds.getCy() - lastReference.bounds.getCy()));
							transform.translate(Math.max(x, lastReference.bounds.getCx()) + OFFSET, y - yoffset);
							transform.rotate(-rotation);
							transform.translate(0, -h / 2);
							reference.bounds.setTransform(transform);
							// check if the label is not cut of
							if(!clipping.contains(reference.bounds.getTopX(), reference.bounds.getTopY())) {
								// then move it to the right... (might still be cut of but that is the default behavior of current charting)
								if(DEBUG) {
									System.out.println("label " + label + " overflows");
								}
								transform.identity();
								int xoffset = (int)Math.rint(OFFSET + lastReference.bounds.getWidth(rotation));
								transform.translate(lastReference.bounds.getCx() + xoffset, y - 2 * OFFSET);
								transform.rotate(-rotation);
								transform.translate(0, -h / 2);
								reference.bounds.setTransform(transform);
							}
							// draw handle...
							gc.setTransform(null);
							int cx = reference.bounds.getCx();
							int cy = reference.bounds.getCy();
							int dx = (cx - x) / 2;
							int dy = OFFSET / 2;
							gc.setLineStyle(SWT.LINE_DASHDOT);
							gc.drawLine(x, y, x + dx, y - dy);
							gc.drawLine(x + dx, y - dy, cx - dx, cy + dy);
							gc.drawLine(cx - dx, cy + dy, cx, cy);
							int ow = 2;
							gc.fillOval(x - ow, y - ow, ow * 2, ow * 2);
						} else {
							if(DEBUG) {
								System.out.println("label " + label + " do not intersect with previous label " + lastReference.label);
							}
							collisions = 0;
						}
					}
					if(DEBUG) {
						reference.bounds.paintBounds();
					}
					if(collisions > detectionDepth) {
						lastReference = null;
						collisions = 0;
					} else {
						lastReference = reference;
					}
					if(DEBUG) {
						System.out.println("Current collisions: " + collisions);
					}
				}
				gc.setTransform(transform);
				gc.drawText(label, 0, 0, true);
				if(reference.id != null && reference.isActive) {
					gc.setForeground(idColor);
					gc.drawText(reference.id, reference.bounds.width + OFFSET / 2, 0, true);
				}
			}
			for(TargetLabel reference : identifications) {
				if(reference.bounds != null) {
					reference.bounds.dispose();
					reference.bounds = null;
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
			rotation = settings.getRotation();
			detectionDepth = settings.getCollisionDetectionDepth();
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
		Collections.sort(identifications, (o1, o2) -> Double.compare(o1.x, o2.x));
		return createVisibleFilter;
	}

	private static final class TargetLabel {

		// constant values
		private final boolean isPeakLabel;
		private final boolean isScanLabel;
		private final String label;
		private final String id;
		private final boolean isActive;
		private final double x;
		private final double y;
		// cached values used for calculation
		private LabelBounds bounds;

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

	private final static class LabelBounds {

		private final float[] pointArray = new float[10];
		private final int[] transformedPoints = new int[10];
		private final int width;
		private final int height;
		private final GC gc;
		private Region region;
		private int x;
		private int y;

		public LabelBounds(GC gc, TargetLabel label) {
			this.gc = gc;
			Point labelSize = gc.textExtent(label.label);
			width = labelSize.x;
			height = labelSize.y;
		}

		public boolean intersects(LabelBounds other) {

			if(other != null && other.region != null) {
				for(int i = 0; i < transformedPoints.length; i += 2) {
					if(other.region.contains(transformedPoints[i], transformedPoints[i + 1]) || region.contains(other.transformedPoints[i], other.transformedPoints[i + 1])) {
						return true;
					}
				}
			}
			return false;
		}

		public void dispose() {

			if(region != null) {
				region.dispose();
			}
			region = null;
		}

		public void setTransform(Transform transform) {

			// p0
			pointArray[0] = x;
			pointArray[1] = y;
			// p1
			pointArray[2] = x + width;
			pointArray[3] = y;
			// p2
			pointArray[4] = x + width;
			pointArray[5] = y + height;
			// p3
			pointArray[6] = x;
			pointArray[7] = y + height;
			// pc
			pointArray[8] = x;
			pointArray[9] = (y + height) / 2f;
			transform.transform(pointArray);
			for(int i = 0; i < transformedPoints.length; i++) {
				transformedPoints[i] = Math.round(pointArray[i]);
			}
			if(region != null) {
				region.dispose();
			}
			region = new Region(gc.getDevice());
			region.add(transformedPoints);
		}

		public double getHeight(int rotation) {

			double rad = Math.toRadians(rotation);
			return Math.cos(rad) * height + Math.sin(rad) * width;
		}

		public double getWidth(int rotation) {

			double rad = Math.toRadians(rotation);
			return Math.cos(rad) * width + Math.sin(rad) * height;
		}

		public int getCx() {

			return transformedPoints[8];
		}

		public int getCy() {

			return transformedPoints[9];
		}

		public int getTopX() {

			return transformedPoints[2];
		}

		public int getTopY() {

			return transformedPoints[3];
		}

		public void paintBounds() {

			gc.setTransform(null);
			Color old_fg = gc.getForeground();
			Font old_font = gc.getFont();
			try {
				Font font = new Font(gc.getDevice(), PreferenceConstants.DEF_CHROMATOGRAM_PEAK_LABEL_FONT_NAME, 8, PreferenceConstants.DEF_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE);
				gc.setFont(font);
				gc.setLineStyle(SWT.LINE_DASH);
				gc.drawPolygon(transformedPoints);
				paintPoint(0, SWT.COLOR_BLUE);
				paintPoint(1, SWT.COLOR_RED);
				paintPoint(2, SWT.COLOR_GREEN);
				paintPoint(3, SWT.COLOR_GRAY);
				paintPoint(4, SWT.COLOR_BLACK);
				font.dispose();
			} finally {
				gc.setFont(old_font);
				gc.setForeground(old_fg);
			}
		}

		private void paintPoint(int p, int color) {

			int i = p * 2;
			int x = transformedPoints[i];
			int y = transformedPoints[i + 1];
			int lw = 5;
			gc.setForeground(gc.getDevice().getSystemColor(color));
			String string;
			if(p == 4) {
				string = "pc";
			} else {
				string = "p" + p;
			}
			Point s = gc.stringExtent(string);
			if(p == 0) {
				gc.drawString(string, x - s.x - 5, y - s.y - 5, true);
			} else if(p == 1) {
				gc.drawString(string, x, y - s.y - 5, true);
			} else if(p == 2) {
				gc.drawString(string, x, y, true);
			} else {
				gc.drawString(string, x - s.x, y, true);
			}
			gc.drawLine(x + lw, y + lw, x - lw, y - lw);
			gc.drawLine(x - lw, y + lw, x + lw, y - lw);
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
