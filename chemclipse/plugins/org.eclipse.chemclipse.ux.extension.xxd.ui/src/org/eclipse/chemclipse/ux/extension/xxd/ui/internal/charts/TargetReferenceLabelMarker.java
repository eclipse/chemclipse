/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring target label support
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramPeak;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.targets.DisplayOption;
import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.ITargetReference;
import org.eclipse.chemclipse.model.targets.LibraryField;
import org.eclipse.chemclipse.model.targets.TargetReference;
import org.eclipse.chemclipse.model.targets.TargetReferenceType;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.workbench.PreferencesSupport;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Fonts;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedChromatogramUI;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtchart.Chart;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.ICustomPaintListener;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.ISeries;
import org.eclipse.swtchart.extensions.core.BaseChart;

public class TargetReferenceLabelMarker implements ICustomPaintListener {

	private static final boolean DEBUG = LabelBounds.DEBUG_LOG;
	private static final int NO_ALPHA = 255;
	//
	private final int offset;
	private final List<TargetLabel> targetLabels = new ArrayList<>();
	private ITargetDisplaySettings targetDisplaySettings;
	private boolean visible = true;
	private final boolean showReferenceId;
	private int rotation;
	private int detectionDepth;
	//
	private DecimalFormat decimalFormatRetentionIndex = ValueFormat.getDecimalFormatEnglish("0.00");
	private DecimalFormat decimalFormatAreaPercent = ValueFormat.getDecimalFormatEnglish("0.000");
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public TargetReferenceLabelMarker(boolean showReferenceId, int offset) {

		this.showReferenceId = showReferenceId;
		this.offset = offset;
	}

	public TargetReferenceLabelMarker(Collection<? extends TargetReference> targetReferences, ITargetDisplaySettings targetDisplaySettings, int offset) {

		this.showReferenceId = false;
		this.offset = offset;
		this.targetDisplaySettings = targetDisplaySettings;
		setTargetReferences(targetReferences);
	}

