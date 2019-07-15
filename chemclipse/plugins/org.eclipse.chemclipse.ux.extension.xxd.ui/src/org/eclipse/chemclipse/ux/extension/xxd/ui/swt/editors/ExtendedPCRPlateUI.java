/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.pcr.model.core.IChannelSpecification;
import org.eclipse.chemclipse.pcr.model.core.IDetectionFormat;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.pcr.model.core.IPlateTableEntry;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.pcr.model.core.PlateTableEntry;
import org.eclipse.chemclipse.pcr.model.core.Position;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PlateListLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.PlateListUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class ExtendedPCRPlateUI {

	private Label labelDataInfo;
	private PlateListUI plateListUI;
	private Combo comboSubsets;
	private ComboViewer comboViewerChannels;
	//
	private IPlate plate;

	public ExtendedPCRPlateUI(Composite parent) {
		initialize(parent);
	}

	public void update(IPlate plate) {

		this.plate = plate;
		updateWidget();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		createPlateTable(parent);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		createDataInfoLabel(composite);
		comboSubsets = createComboSubsets(composite);
		comboViewerChannels = createComboChannelSpecifications(composite);
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
					plateListUI.refresh();
				}
			}
		});
		//
		return combo;
	}

	private ComboViewer createComboChannelSpecifications(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ListContentProvider());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IChannelSpecification) {
					IChannelSpecification channelSpecification = (IChannelSpecification)element;
					return channelSpecification.getName();
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a channel specification.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(plate != null) {
					Object object = comboViewer.getStructuredSelection().getFirstElement();
					if(object instanceof IChannelSpecification) {
						plate.setActiveChannel(combo.getSelectionIndex());
						plateListUI.refresh();
					}
				}
			}
		});
		//
		return comboViewer;
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

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new PreferencePageSWT();
				preferencePage.setTitle("Settings (SWT)");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

		reset();
	}

	private void reset() {

		updateWidget();
	}

	private void createPlateTable(Composite parent) {

		plateListUI = new PlateListUI(parent, SWT.BORDER);
		plateListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		Table table = plateListUI.getTable();
		//
		table.addListener(SWT.MouseDown, new Listener() {

			public void handleEvent(Event event) {

				/*
				 * Mouse click
				 */
				String topic;
				Object data;
				//
				IWell well = getSelectedCell(event);
				if(well != null) {
					topic = IChemClipseEvents.TOPIC_WELL_PCR_UPDATE_SELECTION;
					data = well;
				} else {
					topic = IChemClipseEvents.TOPIC_WELL_PCR_UNLOAD_SELECTION;
					data = null;
				}
				//
				event.widget.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {

						IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
						eventBroker.send(topic, data);
					}
				});
			}
		});
	}

	private IWell getSelectedCell(Event event) {

		Table table = plateListUI.getTable();
		Rectangle clientArea = table.getClientArea();
		Point point = new Point(event.x, event.y);
		int index = table.getTopIndex();
		while(index < table.getItemCount()) {
			boolean visible = false;
			TableItem item = table.getItem(index);
			for(int i = 1; i < PlateListLabelProvider.TITLES.length; i++) {
				Rectangle rectangle = item.getBounds(i);
				if(rectangle.contains(point)) {
					TableItem[] tableItems = table.getSelection();
					TableItem tableItem = tableItems[0];
					Object object = tableItem.getData();
					if(object instanceof PlateTableEntry) {
						PlateTableEntry plateTableEntry = (PlateTableEntry)object;
						return plateTableEntry.getWells().get(i);
					}
				}
				//
				if(!visible && rectangle.intersects(clientArea)) {
					visible = true;
				}
			}
			//
			if(!visible) {
				return null;
			}
			index++;
		}
		//
		return null;
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
			Set<IWell> wells = plate.getWells();
			List<IPlateTableEntry> plateTableEntries = new ArrayList<>();
			for(int i = 65; i <= 72; i++) {
				String row = Character.toString(((char)i));
				IPlateTableEntry plateTableEntry = new PlateTableEntry(row);
				for(IWell well : wells) {
					Position position = well.getPosition();
					if(position.getRow().equals(row)) {
						plateTableEntry.getWells().put(position.getColumn(), well);
					}
				}
				plateTableEntries.add(plateTableEntry);
			}
			plateListUI.setInput(plateTableEntries);
		} else {
			plateListUI.clear();
		}
	}

	private void updateSubsetCombo() {

		if(plate != null) {
			List<String> sampleSubsets = plate.getSampleSubsets();
			comboSubsets.setItems(sampleSubsets.toArray(new String[sampleSubsets.size()]));
		} else {
			comboSubsets.setItems(new String[]{""});
		}
	}

	private void updateChannelSpecifications() {

		if(plate != null) {
			IDetectionFormat detectionFormat = plate.getDetectionFormat();
			if(detectionFormat != null) {
				comboViewerChannels.setInput(detectionFormat.getChannelSpecifications());
			}
		} else {
			comboViewerChannels.setInput(null);
		}
	}
}
