/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
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

import java.util.ArrayList;
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
import org.eclipse.chemclipse.swt.ui.support.Fonts;
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
import org.eclipse.swtchart.extensions.model.ICustomSeries;
import org.eclipse.swtchart.extensions.model.TextElement;

public class TargetReferenceLabelMarker implements ICustomPaintListener {

	private static final int NO_ALPHA = 255;
	//
	private TargetReferenceSettings targetReferenceSettings;
	private List<TargetLabel> targetLabels = new ArrayList<>();
	private ICustomSeries customSeries = null;
	//
	private boolean visible = true;
	private int rotation = 0;
	private int detectionDepth = 0;

	public TargetReferenceLabelMarker(TargetReferenceSettings targetReferenceSettings) {

		this.targetReferenceSettings = targetReferenceSettings;
		//
		BaseChart baseChart = targetReferenceSettings.getBaseChart();
		if(baseChart != null) {
			String label = targetReferenceSettings.getLabel();
			String description = targetReferenceSettings.getDescription();
			this.customSeries = baseChart.createCustomSeries(label, description);
		}
		//
		setTargetReferences(targetReferenceSettings.getTargetReferences());
	}

	/**
	 * Returns an unmodifiable list of the contained target labels.
	 * 
	 * @return {@link List}
	 */
	public List<TargetLabel> getTargetLabels() {

		return Collections.unmodifiableList(targetLabels);
	}

