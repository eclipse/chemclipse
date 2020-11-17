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
 * Christoph Läubrich - update chromatogram selection after delete, allow updating of selection
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.RetentionTimeRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.IColumnMoveListener;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSystem;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.dialogs.InternalStandardDialog;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.TableConfigSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.ListSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageLists;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.PeakScanListUIConfig.InteractionMode;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swtchart.extensions.core.IKeyboardSupport;

@SuppressWarnings("rawtypes")
public class ExtendedPeakScanListUI extends Composite implements IExtendedPartUI, ConfigurableUI<PeakScanListUIConfig> {

	private static final Logger logger = Logger.getLogger(ExtendedPeakScanListUI.class);
	//
	private static final String MENU_CATEGORY = "Peaks/Scans";
	//
	private final ListSupport listSupport = new ListSupport();
	private final TargetExtendedComparator comparator = new TargetExtendedComparator(SortOrder.DESC);
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private final IEventBroker eventBroker = Activator.getDefault().getEventBroker();
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
	private Composite toolbarMain;
	private Composite toolbarLabel;
	private boolean showScans;
	private boolean showPeaks;
	private boolean showScansInRange;
	private boolean showPeaksInRange;
	private boolean moveRetentionTimeOnPeakSelection;
	private InteractionMode interactionMode = InteractionMode.SOURCE;
	private int currentModCount;
	private RetentionTimeRange lastRange;

