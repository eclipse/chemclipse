/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - update chromatogram selection after delete, allow updating of selection
 * Lorenz Gerber - fix update on osx when number of list entries doesn't change
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.RetentionTimeRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.PeakMergerMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.msd.model.support.CalculationType;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.rcp.app.undo.UndoContextFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSystem;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.calibration.IUpdateListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.dialogs.ClassifierDialog;
import org.eclipse.chemclipse.ux.extension.xxd.ui.dialogs.InternalStandardDialog;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.TableConfigSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.operations.DeletePeaksOperation;
import org.eclipse.chemclipse.ux.extension.xxd.ui.operations.DeleteScansOperation;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageLists;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageMergePeaks;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.PeakScanListUIConfig.InteractionMode;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	//
	private AtomicReference<Composite> toolbarMain = new AtomicReference<>();
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfoTop = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfoBottom = new AtomicReference<>();
	private Button buttonToolbarSearch;
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private Button buttonSave;
	private Button buttonComparison;
	private Button buttonMerge;
	private Button buttonDelete;
	private ScanIdentifierUI scanIdentifierUI;
	private Button buttonTableEdit;
	private AtomicReference<PeakScanListUI> tableViewer = new AtomicReference<>();
	//
	private IChromatogramSelection chromatogramSelection;
	//
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

	@Override
	public boolean setFocus() {

		DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
		List<Object> objects = dataUpdateSupport.getUpdates(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION);
		if(!objects.isEmpty()) {
			Object last = objects.get(0);
			if(last instanceof IChromatogramSelection) {
				updateChromatogramSelection((IChromatogramSelection)last);
			}
		}
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
			tableViewer.get().clear();
			currentModCount = -1;
		} else {
			currentModCount = chromatogramSelection.getChromatogram().getModCount();
			lastRange = new RetentionTimeRange(chromatogramSelection);
			updateTable();
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD) {
				buttonSave.setEnabled(true);
			}
			if(interactionMode == InteractionMode.SINK || interactionMode == InteractionMode.BIDIRECTIONAL) {
				updateSelection();
			}
		}
	}

	public void updateTable() {

		PeakScanListUI peakScanListUI = tableViewer.get();
		ISelection selection = peakScanListUI.getSelection();
		peakScanListUI.clear();// on Mac the clear is needed, else no update is fired when the number of list entries does not change
		peakScanListUI.setInput(chromatogramSelection, showPeaks, showPeaksInRange, showScans, showScansInRange);
		peakScanListUI.setSelection(selection);
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
			tableViewer.get().setSelection(new StructuredSelection(selection), true);
		} finally {
			interactionMode = oldMode;
		}
	}

	public void refreshTableViewer() {

		tableViewer.get().refresh(true);
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfoTop(this);
		createToolbarSearch(this);
		createPeakTable(this);
		createToolbarInfoBottom(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfoTop, buttonToolbarInfo, IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarSearch, buttonToolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH, false);
		enableToolbar(toolbarInfoBottom, buttonToolbarInfo, IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
		//
		enableEdit(tableViewer, buttonTableEdit, IMAGE_EDIT_ENTRY, false);
		buttonComparison.setEnabled(false);
		buttonMerge.setEnabled(false);
		buttonDelete.setEnabled(false);
		scanIdentifierUI.setEnabled(false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(10, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, Arrays.asList(toolbarInfoTop, toolbarInfoBottom), IMAGE_INFO, TOOLTIP_INFO);
		buttonToolbarSearch = createButtonToggleToolbar(composite, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH);
		buttonTableEdit = createButtonToggleEditTable(composite, tableViewer, IMAGE_EDIT_ENTRY);
		buttonComparison = createButtonComparison(composite);
		buttonMerge = createButtonMerge(composite);
		buttonDelete = createButtonDelete(composite);
		scanIdentifierUI = createScanIdentifierUI(composite);
		createButtonReset(composite);
		buttonSave = createButtonSave(composite);
		createButtonSettings(composite);
		//
		toolbarMain.set(composite);
	}

	private void createToolbarInfoTop(Composite parent) {

		toolbarInfoTop.set(createToolbarInfo(parent));
	}

	private void createToolbarInfoBottom(Composite parent) {

		toolbarInfoBottom.set(createToolbarInfo(parent));
	}

	private InformationUI createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return informationUI;
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				tableViewer.get().setSearchText(searchText, caseSensitive);
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private void createPeakTable(Composite parent) {

		PeakScanListUI peakScanListUI = new PeakScanListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = peakScanListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {

				propagateSelection(e.display);
			}
		});
		/*
		 * Set/Save the column order.
		 */
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String preferenceColumnOrder = PreferenceConstants.P_COLUMN_ORDER_PEAK_SCAN_LIST;
		String preferenceColumnWidth = PreferenceConstants.P_COLUMN_WIDTH_PEAK_SCAN_LIST;
		peakScanListUI.setColumnMoveWidthSupport(preferenceStore, preferenceColumnOrder, preferenceColumnWidth);
		/*
		 * Add the delete targets support.
		 */
		Display display = peakScanListUI.getTable().getDisplay();
		ITableSettings tableSettings = peakScanListUI.getTableSettings();
		//
		addDeleteMenuItem(display, tableSettings, "Peak/Scan (Delete Identifications)");
		addAnalysisActiveMenuItem(tableSettings, "Peaks (Activate for Analysis)", true);
		addAnalysisActiveMenuItem(tableSettings, "Peaks (Deactivate for Analysis)", false);
		addInternalStandardsMenuItem(display, tableSettings, "Peaks (Edit Internal Standard)");
		addClassifierMenuItem(display, tableSettings, "Peaks (Edit Classifier)");
		//
		addKeyEventProcessors(display, tableSettings);
		peakScanListUI.applySettings(tableSettings);
		//
		tableViewer.set(peakScanListUI);
	}

	private void addDeleteMenuItem(Display display, ITableSettings tableSettings, String label) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return label;
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

	private void addAnalysisActiveMenuItem(ITableSettings tableSettings, String label, boolean activeForAnalysis) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return label;
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				setPeaksActiveForAnalysis(activeForAnalysis);
			}
		});
	}

	private void addInternalStandardsMenuItem(Display display, ITableSettings tableSettings, String label) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return label;
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

	private void addClassifierMenuItem(Display display, ITableSettings tableSettings, String label) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return label;
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				modifyClassifier(display);
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
				} else if(e.keyCode == IKeyboardSupport.KEY_CODE_LC_I && (e.stateMask & SWT.MOD1) == SWT.MOD1) {
					if((e.stateMask & SWT.MOD3) == SWT.MOD3) {
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
				} else if(e.keyCode == IKeyboardSupport.KEY_CODE_LC_S && (e.stateMask & SWT.MOD1) == SWT.MOD1) {
					/*
					 * CTRL + s
					 */
					modifyInternalStandards(display);
				} else if(e.keyCode == IKeyboardSupport.KEY_CODE_LC_D && (e.stateMask & SWT.MOD1) == SWT.MOD1) {
					/*
					 * CTRL + d
					 */
					modifyClassifier(display);
				} else {
					propagateSelection(display);
				}
			}
		});
	}

	private IOperationHistory getOperationHistory() {

		return OperationHistoryFactory.getOperationHistory();
	}

	private void deletePeaksOrIdentifications(Display display) {

		MessageBox messageBox = new MessageBox(display.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Delete Peak(s)/Scan Identification(s)");
		messageBox.setMessage("Would you like to delete the selected peak(s)/scan identification(s)?");
		if(messageBox.open() == SWT.YES) {
			/*
			 * Selected Items.
			 */
			Iterator iterator = tableViewer.get().getStructuredSelection().iterator();
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
			if(!scansToClear.isEmpty()) {
				DeleteScansOperation deleteScans = new DeleteScansOperation(display, chromatogramSelection, scansToClear);
				deleteScans.addContext(UndoContextFactory.getUndoContext());
				try {
					getOperationHistory().execute(deleteScans, null, null);
				} catch(ExecutionException e) {
					logger.warn(e);
				}
			}
			if(!peaksToDelete.isEmpty()) {
				DeletePeaksOperation deletePeaks = new DeletePeaksOperation(display, chromatogramSelection, peaksToDelete);
				deletePeaks.addContext(UndoContextFactory.getUndoContext());
				try {
					getOperationHistory().execute(deletePeaks, null, null);
				} catch(ExecutionException e) {
					logger.warn(e);
				}
			}
			updateChromatogramSelection();
		}
	}

	private void setPeaksActiveForAnalysis(boolean activeForAnalysis) {

		Iterator iterator = tableViewer.get().getStructuredSelection().iterator();
		while(iterator.hasNext()) {
			Object object = iterator.next();
			if(object instanceof IPeak) {
				IPeak peak = (IPeak)object;
				peak.setActiveForAnalysis(activeForAnalysis);
			}
		}
		updateChromatogramSelection();
	}

	private void modifyInternalStandards(Display display) {

		MessageBox messageBox = new MessageBox(display.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Internal Standard (ISTD)");
		messageBox.setMessage("Would you like to modify the internal standards?");
		if(messageBox.open() == SWT.YES) {
			Iterator iterator = tableViewer.get().getStructuredSelection().iterator();
			exitloop:
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof IPeak) {
					IPeak peak = (IPeak)object;
					if(peak.getIntegratedArea() > 0) {
						InternalStandardDialog dialog = new InternalStandardDialog(display.getActiveShell(), peak);
						if(dialog.open() == Window.OK) {
							break exitloop; // Cancel
						}
					}
				}
			}
			/*
			 * Send update.
			 */
			UpdateNotifierUI.update(display, chromatogramSelection);
		}
	}

	private void modifyClassifier(Display display) {

		MessageBox messageBox = new MessageBox(display.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Classifier");
		messageBox.setMessage("Would you like to modify the classifier?");
		if(messageBox.open() == SWT.YES) {
			Iterator iterator = tableViewer.get().getStructuredSelection().iterator();
			exitloop:
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof IPeak) {
					IPeak peak = (IPeak)object;
					ClassifierDialog dialog = new ClassifierDialog(display.getActiveShell(), peak);
					if(dialog.open() == Window.OK) {
						peak.removeClassifier();
						List<String> classifiers = dialog.getValue();
						for(String classifier : classifiers) {
							peak.addClassifier(classifier);
						}
					} else {
						break exitloop; // Cancel
					}
				}
			}
			/*
			 * Send update.
			 */
			tableViewer.get().refresh();
			UpdateNotifierUI.update(display, chromatogramSelection);
		}
	}

	@SuppressWarnings("unchecked")
	private void propagateSelection(Display display) {

		if(interactionMode != InteractionMode.SOURCE && interactionMode != InteractionMode.BIDIRECTIONAL) {
			return;
		}
		//
		IStructuredSelection selection = tableViewer.get().getStructuredSelection();
		buttonComparison.setEnabled(false);
		buttonMerge.setEnabled(false);
		buttonDelete.setEnabled(false);
		scanIdentifierUI.setEnabled(false); // setInput enables/disables the control.
		//
		if(!selection.isEmpty()) {
			buttonDelete.setEnabled(true);
			List list = selection.toList();
			if(list.size() > 1) {
				/*
				 * Add in the future to select/display more than one peak.
				 */
				buttonComparison.setEnabled(list.size() == 2);
				buttonMerge.setEnabled(getSelectedPeaksMSD().size() >= 2);
				/*
				 * Selection Events
				 */
				List<IPeak> selectedPeaks = new ArrayList<>();
				List<IScan> selectedIdentifiedScans = new ArrayList<>();
				List<IScan> scansIdentify = new ArrayList<>();
				//
				for(Object item : list) {
					if(item instanceof IPeak) {
						IPeak peak = (IPeak)item;
						selectedPeaks.add(peak);
						scansIdentify.add(peak.getPeakModel().getPeakMaximum());
					} else if(item instanceof IScan) {
						IScan scan = (IScan)item;
						selectedIdentifiedScans.add(scan);
						scansIdentify.add(scan);
					}
				}
				//
				scanIdentifierUI.setInput(scansIdentify);
				//
				chromatogramSelection.setSelectedPeaks(selectedPeaks);
				chromatogramSelection.setSelectedIdentifiedScans(selectedIdentifiedScans);
				chromatogramSelection.getChromatogram().setDirty(true);
				UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "Peak(s)/Scan(s) selection via the list.");
			} else {
				/*
				 * Only one object.
				 */
				Object object = list.size() == 1 ? list.get(0) : null;
				if(object instanceof IPeak) {
					/*
					 * Fire updates
					 */
					IPeak peak = (IPeak)object;
					IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(SortOrder.DESC, peak.getPeakModel().getPeakMaximum().getRetentionIndex());
					IIdentificationTarget identificationTarget = IIdentificationTarget.getBestIdentificationTarget(peak.getTargets(), identificationTargetComparator);
					if(moveRetentionTimeOnPeakSelection) {
						ChromatogramDataSupport.adjustChromatogramSelection(peak, chromatogramSelection);
					}
					//
					IScan scan = peak.getPeakModel().getPeakMaximum();
					scanIdentifierUI.setInput(scan);
					chromatogramSelection.setSelectedPeak(peak);
					List<IScan> selectedIdentifiedScans = new ArrayList<>();
					chromatogramSelection.setSelectedIdentifiedScans(selectedIdentifiedScans);
					chromatogramSelection.setSelectedScan(null);
					//
					UpdateNotifierUI.update(display, peak);
					UpdateNotifierUI.update(display, identificationTarget);
					if(peak instanceof IPeakMSD) {
						IPeakMSD peakMSD = (IPeakMSD)peak;
						UpdateNotifierUI.update(display, peakMSD.getPeakModel().getPeakMassSpectrum(), identificationTarget);
					}
				} else if(object instanceof IScan) {
					/*
					 * Fire updates
					 */
					IScan scan = (IScan)object;
					IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(SortOrder.DESC, scan.getRetentionIndex());
					IIdentificationTarget identificationTarget = IIdentificationTarget.getBestIdentificationTarget(scan.getTargets(), identificationTargetComparator);
					//
					scanIdentifierUI.setInput(scan);
					chromatogramSelection.setSelectedScan(scan);
					chromatogramSelection.setSelectedIdentifiedScan(scan);
					chromatogramSelection.setSelectedPeaks(new ArrayList<IPeak>());
					//
					UpdateNotifierUI.update(display, scan);
					UpdateNotifierUI.update(display, identificationTarget);
					if(scan instanceof IScanMSD) {
						IScanMSD scanMSD = (IScanMSD)scan;
						UpdateNotifierUI.update(display, scanMSD, identificationTarget);
					}
				}
			}
		}
	}

	public void setEditEnabled(boolean editEnabled) {

		tableViewer.get().setEditEnabled(editEnabled);
		updateLabel();
	}

	private Button createButtonComparison(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Compare two selected scans/peaks.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COMPARISON_SCAN, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection selection = tableViewer.get().getStructuredSelection();
				if(!selection.isEmpty()) {
					List list = selection.toList();
					if(list.size() == 2) {
						IScanMSD massSpectrum1 = getScanMSD(list.get(0));
						IScanMSD massSpectrum2 = getScanMSD(list.get(1));
						UpdateNotifierUI.update(e.display, massSpectrum1, massSpectrum2);
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonMerge(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Merge the selected peaks into a new peak.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MERGE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				List<IChromatogramPeakMSD> peaksToMerge = getSelectedPeaksMSD();
				if(peaksToMerge.size() >= 2) {
					if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
						/*
						 * Merge Peaks
						 */
						CalculationType calculationType = getCalculationTypeMerge();
						boolean mergeIdentificationTargets = preferenceStore.getBoolean(PreferenceConstants.P_MERGE_PEAKS_IDENTIFICATION_TARGETS);
						IPeakMSD peakMSD = PeakMergerMSD.mergePeaks(peaksToMerge, calculationType, mergeIdentificationTargets);
						/*
						 * Modify the chromatogram
						 */
						IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
						IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogram();
						IChromatogramPeakMSD chromatogramPeakMSD = new ChromatogramPeakMSD(peakMSD.getPeakModel(), chromatogramMSD);
						/*
						 * Delete Origins on demand
						 */
						if(preferenceStore.getBoolean(PreferenceConstants.P_MERGE_PEAKS_DELETE_ORIGINS)) {
							chromatogramMSD.removePeaks(peaksToMerge);
						}
						/*
						 * Set and update the new peak.
						 */
						chromatogramMSD.addPeak(chromatogramPeakMSD);
						chromatogramSelectionMSD.setSelectedPeak(chromatogramPeakMSD);
						chromatogramMSD.setDirty(true);
						updateChromatogramSelection();
						//
						UpdateNotifierUI.update(e.display, chromatogramPeakMSD);
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Delete the selected peaks.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deletePeaksOrIdentifications(parent.getDisplay());
			}
		});
		//
		return button;
	}

	private ScanIdentifierUI createScanIdentifierUI(Composite parent) {

		ScanIdentifierUI scanIdentifierUI = new ScanIdentifierUI(parent, SWT.NONE);
		scanIdentifierUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update(Display display) {

				if(chromatogramSelection != null) {
					chromatogramSelection.getChromatogram().setDirty(true);
					UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "Peaks/Scans have been identified.");
				}
			}
		});
		//
		return scanIdentifierUI;
	}

	private CalculationType getCalculationTypeMerge() {

		try {
			return CalculationType.valueOf(preferenceStore.getString(PreferenceConstants.P_MERGE_PEAKS_CALCULATION_TYPE));
		} catch(Exception e) {
			/*
			 * Default SUM on error
			 */
			return CalculationType.SUM;
		}
	}

	private IScanMSD getScanMSD(Object object) {

		IScanMSD massSpectrum;
		//
		if(object instanceof IPeakMSD) {
			IPeakMSD peak = (IPeakMSD)object;
			massSpectrum = peak.getPeakModel().getPeakMassSpectrum();
		} else if(object instanceof IScanMSD) {
			massSpectrum = (IScanMSD)object;
		} else {
			massSpectrum = null;
		}
		//
		return massSpectrum;
	}

	private void createButtonReset(Composite parent) {

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

	private Button createButtonSave(Composite parent) {

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
						Table table = tableViewer.get().getTable();
						int[] indices = table.getSelectionIndices();
						List<IPeak> peaks;
						if(indices.length == 0) {
							peaks = getPeakList(table);
						} else {
							peaks = getPeakList(table, indices);
						}
						//
						if(!peaks.isEmpty()) {
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

	private void createButtonSettings(Composite parent) {

		createSettingsButton(parent, Arrays.asList( //
				PreferencePageSystem.class, //
				PreferencePageMergePeaks.class, //
				PreferencePageScans.class, //
				PreferencePageLists.class //
		), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void updateLabel() {

		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() == null) {
			toolbarInfoTop.get().setText(ChromatogramDataSupport.getChromatogramLabel(null));
			toolbarInfoBottom.get().setText("");
		} else {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String chromatogramLabel = ChromatogramDataSupport.getChromatogramLabel(chromatogram);
			int identifiedPeaks = chromatogram.getNumberOfPeaks();
			int identifiedScans = ChromatogramDataSupport.getIdentifiedScans(chromatogram).size();
			//
			toolbarInfoTop.get().setText(chromatogramLabel);
			toolbarInfoBottom.get().setText("Number of Peaks: " + identifiedPeaks + " | Scans: " + identifiedScans);
		}
	}

	private void applySettings() {

		toolbarSearch.get().reset();
		updateChromatogramSelection();
	}

	private void reset() {

		updateChromatogramSelection();
	}

	private List<IPeak> getPeakList(Table table) {

		List<IPeak> peakList = new ArrayList<>();
		for(TableItem tableItem : table.getItems()) {
			Object object = tableItem.getData();
			if(object instanceof IPeak) {
				peakList.add((IPeak)object);
			}
		}
		return peakList;
	}

	private List<IPeak> getPeakList(Table table, int[] indices) {

		List<IPeak> peakList = new ArrayList<>();
		for(int index : indices) {
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IPeak) {
				peakList.add((IPeak)object);
			}
		}
		return peakList;
	}

	List<IChromatogramPeakMSD> getSelectedPeaksMSD() {

		Table table = tableViewer.get().getTable();
		int[] indices = table.getSelectionIndices();
		List<IChromatogramPeakMSD> peaksMSD;
		//
		if(indices.length >= 2) {
			peaksMSD = new ArrayList<>();
			for(IPeak peak : getPeakList(table, indices)) {
				if(peak instanceof IChromatogramPeakMSD) {
					peaksMSD.add((IChromatogramPeakMSD)peak);
				}
			}
		} else {
			peaksMSD = Collections.emptyList();
		}
		//
		return peaksMSD;
	}

	private List<IScan> getScanList(Table table) {

		List<IScan> scanList = new ArrayList<>();
		for(TableItem tableItem : table.getItems()) {
			Object object = tableItem.getData();
			if(object instanceof IScan) {
				scanList.add((IScan)object);
			}
		}
		return scanList;
	}

	private List<IScan> getScanList(Table table, int[] indices) {

		List<IScan> scanList = new ArrayList<>();
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

			TableConfigSupport tableConfig = new TableConfigSupport(tableViewer.get()::getTableViewerColumns);

			@Override
			public void setToolbarVisible(boolean visible) {

				PartSupport.setCompositeVisibility(toolbarMain.get(), visible);
			}

			@Override
			public boolean isToolbarVisible() {

				return toolbarMain.get().isVisible();
			}

			@Override
			public void setToolbarInfoVisible(boolean visible) {

				enableToolbar(toolbarInfoTop, buttonToolbarInfo, IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, visible);
				enableToolbar(toolbarInfoBottom, buttonToolbarInfo, IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, visible);
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
