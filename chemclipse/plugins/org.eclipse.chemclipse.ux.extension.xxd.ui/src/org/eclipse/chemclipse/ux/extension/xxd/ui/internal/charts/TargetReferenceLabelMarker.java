/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.swt.ui.support.Fonts;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.SignalTargetReference;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings.LibraryField;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetReference;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedChromatogramUI;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
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
	private static final boolean DEBUG_FENCES = false;
	private final int offset;
	private static final int NO_ALPHA = 255;
	private final List<TargetLabel> identifications = new ArrayList<>();
	private boolean visible = true;
	private final boolean showReferenceId;
	private int rotation;
	private int detectionDepth;
	private final IPreferenceStore preferenceStore;

	public TargetReferenceLabelMarker(int offset, IPreferenceStore preferenceStore) {

		this(false, offset, preferenceStore);
	}

	public TargetReferenceLabelMarker(boolean showReferenceId, int offset, IPreferenceStore preferenceStore) {

		this.showReferenceId = showReferenceId;
		this.offset = offset;
		this.preferenceStore = preferenceStore;
	}

	public TargetReferenceLabelMarker(Collection<? extends SignalTargetReference> references, TargetDisplaySettings settings, int offset, IPreferenceStore preferenceStore) {

		this(false, offset, preferenceStore);
		setData(references, settings);
	}

	@Override
	public void paintControl(PaintEvent event) {

		if(visible && !identifications.isEmpty()) {
			Widget widget = event.widget;
			if(widget instanceof IPlotArea) {
				Chart chart = ((IPlotArea)widget).getChart();
				ISeries<?> series = getReferenceSeries(chart);
				if(series != null) {
					IAxisSet axisSet = chart.getAxisSet();
					paintLabels(event.gc, axisSet.getXAxis(series.getXAxisId()), axisSet.getYAxis(series.getYAxisId()));
				}
			}
		}
	}

	/**
	 * 
	 * @param chart
	 * @return the series for the given chart to use as a reference
	 */
	protected ISeries<?> getReferenceSeries(Chart chart) {

		return chart.getSeriesSet().getSeries(ExtendedChromatogramUI.SERIES_ID_CHROMATOGRAM);
	}

	public boolean isVisible() {

		return visible;
	}

	public void setVisible(boolean visible) {

		this.visible = visible;
	}

	private void paintLabels(GC gc, IAxis xAxis, IAxis yAxis) {

		Transform transform = new Transform(gc.getDevice());
		Transform oldTransform = new Transform(gc.getDevice());
		gc.getTransform(oldTransform);
		Map<FontData, Font> fontMap = new IdentityHashMap<FontData, Font>();
		Font oldFont = gc.getFont();
		gc.setAlpha(NO_ALPHA);
		float[] identityMatrix = new float[6];
		oldTransform.getElements(identityMatrix);
		try {
			Color activeColor = gc.getDevice().getSystemColor(SWT.COLOR_BLACK);
			Color inactiveColor = gc.getDevice().getSystemColor(SWT.COLOR_GRAY);
			Color idColor = gc.getDevice().getSystemColor(SWT.COLOR_DARK_GRAY);
			Rectangle clipping = gc.getClipping();
			TargetLabel lastReference = null;
			if(DEBUG) {
				System.out.println("---------------------- start label rendering -----------------------------");
				System.out.println("identityMatrix: " + Arrays.toString(identityMatrix));
			}
			int collisions = 0;
			for(TargetLabel reference : identifications) {
				int x = xAxis.getPixelCoordinate(reference.x);
				int y = yAxis.getPixelCoordinate(reference.y);
				if(!clipping.contains(x, y)) {
					continue;
				}
				if(reference.fontData != null) {
					Font font = fontMap.computeIfAbsent(reference.fontData, fd -> {
						return Fonts.createDPIAwareFont(gc.getDevice(), fd);
					});
					gc.setFont(font);
				} else {
					gc.setFont(oldFont);
				}
				reference.bounds = new LabelBounds(gc, reference);
				String label = reference.label;
				setTransform(transform, x, y, reference, identityMatrix);
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
							float yoffset = lastReference.bounds.offsetY(reference.bounds);
							setTransform(transform, Math.max(x, lastReference.bounds.getCx()) + offset - identityMatrix[4], y - yoffset - offset, reference, identityMatrix);
							// check if the label is not cut of
							if(clipping.contains(Math.round(reference.bounds.getTopX()), Math.round(reference.bounds.getTopY()))) {
								gc.setTransform(oldTransform);
								drawHandle(gc, reference, x, y, true, identityMatrix);
							} else {
								// reset values
								setTransform(transform, x, y, reference, identityMatrix);
								// then move it to the right... (might still be cut of but that is the default behavior of current charting)
								if(DEBUG) {
									System.out.println("label " + label + " overflows");
								}
								float xoffset = lastReference.bounds.offsetX(reference.bounds);
								setTransform(transform, x + xoffset + offset, y, reference, identityMatrix);
								gc.setTransform(oldTransform);
								drawHandle(gc, reference, x, y, false, identityMatrix);
							}
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
					gc.drawText(reference.id, reference.bounds.width + offset / 2, 0, true);
				}
			}
			for(TargetLabel reference : identifications) {
				if(reference.bounds != null) {
					reference.bounds.dispose();
					reference.bounds = null;
				}
			}
		} finally {
			gc.setTransform(oldTransform);
			oldTransform.dispose();
			gc.setFont(oldFont);
			for(Font font : fontMap.values()) {
				font.dispose();
			}
			transform.dispose();
		}
	}

	private int setTransform(Transform transform, float x, float y, TargetLabel reference, float[] identityMatrix) {

		int h = reference.bounds.height;
		transform.setElements(identityMatrix[0], identityMatrix[1], identityMatrix[2], identityMatrix[3], identityMatrix[4], identityMatrix[5]);
		transform.translate(x, y - offset);
		transform.rotate(-rotation);
		transform.translate(0, -h / 2);
		reference.bounds.setTransform(transform);
		return h;
	}

	private void drawHandle(GC gc, TargetLabel reference, int x, int y, boolean upsideDown, float[] identityMatrix) {

		float cx = reference.bounds.getCx() - identityMatrix[4];
		float cy = reference.bounds.getCy() - identityMatrix[5] + offset;
		gc.setLineStyle(SWT.LINE_DASHDOT);
		Path path = new Path(gc.getDevice());
		float dx;
		float dy;
		if(upsideDown) {
			dx = (cx - x) / 2;
			dy = offset / 2;
		} else {
			dy = (y - cy) / 2;
			dx = offset / 2;
		}
		path.moveTo(x, y);
		path.lineTo(x + dx, y - dy);
		path.lineTo(cx - dx, cy + dy);
		path.lineTo(cx, cy);
		gc.drawPath(path);
		path.dispose();
		int ow = 2;
		gc.fillOval(x - ow, y - ow, ow * 2, ow * 2);
		gc.fillOval((int)(cx - ow), (int)(cy - ow), ow * 2, ow * 2);
	}

	public <X> void setRawData(Collection<X> data, int rotation, int detectionDepth, Predicate<X> visibilityFilter, Function<X, String> labelSupplier, Function<X, FontData> fontSupplier, Function<X, org.eclipse.chemclipse.numeric.core.Point> pointSupplier) {

		this.rotation = rotation;
		this.detectionDepth = detectionDepth;
		identifications.clear();
		for(X generic : data) {
			if(visibilityFilter.test(generic)) {
				String label = labelSupplier.apply(generic);
				if(label == null || label.isEmpty()) {
					continue;
				}
				org.eclipse.chemclipse.numeric.core.Point point = pointSupplier.apply(generic);
				TargetLabel targetLabel = new TargetLabel(label, null, fontSupplier.apply(generic), true, point.getX(), point.getY());
				identifications.add(targetLabel);
			}
		}
		Collections.sort(identifications, (o1, o2) -> Double.compare(o1.x, o2.x));
	}

	public Predicate<TargetReference> setData(Collection<? extends SignalTargetReference> identifications, TargetDisplaySettings settings) {

		return setData(identifications, settings, always -> true);
	}

	public Predicate<TargetReference> setData(Collection<? extends SignalTargetReference> input, TargetDisplaySettings settings, Predicate<TargetReference> activeFilter) {

		identifications.clear();
		Predicate<TargetReference> createVisibleFilter = SignalTargetReference.createVisibleFilter(settings);
		if(settings != null) {
			rotation = settings.getRotation();
			detectionDepth = settings.getCollisionDetectionDepth();
			LibraryField field = settings.getField();
			FontData peakFontData = getPeakFontData(preferenceStore);
			FontData scanFontData = getScanFontData(preferenceStore);
			for(SignalTargetReference reference : input) {
				if(createVisibleFilter.test(reference)) {
					String label = reference.getTargetLabel(field);
					if(label == null || label.isEmpty()) {
						continue;
					}
					boolean isPeakLabel = SignalTargetReference.TYPE_PEAK.equals(reference.getType());
					boolean isScanLabel = SignalTargetReference.TYPE_SCAN.equals(reference.getType());
					boolean isActive = activeFilter == null || activeFilter.test(reference);
					ISignal scan = reference.getSignal();
					FontData fd;
					if(isPeakLabel) {
						fd = peakFontData;
					} else if(isScanLabel) {
						fd = scanFontData;
					} else {
						fd = null;
					}
					TargetLabel targetLabel = new TargetLabel(label, showReferenceId ? reference.getName() : null, fd, isActive, scan.getX(), scan.getY());
					identifications.add(targetLabel);
				}
			}
		}
		Collections.sort(identifications, (o1, o2) -> Double.compare(o1.x, o2.x));
		return createVisibleFilter;
	}

	private static final class TargetLabel {

		// constant values
		private final String label;
		private final String id;
		private final boolean isActive;
		private final double x;
		private final double y;
		private final FontData fontData;
		// cached values used for calculation
		private LabelBounds bounds;

		public TargetLabel(String label, String id, FontData fontData, boolean isActive, double x, double y) {

			this.label = label;
			this.id = id;
			this.fontData = fontData;
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
			pointArray[0] = 0;
			pointArray[1] = 0;
			// p1
			pointArray[2] = width;
			pointArray[3] = 0;
			// p2
			pointArray[4] = width;
			pointArray[5] = height;
			// p3
			pointArray[6] = 0;
			pointArray[7] = height;
			// pc
			pointArray[8] = 0;
			pointArray[9] = height / 2f;
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

		public float getCx() {

			return pointArray[8];
		}

		public float getCy() {

			return pointArray[9];
		}

		public float getTopX() {

			return pointArray[2];
		}

		public float getTopY() {

			return pointArray[3];
		}

		public float getBottomX() {

			return pointArray[6];
		}

		public float getBottomY() {

			return pointArray[7];
		}

		public float getRightX() {

			return pointArray[4];
		}

		public float getLeftX() {

			return pointArray[0];
		}

		public float getLeftY() {

			return pointArray[1];
		}

		public float offsetX(LabelBounds other) {

			float w;
			if(transformedPoints[1] == transformedPoints[3] || transformedPoints[0] == transformedPoints[2]) {
				w = getRightX();
			} else {
				float x1 = pointArray[6];
				float y1 = pointArray[7];
				float x2 = pointArray[4];
				float y2 = pointArray[5];
				float dx = x2 - x1;
				float m = (y2 - y1) / dx;
				float b = (x2 * y1 - x1 * y2) / dx;
				w = Math.min((other.getLeftY() - b) / m, getRightX());
			}
			if(DEBUG) {
				System.out.println("w=" + w + " rx=" + getRightX() + ", olx=" + other.getLeftX());
			}
			return w - other.getLeftX();
		}

		public float offsetY(LabelBounds other) {

			float h;
			if(other.getCx() > getTopX() || transformedPoints[0] == transformedPoints[2]) {
				h = getTopY();
			} else {
				float x = other.getBottomX();
				float x1 = pointArray[0];
				float y1 = pointArray[1];
				float x2 = pointArray[2];
				float y2 = pointArray[3];
				float dx = x2 - x1;
				float m = (y2 - y1) / dx;
				float b = (x2 * y1 - x1 * y2) / dx;
				h = Math.min(Math.max(m * x + b, getTopY()), getBottomY());
			}
			if(DEBUG) {
				System.out.println("h=" + h + ", by = " + getBottomY());
			}
			return getBottomY() - h + (other.getCy() - getCy());
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
				gc.setLineStyle(SWT.LINE_SOLID);
				if(DEBUG_FENCES) {
					Path path = new Path(gc.getDevice());
					int sx = transformedPoints[0] - 100;
					for(int x = sx; x < transformedPoints[2] + 100; x += 10) {
						float x1 = pointArray[4];
						float y1 = pointArray[5];
						float x2 = pointArray[6];
						float y2 = pointArray[7];
						float dx = x2 - x1;
						float m = (y2 - y1) / dx;
						float b = (x2 * y1 - x1 * y2) / dx;
						float y = m * x + b;
						System.out.println("f(" + x + ")=" + y);
						if(sx == x) {
							path.moveTo(x, y);
						} else {
							path.lineTo(x, y);
						}
						gc.drawLine(x, (int)y - 10, x, (int)y);
					}
					gc.drawPath(path);
					path.dispose();
				}
				if(DEBUG_FENCES) {
					Path path = new Path(gc.getDevice());
					int sx = transformedPoints[0] - 100;
					for(int x = sx; x < transformedPoints[2] + 100; x += 10) {
						float x1 = pointArray[4];
						float y1 = pointArray[5];
						float x2 = pointArray[6];
						float y2 = pointArray[7];
						float dx = x2 - x1;
						float m = (y2 - y1) / dx;
						float b = (x2 * y1 - x1 * y2) / dx;
						float h = Math.min(Math.max(m * x + b, getTopY()), getBottomY());
						if(x == sx) {
							path.moveTo(x, h);
						} else {
							path.lineTo(x, h);
						}
						gc.drawLine(x, (int)getBottomY(), x, (int)h);
					}
					gc.drawPath(path);
					path.dispose();
				}
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

	public static Font createPeakFont(IPreferenceStore preferenceStore, Device device) {

		String name = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_NAME);
		int height = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE);
		int style = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE);
		Point dpi = device.getDPI();
		int pointHeight = height * 72 / dpi.y;
		return new Font(device, name, pointHeight, style);
	}

	public static Font createScanFont(IPreferenceStore preferenceStore, Device device) {

		String name = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_NAME);
		int height = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE);
		int style = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_STYLE);
		Point dpi = device.getDPI();
		int pointHeight = height * 72 / dpi.y;
		return new Font(device, name, pointHeight, style);
	}

	public static FontData getPeakFontData(IPreferenceStore preferenceStore) {

		String name = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_NAME);
		int height = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE);
		int style = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE);
		return new FontData(name, height, style);
	}

	public static FontData getScanFontData(IPreferenceStore preferenceStore) {

		String name = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_NAME);
		int height = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE);
		int style = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_STYLE);
		return new FontData(name, height, style);
	}
}
