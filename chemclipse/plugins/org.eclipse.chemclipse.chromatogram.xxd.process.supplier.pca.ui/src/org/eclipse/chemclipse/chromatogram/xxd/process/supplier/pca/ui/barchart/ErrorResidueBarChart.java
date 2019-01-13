/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableExtractedVisalization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.utility.PcaColorGroup;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.sun.javafx.charts.Legend;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;

public class ErrorResidueBarChart {

	/*
	 * final static public int DISPLAY_GROUPS = 1;
	 * final static public int DISPLAY_SAMPLES = 0;
	 */
	final static public int SORT_BY_ERROR_RESIDUES = 1;
	final static public int SORT_BY_GROUP_NAME = 0;
	final static public int SORT_BY_NAME = 2;
	private BarChart<String, Number> bc;
	private ContextMenu contextMenu;
	private final List<IPcaResultVisualization> data = new ArrayList<>();
	// private int displayData;
	private FXCanvas fxCanvas;
	// private final Map<String, Color> groupColor = new HashMap<>();
	private Optional<IPcaResults<IPcaResultVisualization, IVariableExtractedVisalization>> pcaResults = Optional.empty();
	private int sortType;

	public ErrorResidueBarChart(Composite parent, Object layoutData) {

		/*
		 * JavaFX init
		 */
		fxCanvas = new FXCanvas(parent, SWT.NONE);
		fxCanvas.setLayoutData(layoutData);
		// parent.addListener(SWT.Resize, (event) -> update());
		// this.displayData = DISPLAY_SAMPLES;
		this.sortType = SORT_BY_GROUP_NAME;
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
		bc.setTitle("Error residue");
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
		// sorting
		Menu itemSort = new Menu("Sort by");
		RadioMenuItem radioSorting = new RadioMenuItem("Group Name");
		ToggleGroup toggleGroup = new ToggleGroup();
		radioSorting.setOnAction(e -> setSortType(SORT_BY_GROUP_NAME, true));
		radioSorting.setToggleGroup(toggleGroup);
		radioSorting.setSelected(SORT_BY_GROUP_NAME == sortType);
		itemSort.getItems().add(radioSorting);
		radioSorting = new RadioMenuItem("Error Residue,");
		radioSorting.setOnAction(e -> setSortType(SORT_BY_ERROR_RESIDUES, true));
		radioSorting.setToggleGroup(toggleGroup);
		radioSorting.setSelected(SORT_BY_ERROR_RESIDUES == sortType);
		itemSort.getItems().add(radioSorting);
		radioSorting = new RadioMenuItem("Name");
		radioSorting.setOnAction(e -> setSortType(SORT_BY_NAME, true));
		radioSorting.setToggleGroup(toggleGroup);
		radioSorting.setSelected(SORT_BY_NAME == sortType);
		itemSort.getItems().add(radioSorting);
		contextMenu.getItems().add(itemSort);
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
									ISample s = pcaResult.getSample();
									if(e.isControlDown()) {
										s.setSelected(!s.isSelected());
									} else {
										ObservableList<ISample> selection = SelectionManagerSample.getInstance().getSelection();
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
		sortData(series);
		return series;
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

	private void hideLegend(BarChart<String, Number> barChart) {

		Legend legend = (Legend)barChart.lookup(".chart-legend");
		legend.getItems().clear();
	}

	public void removeData() {

		data.clear();
		createScene();
	}

	/**
	 * Use update method to make changes
	 *
	 * @param sortType
	 */
	public void setSortType(int sortType, boolean sortData) {

		this.sortType = sortType;
		if(sortData) {
			createScene();
		}
	}

	private void sortData(XYChart.Series<String, Number> series) {

		ObservableList<XYChart.Data<String, Number>> data = series.getData();
		switch(sortType) {
			case SORT_BY_GROUP_NAME:
				Collections.sort(data, (arg0, arg1) -> -Double.compare(((IPcaResult)arg0.getExtraValue()).getErrorMemberShip(), ((IPcaResult)arg1.getExtraValue()).getErrorMemberShip()));
				Comparator<XYChart.Data<String, Number>> comparator = (arg0, arg1) -> {
					String name0 = ((IPcaResult)arg0.getExtraValue()).getGroupName();
					String name1 = ((IPcaResult)arg1.getExtraValue()).getGroupName();
					if(name0 == null && name1 == null) {
						return 0;
					}
					if(name0 != null && name1 == null) {
						return 1;
					}
					if(name0 == null && name1 != null) {
						return -1;
					}
					return name0.compareTo(name1);
				};
				Collections.sort(data, comparator);
				break;
			case SORT_BY_ERROR_RESIDUES:
				Collections.sort(data, (arg0, arg1) -> -Double.compare(((IPcaResult)arg0.getExtraValue()).getErrorMemberShip(), ((IPcaResult)arg1.getExtraValue()).getErrorMemberShip()));
				break;
			case SORT_BY_NAME:
				Collections.sort(data, (arg0, arg1) -> ((IPcaResult)arg0.getExtraValue()).getName().compareTo(((IPcaResult)arg1.getExtraValue()).getName()));
				break;
			default:
				break;
		}
	}

	public void update(IPcaResults<IPcaResultVisualization, IVariableExtractedVisalization> pcaResults) {

		/*
		 * update data
		 */
		this.pcaResults = Optional.of(pcaResults);
		removeData();
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
