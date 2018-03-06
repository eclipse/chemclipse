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
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.parts.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.parts.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePage;
import org.eclipse.e4.ui.di.Focus;
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
import org.eclipse.swt.widgets.Shell;

public class TaskQuickAccessPart extends AbstractDataUpdateSupport implements IDataUpdateSupport {

	private Shell shell = Display.getDefault().getActiveShell();
	//
	private Map<String, Map<Boolean, Map<Button, Image>>> partButtonImageMap;
	private Map<String, Map<Boolean, Map<Button, Image>>> partStackButtonImageMap;

	@Inject
	public TaskQuickAccessPart(Composite parent, MPart part) {
		super(part);
		//
		partButtonImageMap = new HashMap<String, Map<Boolean, Map<Button, Image>>>();
		partStackButtonImageMap = new HashMap<String, Map<Boolean, Map<Button, Image>>>();
		//
		initialize(parent);
	}

	@Focus
	public void setFocus() {

	}

	@Override
	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_TOGGLE_PART_VISIBILITY_TRUE, IChemClipseEvents.PROPERTY_TOGGLE_VISIBILITY);
		registerEvent(IChemClipseEvents.TOPIC_TOGGLE_PART_VISIBILITY_FALSE, IChemClipseEvents.PROPERTY_TOGGLE_VISIBILITY);
		registerEvent(IChemClipseEvents.TOPIC_TOGGLE_PARTSTACK_VISIBILITY_TRUE, IChemClipseEvents.PROPERTY_TOGGLE_VISIBILITY);
		registerEvent(IChemClipseEvents.TOPIC_TOGGLE_PARTSTACK_VISIBILITY_FALSE, IChemClipseEvents.PROPERTY_TOGGLE_VISIBILITY);
	}

	@Override
	public void updateObjects(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(object instanceof String) {
				String id = (String)object;
				if(IChemClipseEvents.TOPIC_TOGGLE_PART_VISIBILITY_TRUE.equals(topic)) {
					setButtonImage(partButtonImageMap, id, true);
				} else if(IChemClipseEvents.TOPIC_TOGGLE_PART_VISIBILITY_FALSE.equals(topic)) {
					setButtonImage(partButtonImageMap, id, false);
				} else if(IChemClipseEvents.TOPIC_TOGGLE_PARTSTACK_VISIBILITY_FALSE.equals(topic)) {
					setButtonImage(partStackButtonImageMap, id, true);
				} else if(IChemClipseEvents.TOPIC_TOGGLE_PARTSTACK_VISIBILITY_FALSE.equals(topic)) {
					setButtonImage(partStackButtonImageMap, id, false);
				}
			}
		}
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
		createQuantitationTask(parent);
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
				addPartStackButtonMappings(partStackId, button, imageActive, imageDefault);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_HEADER, partStackId);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_OVERVIEW, partStackId);
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
				addPartStackButtonMappings(partStackId, button, imageActive, imageDefault);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_OVERLAY, partStackId);
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
				addPartButtonMappingsAndToggle(PartSupport.PARTDESCRIPTOR_TARGETS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_TARGETS), button, imageActive, imageDefault);
				addPartButtonMappingsAndToggle(PartSupport.PARTDESCRIPTOR_SCAN_CHART, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SCAN_CHART), button, imageActive, imageDefault);
				addPartButtonMappingsAndToggle(PartSupport.PARTDESCRIPTOR_SCAN_TABLE, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SCAN_TABLE), button, imageActive, imageDefault);
				addPartButtonMappingsAndToggle(PartSupport.PARTDESCRIPTOR_SCAN_LIST, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SCAN_LIST), button, imageActive, imageDefault);
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
				addPartButtonMappingsAndToggle(PartSupport.PARTDESCRIPTOR_PEAK_CHART, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_CHART), button, imageActive, imageDefault);
				addPartButtonMappingsAndToggle(PartSupport.PARTDESCRIPTOR_PEAK_DETAILS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_DETAILS), button, imageActive, imageDefault);
				addPartButtonMappingsAndToggle(PartSupport.PARTDESCRIPTOR_PEAK_DETECTOR, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_DETECTOR), button, imageActive, imageDefault);
				addPartButtonMappingsAndToggle(PartSupport.PARTDESCRIPTOR_PEAK_LIST, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_LIST), button, imageActive, imageDefault);
			}
		});
	}

	private void createQuantitationTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_QUANTITATION_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_QUANTITATION_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the quantitation modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				addPartButtonMappingsAndToggle(PartSupport.PARTDESCRIPTOR_QUANTITATION, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_QUANTITATION), button, imageActive, imageDefault);
				addPartButtonMappingsAndToggle(PartSupport.PARTDESCRIPTOR_INTERNAL_STANDARDS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_INTERNAL_STANDARDS), button, imageActive, imageDefault);
				addPartButtonMappingsAndToggle(PartSupport.PARTDESCRIPTOR_INTEGRATION_AREA, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_INTEGRATION_AREA), button, imageActive, imageDefault);
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

				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				addPartButtonMappingsAndToggle(PartSupport.PARTDESCRIPTOR_SUBTRACT_SCAN, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SUBTRACT_SCAN_PART), button, imageActive, imageDefault);
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
				addPartButtonMappingsAndToggle(PartSupport.PARTDESCRIPTOR_COMBINED_SCAN, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_COMBINED_SCAN_PART), button, imageActive, imageDefault);
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
				String partStackId = preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_COMPARISON_SCAN_CHART);
				addPartStackButtonMappings(partStackId, button, imageActive, imageDefault);
				PartSupport.setPartStackVisibility(partStackId, true);
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_COMPARISON_SCAN, partStackId);
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
				PreferenceDialog preferenceDialog = new PreferenceDialog(shell, preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				preferenceDialog.open();
			}
		});
	}

	private void addPartButtonMappingsAndToggle(String partId, String partStackId, Button button, Image imageActive, Image imageDefault) {

		addPartButtonMappings(partStackId, button, imageActive, imageDefault);
		PartSupport.togglePartVisibility(partId, partStackId);
	}

	private void addPartButtonMappings(String partStackId, Button button, Image imageActive, Image imageDefault) {

		addButtonMappings(partButtonImageMap, partStackId, button, imageActive, imageDefault);
	}

	private void addPartStackButtonMappings(String partStackId, Button button, Image imageActive, Image imageDefault) {

		addButtonMappings(partStackButtonImageMap, partStackId, button, imageActive, imageDefault);
	}

	private void addButtonMappings(Map<String, Map<Boolean, Map<Button, Image>>> buttonImageMap, String partStackId, Button button, Image imageActive, Image imageDefault) {

		HashMap<Boolean, Map<Button, Image>> imageMap = new HashMap<Boolean, Map<Button, Image>>();
		/*
		 * Active
		 */
		Map<Button, Image> activeMap = new HashMap<Button, Image>();
		activeMap.put(button, imageActive);
		imageMap.put(true, activeMap);
		/*
		 * Default
		 */
		Map<Button, Image> defaultMap = new HashMap<Button, Image>();
		defaultMap.put(button, imageDefault);
		imageMap.put(false, defaultMap);
		//
		buttonImageMap.put(partStackId, imageMap);
	}

	private void setButtonImage(Map<String, Map<Boolean, Map<Button, Image>>> buttonImageMap, String id, boolean visible) {

		Map<Boolean, Map<Button, Image>> imageMap = buttonImageMap.get(id);
		if(imageMap != null) {
			Map<Button, Image> buttonMap = imageMap.get(visible);
			if(buttonMap != null) {
				for(Button button : buttonMap.keySet()) {
					Image image = buttonMap.get(button);
					if(image != null) {
						button.setImage(image);
					}
				}
			}
		}
	}
}