	public ExtendedPeakScanListUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	private void updateFromPreferences() {

		if(preferenceStore != null) {
			showPeaks = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_PEAKS_IN_LIST);
			showPeaksInRange = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_PEAKS_IN_SELECTED_RANGE);
			showScans = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_SCANS_IN_LIST);
			showScansInRange = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_SCANS_IN_SELECTED_RANGE);
			moveRetentionTimeOnPeakSelection = preferenceStore.getBoolean(PreferenceConstants.P_MOVE_RETENTION_TIME_ON_PEAK_SELECTION);
		}
	}

	public boolean setFocus() {

		updateChromatogramSelection();
		return true;
	}

	public void updateChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		if(hasChanged(chromatogramSelection)) {
			this.chromatogramSelection = chromatogramSelection;
			updateChromatogramSelection();
		}
	}

	private boolean hasChanged(IChromatogramSelection chromatogramSelection) {

		boolean referenceChanged = this.chromatogramSelection != chromatogramSelection;
		if(!referenceChanged && chromatogramSelection != null) {
			if(chromatogramSelection.getChromatogram().getModCount() != currentModCount) {
				return true;
			}
			if(lastRange != null && !lastRange.contentEquals(chromatogramSelection)) {
				return true;
			}
		}
		return referenceChanged;
	}

	public void updateChromatogramSelection() {

		updateFromPreferences();
		updateLabel();
		buttonSave.setEnabled(false);
		//
		if(chromatogramSelection == null) {
			peakScanListUI.clear();
			currentModCount = -1;
		} else {
			currentModCount = chromatogramSelection.getChromatogram().getModCount();
			lastRange = new RetentionTimeRange(chromatogramSelection);
			peakScanListUI.setInput(chromatogramSelection, showPeaks, showPeaksInRange, showScans, showScansInRange);
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD) {
				buttonSave.setEnabled(true);
			}
			if(interactionMode == InteractionMode.SINK || interactionMode == InteractionMode.BIDIRECTIONAL) {
				updateSelection();
			}
		}
	}

	public void updateSelection() {

		InteractionMode oldMode = interactionMode;
		try {
			interactionMode = InteractionMode.NONE;
			List<Object> selection = new ArrayList<>(2);
			if(chromatogramSelection != null) {
				if(showPeaks) {
					IPeak selectedPeak = chromatogramSelection.getSelectedPeak();
					if(selectedPeak != null) {
						selection.add(selectedPeak);
					}
				}
				if(showScans) {
					IScan selectedScan = chromatogramSelection.getSelectedIdentifiedScan();
					if(selectedScan != null) {
						selection.add(selectedScan);
					}
				}
			}
			peakScanListUI.setSelection(new StructuredSelection(selection), true);
		} finally {
			interactionMode = oldMode;
		}
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		toolbarInfoTop = createToolbarInfoTop(this);
		toolbarSearch = createToolbarSearch(this);
		peakScanListUI = createPeakTable(this);
		toolbarInfoBottom = createToolbarInfoBottom(this);
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

				propagateSelection(e.display);
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
		Display display = listUI.getTable().getDisplay();
		ITableSettings tableSettings = listUI.getTableSettings();
		//
		addDeleteMenuEntry(display, tableSettings);
		addVerifyTargetsMenuEntry(tableSettings);
		addUnverifyTargetsMenuEntry(tableSettings);
		modifyInternalStandardsMenuEntry(display, tableSettings);
		//
		addKeyEventProcessors(display, tableSettings);
		listUI.applySettings(tableSettings);
		//
		return listUI;
	}

	private void addDeleteMenuEntry(Display display, ITableSettings tableSettings) {

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

				deletePeaksOrIdentifications(display);
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

	private void modifyInternalStandardsMenuEntry(Display display, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Modify ISTDs (Internal Standards)";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				modifyInternalStandards(display);
			}
		});
	}

	private void addKeyEventProcessors(Display display, ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					/*
					 * DEL
					 */
					deletePeaksOrIdentifications(display);
				} else if(e.keyCode == IKeyboardSupport.KEY_CODE_LC_I && (e.stateMask & SWT.CTRL) == SWT.CTRL) {
					if((e.stateMask & SWT.ALT) == SWT.ALT) {
						/*
						 * CTRL + ALT + i
						 */
						setPeaksActiveForAnalysis(false);
					} else {
						/*
						 * CTRL + i
						 */
						setPeaksActiveForAnalysis(true);
					}
				} else if(e.keyCode == IKeyboardSupport.KEY_CODE_LC_S && (e.stateMask & SWT.CTRL) == SWT.CTRL) {
					/*
					 * CTRL + s
					 */
					modifyInternalStandards(display);
				} else {
					propagateSelection(display);
				}
			}
		});
	}

	private void deletePeaksOrIdentifications(Display display) {

		MessageBox messageBox = new MessageBox(display.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Delete Peak(s)/Scan Identification(s)");
		messageBox.setMessage("Would you like to delete the selected peak(s)/scan identification(s)?");
		if(messageBox.open() == SWT.YES) {
			/*
			 * Selected Items.
			 */
			Iterator iterator = peakScanListUI.getStructuredSelection().iterator();
			List<IScan> scansToClear = new ArrayList<>();
			List<IPeak> peaksToDelete = new ArrayList<>();
			/*
			 * Collect
			 */
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof IPeak) {
					peaksToDelete.add((IPeak)object);
				} else if(object instanceof IScan) {
					scansToClear.add((IScan)object);
				}
			}
			/*
			 * Clear scan(s) / peak(s)
			 */
			deleteScanIdentifications(scansToClear);
			deletePeaks(peaksToDelete);
			/*
			 * Send update.
			 */
			if(scansToClear.size() > 0 || peaksToDelete.size() > 0) {
				if(chromatogramSelection != null) {
					chromatogramSelection.update(true);
				}
			}
			//
			sendEvent(display, IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION, chromatogramSelection);
			updateChromatogramSelection();
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

	private void deleteScanIdentifications(List<IScan> scans) {

		if(scans.size() > 0) {
			/*
			 * Remove the selected identified scan.
			 */
			if(chromatogramSelection != null) {
				chromatogramSelection.removeSelectedIdentifiedScan();
			}
			/*
			 * Clear the targets.
			 */
			for(IScan scan : scans) {
				scan.getTargets().clear();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void deletePeaks(List<IPeak> peaks) {

		if(peaks.size() > 0) {
			if(chromatogramSelection != null) {
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				chromatogram.removePeaks(peaks);
			}
		}
	}

	private void modifyInternalStandards(Display display) {

		MessageBox messageBox = new MessageBox(display.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Internal Standard (ISTD)");
		messageBox.setMessage("Would you like to modify the ISTD(s)?");
		if(messageBox.open() == SWT.YES) {
			Iterator iterator = peakScanListUI.getStructuredSelection().iterator();
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof IPeak) {
					IPeak peak = (IPeak)object;
					if(peak.getIntegratedArea() > 0) {
						InternalStandardDialog dialog = new InternalStandardDialog(display.getActiveShell(), peak);
						if(IDialogConstants.OK_ID == dialog.open()) {
							logger.info("Successfully modified ISTDs.");
						}
					}
				}
			}
			/*
			 * Send update.
			 */
			sendEvent(display, IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION, chromatogramSelection);
		}
	}

	@SuppressWarnings("unchecked")
	private void propagateSelection(Display display) {

		if(interactionMode != InteractionMode.SOURCE && interactionMode != InteractionMode.BIDIRECTIONAL) {
			return;
		}
		IStructuredSelection selection = peakScanListUI.getStructuredSelection();
		if(!selection.isEmpty()) {
			List list = selection.toList();
			if(list.size() > 1) {
				// we can't select more than one item at once for now
				return;
			}
			for(Object object : list) {
				if(object instanceof IPeak) {
					/*
					 * Fire updates
					 */
					IPeak peak = (IPeak)object;
					IIdentificationTarget target = IIdentificationTarget.getBestIdentificationTarget(peak.getTargets(), comparator);
					if(moveRetentionTimeOnPeakSelection) {
						ChromatogramDataSupport.adjustChromatogramSelection(peak, chromatogramSelection);
					}
					//
					chromatogramSelection.setSelectedPeak(peak);
					sendEvent(display, IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, peak);
					sendEvent(display, IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE, target);
					//
					if(peak instanceof IPeakMSD) {
						/*
						 * Send the mass spectrum update, e.g. used by the comparison part.
						 */
						IPeakMSD peakMSD = (IPeakMSD)peak;
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN, peakMSD.getExtractedMassSpectrum());
						map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_ENTRY, target);
						sendEvent(display, IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE, map);
					}
				} else if(object instanceof IScan) {
					/*
					 * Fire updates
					 */
					IScan scan = (IScan)object;
					IIdentificationTarget target = IIdentificationTarget.getBestIdentificationTarget(scan.getTargets(), comparator);
					//
					chromatogramSelection.setSelectedIdentifiedScan(scan);
					sendEvent(display, IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, scan);
					sendEvent(display, IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE, target);
					//
					if(scan instanceof IScanMSD) {
						/*
						 * Send the identification target update to let e.g. the molecule renderer react on an update.
						 */
						IScanMSD scanMSD = (IScanMSD)scan;
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN, scanMSD);
						map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_ENTRY, target);
						sendEvent(display, IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE, map);
					}
				}
			}
		}
	}

	private void sendEvent(Display display, String topic, Object data) {

		if(display != null) {
			if(eventBroker != null) {
				display.asyncExec(new Runnable() {

					@Override
					public void run() {

						eventBroker.send(topic, data);
					}
				});
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
							DatabaseFileSupport.savePeaks(e.display.getActiveShell(), peaks, chromatogram.getName());
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
							DatabaseFileSupport.saveMassSpectra(e.display.getActiveShell(), massSpectra, chromatogram.getName());
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

		createSettingsButton(parent, Arrays.asList(PreferencePageSystem.class, PreferencePageLists.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void updateLabel() {

		if(labelChromatogramName.isDisposed() || labelChromatogramInfo.isDisposed()) {
			return;
		}
		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() == null) {
			labelChromatogramName.setText(ChromatogramDataSupport.getChromatogramLabel(null));
			labelChromatogramInfo.setText("");
		} else {
			String editInformation = peakScanListUI.isEditEnabled() ? "Edit is enabled." : "Edit is disabled.";
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String chromatogramLabel = ChromatogramDataSupport.getChromatogramLabel(chromatogram);
			int identifiedPeaks = chromatogram.getNumberOfPeaks();
			int identifiedScans = ChromatogramDataSupport.getIdentifiedScans(chromatogram).size();
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

			TableConfigSupport tableConfig = new TableConfigSupport(peakScanListUI::getTableViewerColumns);

			@Override
			public void setToolbarVisible(boolean visible) {

				PartSupport.setCompositeVisibility(toolbarMain, visible);
			}

			@Override
			public boolean isToolbarVisible() {

				return toolbarMain.isVisible();
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
			public void setVisibleColumns(Set<String> visibleColumns) {

				tableConfig.setVisibleColumns(visibleColumns);
			}

			@Override
			public void setShowScans(boolean show, boolean inRange) {

				ExtendedPeakScanListUI.this.showScans = show;
				ExtendedPeakScanListUI.this.showScansInRange = inRange;
			}

			@Override
			public void setShowPeaks(boolean show, boolean inRange) {

				ExtendedPeakScanListUI.this.showPeaks = show;
				ExtendedPeakScanListUI.this.showPeaksInRange = inRange;
			}

			@Override
			public void setMoveRetentionTimeOnPeakSelection(boolean enabled) {

				ExtendedPeakScanListUI.this.moveRetentionTimeOnPeakSelection = enabled;
			}

			@Override
			public void setInteractionMode(InteractionMode interactionMode) {

				ExtendedPeakScanListUI.this.interactionMode = interactionMode;
			}

			@Override
			public Set<String> getColumns() {

				return tableConfig.getColumns();
			}

			@Override
			public int getColumWidth(String column) {

				return tableConfig.getColumWidth(column);
			}

			@Override
			public void setColumWidth(String column, int width) {

				tableConfig.setColumWidth(column, width);
			}
		};
	}
}
