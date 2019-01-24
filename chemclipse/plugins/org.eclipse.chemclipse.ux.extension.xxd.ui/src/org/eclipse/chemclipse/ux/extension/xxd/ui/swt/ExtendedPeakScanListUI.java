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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.IColumnMoveListener;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.ListSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageLists;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swtchart.extensions.core.BaseChart;

@SuppressWarnings("rawtypes")
public class ExtendedPeakScanListUI implements ConfigurableUI<PeakScanListUIConfig> {

	private static final Logger logger = Logger.getLogger(ExtendedPeakScanListUI.class);
	//
	private static final String MENU_CATEGORY = "Peaks/Scans";
	//
	private Composite toolbarInfoTop;
	private Composite toolbarInfoBottom;
	private Composite toolbarSearch;
	private Button buttonSave;
	private Label labelChromatogramName;
	private Label labelChromatogramInfo;
	private SearchSupportUI searchSupportUI;
	private PeakScanListUI peakScanListUI;
	private IChromatogramSelection chromatogramSelection;
	//
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();
	private ListSupport listSupport = new ListSupport();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private TargetExtendedComparator comparator = new TargetExtendedComparator(SortOrder.DESC);
	//
	private Map<String, Object> map = new HashMap<String, Object>();
	private Composite toolbarMain;
	private Composite toolbarLabel;
	private boolean showScans;
	private boolean showPeaks;
	private boolean showSelectedRange;

