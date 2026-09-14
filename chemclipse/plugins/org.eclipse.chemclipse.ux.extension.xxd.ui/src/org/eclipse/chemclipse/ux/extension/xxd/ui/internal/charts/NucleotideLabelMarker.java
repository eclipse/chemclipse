/*******************************************************************************
 * Copyright (c) 2019, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Matthias Mailänder - adapted for nucleotide display
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.eclipse.chemclipse.dsd.model.core.Nucleobase;
import org.eclipse.chemclipse.dsd.model.core.NucleotideSequence;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.ITargetReference;
import org.eclipse.chemclipse.model.targets.TargetReference;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

public class NucleotideLabelMarker implements ICustomPaintListener {

	private static final int NO_ALPHA = 255;

	private IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
	private ITheme currentTheme = themeManager.getCurrentTheme();
	private FontRegistry fontRegistry = currentTheme.getFontRegistry();

	private TargetReferenceSettings targetReferenceSettings;
	private List<NucleotideLabel> nucleotideLabels = new ArrayList<>();
	private ICustomSeries customSeries = null;

	private boolean visible = true;
	private int detectionDepth = 0;

	public NucleotideLabelMarker(TargetReferenceSettings targetReferenceSettings) {

		this.targetReferenceSettings = targetReferenceSettings;

		BaseChart baseChart = targetReferenceSettings.getBaseChart();
		if(baseChart != null) {
			String label = targetReferenceSettings.getLabel();
			String description = targetReferenceSettings.getDescription();
			this.customSeries = baseChart.createCustomSeries(label, description);
		}

		setTargetReferences(targetReferenceSettings.getTargetReferences());
	}

	/**
	 * Returns an unmodifiable list of the contained target labels.
	 *
	 * @return {@link List}
	 */
	public List<NucleotideLabel> getNucleotideLabels() {

		return Collections.unmodifiableList(nucleotideLabels);
	}

	public ICustomSeries getCustomSeries() {

		return customSeries;
	}

	@Override
	public void paintControl(PaintEvent event) {

		if(visible && !nucleotideLabels.isEmpty()) {
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

		Transform transform = new Transform(gc.getDevice());
		Transform oldTransform = new Transform(gc.getDevice());
		gc.getTransform(oldTransform);
		Map<FontData, Font> fontMap = new IdentityHashMap<>();
		Font oldFont = gc.getFont();
		gc.setAlpha(NO_ALPHA);
		float[] identityMatrix = new float[6];
		oldTransform.getElements(identityMatrix);

		try {
			/*
			 * The registry returns null if the theme definition is missing, which would let the GC fail the whole plot area repaint.
			 */

			Rectangle clipping = gc.getClipping();
			NucleotideLabel lastReference = null;

			int collisions = 0;
			for(NucleotideLabel reference : nucleotideLabels) {

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
					textElement.setColor(reference.getColor());
					textElement.setX(reference.getX());
					textElement.setY(reference.getY());
					textElement.setRotation(0);
					customSeries.getTextElements().add(textElement);
				}

				if(reference.getFont() != null) {
					gc.setFont(reference.getFont());
				} else {
					gc.setFont(oldFont);
				}

				reference.setBounds(new LabelBounds(gc, reference.getLabel()));
				String label = reference.getLabel();
				setTransform(transform, x, y, reference, identityMatrix);
				if(reference.isActive()) {
					gc.setForeground(reference.getColor());
					gc.setBackground(reference.getColor());
				} else {
					gc.setForeground(reference.getColor());
					gc.setBackground(reference.getColor());
				}

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

					if(collisions > detectionDepth) {
						lastReference = null;
						collisions = 0;
					} else {
						lastReference = reference;
					}
				}

				gc.setTransform(transform);
				gc.drawText(label, 0, 0, true);

				if(reference.getId() != null && reference.isActive()) {
					gc.setForeground(reference.getColor());
					gc.drawText(reference.getId(), reference.getBounds().getWidth() + offset / 2, 0, true);
				}
			}

			for(NucleotideLabel reference : nucleotideLabels) {
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

	private int setTransform(Transform transform, float x, float y, NucleotideLabel reference, float[] identityMatrix) {

		int offset = reference.getBounds().getWidth();
		int h = reference.getBounds().getHeight();
		transform.setElements(identityMatrix[0], identityMatrix[1], identityMatrix[2], identityMatrix[3], identityMatrix[4], identityMatrix[5]);
		transform.translate(x, y - offset);
		transform.rotate(0);
		transform.translate(0, -h / 2);
		reference.getBounds().setTransform(transform);

		return h;
	}

	private void drawHandle(GC gc, NucleotideLabel reference, int x, int y, boolean upsideDown, float[] identityMatrix) {

		int offset = reference.getBounds().getWidth();
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

	private Nucleobase getNucleobase(ITargetReference targetReference) {

		IIdentificationTarget identificationTarget = targetReference.getBestIdentificationTarget();
		if(identificationTarget != null) {
			return NucleotideSequence.getNucleobase(identificationTarget);
		}

		return null;
	}

	private Predicate<ITargetReference> setTargetReferences(Collection<? extends TargetReference> targetReferences) {

		return setTargetReferences(targetReferences, _ -> true);
	}

	private Predicate<ITargetReference> setTargetReferences(Collection<? extends TargetReference> targetReferences, Predicate<ITargetReference> activeFilter) {

		nucleotideLabels.clear();

		ITargetDisplaySettings targetDisplaySettings = targetReferenceSettings.getTargetDisplaySettings();
		Predicate<ITargetReference> visibilityFilter = TargetReference.createVisibilityFilter(targetDisplaySettings);

		if(targetDisplaySettings != null) {
			/*
			 * Settings
			 */
			detectionDepth = targetDisplaySettings.getCollisionDetectionDepth();

			Font scanFont = fontRegistry.get(NucleotideLabelMarker.class.getName() + ".Scan.Font");

			for(ITargetReference targetReference : targetReferences) {
				if(visibilityFilter.test(targetReference)) {
					/*
					 * Get the label.
					 */
					Nucleobase nucleobase = getNucleobase(targetReference);
					if(nucleobase == null) {
						continue;
					}

					boolean isActive = activeFilter == null || activeFilter.test(targetReference);

					ISignal scan = targetReference.getSignal();
					Font font;
					font = scanFont;

					double x = scan instanceof IScan scanX ? scanX.getScanNumber() : scan.getX();
					NucleotideLabel targetLabel = new NucleotideLabel(nucleobase, null, font, isActive, x, scan.getY());
					nucleotideLabels.add(targetLabel);
				}
			}
		}

		Collections.sort(nucleotideLabels, (o1, o2) -> Double.compare(o1.getX(), o2.getX()));
		return visibilityFilter;
	}
}
