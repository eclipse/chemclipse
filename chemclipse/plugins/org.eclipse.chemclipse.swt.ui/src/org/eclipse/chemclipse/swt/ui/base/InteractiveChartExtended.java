/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.base;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxis.Direction;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.charts.InteractiveChart;
import org.eclipse.swtchart.extensions.properties.AxisPage;
import org.eclipse.swtchart.extensions.properties.AxisTickPage;
import org.eclipse.swtchart.extensions.properties.ChartPage;
import org.eclipse.swtchart.extensions.properties.GridPage;
import org.eclipse.swtchart.extensions.properties.LegendPage;
import org.eclipse.swtchart.extensions.properties.PropertiesResources;
import org.eclipse.swtchart.extensions.properties.SeriesLabelPage;
import org.eclipse.swtchart.extensions.properties.SeriesPage;

public class InteractiveChartExtended extends InteractiveChart implements PaintListener, KeyListener, MouseListener, MouseMoveListener, MouseWheelListener, IResetUpdateListener {

	private long clickedTimeInMilliseconds;
	private int xStart;
	//
	public static final String ADJUST_AXIS_RANGE_GROUP = "Unzoom";
	public static final String ADJUST_AXIS_RANGE = "Reset 1:1";
	public static final String ADJUST_X_AXIS_RANGE = "Unzoom X-Axis";
	public static final String ADJUST_Y_AXIS_RANGE = "Unzoom Y-Axis";
	public static final String ADJUST_PREVIOUS_AXIS_RANGE = "Previous Selection";
	public static final String LEGEND = "Legend";
	public static final String LEGEND_SHOW = "Show Legend";
	public static final String LEGEND_HIDE = "Hide Legend";
	public static final String GRAPH = "Graph";
	public static final String GRAPH_SETTINGS = "Settings";
	//
	private static final String X_AXIS = "Axis(x)";
	private static final String GRID = "Grid";
	private static final String TICK = "Tick";
	private static final String Y_AXIS = "Axis(y)";
	private static final String SERIES = "Series";
	private static final String LABEL = "Label";
	//
	private PropertiesResources resources;

	public InteractiveChartExtended(Composite parent, int style) {
		super(parent, style);
		resources = new PropertiesResources();
		init();
	}

	@Override
	public void handleEvent(Event event) {

		// super.handleEvent(event);
		switch(event.type) {
			case SWT.Selection:
				widgetSelected(event);
				break;
			case SWT.Resize:
				updateLayout();
				redraw();
				break;
			default:
				break;
		}
	}

	@Override
	public void paintControl(PaintEvent e) {

		selection.draw(e.gc);
	}