	@Inject
	public ExtendedPeakScanListUI(Composite parent, IPreferenceStore store) {
		initialize(parent);
		if(store != null) {
			showPeaks = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_PEAKS_IN_LIST);
			showSelectedRange = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_PEAKS_IN_SELECTED_RANGE) || preferenceStore.getBoolean(PreferenceConstants.P_SHOW_SCANS_IN_SELECTED_RANGE);
			showScans = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_SCANS_IN_LIST);
		}
	}

	@Focus
	public void setFocus() {

		updateChromatogramSelection();
	}

	public void updateChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
		updateChromatogramSelection();
	}

	public void updateChromatogramSelection() {

		updateLabel();
		buttonSave.setEnabled(false);
		//
		if(chromatogramSelection == null) {
			peakScanListUI.clear();
		} else {
			peakScanListUI.setInput(chromatogramSelection, showPeaks, showScans, showSelectedRange);
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD) {
				buttonSave.setEnabled(true);
			}
		}
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfoTop = createToolbarInfoTop(parent);
		toolbarSearch = createToolbarSearch(parent);
		peakScanListUI = createPeakTable(parent);
		toolbarInfoBottom = createToolbarInfoBottom(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfoTop, true);
		PartSupport.setCompositeVisibility(toolbarSearch, false);
		PartSupport.setCompositeVisibility(toolbarInfoBottom, true);
		//
		peakScanListUI.setEditEnabled(false);
	}

	private void createToolbarMain(Composite parent) {

		toolbarMain = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		toolbarMain.setLayoutData(gridData);
		toolbarMain.setLayout(new GridLayout(6, false));
		//
		createButtonToggleToolbarInfo(toolbarMain);
		createButtonToggleToolbarSearch(toolbarMain);
		createButtonToggleEditModus(toolbarMain);
		createResetButton(toolbarMain);
		buttonSave = createSaveButton(toolbarMain);
		createSettingsButton(toolbarMain);
	}

	private Composite createToolbarInfoTop(Composite parent) {

		toolbarLabel = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		toolbarLabel.setLayoutData(gridData);
		toolbarLabel.setLayout(new GridLayout(1, false));
		//
		labelChromatogramName = new Label(toolbarLabel, SWT.NONE);
		labelChromatogramName.setText("");
		labelChromatogramName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return toolbarLabel;
	}

	private Composite createToolbarSearch(Composite parent) {

		searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				peakScanListUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		return searchSupportUI;
	}

	private PeakScanListUI createPeakTable(Composite parent) {

		PeakScanListUI listUI = new PeakScanListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = listUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				propagateSelection();
			}
		});
		/*
		 * Set/Save the column order.
		 */
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String preferenceName = PreferenceConstants.P_COLUMN_ORDER_PEAK_SCAN_LIST;
		listSupport.setColumnOrder(table, preferenceStore.getString(preferenceName));
		listUI.addColumnMoveListener(new IColumnMoveListener() {

			@Override
			public void handle() {

				String columnOrder = listSupport.getColumnOrder(table);
				preferenceStore.setValue(preferenceName, columnOrder);
			}
		});
		/*
		 * Add the delete targets support.
		 */
		Shell shell = listUI.getTable().getShell();
		ITableSettings tableSettings = listUI.getTableSettings();
		addDeleteMenuEntry(shell, tableSettings);
		addVerifyTargetsMenuEntry(tableSettings);
		addUnverifyTargetsMenuEntry(tableSettings);
		addKeyEventProcessors(shell, tableSettings);
		listUI.applySettings(tableSettings);
		//
		return listUI;
	}

	private void addDeleteMenuEntry(Shell shell, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Peak(s)/Scan Identification(s)";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deletePeaksOrIdentifications(shell);
			}
		});
	}

	private void addVerifyTargetsMenuEntry(ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Select Peak(s) for Analysis";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				setPeaksActiveForAnalysis(true);
			}
		});
	}

	private void addUnverifyTargetsMenuEntry(ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Deselect Peak(s) for Analysis";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				setPeaksActiveForAnalysis(false);
			}
		});
	}

	private void addKeyEventProcessors(Shell shell, ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					/*
					 * DEL
					 */
					deletePeaksOrIdentifications(shell);
				} else if(e.keyCode == BaseChart.KEY_CODE_i && (e.stateMask & SWT.CTRL) == SWT.CTRL) {
					if((e.stateMask & SWT.ALT) == SWT.ALT) {
						/*
						 * CTRL + ALT + I
						 */
						setPeaksActiveForAnalysis(false);
					} else {
						/*
						 * CTRL + I
						 */
						setPeaksActiveForAnalysis(true);
					}
				} else {
					propagateSelection();
				}
			}
		});
	}

	private void deletePeaksOrIdentifications(Shell shell) {

		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Delete Peak(s)/Scan Identification(s)");
		messageBox.setMessage("Would you like to delete the selected peak(s)/scan identification(s)?");
		if(messageBox.open() == SWT.YES) {
			Iterator iterator = peakScanListUI.getStructuredSelection().iterator();
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof IPeak) {
					deletePeak((IPeak)object);
				} else if(object instanceof IScan) {
					deleteScanIdentification((IScan)object);
				}
			}
			updateChromatogramSelection();
		}
	}

	private void deleteScanIdentification(IScan scan) {

		if(chromatogramSelection != null) {
			scan.removeAllTargets();
		}
	}

	private void setPeaksActiveForAnalysis(boolean activeForAnalysis) {

		Iterator iterator = peakScanListUI.getStructuredSelection().iterator();
		while(iterator.hasNext()) {
			Object object = iterator.next();
			if(object instanceof IPeak) {
				IPeak peak = (IPeak)object;
				peak.setActiveForAnalysis(activeForAnalysis);
			}
		}
		updateChromatogramSelection();
	}

	private void deletePeak(IPeak peak) {

		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD && peak instanceof IChromatogramPeakMSD) {
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				chromatogramMSD.removePeak((IChromatogramPeakMSD)peak);
			} else if(chromatogram instanceof IChromatogramCSD && peak instanceof IChromatogramPeakCSD) {
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
				chromatogramCSD.removePeak((IChromatogramPeakCSD)peak);
			} else if(chromatogram instanceof IChromatogramWSD) {
				IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
				chromatogramWSD.removePeak((IChromatogramPeakWSD)peak);
			}
		}
	}

	private void propagateSelection() {

		Table table = peakScanListUI.getTable();
		int index = table.getSelectionIndex();
		if(index >= 0) {
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IPeak) {
				/*
				 * Fire updates
				 */
				IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
				IPeak peak = (IPeak)object;
				IIdentificationTarget target = IIdentificationTarget.getBestIdentificationTarget(peak.getTargets(), comparator);
				boolean moveRetentionTimeOnPeakSelection = preferenceStore.getBoolean(PreferenceConstants.P_MOVE_RETENTION_TIME_ON_PEAK_SELECTION);
				if(moveRetentionTimeOnPeakSelection) {
					chromatogramDataSupport.adjustChromatogramSelection(peak, chromatogramSelection);
				}
				//
				DisplayUtils.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {

						chromatogramSelection.setSelectedPeak(peak);
						eventBroker.send(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, peak);
					}
				});
				//
				DisplayUtils.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {

						eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE, target);
					}
				});
				//
				if(peak instanceof IPeakMSD) {
					IPeakMSD peakMSD = (IPeakMSD)peak;
					DisplayUtils.getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {

							/*
							 * Send the mass spectrum update, e.g. used by the comparison part.
							 */
							map.clear();
							map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN, peakMSD.getExtractedMassSpectrum());
							map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_ENTRY, target);
							eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE, map);
						}
					});
				}
			} else if(object instanceof IScan) {
				/*
				 * Fire updates
				 */
				IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
				IScan scan = (IScan)object;
				IIdentificationTarget target = IIdentificationTarget.getBestIdentificationTarget(scan.getTargets(), comparator);
				//
				DisplayUtils.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {

						chromatogramSelection.setSelectedIdentifiedScan(scan);
						eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, scan);
					}
				});
				//
				DisplayUtils.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {

						eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE, target);
					}
				});
				//
				if(scan instanceof IScanMSD) {
					IScanMSD scanMSD = (IScanMSD)scan;
					DisplayUtils.getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {

							/*
							 * Send the identification target update to let e.g. the molecule renderer react on an update.
							 */
							map.clear();
							map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN, scanMSD);
							map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_ENTRY, target);
							eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE, map);
						}
					});
				}
			}
		}
	}

	private Composite createToolbarInfoBottom(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
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
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_ENTRY_DEFAULT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean editEnabled = !peakScanListUI.isEditEnabled();
				peakScanListUI.setEditEnabled(editEnabled);
				button.setImage(ApplicationImageFactory.getInstance().getImage((editEnabled) ? IApplicationImage.IMAGE_EDIT_ENTRY_ACTIVE : IApplicationImage.IMAGE_EDIT_ENTRY_DEFAULT, IApplicationImage.SIZE_16x16));
				updateLabel();
			}
		});
		//
		return button;
	}

	public void setEditEnabled(boolean editEnabled) {

		peakScanListUI.setEditEnabled(editEnabled);
		updateLabel();
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the peak/scan list.");
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
		button.setToolTipText("Save the peak/scan list.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					if(chromatogramSelection != null && chromatogramSelection.getChromatogram() != null) {
						/*
						 * Peaks
						 */
						IChromatogram chromatogram = chromatogramSelection.getChromatogram();
						Table table = peakScanListUI.getTable();
						int[] indices = table.getSelectionIndices();
						List<IPeak> peaks;
						if(indices.length == 0) {
							peaks = getPeakList(table);
						} else {
							peaks = getPeakList(table, indices);
						}
						//
						if(peaks.size() > 0) {
							DatabaseFileSupport.savePeaks(DisplayUtils.getShell(), peaks, chromatogram.getName());
						}
						/*
						 * Scans
						 */
						List<IScan> scans;
						if(indices.length == 0) {
							scans = getScanList(table);
						} else {
							scans = getScanList(table, indices);
						}
						//
						MassSpectra massSpectra = new MassSpectra();
						for(IScan scan : scans) {
							if(scan instanceof IScanMSD) {
								massSpectra.addMassSpectrum((IScanMSD)scan);
							}
						}
						//
						if(massSpectra.size() > 0) {
							DatabaseFileSupport.saveMassSpectra(DisplayUtils.getShell(), massSpectra, chromatogram.getName());
						}
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
				IPreferencePage preferencePageLists = new PreferencePageLists();
				preferencePageLists.setTitle("Lists");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageSWT));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageLists));
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

	private void updateLabel() {

		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() == null) {
			labelChromatogramName.setText(chromatogramDataSupport.getChromatogramLabel(null));
			labelChromatogramInfo.setText("");
		} else {
			String editInformation = peakScanListUI.isEditEnabled() ? "Edit is enabled." : "Edit is disabled.";
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String chromatogramLabel = chromatogramDataSupport.getChromatogramLabel(chromatogram);
			int identifiedPeaks = chromatogram.getNumberOfPeaks();
			int identifiedScans = chromatogramDataSupport.getIdentifiedScans(chromatogram).size();
			//
			labelChromatogramName.setText(chromatogramLabel + " - " + editInformation);
			labelChromatogramInfo.setText("Number of Peaks: " + identifiedPeaks + " | Scans: " + identifiedScans);
		}
	}

	private void applySettings() {

		searchSupportUI.reset();
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

	private List<IScan> getScanList(Table table) {

		List<IScan> scanList = new ArrayList<IScan>();
		for(TableItem tableItem : table.getItems()) {
			Object object = tableItem.getData();
			if(object instanceof IScan) {
				scanList.add((IScan)object);
			}
		}
		return scanList;
	}

	private List<IScan> getScanList(Table table, int[] indices) {

		List<IScan> scanList = new ArrayList<IScan>();
		for(int index : indices) {
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IScan) {
				scanList.add((IScan)object);
			}
		}
		return scanList;
	}

	@Override
	public PeakScanListUIConfig getConfig() {

		return new PeakScanListUIConfig() {

			@Override
			public void setToolbarVisible(boolean visible) {

				PartSupport.setCompositeVisibility(toolbarMain, visible);
			}

			@Override
			public void setToolbarInfoVisible(boolean visible) {

				PartSupport.setCompositeVisibility(toolbarLabel, visible);
			}

			@Override
			public boolean hasToolbarInfo() {

				return true;
			}

			@Override
			public void setShowScans(boolean showScans) {

				ExtendedPeakScanListUI.this.showScans = showScans;
			}

			@Override
			public void setShowPeaks(boolean showPeaks) {

				ExtendedPeakScanListUI.this.showPeaks = showPeaks;
			}

			@Override
			public void setShowSelectedRange(boolean showSelectedRange) {

				ExtendedPeakScanListUI.this.showSelectedRange = showSelectedRange;
			}

			@Override
			public void setVisibleColumns(Set<String> visibleColumns) {

				List<TableViewerColumn> columns = peakScanListUI.getTableViewerColumns();
				for(TableViewerColumn column : columns) {
					TableColumn tableColumn = column.getColumn();
					if(visibleColumns.contains(tableColumn.getText())) {
						Object oldWidth = tableColumn.getData("OLD_WITH");
						if(oldWidth instanceof Number) {
							tableColumn.setWidth(((Number)oldWidth).intValue());
						}
						Object oldResizable = tableColumn.getData("OLD_RESIZABLE");
						if(oldResizable instanceof Boolean) {
							tableColumn.setResizable(((Boolean)oldResizable).booleanValue());
						}
					} else {
						tableColumn.setData("OLD_WITH", tableColumn.getWidth());
						tableColumn.setData("OLD_RESIZABLE", tableColumn.getResizable());
						tableColumn.setWidth(0);
						tableColumn.setResizable(false);
					}
				}
			}
		};
	}
}
