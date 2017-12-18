/*******************************************************************************
 * Copyright (c) 2017 pwenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * pwenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.support.FilterSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.ScanChartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSubtract;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ExtendedSubtractScanUI {

	private static final Logger logger = Logger.getLogger(ExtendedScanChartUI.class);
	//
	private TabFolder tabFolder;
	private static final int INDEX_CHART = 0;
	private static final int INDEX_TABLE = 1;
	//
	private IScan scan;
	private ScanChartUI scanChartUI;
	private ExtendedScanTableUI extendedScanTableUI;
	//
	private IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
	private List<EventHandler> registeredEventHandler;
	private IChromatogramSelectionMSD chromatogramSelectionMSD;

	@Inject
	public ExtendedSubtractScanUI(Composite parent, MPart part) {
		initialize(parent);
		registeredEventHandler = new ArrayList<EventHandler>();
		registerEventBroker(eventBroker);
	}

	@Focus
	public void setFocus() {

		updateScan();
	}

	public void update(IScan scan) {

		this.scan = scan;
		updateScan();
	}

	private void updateScan() {

		updateScanData();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		createScanTabFolderSection(parent);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(6, false));
		//
		createAddSelectedScanButton(composite);
		createAddCombinedScanButton(composite);
		createClearSessionButton(composite);
		createLoadSessionButton(composite);
		createStoreSessionButton(composite);
		createSettingsButton(composite);
	}

	private void createScanTabFolderSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, true));
		//
		tabFolder = new TabFolder(composite, SWT.BOTTOM);
		tabFolder.setBackground(Colors.WHITE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateScanData();
			}
		});
		//
		createScanChart(parent);
		createScanTable(parent);
	}

	private void createScanChart(Composite parent) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Chart");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		tabItem.setControl(composite);
		//
		scanChartUI = new ScanChartUI(composite, SWT.BORDER);
		scanChartUI.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createScanTable(Composite parent) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Table");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		composite.setLayout(new GridLayout(1, true));
		tabItem.setControl(composite);
		//
		extendedScanTableUI = new ExtendedScanTableUI(composite);
		extendedScanTableUI.enabledEdit(true);
	}

	private void createAddSelectedScanButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add selected scan to subtract spectrum.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_ADD_SELECTED_SCAN, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(chromatogramSelectionMSD != null && chromatogramSelectionMSD.getSelectedScan() != null) {
					/*
					 * Add the selected scan to the session MS.
					 */
					IScanMSD massSpectrum1 = PreferenceSupplier.getSessionSubtractMassSpectrum();
					IVendorMassSpectrum massSpectrum2 = chromatogramSelectionMSD.getSelectedScan();
					boolean useNormalize = org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNormalizedScan();
					IScanMSD normalizedMassSpectrum = FilterSupport.getCombinedMassSpectrum(massSpectrum1, massSpectrum2, null, useNormalize);
					PreferenceSupplier.setSessionSubtractMassSpectrum(normalizedMassSpectrum);
					eventBroker.send(IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, true);
				}
			}
		});
	}

	private void createAddCombinedScanButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add combined scan to subtract spectrum.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_ADD_COMBINED_SCAN, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(chromatogramSelectionMSD != null) {
					boolean useNormalize = org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNormalizedScan();
					IScanMSD massSpectrum1 = PreferenceSupplier.getSessionSubtractMassSpectrum();
					IScanMSD massSpectrum2 = FilterSupport.getCombinedMassSpectrum(chromatogramSelectionMSD, null, useNormalize);
					IScanMSD normalizedMassSpectrum = FilterSupport.getCombinedMassSpectrum(massSpectrum1, massSpectrum2, null, useNormalize);
					PreferenceSupplier.setSessionSubtractMassSpectrum(normalizedMassSpectrum);
					eventBroker.send(IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, true);
				}
			}
		});
	}

	private void createClearSessionButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Clear the session spectrum.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_CLEAR_SESSION_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceSupplier.setSessionSubtractMassSpectrum(null);
				eventBroker.send(IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, true);
			}
		});
	}

	private void createLoadSessionButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Load the session spectrum.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_LOAD_SESSION_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceSupplier.loadSessionSubtractMassSpectrum();
				eventBroker.send(IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, true);
			}
		});
	}

	private void createStoreSessionButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Store the session spectrum.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_STORE_SESSION_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceSupplier.storeSessionSubtractMassSpectrum();
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

				IPreferencePage preferencePageScans = new PreferencePageScans();
				preferencePageScans.setTitle("Scan Settings");
				IPreferencePage preferencePageSubtract = new PreferencePageSubtract();
				preferencePageSubtract.setTitle("Subtract Settings");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageScans));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageSubtract));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getDefault().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

		updateScan();
	}

	private void registerEventBroker(IEventBroker eventBroker) {

		if(eventBroker != null) {
			registeredEventHandler.add(registerEventHandler(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION, IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION));
		}
	}

	private EventHandler registerEventHandler(IEventBroker eventBroker, String property, String topic) {

		EventHandler eventHandler = new EventHandler() {

			public void handleEvent(Event event) {

				try {
					Object object = event.getProperty(property);
					if(object instanceof IChromatogramSelectionMSD) {
						chromatogramSelectionMSD = (IChromatogramSelectionMSD)object;
					} else {
						chromatogramSelectionMSD = null;
					}
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
	}

	private void updateScanData() {

		switch(tabFolder.getSelectionIndex()) {
			case INDEX_CHART:
				scanChartUI.setInput(scan);
				break;
			case INDEX_TABLE:
				extendedScanTableUI.update(scan);
				break;
		}
	}
}