	@Override
	public void paintControl(PaintEvent event) {

		if(visible && !targetLabels.isEmpty()) {
			Widget widget = event.widget;
			if(widget instanceof IPlotArea plotArea) {
				Chart chart = plotArea.getChart();
				if(chart instanceof BaseChart baseChart) {
					if(baseChart.isBufferActive()) {
						return;
					}
				}
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
		Map<FontData, Font> fontMap = new IdentityHashMap<>();
		Font oldFont = gc.getFont();
		gc.setAlpha(NO_ALPHA);
		float[] identityMatrix = new float[6];
		oldTransform.getElements(identityMatrix);
		try {
			Color activeColor = getActiveColor(preferenceStore);
			Color inactiveColor = getInactiveColor(preferenceStore);
			Color idColor = getIdColor(preferenceStore);
			Rectangle clipping = gc.getClipping();
			TargetLabel lastReference = null;
			if(DEBUG) {
				System.out.println("---------------------- start label rendering -----------------------------");
				System.out.println("identityMatrix: " + Arrays.toString(identityMatrix));
			}
			int collisions = 0;
			for(TargetLabel reference : targetLabels) {
				int x = xAxis.getPixelCoordinate(reference.getX());
				int y = yAxis.getPixelCoordinate(reference.getY());
				if(!clipping.contains(x, y)) {
					continue;
				}
				if(reference.getFontData() != null) {
					Font font = fontMap.computeIfAbsent(reference.getFontData(), fd -> {
						return Fonts.createDPIAwareFont(gc.getDevice(), fd);
					});
					gc.setFont(font);
				} else {
					gc.setFont(oldFont);
				}
				reference.setBounds(new LabelBounds(gc, reference));
				String label = reference.getLabel();
				setTransform(transform, x, y, reference, identityMatrix);
				if(reference.isActive()) {
					gc.setForeground(activeColor);
					gc.setBackground(activeColor);
				} else {
					gc.setForeground(inactiveColor);
					gc.setBackground(inactiveColor);
				}
				if(detectionDepth > 0) {
					if(lastReference != null && lastReference.getBounds() != null) {
						if(lastReference.getBounds().getCx() > reference.getBounds().getCx() || lastReference.getBounds().intersects(reference.getBounds())) {
							collisions++;
							if(DEBUG) {
								System.out.println("label " + label + " intersects with previous label " + lastReference.getLabel());
							}
							// first guess is to move the label up
							float yoffset = lastReference.getBounds().offsetY(reference.getBounds());
							setTransform(transform, Math.max(x, lastReference.getBounds().getCx()) + offset - identityMatrix[4], y - yoffset - offset, reference, identityMatrix);
							// check if the label is not cut of
							if(clipping.contains(Math.round(reference.getBounds().getTopX()), Math.round(reference.getBounds().getTopY()))) {
								gc.setTransform(oldTransform);
								drawHandle(gc, reference, x, y, true, identityMatrix);
							} else {
								// reset values
								setTransform(transform, x, y, reference, identityMatrix);
								// then move it to the right... (might still be cut of but that is the default behavior of current charting)
								if(DEBUG) {
									System.out.println("label " + label + " overflows");
								}
								float xoffset = lastReference.getBounds().offsetX(reference.getBounds());
								setTransform(transform, x + xoffset + offset, y, reference, identityMatrix);
								gc.setTransform(oldTransform);
								drawHandle(gc, reference, x, y, false, identityMatrix);
							}
						} else {
							if(DEBUG) {
								System.out.println("label " + label + " do not intersect with previous label " + lastReference.getLabel());
							}
							collisions = 0;
						}
					}
					if(DEBUG) {
						reference.getBounds().paintBounds();
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
				if(reference.getId() != null && reference.isActive()) {
					gc.setForeground(idColor);
					gc.drawText(reference.getId(), reference.getBounds().getWidth() + offset / 2, 0, true);
				}
			}
			for(TargetLabel reference : targetLabels) {
				if(reference.getBounds() != null) {
					reference.getBounds().dispose();
					reference.setBounds(null);
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

		int h = reference.getBounds().getHeight();
		transform.setElements(identityMatrix[0], identityMatrix[1], identityMatrix[2], identityMatrix[3], identityMatrix[4], identityMatrix[5]);
		transform.translate(x, y - offset);
		transform.rotate(-rotation);
		transform.translate(0, -h / 2);
		reference.getBounds().setTransform(transform);
		return h;
	}

	private void drawHandle(GC gc, TargetLabel reference, int x, int y, boolean upsideDown, float[] identityMatrix) {

		float cx = reference.getBounds().getCx() - identityMatrix[4];
		float cy = reference.getBounds().getCy() - identityMatrix[5] + offset;
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

	public Predicate<ITargetReference> setTargetReferences(Collection<? extends TargetReference> targetReferences) {

		return setTargetReferences(targetReferences, always -> true);
	}

	private Predicate<ITargetReference> setTargetReferences(Collection<? extends TargetReference> targetReferences, Predicate<ITargetReference> activeFilter) {

		targetLabels.clear();
		Predicate<ITargetReference> visibilityFilter = TargetReference.createVisibilityFilter(targetDisplaySettings);
		//
		if(targetDisplaySettings != null) {
			/*
			 * Settings
			 */
			rotation = targetDisplaySettings.getRotation();
			detectionDepth = targetDisplaySettings.getCollisionDetectionDepth();
			DisplayOption displayOption = targetDisplaySettings.getDisplayOption();
			LibraryField libraryField = targetDisplaySettings.getLibraryField();
			FontData peakFontData = getPeakFontData(preferenceStore);
			FontData scanFontData = getScanFontData(preferenceStore);
			//
			int number = 1;
			for(ITargetReference targetReference : targetReferences) {
				if(visibilityFilter.test(targetReference)) {
					/*
					 * Get the label.
					 */
					String label = null;
					switch(displayOption) {
						case NUMBERS:
							label = String.valueOf(number++);
							break;
						case RETENTION_TIME:
							label = targetReference.getRetentionTimeMinutes();
							break;
						case RETENTION_INDEX:
							label = decimalFormatRetentionIndex.format(targetReference.getRetentionIndex());
							break;
						case AREA_PERCENT:
							label = getAreaPercent(targetReference);
							break;
						default:
							label = targetReference.getTargetLabel(libraryField);
							if(label == null || label.isEmpty()) {
								continue;
							}
							break;
					}
					//
					boolean isPeakLabel = TargetReferenceType.PEAK.equals(targetReference.getType());
					boolean isScanLabel = TargetReferenceType.SCAN.equals(targetReference.getType());
					boolean isActive = activeFilter == null || activeFilter.test(targetReference);
					//
					ISignal scan = targetReference.getSignal();
					FontData fontData;
					if(isPeakLabel) {
						fontData = peakFontData;
					} else if(isScanLabel) {
						fontData = scanFontData;
					} else {
						fontData = null;
					}
					//
					TargetLabel targetLabel = new TargetLabel(label, showReferenceId ? targetReference.getRetentionTimeMinutes() : null, fontData, isActive, scan.getX(), scan.getY());
					targetLabels.add(targetLabel);
				}
			}
		}
		//
		Collections.sort(targetLabels, (o1, o2) -> Double.compare(o1.getX(), o2.getX()));
		return visibilityFilter;
	}

	private String getAreaPercent(ITargetReference targetReference) {

		ISignal signal = targetReference.getSignal();
		if(signal instanceof IChromatogramPeak chromatogramPeak) {
			IChromatogram<?> chromatogram = chromatogramPeak.getChromatogram();
			if(chromatogram != null) {
				double chromatogramPeakArea = chromatogram.getPeakIntegratedArea();
				if(chromatogramPeakArea > 0) {
					double peakAreaPercent = (100.0d / chromatogramPeakArea) * chromatogramPeak.getIntegratedArea();
					return decimalFormatAreaPercent.format(peakAreaPercent);
				}
			}
		}
		//
		return "";
	}

	public static FontData getPeakFontData(IPreferenceStore preferenceStore) {

		String name = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_NAME);
		int height = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE);
		int style = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE);
		//
		return new FontData(name, height, style);
	}

	public static FontData getScanFontData(IPreferenceStore preferenceStore) {

		String name = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_NAME);
		int height = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE);
		int style = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_STYLE);
		//
		return new FontData(name, height, style);
	}

	public static Color getActiveColor(IPreferenceStore preferenceStore) {

		if(!PreferencesSupport.isDarkTheme())
			return Colors.getColor(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_ACTIVE_TARGET_LABEL_FONT_COLOR));
		else
			return Colors.getColor(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_ACTIVE_TARGET_LABEL_FONT_DARK_COLOR));
	}

	public static Color getInactiveColor(IPreferenceStore preferenceStore) {

		if(!PreferencesSupport.isDarkTheme())
			return Colors.getColor(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_INACTIVE_TARGET_LABEL_FONT_COLOR));
		else
			return Colors.getColor(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_INACTIVE_TARGET_LABEL_FONT_DARK_COLOR));
	}

	public static Color getIdColor(IPreferenceStore preferenceStore) {

		if(!PreferencesSupport.isDarkTheme())
			return Colors.getColor(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_ID_TARGET_LABEL_FONT_COLOR));
		else
			return Colors.getColor(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_ID_TARGET_LABEL_FONT_DARK_COLOR));
	}
}