/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ScrollableChart;

public interface IExtendedPartUI {

	Logger logger = Logger.getLogger(IExtendedPartUI.class);
	//
	String PREFIX_SHOW = "Show";
	String PREFIX_HIDE = "Hide";
	String PREFIX_ENABLE = "Enable";
	String PREFIX_DISABLE = "Disable";
	//
	String TITLE_SETTINGS = "Settings";
	//
	String TOOLTIP_TABLE = "the table content edit modus.";
	String TOOLTIP_INFO = "additional information.";
	String TOOLTIP_RESULTS = "results information.";
	String TOOLTIP_EDIT = "the edit toolbar.";
	String TOOLTIP_SEARCH = "the search toolbar.";
	String TOOLTIP_TYPES = "the types toolbar.";
	String TOOLTIP_LEGEND = "the chart legend.";
	String TOOLTIP_LEGEND_MARKER = "the chart legend marker.";
	String TOOLTIP_CHART_GRID = "the chart grid.";
	//
	String IMAGE_INFO = IApplicationImage.IMAGE_INFO;
	String IMAGE_RESULTS = IApplicationImage.IMAGE_RESULTS;
	String IMAGE_EDIT = IApplicationImage.IMAGE_EDIT;
	String IMAGE_SEARCH = IApplicationImage.IMAGE_SEARCH;
	String IMAGE_TYPES = IApplicationImage.IMAGE_TYPES;
	String IMAGE_LEGEND = IApplicationImage.IMAGE_TAG;
	String IMAGE_LEGEND_MARKER = IApplicationImage.IMAGE_CHART_LEGEND_MARKER;
	String IMAGE_EDIT_ENTRY = IApplicationImage.IMAGE_EDIT_ENTRY;
	String IMAGE_CHART_GRID = IApplicationImage.IMAGE_GRID;

