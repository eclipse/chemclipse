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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.errorresidue;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableExtractedVisalization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.utility.PcaColorGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.sun.javafx.charts.Legend;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swt.FXCanvas;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
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
	private Optional<IPcaResults<IPcaResultVisualization, IVariableExtractedVisalization>> pcaResults = Optional.empty();

	public ExplainedVarianceBarChart(Composite parent, Object layoutData) {
		/*
		 * JavaFX init
		 */
		fxCanvas = new FXCanvas(parent, SWT.NONE);
		fxCanvas.setLayoutData(layoutData);
		contextMenu = createContextMenu();
		createScene();
	}

	/**
	 * set color bar according to group name
	 */
	private String barStyle(IPcaResultVisualization pcaResult) {

		Color c = PcaColorGroup.getSampleColorFX(pcaResult);
		if(SelectionManagerSample.getInstance().getSelection().contains(pcaResult.getSample())) {
			c = PcaColorGroup.getActualSelectedColor(c);
		} else if(!pcaResult.getSample().isSelected()) {
			c = PcaColorGroup.getUnselectedColor(c);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("-fx-bar-fill: rgb(");
		sb.append((int)(255 * c.getRed()));
		sb.append(",");
		sb.append((int)(255 * c.getGreen()));
		sb.append(",");
		sb.append((int)(255 * c.getBlue()));
		sb.append(");");
		sb.append("-fx-opacity: ");
		sb.append(c.getOpacity());
		return sb.toString();
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
		setUpZooming(bc, rect);
		/*
		 * create scene
		 */
		Point p = fxCanvas.getParent().getSize();
		Scene scene = new Scene(pane, p.x, p.y);
		fxCanvas.setScene(scene);
		fxCanvas.getParent().layout(true);
	}

	private XYChart.Series<String, Number> getSerie() {

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		for(int i = 0; i < data2.size(); i++) {
			StringBuilder name = new StringBuilder("Comp ");
			name.append(Integer.toString(i));
			final XYChart.Data<String, Number> d2 = new XYChart.Data<>(name.toString(), data2.get(i));
		}
		for(IPcaResultVisualization pcaResult : data) {
			if(!pcaResult.isDisplayed()) {
				continue;
			}
			String name = pcaResult.getName();
			final XYChart.Data<String, Number> d = new XYChart.Data<>(name, pcaResult.getErrorMemberShip());
			/*
			 * set bar color and add tooltip
			 */
			d.nodeProperty().addListener(new ChangeListener<Node>() {

				@Override
				public void changed(ObservableValue<? extends Node> ov, Node oldNode, final Node node) {

					if(node != null) {
						node.setStyle(barStyle(pcaResult));
						DecimalFormat format = new DecimalFormat("#.###E0", new DecimalFormatSymbols(Locale.US));
						Tooltip t = new Tooltip(pcaResult.getName() + '\n' + format.format(pcaResult.getErrorMemberShip()));
						Tooltip.install(node, t);
						node.setOnMouseClicked(e -> {
							if(e.getButton().equals(MouseButton.PRIMARY)) {
								if(e.getClickCount() == 2) {
									ISample<? extends ISampleData> s = pcaResult.getSample();
									if(e.isControlDown()) {
										s.setSelected(!s.isSelected());
									} else {
										ObservableList<ISample<? extends ISampleData>> selection = SelectionManagerSample.getInstance().getSelection();
										if(!selection.contains(s)) {
											selection.setAll(s);
										} else {
											selection.remove(s);
										}
									}
								}
							}
						});
					}
				}
			});
			d.setExtraValue(pcaResult);
			series.getData().add(d);
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
		yAxis.setLabel("Explained Variance");
		/*
		 * set format
		 */
		DecimalFormat format = new DecimalFormat("#.##E0", new DecimalFormatSymbols(Locale.US));
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
		createScene();
	}

	private void setUpZooming(BarChart<String, Number> chart, Rectangle rect) {

		final Node chartBackground = chart.lookup(".chart-plot-background");
		final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
		/*
		 * double click - show whole plot
		 */
		chart.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(final MouseEvent event) {

				if(event.getButton() == null) {
					return;
				}
				if(event.getButton().equals(MouseButton.SECONDARY)) {
					contextMenu.show(chart, event.getScreenX(), event.getScreenY());
					// updateSeries();
				}
				if(event.getButton().equals(MouseButton.PRIMARY)) {
					contextMenu.hide();
				}
			}
		});
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
						final Iterator<XYChart.Series<String, Number>> it = chart.getData().iterator();
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
		});
	}

	public void update(IPcaResults<IPcaResultVisualization, IVariableExtractedVisalization> pcaResults) {

		/*
		 * update data
		 */
		this.pcaResults = Optional.of(pcaResults);
		removeData();
		Double[] doubleArray = ArrayUtils.toObject(pcaResults.getExplainedVariance());
		List<Double> list = Arrays.asList(doubleArray);
		data2.addAll(list);
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
				if(n != null) {
					n.setStyle(barStyle((IPcaResultVisualization)d.getExtraValue()));
				}
			}
		}
	}
}
