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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.errorresidue;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.PcaColorGroup;
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
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
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
	private BarChart<String, Number> bc;
	private ContextMenu contextMenu;
	private final List<IPcaResult> data = new ArrayList<>();
	private int displayData;
	private FXCanvas fxCanvas;
	private final Map<String, Color> groupColor = new HashMap<>();
	private Optional<IPcaResults> pcaResults = Optional.empty();
	private boolean showLegend;
	private int sortType;

	public ErrorResidueBarChart(Composite parent, Object layoutData) {
		showLegend = false;
		/*
		 * JavaFX init
		 */
		fxCanvas = new FXCanvas(parent, SWT.NONE);
		fxCanvas.setLayoutData(layoutData);
		// parent.addListener(SWT.Resize, (event) -> update());
		this.displayData = DISPLAY_SAMPLES;
		this.sortType = SORT_BY_GROUP_NAME;
		contextMenu = createContextMenu();
		createScene();
	}

	/**
	 * set color bar according to group name
	 */
	private String barStyle(IPcaResult pcaResult) {

		Color c = groupColor.get(pcaResult.getGroupName());
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
		CheckMenuItem itemDisplayLegend = new CheckMenuItem("Display Legend");
		itemDisplayLegend.setOnAction(e -> {
			if(((CheckMenuItem)e.getSource()).isSelected()) {
				showLegend = true;
				createLegend(bc);
			} else {
				showLegend = false;
				hideLegend(bc);
			}
		});
		itemDisplayLegend.setSelected(showLegend);
		contextMenu.getItems().add(itemDisplayLegend);
		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
		contextMenu.getItems().add(separatorMenuItem);
		// switch group/samples
		ToggleGroup toggleGroup = new ToggleGroup();
		RadioMenuItem radioDisplay = new RadioMenuItem("Display Samples");
		radioDisplay.setSelected(DISPLAY_SAMPLES == displayData);
		radioDisplay.setOnAction(e -> {
			if(pcaResults.isPresent()) {
				displayData = DISPLAY_SAMPLES;
				update(pcaResults.get());
			}
		});
		radioDisplay.setToggleGroup(toggleGroup);
		contextMenu.getItems().add(radioDisplay);
		radioDisplay = new RadioMenuItem("Display Groups");
		radioDisplay.setSelected(DISPLAY_GROUPS == displayData);
		radioDisplay.setOnAction(e -> {
			if(pcaResults.isPresent()) {
				displayData = DISPLAY_GROUPS;
				update(pcaResults.get());
			}
		});
		radioDisplay.setToggleGroup(toggleGroup);
		contextMenu.getItems().add(radioDisplay);
		// sorting
		separatorMenuItem = new SeparatorMenuItem();
		contextMenu.getItems().add(separatorMenuItem);
		Menu itemSort = new Menu("Sort by");
		separatorMenuItem = new SeparatorMenuItem();
		RadioMenuItem radioSorting = new RadioMenuItem("Group Name");
		toggleGroup = new ToggleGroup();
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
		bc = createBarChart();
		bc.getData().add(getSerie());
		if(showLegend) {
			createLegend(bc);
		} else {
			hideLegend(bc);
		}
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

	private XYChart.Series<String, Number> getSerie() {

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		for(IPcaResult pcaResult : data) {
			if(!pcaResult.isDisplayed()) {
				continue;
			}
			String name = null;
			/*
			 * set name displayed on x-axis
			 */
			if(DISPLAY_GROUPS == displayData) {
				name = pcaResult.getGroupName();
			} else {
				name = pcaResult.getName();
			}
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
		sortData(series);
		return series;
	}

	public int getSortType() {

		return sortType;
	}

	private Axis<String> getXAxis() {

		CategoryAxis xAxis = new CategoryAxis();
		if(displayData == DISPLAY_SAMPLES) {
			xAxis.setLabel("Sample Names");
		} else {
			xAxis.setLabel("Group Names");
		}
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
		groupColor.clear();
		createScene();
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
	public void setSortType(int sortType, boolean sortData) {

		this.sortType = sortType;
		if(sortData) {
			createScene();
		}
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

	public void update(IPcaResults pcaResults) {

		/*
		 * update data
		 */
		this.pcaResults = Optional.of(pcaResults);
		removeData();
		if(displayData == DISPLAY_SAMPLES) {
			data.addAll(pcaResults.getPcaResultList());
		} else {
			data.addAll(pcaResults.getPcaResultGroupsList());
		}
		groupColor.clear();
		groupColor.putAll(PcaColorGroup.getColorJavaFx(PcaUtils.getGroupNames(pcaResults)));
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
					n.setStyle(barStyle((IPcaResult)d.getExtraValue()));
				}
			}
		}
	}
}
