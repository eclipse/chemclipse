/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePage;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
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
		createSettingsTask(parent);
		//
		initializeParts();
	}

	private void createOverviewTask(Composite parent) {

		String partId_1 = PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_HEADER;
		partMap.put(partId_1, PartSupport.PARTSTACK_OVERVIEW);
		//
		String partId_2 = PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_OVERVIEW;
		partMap.put(partId_2, PartSupport.PARTSTACK_OVERVIEW);
		//
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
				 * Show the part stack on demand. It's hidden by default.
				 */
				String partStackId = partMap.get(partId_1);
				PartSupport.setPartStackVisibility(partStackId, true);
				togglePartVisibility(button, partId_1, imageActive, imageDefault);
				togglePartVisibility(button, partId_2, imageActive, imageDefault);
			}
		});
	}

	private void createOverlayTask(Composite parent) {

		String partId_1 = PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_OVERLAY;
		partMap.put(partId_1, PartSupport.PARTSTACK_BOTTOM_LEFT);
		//
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

				togglePartVisibility(button, partId_1, imageActive, imageDefault);
			}
		});
	}

	private void createSelectedScansTask(Composite parent) {

		/*
		 * Default part stack position.
		 */
		String targetsPartId = PartSupport.PARTDESCRIPTOR_TARGETS;
		partMap.put(targetsPartId, PreferenceConstants.DEF_STACK_POSITION_TARGETS);
		//
		String scanChartPartId = PartSupport.PARTDESCRIPTOR_SCAN_CHART;
		partMap.put(scanChartPartId, PreferenceConstants.DEF_STACK_POSITION_SCAN_CHART);
		//
		String scanTablePartId = PartSupport.PARTDESCRIPTOR_SCAN_TABLE;
		partMap.put(scanTablePartId, PreferenceConstants.DEF_STACK_POSITION_SCAN_TABLE);
		//
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

				/*
				 * Selected part stack position.
				 */
				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				String targetsPartStackId = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_TARGETS);
				String scanChartPartStackId = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_TARGETS);
				String scanTablePartStackId = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_TARGETS);
				//
				togglePartVisibility(button, targetsPartId, targetsPartStackId, imageActive, imageDefault);
				togglePartVisibility(button, scanChartPartId, scanChartPartStackId, imageActive, imageDefault);
				togglePartVisibility(button, scanTablePartId, scanTablePartStackId, imageActive, imageDefault);
			}
		});
	}

	private void createSelectedPeaksTask(Composite parent) {

		String partId_1 = PartSupport.PARTDESCRIPTOR_PEAK_CHART;
		partMap.put(partId_1, PartSupport.PARTSTACK_BOTTOM_RIGHT);
		//
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

				togglePartVisibility(button, partId_1, imageActive, imageDefault);
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

	private void initializeParts() {

		/*
		 * It's important to set the initial visibility of the parts.
		 * Otherwise, PartSupport.togglePartVisibility won't work as expected.
		 * Key = PartID
		 * Value = PartStackID
		 */
		for(Map.Entry<String, String> part : partMap.entrySet()) {
			PartSupport.setPartVisibility(part.getKey(), part.getValue(), false);
		}
	}

	private void togglePartVisibility(Button button, String partId, Image imageActive, Image imageDefault) {

		String partStackId = partMap.get(partId);
		togglePartVisibility(button, partId, partStackId, imageActive, imageDefault);
	}

	private void togglePartVisibility(Button button, String partId, String partStackId, Image imageActive, Image imageDefault) {

		/*
		 * Only activate the part if it has been selected by the user.
		 */
		if(partStackId != null && !partStackId.equals(PartSupport.PARTSTACK_NONE)) {
			/*
			 * Initialize the part status if the user
			 * has chosen another than the initial position.
			 */
			String defaultPartStackId = partMap.get(partId);
			if(defaultPartStackId == null || !partStackId.equals(defaultPartStackId)) {
				PartSupport.setPartVisibility(partId, partStackId, false);
				partMap.put(partId, partStackId);
			}
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