	public ICustomSeries getCustomSeries() {

		return customSeries;
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
				//
				ISeries<?> series = getReferenceSeries(chart);
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

	/**
	 * Returns the series reference.
	 * 
	 * @param chart
	 * @return the series for the given chart to use as a reference
	 */
	private ISeries<?> getReferenceSeries(Chart chart) {

		return chart.getSeriesSet().getSeries(targetReferenceSettings.getReferenceSeriesId());
	}

	private void paintLabels(GC gc, IAxis xAxis, IAxis yAxis) {

		int offset = targetReferenceSettings.getOffset();
		//
		Transform transform = new Transform(gc.getDevice());
		Transform oldTransform = new Transform(gc.getDevice());
		gc.getTransform(oldTransform);
		Map<FontData, Font> fontMap = new IdentityHashMap<>();
		Font oldFont = gc.getFont();
		gc.setAlpha(NO_ALPHA);
		float[] identityMatrix = new float[6];
		oldTransform.getElements(identityMatrix);
		//
		try {
			Color colorActive = TargetReferencesSupport.getActiveColor();
			Color colorInactive = TargetReferencesSupport.getInactiveColor();
			Color colorId = TargetReferencesSupport.getIdColor();
			//
			Rectangle clipping = gc.getClipping();
			TargetLabel lastReference = null;
			//
			int collisions = 0;
			for(TargetLabel reference : targetLabels) {
				int x = xAxis.getPixelCoordinate(reference.getX());
				int y = yAxis.getPixelCoordinate(reference.getY());
				if(!clipping.contains(x, y)) {
					continue;
				}
				/*
				 * Map the labels to be drawn via SWTChart
				 */
				if(customSeries != null) {
					TextElement textElement = new TextElement();
					textElement.setLabel(reference.getLabel());
					textElement.setColor(reference.isActive() ? colorActive : colorInactive);
					textElement.setX(reference.getX());
					textElement.setY(reference.getY());
					textElement.setRotation(-rotation);
					customSeries.getTextElements().add(textElement);
				}
				//
				if(reference.getFontData() != null) {
					Font font = fontMap.computeIfAbsent(reference.getFontData(), fd -> {
						return Fonts.createDPIAwareFont(gc.getDevice(), fd);
					});
					gc.setFont(font);
				} else {
					gc.setFont(oldFont);
				}
				//
				reference.setBounds(new LabelBounds(gc, reference));
				String label = reference.getLabel();
				setTransform(transform, x, y, reference, identityMatrix);
				if(reference.isActive()) {
					gc.setForeground(colorActive);
					gc.setBackground(colorActive);
				} else {
					gc.setForeground(colorInactive);
					gc.setBackground(colorInactive);
				}
				//
				if(detectionDepth > 0) {
					if(lastReference != null && lastReference.getBounds() != null) {
						if(lastReference.getBounds().getCx() > reference.getBounds().getCx() || lastReference.getBounds().intersects(reference.getBounds())) {
							collisions++;
							/*
							 * first guess is to move the label up
							 */
							float yoffset = lastReference.getBounds().offsetY(reference.getBounds());
							setTransform(transform, Math.max(x, lastReference.getBounds().getCx()) + offset - identityMatrix[4], y - yoffset - offset, reference, identityMatrix);
							/*
							 * check if the label is not cut of
							 */
							if(clipping.contains(Math.round(reference.getBounds().getTopX()), Math.round(reference.getBounds().getTopY()))) {
								gc.setTransform(oldTransform);
								drawHandle(gc, reference, x, y, true, identityMatrix);
							} else {
								/*
								 * reset values
								 */
								setTransform(transform, x, y, reference, identityMatrix);
								/*
								 * then move it to the right... (might still be cut of but that is the default behavior of current charting)
								 */
								float xoffset = lastReference.getBounds().offsetX(reference.getBounds());
								setTransform(transform, x + xoffset + offset, y, reference, identityMatrix);
								gc.setTransform(oldTransform);
								drawHandle(gc, reference, x, y, false, identityMatrix);
							}
						} else {
							collisions = 0;
						}
					}
					//
					if(collisions > detectionDepth) {
						lastReference = null;
						collisions = 0;
					} else {
						lastReference = reference;
					}
				}
				//
				gc.setTransform(transform);
				gc.drawText(label, 0, 0, true);
				//
				if(reference.getId() != null && reference.isActive()) {
					gc.setForeground(colorId);
					gc.drawText(reference.getId(), reference.getBounds().getWidth() + offset / 2, 0, true);
				}
			}
			//
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

		int offset = targetReferenceSettings.getOffset();
		int h = reference.getBounds().getHeight();
		transform.setElements(identityMatrix[0], identityMatrix[1], identityMatrix[2], identityMatrix[3], identityMatrix[4], identityMatrix[5]);
		transform.translate(x, y - offset);
		transform.rotate(-rotation);
		transform.translate(0, -h / 2);
		reference.getBounds().setTransform(transform);
		//
		return h;
	}

	private void drawHandle(GC gc, TargetLabel reference, int x, int y, boolean upsideDown, float[] identityMatrix) {

		int offset = targetReferenceSettings.getOffset();
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

	private Predicate<ITargetReference> setTargetReferences(Collection<? extends TargetReference> targetReferences) {

		return setTargetReferences(targetReferences, always -> true);
	}

	private Predicate<ITargetReference> setTargetReferences(Collection<? extends TargetReference> targetReferences, Predicate<ITargetReference> activeFilter) {

		targetLabels.clear();
		//
		ITargetDisplaySettings targetDisplaySettings = targetReferenceSettings.getTargetDisplaySettings();
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
			FontData peakFontData = TargetReferencesSupport.getPeakFontData();
			FontData scanFontData = TargetReferencesSupport.getScanFontData();
			//
			int number = 1;
			for(ITargetReference targetReference : targetReferences) {
				if(visibilityFilter.test(targetReference)) {
					/*
					 * Get the label.
					 */
					String labelDisplay = null;
					String labelStandard = targetReference.getTargetLabel(libraryField);
					//
					switch(displayOption) {
						case NUMBERS:
							labelDisplay = String.valueOf(number++);
							break;
						case NUMBERS_STANDARD:
							labelDisplay = getConcatenatedLabel(String.valueOf(number++), labelStandard);
							break;
						case RETENTION_TIME:
							labelDisplay = targetReference.getRetentionTimeMinutes();
							break;
						case RETENTION_TIME_STANDARD:
							labelDisplay = getConcatenatedLabel(targetReference.getRetentionTimeMinutes(), labelStandard);
							break;
						case RETENTION_INDEX:
							labelDisplay = TargetReferencesSupport.DECIMAL_FORMAT_RI.format(targetReference.getRetentionIndex());
							break;
						case RETENTION_INDEX_STANDARD:
							labelDisplay = getConcatenatedLabel(TargetReferencesSupport.DECIMAL_FORMAT_RI.format(targetReference.getRetentionIndex()), labelStandard);
							break;
						case RETENTION_INDEX_AREA_PERCENT:
							labelDisplay = TargetReferencesSupport.DECIMAL_FORMAT_RI.format(targetReference.getRetentionIndex()) + " (" + getAreaPercent(targetReference) + ")";
							break;
						case AREA_PERCENT:
							labelDisplay = getAreaPercent(targetReference);
							break;
						case AREA_PERCENT_STANDARD:
							labelDisplay = getConcatenatedLabel(getAreaPercent(targetReference), labelStandard);
							break;
						default:
							labelDisplay = labelStandard;
							if(labelDisplay == null || labelDisplay.isEmpty()) {
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
					TargetLabel targetLabel = new TargetLabel(labelDisplay, targetReferenceSettings.isShowReferenceId() ? targetReference.getRetentionTimeMinutes() : null, fontData, isActive, scan.getX(), scan.getY());
					targetLabels.add(targetLabel);
				}
			}
		}
		//
		Collections.sort(targetLabels, (o1, o2) -> Double.compare(o1.getX(), o2.getX()));
		return visibilityFilter;
	}

	private String getConcatenatedLabel(String displayLabel, String targetLabel) {

		if(targetLabel == null || targetLabel.isEmpty()) {
			return displayLabel;
		} else {
			return displayLabel + " [" + targetLabel + "]";
		}
	}

	private String getAreaPercent(ITargetReference targetReference) {

		ISignal signal = targetReference.getSignal();
		if(signal instanceof IChromatogramPeak chromatogramPeak) {
			IChromatogram<?> chromatogram = chromatogramPeak.getChromatogram();
			if(chromatogram != null) {
				double chromatogramPeakArea = chromatogram.getPeakIntegratedArea();
				if(chromatogramPeakArea > 0) {
					double peakAreaPercent = (100.0d / chromatogramPeakArea) * chromatogramPeak.getIntegratedArea();
					return TargetReferencesSupport.DECIMAL_FORMAT_AREA_PERCENT.format(peakAreaPercent);
				}
			}
		}
		//
		return "";
	}
}