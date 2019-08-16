/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.barchart;

import java.util.Iterator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class ZoomBarChart {

	public ZoomBarChart() {
	}

	public void setUpZooming(XYChart chart, Rectangle rect) {

		final Node chartBackground = chart.lookup(".chart-plot-background");
		final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
		/*
		 * set start point of rectangle
		 */
		chart.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(final MouseEvent event) {

				if(event.getButton() == null || !event.getButton().equals(MouseButton.PRIMARY)) {
					return;
				}
				mouseAnchor.set(new Point2D(event.getX(), event.getY()));
			}
		});
		/*
		 * Create a rectangle to select the graph data
		 */
		chart.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(final MouseEvent event) {

				if(event.getButton() == null || !event.getButton().equals(MouseButton.PRIMARY)) {
					return;
				}
				final double x = event.getX();
				if(mouseAnchor.isNotNull().get()) {
					rect.setX(Math.min(x, mouseAnchor.get().getX()));
					rect.setY(chart.getLayoutY());
					rect.setWidth(Math.abs(x - mouseAnchor.get().getX()));
					rect.setHeight(chart.getHeight());
				}
			}
		});
		/*
		 * Select the data that is bounded by a rectangle
		 */
		chart.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(final MouseEvent event) {

				buttonRelease(event, chart, rect);
			}
		});
		rect.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(final MouseEvent event) {

				buttonRelease(event, chart, rect);
			}
		});
	}

	private void buttonRelease(MouseEvent event, XYChart chart, Rectangle rect) {

		final Node chartBackground = chart.lookup(".chart-plot-background");
		if(event.getButton() == null || !event.getButton().equals(MouseButton.PRIMARY)) {
			return;
		}
		final Bounds bb = chartBackground.sceneToLocal(rect.getBoundsInLocal());
		double minx = bb.getMinX();
		double maxx = bb.getMaxX();
		/*
		 * refuse click
		 */
		if((maxx - minx) > 2) {
			/*
			 * select x-Axis
			 */
			CategoryAxis axisX = (CategoryAxis)chart.getXAxis();
			/*
			 * get first and last selected bar in chart
			 */
			ObservableList<Axis.TickMark<String>> list = axisX.getTickMarks();
			double minMarkPostion = Double.POSITIVE_INFINITY;
			int minMarkIndex = -1;
			double maxMarkPostion = Double.POSITIVE_INFINITY;
			int maxMarkIndex = -1;
			int i = 0;
			for(Axis.TickMark<String> mark : list) {
				double markPostion = mark.getPosition();
				/*
				 * get index first selected bar
				 */
				if(Math.abs(markPostion - minx) < Math.abs(minMarkPostion - minx) && markPostion - minx > 0) {
					minMarkPostion = markPostion;
					minMarkIndex = i;
				}
				/*
				 * get index last selected bar
				 */
				if(Math.abs(markPostion - maxx) < Math.abs(maxMarkPostion - maxx) && maxx - markPostion > 0) {
					maxMarkPostion = markPostion;
					maxMarkIndex = i;
				}
				i++;
			}
			/*
			 * Remove the unselected date by index
			 */
			if(!(minMarkIndex == -1 || maxMarkIndex == -1) && (minMarkIndex <= maxMarkIndex)) {
				final Iterator<XYChart.Series> it = chart.getData().iterator();
				ObservableList data = chart.getData();
				while(it.hasNext()) {
					final XYChart.Series<String, Number> s = it.next();
					for(int j = s.getData().size() - 1; j >= 0; j--) {
						if(j > maxMarkIndex || j < minMarkIndex) {
							data.remove(s.getData().get(j).getExtraValue());
							s.getData().remove(j);
						}
					}
				}
			}
		}
		/*
		 * close rectangle
		 */
		rect.setWidth(0);
		rect.setHeight(0);
	}
}
