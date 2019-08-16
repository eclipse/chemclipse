/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.barchart;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.sun.javafx.charts.Legend;

import javafx.embed.swt.FXCanvas;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;

public class ExplainedVarianceBarChart {

	private BarChart<String, Number> bc;
	private ContextMenu contextMenu;
	private final List<IPcaResultVisualization> data = new ArrayList<>();
	private final List<Double> data2 = new ArrayList<>();
	private FXCanvas fxCanvas;
	private Optional<IPcaResults<IPcaResultVisualization, IVariableVisualization>> pcaResults = Optional.empty();

	public ExplainedVarianceBarChart(Composite parent, Object layoutData) {
		/*
		 * JavaFX init
		 */
		fxCanvas = new FXCanvas(parent, SWT.NONE);
		fxCanvas.setLayoutData(layoutData);
		contextMenu = createContextMenu();
		createScene();
	}

	private BarChart<String, Number> createBarChart() {

		final BarChart<String, Number> bc = new BarChart<>(getXAxis(), getYAxis());
		bc.setTitle("Explained variance");
		bc.setAnimated(false);
		return bc;
	}

	private ContextMenu createContextMenu() {

		ContextMenu contextMenu = new ContextMenu();
		// reset chart
		MenuItem itemDisplay = new MenuItem("Reset Chart 1:1");
		itemDisplay.setOnAction(e -> {
			if(pcaResults.isPresent()) {
				update(pcaResults.get());
			}
		});
		contextMenu.getItems().add(itemDisplay);
		return contextMenu;
	}

	private void createScene() {

		/*
		 * create bar chart
		 */
		bc = createBarChart();
		bc.getData().add(getSerie());
		hideLegend(bc);
		/*
		 * Initialize rectangle which is used for zooming
		 */
		Rectangle rect = new Rectangle();
		rect.setManaged(false);
		rect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
		//
		StackPane pane = new StackPane();
		pane.getChildren().add(bc);
		pane.getChildren().add(rect);
		/*
		 * add zooming
		 */
		new ZoomBarChart().setUpZooming(bc, rect);
		/*
		 * show menu
		 */
		bc.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(final MouseEvent event) {

				if(event.getButton() == null) {
					return;
				}
				if(event.getButton().equals(MouseButton.SECONDARY)) {
					contextMenu.show(bc, event.getScreenX(), event.getScreenY());
					// updateSeries();
				}
				if(event.getButton().equals(MouseButton.PRIMARY)) {
					contextMenu.hide();
				}
			}
		});
		/*
		 * create scene
		 */
		Point p = fxCanvas.getParent().getSize();
		Scene scene = new Scene(pane, p.x, p.y);
		scene.getStylesheets().add("css/error_residue_chart.css");
		fxCanvas.setScene(scene);
		fxCanvas.getParent().layout(true);
	}

	private XYChart.Series<String, Number> getSerie() {

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		ListIterator<Double> iter = data2.listIterator();
		double variance = 0;
		while(iter.hasNext()) {
			StringBuilder name = new StringBuilder("Comp ");
			name.append(Integer.toString(iter.nextIndex() + 1));
			variance = iter.next();// data2.get(iter.nextIndex());
			final XYChart.Data<String, Number> d3 = new XYChart.Data<>(name.toString(), variance);
			/*
			 * d3.nodeProperty().addListener(new ChangeListener<Node>() {
			 * @Override
			 * public void changed(ObservableValue<? extends Node> ov, Node oldNode, final Node node) {
			 * if(node != null) {
			 * DecimalFormat format = new DecimalFormat("#.###E0", new DecimalFormatSymbols(Locale.US));
			 * Tooltip t = new Tooltip(name.toString() + '\n' + format.format(iter.next()));
			 * Tooltip.install(node, t);
			 * }
			 * }
			 * });
			 */
			// d2.setExtraValue(pcaResult);
			series.getData().add(d3);
		}
		return series;
	}

	private Axis<String> getXAxis() {

		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Principal Components");
		return xAxis;
	}

	private Axis<Number> getYAxis() {

		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Explained Variance (cumulative)");
		/*
		 * set format
		 */
		DecimalFormat format = new DecimalFormat("#.##%", new DecimalFormatSymbols(Locale.US));
		yAxis.setTickLabelFormatter(new StringConverter<Number>() {

			@Override
			public Number fromString(String string) {

				try {
					return format.parse(string);
				} catch(ParseException e) {
					return 0;
				}
			}

			@Override
			public String toString(Number number) {

				return format.format(number.doubleValue());
			}
		});
		return yAxis;
	}

	private void hideLegend(BarChart<String, Number> barChart) {

		Legend legend = (Legend)barChart.lookup(".chart-legend");
		legend.getItems().clear();
	}

	public void removeData() {

		data.clear();
		data2.clear();
		createScene();
	}

	public void update(IPcaResults<IPcaResultVisualization, IVariableVisualization> pcaResults) {

		/*
		 * update data
		 */
		this.pcaResults = Optional.of(pcaResults);
		removeData();
		for(double d : pcaResults.getCumulativeExplainedVariances()) {
			data2.add(d);
		}
		data.addAll(pcaResults.getPcaResultList());
		/*
		 * create scene this method support resize windows
		 */
		createScene();
	}

	public void updateSelection() {

		final Iterator<XYChart.Series<String, Number>> it = bc.getData().iterator();
		while(it.hasNext()) {
			final XYChart.Series<String, Number> s = it.next();
			for(XYChart.Data<String, Number> d : s.getData()) {
				Node n = d.getNode();
			}
		}
	}
}
