/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.errorresidue;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.PcaEditor;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.PcaColorGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

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
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;

public class ErrorResidueBarChart {

	final static public int DISPLAY_GROUPS = 1;
	final static public int DISPLAY_SAMPLES = 0;
	final static public int SORT_BY_ERROR_RESIDUES = 1;
	final static public int SORT_BY_GROUP_NAME = 0;
	final static public int SORT_BY_NAME = 2;
	private List<ISample> data = new ArrayList<>();
	private int displayData;
	private FXCanvas fxCanvas;
	private Map<String, Color> groupColor;
	private PcaEditor pcaEditor;
	private XYChart.Series<String, Number> series = new XYChart.Series<>();
	private int sortType;

	public ErrorResidueBarChart(PcaEditor pcaEditor, Composite parent, FormToolkit formToolkit) {
		/*
		 * JavaFX init
		 */
		fxCanvas = new FXCanvas(parent, SWT.NONE);
		parent.addListener(SWT.Resize, (event) -> update());
		this.pcaEditor = pcaEditor;
		this.displayData = DISPLAY_SAMPLES;
		this.sortType = SORT_BY_GROUP_NAME;
		update();
	}

	/**
	 * set color bar according to group name
	 */
	private String barStyle(ISample sample) {

		Color c = groupColor.get(sample.getGroupName());
		StringBuilder sb = new StringBuilder();
		sb.append("-fx-bar-fill: rgb(");
		sb.append((int)(255 * c.getRed()));
		sb.append(",");
		sb.append((int)(255 * c.getGreen()));
		sb.append(",");
		sb.append((int)(255 * c.getBlue()));
		sb.append(")");
		return sb.toString();
	}

	private BarChart<String, Number> createBarChart() {

		final BarChart<String, Number> bc = new BarChart<>(getXAxis(), getYAxis());
		bc.setTitle("Error residue");
		bc.setAnimated(false);
		return bc;
	}

	private void createLegend(BarChart<String, Number> barChart) {

		Legend legend = (Legend)barChart.lookup(".chart-legend");
		legend.getItems().clear();
		Iterator<Entry<String, Color>> it = groupColor.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, Color> entry = it.next();
			Legend.LegendItem li = new Legend.LegendItem(entry.getKey(), new Rectangle(10, 10, entry.getValue()));
			legend.getItems().add(li);
		}
	}

	private void createScene() {

		/*
		 * create bar chart
		 */
		BarChart<String, Number> bc = createBarChart();
		updateSeries();
		bc.getData().add(series);
		createLegend(bc);
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

	public int getDisplayData() {

		return displayData;
	}

	public int getSortType() {

		return sortType;
	}

	private Axis<String> getXAxis() {

		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Sample Names");
		return xAxis;
	}

	private Axis<Number> getYAxis() {

		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Error Values");
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

	/**
	 * Use update method to make changes
	 *
	 * @param displayData
	 */
	public void setDisplayData(int displayData) {

		this.displayData = displayData;
	}

	/**
	 * Use update method to make changes
	 *
	 * @param sortType
	 */
	public void setSortType(int sortType) {

		this.sortType = sortType;
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

				if(event.getButton().equals(MouseButton.PRIMARY)) {
					if(event.getClickCount() == 2) {
						updateSeries();
					}
				}
			}
		});
		/*
		 * set start point of rectangle
		 */
		chart.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(final MouseEvent event) {

				mouseAnchor.set(new Point2D(event.getX(), event.getY()));
			}
		});
		/*
		 * Create a rectangle to select the graph data
		 */
		chart.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(final MouseEvent event) {

				final double x = event.getX();
				rect.setX(Math.min(x, mouseAnchor.get().getX()));
				rect.setY(chart.getLayoutY());
				rect.setWidth(Math.abs(x - mouseAnchor.get().getX()));
				rect.setHeight(chart.getHeight());
			}
		});
		/*
		 * Select the data that is bounded by a rectangle
		 */
		chart.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(final MouseEvent event) {

				final Bounds bb = chartBackground.sceneToLocal(rect.getBoundsInLocal());
				double minx = bb.getMinX();
				double maxx = bb.getMaxX();
				/*
				 * refuse click
				 */
				if(maxx - minx < 2) {
					return;
				}
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
				if(!(minMarkIndex == -1 || maxMarkIndex == -1)) {
					final Iterator<XYChart.Series<String, Number>> it = chart.getData().iterator();
					while(it.hasNext()) {
						final XYChart.Series<String, Number> s = it.next();
						for(int j = s.getData().size() - 1; j >= 0; j--) {
							if(j > maxMarkIndex || j < minMarkIndex) {
								s.getData().remove(j);
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

	private void sortData() {

		switch(sortType) {
			case SORT_BY_GROUP_NAME:
				PcaUtils.sortSampleListByErrorMemberShip(data, true);
				PcaUtils.sortSampleListByGroup(data);
				break;
			case SORT_BY_ERROR_RESIDUES:
				PcaUtils.sortSampleListByErrorMemberShip(data, true);
				break;
			case SORT_BY_NAME:
				PcaUtils.sortSampleListByName(data);
				break;
			default:
				break;
		}
	}

	public void update() {

		/*
		 * update data
		 */
		data.clear();
		groupColor = null;
		IPcaResults pcaResults = pcaEditor.getPcaResults();
		if(pcaResults != null) {
			/*
			 * select data to display samples or groups
			 */
			if(displayData == DISPLAY_SAMPLES) {
				data.addAll(pcaResults.getSampleList());
			} else if(displayData == DISPLAY_GROUPS) {
				data.addAll(pcaResults.getGroupList());
			}
			groupColor = PcaColorGroup.getColorJavaFx(PcaUtils.getGroupNames(pcaResults.getSampleList(), true));
		}
		/*
		 * sort data
		 */
		sortData();
		/*
		 * create scene this method support resize windows
		 */
		createScene();
	}

	private void updateSeries() {

		series.getData().clear();
		for(ISample sample : data) {
			String name = null;
			/*
			 * set name displayed on x-axis
			 */
			if(DISPLAY_GROUPS == displayData) {
				name = sample.getGroupName();
			} else {
				name = sample.getName();
			}
			final String tooltipName = name;
			XYChart.Data<String, Number> d = new XYChart.Data<>(name, sample.getPcaResult().getErrorMemberShip());
			/*
			 * set bar color and add tooltip
			 */
			d.nodeProperty().addListener(new ChangeListener<Node>() {

				@Override
				public void changed(ObservableValue<? extends Node> ov, Node oldNode, final Node node) {

					if(node != null) {
						node.setStyle(barStyle(sample));
						DecimalFormat format = new DecimalFormat("#.###E0", new DecimalFormatSymbols(Locale.US));
						Tooltip t = new Tooltip(tooltipName + '\n' + format.format(sample.getPcaResult().getErrorMemberShip()));
						Tooltip.install(node, t);
					}
				}
			});
			d.setExtraValue(sample);
			series.getData().add(d);
		}
	}
}
