/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePCR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.PCRPlate;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class ExtendedPCRPlateUI extends Composite implements IExtendedPartUI {

	private Label labelDataInfo;
	private PCRPlate pcrPlate;
	private Combo comboSubsets;
	private Combo comboChannels;
	//
	private IPlate plate;

	public ExtendedPCRPlateUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IPlate plate) {

		this.plate = plate;
		updateWidget();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createPlateUI(this);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		createDataInfoLabel(composite);
		comboSubsets = createComboSubsets(composite);
		comboChannels = createComboChannels(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private void createDataInfoLabel(Composite parent) {

		labelDataInfo = new Label(parent, SWT.NONE);
		labelDataInfo.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		labelDataInfo.setLayoutData(gridData);
	}

	private Combo createComboSubsets(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Selection of the subsets");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String activeSubset = combo.getText();
				if(plate != null) {
					plate.setActiveSubset(activeSubset);
					fireUpdate(e.widget.getDisplay(), pcrPlate.getSelectedWell());
					pcrPlate.refresh();
				}
			}
		});
		//
		return combo;
	}

	private Combo createComboChannels(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Select a channel specification.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(plate != null) {
					String selection = combo.getText();
					if(IPlate.ALL_CHANNELS.equals(selection)) {
						plate.setActiveChannel(-1);
					} else {
						plate.setActiveChannel(combo.getSelectionIndex() - 1);
					}
					fireUpdate(e.widget.getDisplay(), pcrPlate.getSelectedWell());
					pcrPlate.refresh();
				}
			}
		});
		//
		return combo;
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the scan");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePagePCR.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applySettings() {

		reset();
	}

	private void reset() {

		updateWidget();
	}

	private void createPlateUI(Composite parent) {

		pcrPlate = new PCRPlate(parent, SWT.BORDER);
		pcrPlate.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void fireUpdate(Display display, Object data) {

		if(display != null) {
			//
			String topic = (data instanceof IWell) ? IChemClipseEvents.TOPIC_WELL_PCR_UPDATE_SELECTION : IChemClipseEvents.TOPIC_WELL_PCR_UNLOAD_SELECTION;
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					IEventBroker eventBroker = Activator.getDefault().getEventBroker();
					eventBroker.send(topic, (data instanceof IWell) ? data : null);
				}
			});
		}
	}

	private void updateWidget() {

		updateInfo();
		updateWellPositions();
		updateSubsetCombo();
		updateChannelSpecifications();
	}

	private void updateInfo() {

		if(plate != null) {
			labelDataInfo.setText("Number of Wells: " + plate.getWells().size());
		} else {
			labelDataInfo.setText("");
		}
	}

	private void updateWellPositions() {

		if(plate != null) {
			pcrPlate.setPlate(plate);
		} else {
			pcrPlate.setPlate(null);
		}
	}

	private void updateSubsetCombo() {

		if(plate != null) {
			int selectionIndex = comboSubsets.getSelectionIndex();
			List<String> subsets = plate.getSampleSubsets();
			comboSubsets.setItems(subsets.toArray(new String[subsets.size()]));
			setComboSelection(subsets, comboSubsets, selectionIndex);
		} else {
			comboSubsets.setItems(new String[]{""});
		}
	}

	private void updateChannelSpecifications() {

		if(plate != null) {
			int selectionIndex = comboChannels.getSelectionIndex();
			List<String> channels = plate.getActiveChannels();
			channels.add(0, IPlate.ALL_CHANNELS);
			comboChannels.setItems(channels.toArray(new String[channels.size()]));
			setComboSelection(channels, comboChannels, selectionIndex);
		} else {
			comboChannels.setItems(new String[]{""});
		}
	}

	private void setComboSelection(List<String> items, Combo combo, int selectionIndex) {

		/*
		 * Set the last selection.
		 */
		if(items.size() > 0) {
			if(selectionIndex < 0) {
				combo.select(0);
			} else {
				if(selectionIndex < items.size()) {
					combo.select(selectionIndex);
				}
			}
		}
	}
}