	default Button createButton(Composite parent, String text, String tooltip, String image) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(tooltip);
		button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImageProvider.SIZE_16x16));
		//
		return button;
	}

	default Button createButtonToggleToolbar(Composite parent, AtomicReference<? extends Composite> toolbar, String image, String tooltip) {

		return createButtonToggleToolbar(parent, Arrays.asList(toolbar), image, tooltip);
	}

	default Button createButtonToggleToolbar(Composite parent, List<AtomicReference<? extends Composite>> toolbars, String image, String tooltip) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		setButtonImage(button, image, PREFIX_SHOW, PREFIX_HIDE, tooltip, false);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				for(AtomicReference<? extends Composite> toolbar : toolbars) {
					Composite composite = toolbar.get();
					if(composite != null) {
						boolean active = PartSupport.toggleCompositeVisibility(composite);
						setButtonImage(button, image, PREFIX_SHOW, PREFIX_HIDE, tooltip, active);
					}
				}
			}
		});
		//
		return button;
	}

	default Button createButtonToggleEditTable(Composite parent, AtomicReference<? extends ExtendedTableViewer> viewer, String image) {

		return createButtonToggleEditTable(parent, Arrays.asList(viewer), image);
	}

	default Button createButtonToggleEditTable(Composite parent, List<AtomicReference<? extends ExtendedTableViewer>> viewers, String image) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_TABLE, false);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Get the first status
				 */
				boolean edit = false;
				exitloop:
				for(AtomicReference<? extends ExtendedTableViewer> viewer : viewers) {
					ExtendedTableViewer tableViewer = viewer.get();
					if(tableViewer != null) {
						edit = !tableViewer.isEditEnabled();
						break exitloop;
					}
				}
				/*
				 * Apply the edit status and set the button image.
				 */
				for(AtomicReference<? extends ExtendedTableViewer> viewer : viewers) {
					ExtendedTableViewer tableViewer = viewer.get();
					tableViewer.setEditEnabled(edit);
				}
				//
				setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_TABLE, edit);
			}
		});
		//
		return button;
	}

	default Button createButtonToggleChartGrid(Composite parent, AtomicReference<? extends ScrollableChart> chartControl, String image, ChartGridSupport chartGridSupport) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_CHART_GRID, false);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				ScrollableChart scrollableChart = chartControl.get();
				if(scrollableChart != null) {
					IChartSettings chartSettings = scrollableChart.getChartSettings();
					boolean isGridDisplayed = !chartGridSupport.isGridDisplayed(chartSettings);
					chartGridSupport.showGrid(scrollableChart.getChartSettings(), isGridDisplayed);
					scrollableChart.applySettings(chartSettings);
					setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_CHART_GRID, isGridDisplayed);
				}
			}
		});
		//
		return button;
	}

	default Button createButtonToggleChartLegend(Composite parent, AtomicReference<? extends ScrollableChart> scrollableChart, String image) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_LEGEND, false);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				ScrollableChart chart = scrollableChart.get();
				if(chart != null) {
					boolean enabled = chart.toggleSeriesLegendVisibility();
					setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_LEGEND, enabled);
				}
			}
		});
		//
		return button;
	}

	default Button createButtonToggleLegendMarker(Composite parent, AtomicReference<? extends ScrollableChart> scrollableChart, String image) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_LEGEND_MARKER, false);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				ScrollableChart chart = scrollableChart.get();
				if(chart != null) {
					boolean enabled = chart.togglePositionLegendVisibility();
					setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_LEGEND_MARKER, enabled);
					chart.redraw();
				}
			}
		});
		//
		return button;
	}

	default Button createSettingsButton(Composite parent, List<Class<? extends IPreferencePage>> preferencePages, ISettingsHandler settingsHandler) {

		Button button = createSettingsButtonBasic(parent);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {

				showPreferencesDialog(event, preferencePages, settingsHandler);
			}
		});
		//
		return button;
	}

	default Button createSettingsButtonBasic(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Open the Settings");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		//
		return button;
	}

	default void showPreferencesDialog(SelectionEvent event, List<Class<? extends IPreferencePage>> preferencePages, ISettingsHandler settingsHandler) {

		if(!preferencePages.isEmpty()) {
			/*
			 * Collect the pages
			 */
			TreeMap<String, IPreferencePage> preferencePageMap = new TreeMap<>();
			for(int i = 0; i < preferencePages.size(); i++) {
				try {
					Class<? extends IPreferencePage> page = preferencePages.get(i);
					IPreferencePage preferencePage = page.getConstructor().newInstance();
					String title = preferencePage.getTitle();
					if(title == null || title.isEmpty() || title.isBlank()) {
						title = "--";
					}
					preferencePageMap.put(title, preferencePage);
				} catch(Exception exception) {
					logger.warn(exception);
				}
			}
			/*
			 * Add the pages sorted by title.
			 */
			PreferenceManager preferenceManager = new PreferenceManager();
			int i = 1;
			for(IPreferencePage preferencePage : preferencePageMap.values()) {
				preferenceManager.addToRoot(new PreferenceNode(Integer.toString(i++), preferencePage));
			}
			/*
			 * Open the dialog
			 */
			PreferenceDialog preferenceDialog = new PreferenceDialog(event.display.getActiveShell(), preferenceManager);
			preferenceDialog.create();
			preferenceDialog.setMessage(TITLE_SETTINGS);
			//
			if(preferenceDialog.open() == Window.OK) {
				try {
					if(settingsHandler != null) {
						settingsHandler.apply(event.display);
					}
				} catch(Exception exception) {
					MessageDialog.openError(event.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
				}
			}
		} else {
			MessageDialog.openInformation(event.display.getActiveShell(), TITLE_SETTINGS, "No setting pages have been defined.");
		}
	}

	default void enableToolbar(AtomicReference<? extends Composite> toolbar, boolean active) {

		Composite composite = toolbar.get();
		if(composite != null) {
			PartSupport.setCompositeVisibility(composite, active);
		}
	}

	default void enableToolbar(AtomicReference<? extends Composite> toolbar, Button button, String image, String tooltip, boolean active) {

		Composite composite = toolbar.get();
		if(composite != null) {
			PartSupport.setCompositeVisibility(composite, active);
			setButtonImage(button, image, PREFIX_SHOW, PREFIX_HIDE, tooltip, active);
		}
	}

	default void enableEdit(AtomicReference<? extends ExtendedTableViewer> viewer, Button button, String image, boolean edit) {

		enableEdit(Arrays.asList(viewer), button, image, edit);
	}

	default void enableEdit(List<AtomicReference<? extends ExtendedTableViewer>> viewers, Button button, String image, boolean edit) {

		for(AtomicReference<? extends ExtendedTableViewer> viewer : viewers) {
			ExtendedTableViewer tableViewer = viewer.get();
			if(tableViewer != null) {
				tableViewer.setEditEnabled(edit);
			}
		}
		//
		setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_TABLE, edit);
	}

	default void enableChartGrid(AtomicReference<? extends ScrollableChart> chartControl, Button button, String image, ChartGridSupport chartGridSupport) {

		ScrollableChart scrollableChart = chartControl.get();
		if(scrollableChart != null) {
			boolean isGridDisplayed = chartGridSupport.isGridDisplayed(scrollableChart.getChartSettings());
			setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_CHART_GRID, isGridDisplayed);
		}
	}

	default void enableButton(Button button, String image, String tooltip, boolean active) {

		setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, tooltip, active);
	}

	default void setButtonImage(Button button, String image, String prefixActivate, String prefixDeactivate, String tooltip, boolean enabled) {

		button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImageProvider.SIZE_16x16, enabled));
		StringBuilder builder = new StringBuilder();
		builder.append(enabled ? prefixDeactivate : prefixActivate);
		builder.append(" ");
		builder.append(tooltip);
		button.setToolTipText(builder.toString());
	}
}