/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
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
	//
	String IMAGE_INFO = IApplicationImage.IMAGE_INFO;
	String IMAGE_RESULTS = IApplicationImage.IMAGE_RESULTS;
	String IMAGE_EDIT = IApplicationImage.IMAGE_EDIT;
	String IMAGE_SEARCH = IApplicationImage.IMAGE_SEARCH;
	String IMAGE_TYPES = IApplicationImage.IMAGE_TYPES;
	String IMAGE_LEGEND = IApplicationImage.IMAGE_TAG;
	String IMAGE_LEGEND_MARKER = IApplicationImage.IMAGE_CHART_LEGEND_MARKER;
	String IMAGE_EDIT_ENTRY = IApplicationImage.IMAGE_EDIT_ENTRY;

	default Button createButton(Composite parent, String text, String tooltip, String image) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete the selected header entrie(s).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16));
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

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_TABLE, false);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				ExtendedTableViewer tableViewer = viewer.get();
				if(tableViewer != null) {
					boolean edit = !tableViewer.isEditEnabled();
					tableViewer.setEditEnabled(edit);
					setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_TABLE, edit);
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

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Open the Settings");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(preferencePages.size() > 0) {
					/*
					 * Add the pages
					 */
					PreferenceManager preferenceManager = new PreferenceManager();
					for(int i = 0; i < preferencePages.size(); i++) {
						try {
							Class<? extends IPreferencePage> page = preferencePages.get(i);
							IPreferencePage preferencePage = page.getConstructor().newInstance();
							preferenceManager.addToRoot(new PreferenceNode(Integer.toString(i + 1), preferencePage));
						} catch(InstantiationException | IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException
								| NoSuchMethodException
								| SecurityException e1) {
							logger.warn(e1);
						}
					}
					//
					PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
					preferenceDialog.create();
					preferenceDialog.setMessage(TITLE_SETTINGS);
					//
					if(preferenceDialog.open() == Window.OK) {
						try {
							if(settingsHandler != null) {
								settingsHandler.apply(e.display);
							}
						} catch(Exception e1) {
							System.out.println(e1);
							MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
						}
					}
				} else {
					MessageDialog.openInformation(e.display.getActiveShell(), TITLE_SETTINGS, "No setting page(s) have been defined.");
				}
			}
		});
		//
		return button;
	}

	default void enableToolbar(AtomicReference<? extends Composite> toolbar, Button button, String image, String tooltip, boolean active) {

		Composite composite = toolbar.get();
		if(composite != null) {
			PartSupport.setCompositeVisibility(composite, active);
			setButtonImage(button, image, PREFIX_SHOW, PREFIX_HIDE, tooltip, active);
		}
	}

	default void enableEdit(AtomicReference<? extends ExtendedTableViewer> viewer, Button button, String image, boolean edit) {

		ExtendedTableViewer tableViewer = viewer.get();
		if(tableViewer != null) {
			tableViewer.setEditEnabled(edit);
			setButtonImage(button, image, PREFIX_ENABLE, PREFIX_DISABLE, TOOLTIP_TABLE, edit);
		}
	}

	default void setButtonImage(Button button, String image, String prefixActivate, String prefixDeactivate, String tooltip, boolean enabled) {

		button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16, enabled));
		StringBuilder builder = new StringBuilder();
		builder.append(enabled ? prefixDeactivate : prefixActivate);
		builder.append(" ");
		builder.append(tooltip);
		button.setToolTipText(builder.toString());
	}
}
