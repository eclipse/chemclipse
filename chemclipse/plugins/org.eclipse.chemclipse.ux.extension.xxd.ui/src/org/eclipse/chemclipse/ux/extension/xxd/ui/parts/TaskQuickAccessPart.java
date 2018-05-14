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

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateSupport;
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
	private IPreferenceStore preferenceStore;

	@Inject
	public TaskQuickAccessPart(Composite parent, MPart part) {
		super(part);
		preferenceStore = Activator.getDefault().getPreferenceStore();
		initialize(parent);
	}

	@Focus
	public void setFocus() {

	}

	@Override
	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_TOGGLE_PART_VISIBILITY_TRUE, IChemClipseEvents.PROPERTY_TOGGLE_PART_VISIBILITY);
		registerEvent(IChemClipseEvents.TOPIC_TOGGLE_PART_VISIBILITY_FALSE, IChemClipseEvents.PROPERTY_TOGGLE_PART_VISIBILITY);
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
					PartSupport.setButtonImage(id, true);
				} else if(IChemClipseEvents.TOPIC_TOGGLE_PART_VISIBILITY_FALSE.equals(topic)) {
					PartSupport.setButtonImage(id, false);
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
		createScansTask(parent);
		createPeaksTask(parent);
		createScanAndPeakListTask(parent);
		createQuantitationTask(parent);
		createSubtractScanTask(parent);
		createCombinedScanTask(parent);
		createComparisonScanTask(parent);
		createMeasurementResultTask(parent);
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

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_HEADER_DATA, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_HEADER_DATA));
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_OVERVIEW, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_CHROMATOGRAM_OVERVIEW));
			}
		});
		//
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_HEADER_DATA, button, imageActive, imageDefault);
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_CHROMATOGRAM_OVERVIEW, button, imageActive, imageDefault);
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

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_OVERLAY_CHROMATOGRAM, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_OVERLAY_CHROMATOGRAM));
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_OVERLAY_NMR, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_OVERLAY_NMR));
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_OVERLAY_XIR, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_OVERLAY_XIR));
			}
		});
		//
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_OVERLAY_CHROMATOGRAM, button, imageActive, imageDefault);
	}

	private void createScansTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED_SCANS_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED_SCANS_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the scan(s) modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_TARGETS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_TARGETS));
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_SCAN_CHART, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SCAN_CHART));
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_SCAN_TABLE, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SCAN_TABLE));
			}
		});
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_TARGETS, button, imageActive, imageDefault);
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_SCAN_CHART, button, imageActive, imageDefault);
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_SCAN_TABLE, button, imageActive, imageDefault);
	}

	private void createPeaksTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED_PEAKS_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED_PEAKS_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the peak(s) modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_PEAK_CHART, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_CHART));
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_PEAK_DETAILS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_DETAILS));
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_PEAK_DETECTOR, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_DETECTOR));
			}
		});
		//
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_PEAK_CHART, button, imageActive, imageDefault);
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_PEAK_DETAILS, button, imageActive, imageDefault);
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_PEAK_DETECTOR, button, imageActive, imageDefault);
	}

	private void createScanAndPeakListTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SCAN_PEAK_LIST_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SCAN_PEAK_LIST_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the scan/peak list modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_SCAN_LIST, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SCAN_LIST));
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_PEAK_LIST, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_PEAK_LIST));
			}
		});
		//
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_SCAN_LIST, button, imageActive, imageDefault);
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_PEAK_LIST, button, imageActive, imageDefault);
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

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_QUANTITATION, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_QUANTITATION));
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_INTERNAL_STANDARDS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_INTERNAL_STANDARDS));
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_INTEGRATION_AREA, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_INTEGRATION_AREA));
			}
		});
		//
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_QUANTITATION, button, imageActive, imageDefault);
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_INTERNAL_STANDARDS, button, imageActive, imageDefault);
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_INTEGRATION_AREA, button, imageActive, imageDefault);
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

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_SUBTRACT_SCAN, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_SUBTRACT_SCAN_PART));
			}
		});
		//
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_SUBTRACT_SCAN, button, imageActive, imageDefault);
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

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_COMBINED_SCAN, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_COMBINED_SCAN_PART));
			}
		});
		//
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_COMBINED_SCAN, button, imageActive, imageDefault);
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

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_COMPARISON_SCAN, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_COMPARISON_SCAN_CHART));
				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_MS_LIBRARY_STACK, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_MS_LIBRARY_STACK));
			}
		});
		//
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_COMPARISON_SCAN, button, imageActive, imageDefault);
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_MS_LIBRARY_STACK, button, imageActive, imageDefault);
	}

	private void createMeasurementResultTask(Composite parent) {

		Image imageActive = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MEASUREMENT_RESULTS_ACTIVE, IApplicationImage.SIZE_16x16);
		Image imageDefault = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MEASUREMENT_RESULTS_DEFAULT, IApplicationImage.SIZE_16x16);
		//
		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Toggle the measurement result modus");
		button.setImage(imageDefault);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.togglePartVisibility(PartSupport.PARTDESCRIPTOR_MEASUREMENT_RESULTS, preferenceStore.getString(PreferenceConstants.P_STACK_POSITION_MEASUREMENT_RESULTS));
			}
		});
		//
		PartSupport.addPartImageMappings(PartSupport.PARTDESCRIPTOR_MEASUREMENT_RESULTS, button, imageActive, imageDefault);
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
}
