/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramSupport;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class ExtendedPeakListUI {

	private static final Logger logger = Logger.getLogger(ExtendedPeakListUI.class);
	//
	private Composite toolbarInfoTop;
	private Composite toolbarInfoBottom;
	private Composite toolbarSearch;
	private Button buttonSavePeaks;
	private Label labelChromatogramName;
	private Label labelChromatogramInfo;
	private PeakListUI peakListUI;
	private IChromatogramSelection chromatogramSelection;
	//
	private Display display = Display.getDefault();
	private Shell shell = display.getActiveShell();

	@Inject
	public ExtendedPeakListUI(Composite parent) {
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateChromatogramSelection();
	}

	public void updateChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
		updateChromatogramSelection();
	}

	private void updateChromatogramSelection() {

		updateLabel();
		buttonSavePeaks.setEnabled(false);
		//
		if(chromatogramSelection == null) {
			peakListUI.clear();
		} else {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD) {
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				peakListUI.setInput(chromatogramMSD.getPeaks());
				buttonSavePeaks.setEnabled(true);
			} else if(chromatogram instanceof IChromatogramCSD) {
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
				peakListUI.setInput(chromatogramCSD.getPeaks());
			} else if(chromatogram instanceof IChromatogramWSD) {
				peakListUI.clear();
				// IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
				// peakListUI.setInput(chromatogramWSD.getPeaks());
			}
		}
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfoTop = createToolbarInfoTop(parent);
		toolbarSearch = createToolbarSearch(parent);
		peakListUI = createPeakTable(parent);
		toolbarInfoBottom = createToolbarInfoBottom(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfoTop, true);
		PartSupport.setCompositeVisibility(toolbarSearch, false);
		PartSupport.setCompositeVisibility(toolbarInfoBottom, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(6, false));
		//
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarSearch(composite);
		createButtonToggleEditModus(composite);
		createResetButton(composite);
		buttonSavePeaks = createSaveButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarInfoTop(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		composite.setVisible(false);
		//
		labelChromatogramName = new Label(composite, SWT.NONE);
		labelChromatogramName.setText("");
		labelChromatogramName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				peakListUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		return searchSupportUI;
	}

	private PeakListUI createPeakTable(Composite parent) {

		PeakListUI peakListUI = new PeakListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = peakListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = peakListUI.getStructuredSelection().getFirstElement();
				if(object instanceof IPeak) {
					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					eventBroker.send(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, (IPeak)object);
				}
			}
		});
		/*
		 * Add the delete targets support.
		 */
		// ITableSettings tableSettings = targetsListUI.getTableSettings();
		// targetsListUI.applySettings(tableSettings);
		//
		return peakListUI;
	}

	private Composite createToolbarInfoBottom(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		composite.setVisible(false);
		//
		labelChromatogramInfo = new Label(composite, SWT.NONE);
		labelChromatogramInfo.setText("");
		labelChromatogramInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfoTop);
				PartSupport.toggleCompositeVisibility(toolbarInfoBottom);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleToolbarSearch(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle search toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarSearch);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleEditModus(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Enable/disable to edit the table.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_ENTRY, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean editEnabled = !peakListUI.isEditEnabled();
				peakListUI.setEditEnabled(editEnabled);
				updateLabel();
			}
		});
		//
		return button;
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the peak list.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset();
			}
		});
	}

	private Button createSaveButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Save the peak list.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					if(chromatogramSelection != null && chromatogramSelection.getChromatogram() != null) {
						IChromatogram chromatogram = chromatogramSelection.getChromatogram();
						Table table = peakListUI.getTable();
						int[] indices = table.getSelectionIndices();
						List<IPeak> peaks;
						if(indices.length == 0) {
							peaks = getPeakList(table);
						} else {
							peaks = getPeakList(table, indices);
						}
						DatabaseFileSupport.savePeaks(shell, peaks, chromatogram.getName());
					}
				} catch(NoConverterAvailableException e1) {
					logger.warn(e1);
				}
			}
		});
		return button;
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePageSWT = new PreferencePageSWT();
				preferencePageSWT.setTitle("Settings (SWT)");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageSWT));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(shell, preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(shell, "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void updateLabel() {

		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() == null) {
			labelChromatogramName.setText(ChromatogramSupport.getChromatogramLabel(null));
			labelChromatogramInfo.setText("");
		} else {
			String editInformation = peakListUI.isEditEnabled() ? "Edit is enabled." : "Edit is disabled.";
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String chromatogramLabel = ChromatogramSupport.getChromatogramLabel(chromatogram);
			labelChromatogramName.setText(chromatogramLabel + " - " + editInformation);
			labelChromatogramInfo.setText("Number of Peaks: " + chromatogram.getNumberOfPeaks());
		}
	}

	private void applySettings() {

		updateChromatogramSelection();
	}

	private void reset() {

		updateChromatogramSelection();
	}

	private List<IPeak> getPeakList(Table table) {

		List<IPeak> peakList = new ArrayList<IPeak>();
		for(TableItem tableItem : table.getItems()) {
			Object object = tableItem.getData();
			if(object instanceof IPeak) {
				peakList.add((IPeak)object);
			}
		}
		return peakList;
	}

	private List<IPeak> getPeakList(Table table, int[] indices) {

		List<IPeak> peakList = new ArrayList<IPeak>();
		for(int index : indices) {
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IPeak) {
				peakList.add((IPeak)object);
			}
		}
		return peakList;
	}
}