	@Override
	public void dispose() {

		super.dispose();
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {

	}

	@Override
	public void mouseDown(MouseEvent e) {

		if(e.button == 1) {
			xStart = e.x;
			selection.setStartPoint(e.x, e.y);
			clickedTimeInMilliseconds = System.currentTimeMillis();
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {

		if(e.button == 1 && System.currentTimeMillis() - clickedTimeInMilliseconds > 100) {
			/*
			 * If the selection is too narrow, skip it.
			 * That prevents unwanted zooming.
			 */
			Composite plotArea = getPlotArea();
			int minSelectedWidth = plotArea.getBounds().width / 30;
			int deltaWidth = Math.abs(xStart - e.x);
			if(deltaWidth >= minSelectedWidth) {
				/*
				 * Calculate the range for each axis.
				 */
				for(IAxis axis : getAxisSet().getAxes()) {
					/*
					 * Get the range.
					 */
					Point range = null;
					if((getOrientation() == SWT.HORIZONTAL && axis.getDirection() == Direction.X) || (getOrientation() == SWT.VERTICAL && axis.getDirection() == Direction.Y)) {
						range = selection.getHorizontalRange();
					} else {
						range = selection.getVerticalRange();
					}
					/*
					 * Set the range.
					 */
					if(range != null && range.x != range.y) {
						setRange(range, axis);
					}
				}
			}
		}
		selection.dispose();
		redraw();
	}

	@Override
	public void mouseMove(MouseEvent e) {

		if(!selection.isDisposed()) {
			selection.setEndPoint(e.x, e.y);
			redraw();
		}
	}

	@Override
	public void mouseScrolled(MouseEvent e) {

		for(IAxis axis : getAxes(SWT.HORIZONTAL)) {
			double coordinate = axis.getDataCoordinate(e.x);
			if(e.count > 0) {
				axis.zoomIn(coordinate);
			} else {
				axis.zoomOut(coordinate);
			}
		}
		for(IAxis axis : getAxes(SWT.VERTICAL)) {
			double coordinate = axis.getDataCoordinate(e.y);
			if(e.count > 0) {
				axis.zoomIn(coordinate);
			} else {
				axis.zoomOut(coordinate);
			}
		}
		redraw();
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if(e.keyCode == SWT.ARROW_DOWN) {
			if(e.stateMask == SWT.CTRL) {
				getAxisSet().zoomOut();
			} else {
				for(IAxis axis : getAxes(SWT.VERTICAL)) {
					axis.scrollDown();
				}
			}
			redraw();
		} else if(e.keyCode == SWT.ARROW_UP) {
			if(e.stateMask == SWT.CTRL) {
				getAxisSet().zoomIn();
			} else {
				for(IAxis axis : getAxes(SWT.VERTICAL)) {
					axis.scrollUp();
				}
			}
			redraw();
		} else if(e.keyCode == SWT.ARROW_LEFT) {
			for(IAxis axis : getAxes(SWT.HORIZONTAL)) {
				axis.scrollDown();
			}
			redraw();
		} else if(e.keyCode == SWT.ARROW_RIGHT) {
			for(IAxis axis : getAxes(SWT.HORIZONTAL)) {
				axis.scrollUp();
			}
			redraw();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	public void widgetSelected(Event e) {

		if(!(e.widget instanceof MenuItem)) {
			return;
		}
		MenuItem menuItem = (MenuItem)e.widget;
		if(menuItem.getText().equals(ADJUST_AXIS_RANGE)) {
			adjustRange();
		} else if(menuItem.getText().equals(ADJUST_X_AXIS_RANGE)) {
			adjustXRange();
		} else if(menuItem.getText().equals(ADJUST_Y_AXIS_RANGE)) {
			adjustYRange();
		} else if(menuItem.getText().equals(ADJUST_PREVIOUS_AXIS_RANGE)) {
			adjustPreviousRange();
		} else if(menuItem.getText().equals(LEGEND_SHOW)) {
			getLegend().setVisible(true);
		} else if(menuItem.getText().equals(LEGEND_HIDE)) {
			getLegend().setVisible(false);
		} else if(menuItem.getText().equals(GRAPH_SETTINGS)) {
			openSettingsDialog();
		}
		redraw();
	}

	@Override
	public void adjustRange() {

		getAxisSet().adjustRange();
	}

	@Override
	public void adjustXRange() {

		for(IAxis axis : getAxisSet().getXAxes()) {
			axis.adjustRange();
		}
	}

	@Override
	public void adjustYRange() {

		for(IAxis axis : getAxisSet().getYAxes()) {
			axis.adjustRange();
		}
	}

	@Override
	public void adjustPreviousRange() {

		getAxisSet().adjustRange();
	}

	/**
	 * Opens the properties dialog.
	 */
	private void openSettingsDialog() {

		/*
		 * Create the preferences dialog.
		 */
		PreferenceManager manager = new PreferenceManager();
		PreferenceNode chartNode = new PreferenceNode(GRAPH);
		//
		chartNode.setPage(new ChartPage(this, resources, GRAPH));
		manager.addToRoot(chartNode);
		//
		PreferenceNode legendNode = new PreferenceNode(LEGEND);
		legendNode.setPage(new LegendPage(this, resources, LEGEND));
		manager.addTo(GRAPH, legendNode);
		//
		PreferenceNode xAxisNode = new PreferenceNode(X_AXIS);
		xAxisNode.setPage(new AxisPage(this, resources, Direction.X, X_AXIS));
		manager.addTo(GRAPH, xAxisNode);
		//
		PreferenceNode xGridNode = new PreferenceNode(GRID);
		xGridNode.setPage(new GridPage(this, resources, Direction.X, GRID));
		manager.addTo(GRAPH + "." + X_AXIS, xGridNode);
		//
		PreferenceNode xTickNode = new PreferenceNode(TICK);
		xTickNode.setPage(new AxisTickPage(this, resources, Direction.X, TICK));
		manager.addTo(GRAPH + "." + X_AXIS, xTickNode);
		//
		PreferenceNode yAxisNode = new PreferenceNode(Y_AXIS);
		yAxisNode.setPage(new AxisPage(this, resources, Direction.Y, Y_AXIS));
		manager.addTo(GRAPH, yAxisNode);
		//
		PreferenceNode yGridNode = new PreferenceNode(GRID);
		yGridNode.setPage(new GridPage(this, resources, Direction.Y, GRID));
		manager.addTo(GRAPH + "." + Y_AXIS, yGridNode);
		//
		PreferenceNode yTickNode = new PreferenceNode(TICK);
		yTickNode.setPage(new AxisTickPage(this, resources, Direction.Y, TICK));
		manager.addTo(GRAPH + "." + Y_AXIS, yTickNode);
		//
		PreferenceNode plotNode = new PreferenceNode(SERIES);
		plotNode.setPage(new SeriesPage(this, resources, SERIES));
		manager.addTo(GRAPH, plotNode);
		//
		PreferenceNode labelNode = new PreferenceNode(LABEL);
		labelNode.setPage(new SeriesLabelPage(this, resources, LABEL));
		manager.addTo(GRAPH + "." + SERIES, labelNode);
		/*
		 * Create and show the dialog.
		 */
		PreferenceDialog preferenceDialog = new PreferenceDialog(getShell(), manager);
		preferenceDialog.create();
		preferenceDialog.getShell().setText(GRAPH_SETTINGS);
		preferenceDialog.getTreeViewer().expandAll();
		preferenceDialog.open();
	}

	private IAxis[] getAxes(int orientation) {

		IAxis[] axes;
		if(getOrientation() == orientation) {
			axes = getAxisSet().getXAxes();
		} else {
			axes = getAxisSet().getYAxes();
		}
		return axes;
	}

	private void setRange(Point range, IAxis axis) {

		if(range == null) {
			return;
		}
		double min = axis.getDataCoordinate(range.x);
		double max = axis.getDataCoordinate(range.y);
		axis.setRange(new Range(min, max));
	}

	private void init() {

		Composite plotArea = getPlotArea();
		plotArea.addPaintListener(this);
		plotArea.addKeyListener(this);
		plotArea.addMouseListener(this);
		plotArea.addMouseMoveListener(this);
		plotArea.addMouseWheelListener(this);
		//
		createMenuItems();
	}

	private void createMenuItems() {

		Menu menu = new Menu(getPlotArea());
		getPlotArea().setMenu(menu);
		/*
		 * Adjust Ranges
		 */
		MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
		menuItem.setText(ADJUST_AXIS_RANGE_GROUP);
		Menu adjustAxisRangeMenu = new Menu(menuItem);
		menuItem.setMenu(adjustAxisRangeMenu);
		//
		menuItem = new MenuItem(adjustAxisRangeMenu, SWT.PUSH);
		menuItem.setText(ADJUST_AXIS_RANGE);
		menuItem.addListener(SWT.Selection, this);
		//
		menuItem = new MenuItem(adjustAxisRangeMenu, SWT.PUSH);
		menuItem.setText(ADJUST_X_AXIS_RANGE);
		menuItem.addListener(SWT.Selection, this);
		//
		menuItem = new MenuItem(adjustAxisRangeMenu, SWT.PUSH);
		menuItem.setText(ADJUST_Y_AXIS_RANGE);
		menuItem.addListener(SWT.Selection, this);
		//
		menuItem = new MenuItem(adjustAxisRangeMenu, SWT.PUSH);
		menuItem.setText(ADJUST_PREVIOUS_AXIS_RANGE);
		menuItem.addListener(SWT.Selection, this);
		/*
		 * Separator
		 */
		menuItem = new MenuItem(menu, SWT.SEPARATOR);
		/*
		 * Legend
		 */
		menuItem = new MenuItem(menu, SWT.CASCADE);
		menuItem.setText(LEGEND);
		Menu legendMenu = new Menu(menuItem);
		menuItem.setMenu(legendMenu);
		//
		menuItem = new MenuItem(legendMenu, SWT.PUSH);
		menuItem.setText(LEGEND_SHOW);
		menuItem.addListener(SWT.Selection, this);
		//
		menuItem = new MenuItem(legendMenu, SWT.PUSH);
		menuItem.setText(LEGEND_HIDE);
		menuItem.addListener(SWT.Selection, this);
		/*
		 * Separator
		 */
		menuItem = new MenuItem(menu, SWT.SEPARATOR);
		/*
		 * Graph
		 */
		menuItem = new MenuItem(menu, SWT.CASCADE);
		menuItem.setText(GRAPH);
		Menu graphMenu = new Menu(menuItem);
		menuItem.setMenu(graphMenu);
		//
		menuItem = new MenuItem(graphMenu, SWT.PUSH);
		menuItem.setText(GRAPH_SETTINGS);
		menuItem.addListener(SWT.Selection, this);
	}
}
