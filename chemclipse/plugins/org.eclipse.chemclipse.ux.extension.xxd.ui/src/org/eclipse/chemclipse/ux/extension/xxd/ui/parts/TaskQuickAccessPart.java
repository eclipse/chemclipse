/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePage;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class TaskQuickAccessPart {

	private Map<String, String> partMap;

	@Inject
	public TaskQuickAccessPart(Composite parent) {
		partMap = new HashMap<String, String>();
		initialize(parent);
	}

	private void initialize(Composite parent) {

		/*
		 * Add buttons here to focus specialized views.
		 */
		parent.setLayout(new RowLayout());
		//
		createOverviewTask(parent);
		createOverlayTask(parent);
		createSelectedScansTask(parent);
		createSelectedPeaksTask(parent);
		createSubtractScanTask(parent);
		createCombinedScanTask(parent);
		createComparisonScanTask(parent);
		createSettingsTask(parent);
	}

	private void createOverviewTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_OVERVIEW_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_OVERVIEW_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the overview modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Show the part stack on demand. The default is hidden initially.
				 */
				String partStackId = PartSupport.PARTSTACK_LEFT_CENTER;
				PartSupport.setPartStackVisibility(partStackId, true);
				togglePartVisibility(button, PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_HEADER, partStackId, imageActive, imageDefault);
				togglePartVisibility(button, PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_OVERVIEW, partStackId, imageActive, imageDefault);
			}
		});
	}

	private void createOverlayTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_OVERLAY_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_OVERLAY_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the overlay modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				String partStackId = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_OVERLAY);
				togglePartVisibility(button, PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_OVERLAY, partStackId, imageActive, imageDefault);
			}
		});
	}

	private void createSelectedScansTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED_SCANS_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED_SCANS_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the selected scan(s) modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				String targetsPartStackId = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_TARGETS);
				String scanChartPartStackId = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SCAN_CHART);
				String scanTablePartStackId = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SCAN_TABLE);
				//
				togglePartVisibility(button, PartSupport.PARTDESCRIPTOR_TARGETS, targetsPartStackId, imageActive, imageDefault);
				togglePartVisibility(button, PartSupport.PARTDESCRIPTOR_SCAN_CHART, scanChartPartStackId, imageActive, imageDefault);
				togglePartVisibility(button, PartSupport.PARTDESCRIPTOR_SCAN_TABLE, scanTablePartStackId, imageActive, imageDefault);
			}
		});
	}

	private void createSelectedPeaksTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED_PEAKS_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED_PEAKS_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the selected peak(s) modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				String partStackIdChart = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_CHART);
				String partStackIdDetails = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_DETAILS);
				//
				togglePartVisibility(button, PartSupport.PARTDESCRIPTOR_PEAK_CHART, partStackIdChart, imageActive, imageDefault);
				togglePartVisibility(button, PartSupport.PARTDESCRIPTOR_PEAK_DETAILS, partStackIdDetails, imageActive, imageDefault);
			}
		});
	}

	private void createSubtractScanTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_SCAN_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_SCAN_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the subtract scan modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Show the part stack on demand. The default is hidden initially.
				 */
				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				String partStackId = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SUBTRACT_SCAN_PART);
				PartSupport.setPartStackVisibility(partStackId, true);
				togglePartVisibility(button, PartSupport.PARTDESCRIPTOR_SUBTRACT_SCAN, partStackId, imageActive, imageDefault);
			}
		});
	}

	private void createCombinedScanTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COMBINED_SCAN_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COMBINED_SCAN_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the combined scan modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				String partStackId = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_COMBINED_SCAN_PART);
				togglePartVisibility(button, PartSupport.PARTDESCRIPTOR_COMBINED_SCAN, partStackId, imageActive, imageDefault);
			}
		});
	}

	private void createComparisonScanTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COMPARISON_SCAN_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COMPARISON_SCAN_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the comparison scan modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Show the part stack on demand. The default is hidden initially.
				 */
				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				String partStackId = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_COMPARISON_SCAN_PART);
				PartSupport.setPartStackVisibility(partStackId, true);
				togglePartVisibility(button, PartSupport.PARTDESCRIPTOR_COMPARISON_SCAN, partStackId, imageActive, imageDefault);
			}
		});
	}

	private void createSettingsTask(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new PreferencePage();
				preferencePage.setTitle("UI Settings");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getDefault().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				preferenceDialog.open();
			}
		});
	}

	private void togglePartVisibility(Button button, String partId, String partStackId, Image imageActive, Image imageDefault) {

		if(PartSupport.PARTSTACK_NONE.equals(partStackId)) {
			/*
			 * Hide the part if it is visible.
			 */
			String currentPartStackId = partMap.get(partId);
			if(currentPartStackId != null) {
				PartSupport.setPartVisibility(partId, currentPartStackId, false);
			}
		} else {
			/*
			 * Initialize the part status if the user
			 * has chosen another than the initial position.
			 */
			String currentPartStackId = partMap.get(partId);
			if(currentPartStackId == null) {
				/*
				 * Initialize the part.
				 */
				PartSupport.setPartVisibility(partId, partStackId, false);
			} else {
				/*
				 * Move the part to another part stack.
				 */
				if(!partStackId.equals(currentPartStackId)) {
					MPart part = PartSupport.getPart(partId, currentPartStackId);
					MPartStack defaultPartStack = PartSupport.getPartStack(currentPartStackId);
					MPartStack partStack = PartSupport.getPartStack(partStackId);
					defaultPartStack.getChildren().remove(part);
					partStack.getChildren().add(part);
				}
			}
			partMap.put(partId, partStackId);
			/*
			 * Toggle visibility.
			 */
			MPart part = PartSupport.getPart(partId, partStackId);
			if(PartSupport.togglePartVisibility(part, partStackId)) {
				button.setImage(imageActive);
			} else {
				button.setImage(imageDefault);
			}
		}
	}
}
