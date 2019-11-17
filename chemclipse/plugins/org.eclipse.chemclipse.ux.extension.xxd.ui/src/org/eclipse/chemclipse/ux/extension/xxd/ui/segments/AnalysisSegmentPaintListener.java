/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.segments;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.ux.extension.xxd.ui.segments.AnalysisSegmentColorScheme.AnalysisSegmentColors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.segments.AnalysisSegmentColorScheme.Type;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedChromatogramUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtchart.Chart;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.ICustomPaintListener;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.ISeries;

public class AnalysisSegmentPaintListener<X extends IAnalysisSegment> implements ICustomPaintListener {

	private static final int ALPHA = 100;
	private final AnalysisSegmentColorScheme colorScheme;
	private final Supplier<Collection<X>> segmentSupplier;
	private final Predicate<X> selectionCheck;

	public AnalysisSegmentPaintListener(AnalysisSegmentColorScheme colorScheme, Supplier<Collection<X>> segmentSupplier, Predicate<X> selectionCheck) {
		this.colorScheme = colorScheme;
		this.segmentSupplier = segmentSupplier;
		this.selectionCheck = selectionCheck;
	}

	@Override
	public void paintControl(PaintEvent evt) {

		Widget widget = evt.widget;
		if(widget instanceof IPlotArea) {
			IPlotArea plotArea = (IPlotArea)widget;
			Chart chart = plotArea.getChart();
			ISeries<?> series = chart.getSeriesSet().getSeries(ExtendedChromatogramUI.SERIES_ID_CHROMATOGRAM);
			if(series != null) {
				GC gc = evt.gc;
				AnalysisSegmentColors colors = colorScheme.create(gc);
				IAxis xAxis = chart.getAxisSet().getXAxis(series.getXAxisId());
				Rectangle clientArea = chart.getClientArea();
				int y = clientArea.y;
				int height = clientArea.height;
				gc.setLineStyle(SWT.LINE_SOLID);
				boolean alternate = false;
				gc.setForeground(colors.get(Type.LINE));
				for(X segment : segmentSupplier.get()) {
					int x1 = xAxis.getPixelCoordinate(segment.getStartRetentionTime());
					int x2 = xAxis.getPixelCoordinate(segment.getStopRetentionTime()) - x1;
					boolean isSelected = selectionCheck.test(segment);
					if(isSelected) {
						gc.setBackground(colors.get(Type.SELECTION));
						gc.setAlpha(ALPHA + 50);
					} else if(alternate) {
						gc.setBackground(colors.get(Type.SEGMENT_ODD));
						gc.setAlpha(ALPHA);
					} else {
						gc.setBackground(colors.get(Type.SEGMENT_EVEN));
						gc.setAlpha(ALPHA);
					}
					gc.fillRectangle(x1, y, x2, height);
					gc.setAlpha(255);
					if(isSelected) {
						gc.drawLine(x1, 0, x1, height);
						gc.drawLine(x2, 0, x2, height);
					}
					alternate = !alternate;
				}
				colors.dispose();
			}
		}
	}

	@Override
	public boolean drawBehindSeries() {

		return true;
	}
}
