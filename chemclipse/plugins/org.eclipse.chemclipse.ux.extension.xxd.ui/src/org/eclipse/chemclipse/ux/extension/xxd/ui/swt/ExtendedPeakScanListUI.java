/*******************************************************************************
 * Copyright (c) 2018, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
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
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramPeak;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.CalculationType;
import org.eclipse.chemclipse.model.support.RetentionTimeRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.PeakMergerMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.rcp.app.undo.UndoContextFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSystem;
import org.eclipse.chemclipse.ux.extension.ui.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.dialogs.ClassifierDialog;
import org.eclipse.chemclipse.ux.extension.xxd.ui.dialogs.InternalStandardDialog;
import org.eclipse.chemclipse.ux.extension.xxd.ui.help.HelpContext;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.TableConfigSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.operations.DeletePeaksOperation;
import org.eclipse.chemclipse.ux.extension.xxd.ui.operations.DeleteScanTargetsOperation;
import org.eclipse.chemclipse.ux.extension.xxd.ui.operations.DeleteTargetsOperation;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageMergePeaks;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageUnknownTarget;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePeakScanPageList;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.IdentificationTargetSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.PeakScanListUIConfig.InteractionMode;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.e4.ui.services.EContextService;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swtchart.extensions.core.IKeyboardSupport;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

public class ExtendedPeakScanListUI extends Composite implements IExtendedPartUI, ConfigurableUI<PeakScanListUIConfig> {

	private static final Logger logger = Logger.getLogger(ExtendedPeakScanListUI.class);

	private static final String MENU_CATEGORY = "Peaks/Scans";
	private static final String DESCRIPTION_PEAKS = "Number Peaks:";
	private static final String DESCRIPTION_SCANS = "Scans:";

	private static final String KEY_CLASS_PREFIX = "org.eclipse.chemclipse.ux.extension.xxd.ui.PeakScanList.";

	private EContextService contextService = Activator.getDefault().getApplication().getContext().get(EContextService.class);
	private IBindingService bindingService = PlatformUI.getWorkbench().getService(IBindingService.class);

	private AtomicReference<Composite> toolbarMain = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarInfo = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfoTop = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfoBottom = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarSearch = new AtomicReference<>();
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private AtomicReference<Button> buttonSave = new AtomicReference<>();
	private AtomicReference<Button> buttonComparison = new AtomicReference<>();
	private AtomicReference<Button> buttonMerge = new AtomicReference<>();
	private AtomicReference<Button> buttonDelete = new AtomicReference<>();
	private AtomicReference<ScanIdentifierUI> scanIdentifierControl = new AtomicReference<>();
	private AtomicReference<PeakScanListUI> tableViewer = new AtomicReference<>();

	private IChromatogramSelection chromatogramSelection;

	private boolean showScans = true;
	private boolean showPeaks = true;
	private boolean showScansInRange = true;
	private boolean showPeaksInRange = true;
	private boolean showPeakProfilesSelectionAll = true;
	private boolean moveRetentionTimeOnPeakSelection = true;

	private int currentModCount;
	private InteractionMode interactionMode = InteractionMode.SOURCE;
	private RetentionTimeRange lastRange;

	public ExtendedPeakScanListUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public boolean setFocus() {

		DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
		List<Object> objects = dataUpdateSupport.getUpdates(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION);
		if(!objects.isEmpty()) {
			Object last = objects.get(0);
			if(last instanceof IChromatogramSelection chromatogramSelection) {
				updateChromatogramSelection(chromatogramSelection);
			}
		}
		updateLabel();
		return true;
	}

	public void updateChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		if(hasChanged(chromatogramSelection)) {
			this.chromatogramSelection = chromatogramSelection;
			updateChromatogramSelection();
			updateLabel();
		}
	}

	public void updateChromatogramSelection() {

		updateFromPreferences();
		updateLabel();
		buttonSave.get().setEnabled(false);

		if(chromatogramSelection == null) {
			tableViewer.get().clear();
			currentModCount = -1;
		} else {
			currentModCount = chromatogramSelection.getChromatogram().getModCount();
			lastRange = new RetentionTimeRange(chromatogramSelection);
			tableViewer.get().setInput(chromatogramSelection, showPeaks, showPeaksInRange, showScans, showScansInRange);
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD) {
				buttonSave.get().setEnabled(true);
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

		createToolbarMain(this);
		createToolbarInfoTop(this);
		createToolbarSearch(this);
		createPeakTable(this);
		createToolbarInfoBottom(this);

		initialize();
	}

	private void initialize() {

		contextService.activateContext("org.eclipse.chemclipse.ux.extension.xxd.ui.PeakScanList");

		enableToolbar(toolbarInfoTop, buttonToolbarInfo.get(), IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarSearch, buttonToolbarSearch.get(), IMAGE_SEARCH, TOOLTIP_SEARCH, false);
		enableToolbar(toolbarInfoBottom, buttonToolbarInfo.get(), IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);

		buttonComparison.get().setEnabled(false);
		buttonMerge.get().setEnabled(false);
		buttonDelete.get().setEnabled(false);
		scanIdentifierControl.get().setEnabled(false);
		setHelp(this, HelpContext.PEAK_SCAN_LIST);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(11, false));

		createButtonInfo(composite);
		createButtonSearch(composite);
		createButtonComparison(composite);
		createButtonMerge(composite);
		createButtonDelete(composite);
		createScanIdentifierUI(composite);
		createButtonReset(composite);
		createButtonSave(composite);
		createButtonHelp(composite, HelpContext.PEAK_SCAN_LIST);
		createButtonSettings(composite);

		toolbarMain.set(composite);
	}

	private void createButtonInfo(Composite parent) {

		buttonToolbarInfo.set(createButtonToggleToolbar(parent, Arrays.asList(toolbarInfoTop, toolbarInfoBottom), IMAGE_INFO, TOOLTIP_INFO));
	}

	private void createButtonSearch(Composite parent) {

		buttonToolbarSearch.set(createButtonToggleToolbar(parent, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH));
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

		return informationUI;
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener((searchText, caseSensitive) -> {

			tableViewer.get().setSearchText(searchText, caseSensitive);
			updateLabel();
		});

		toolbarSearch.set(searchSupportUI);
	}

	private void createPeakTable(Composite parent) {

		PeakScanListUI peakScanListUI = new PeakScanListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = peakScanListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {

				propagateSelection();
			}
		});
		/*
		 * Add the delete targets support.
		 */
		ITableSettings tableSettings = peakScanListUI.getTableSettings();
		addDeleteMenuItem(tableSettings, "Peak/Scan (Delete Identifications)");
		addAnalysisActiveMenuItem(tableSettings, "Peaks (Activate for Analysis)", true);
		addAnalysisActiveMenuItem(tableSettings, "Peaks (Deactivate for Analysis)", false);
		addInternalStandardsMenuItem(tableSettings, "Peaks (Edit Internal Standard)");
		addClassifierMenuItem(tableSettings, "Peaks (Edit Classifier)");
		addKeyEventProcessors(tableSettings);
		peakScanListUI.applySettings(tableSettings);

		tableViewer.set(peakScanListUI);
	}

	private void addDeleteMenuItem(ITableSettings tableSettings, String label) {

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

				deletePeaksOrIdentifications();
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

	private void addInternalStandardsMenuItem(ITableSettings tableSettings, String label) {

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

				modifyInternalStandards();
			}
		});
	}

	private void addClassifierMenuItem(ITableSettings tableSettings, String label) {

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

				modifyClassifier();
			}
		});
	}

	private void addKeyEventProcessors(ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor((_, e) -> {

			if(matchesKeyPress("InternalStandard", e)) {
				modifyInternalStandards();
			} else if(matchesKeyPress("Classifier", e)) {
				modifyClassifier();
			} else if(matchesKeyPress("DeleteTargets", e)) {
				deleteTargetsAll();
			} else if(matchesKeyPress("Unknown", e)) {
				addTargetsUnknown();
			} else if(matchesKeyPress("Query", e)) {
				scanIdentifierControl.get().runIdentification();
				tableViewer.get().refresh();
				UpdateNotifierUI.update(getDisplay(), chromatogramSelection);
			} else if(matchesKeyPress("ActiveAnalysis", e)) {
				setPeaksActiveForAnalysis(true);
			} else if(matchesKeyPress("InactiveAnalysis", e)) {
				setPeaksActiveForAnalysis(false);
			} else if(e.keyCode == SWT.DEL) {
				deletePeaksOrIdentifications();
			} else if(e.stateMask == SWT.MOD1 && e.keyCode == 'v') {
				/*
				 * Copy Target
				 */
				Clipboard clipboard = new Clipboard(e.display);
				TextTransfer transfer = TextTransfer.getInstance();
				if(clipboard.getContents(transfer) instanceof String content) {
					if(!content.isBlank()) {
						addTargetsUnknown(content.trim());
					}
				}
			} else if((e.stateMask & SWT.MOD1) == SWT.MOD1) {
				if(e.keyCode == IKeyboardSupport.KEY_CODE_LC_A) {
					if(showPeakProfilesSelectionAll) {
						propagateSelection(); // CTRL + A
					}
				}
			} else {
				propagateSelection();
			}
		});
	}

	private boolean matchesKeyPress(String commandSuffix, KeyEvent e) {

		TriggerSequence triggerSequence = bindingService.getBestActiveBindingFor(KEY_CLASS_PREFIX + commandSuffix);
		if(triggerSequence instanceof KeySequence keySequence) {
			KeyStroke[] bindingStrokes = keySequence.getKeyStrokes();
			int accelerator = SWTKeySupport.convertEventToUnmodifiedAccelerator(e);
			KeyStroke eventStroke = SWTKeySupport.convertAcceleratorToKeyStroke(accelerator);
			return bindingStrokes.length > 0 && bindingStrokes[0].equals(eventStroke);
		}
		return false;
	}

	private IOperationHistory getOperationHistory() {

		return OperationHistoryFactory.getOperationHistory();
	}

	private void deletePeaksOrIdentifications() {

		MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Delete Peaks/Scan Identifications");
		messageBox.setMessage("Would you like to delete the selected peaks/scan identifications?");
		if(messageBox.open() == SWT.YES) {
			/*
			 * Selected Items.
			 */
			Iterator<?> iterator = tableViewer.get().getStructuredSelection().iterator();
			List<IScan> scanTargetsToClear = new ArrayList<>();
			List<IChromatogramPeak> peaksToDelete = new ArrayList<>();
			/*
			 * Collect
			 */
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof IChromatogramPeak peak) {
					peaksToDelete.add(peak);
				} else if(object instanceof IScan scan) {
					scanTargetsToClear.add(scan);
				}
			}
			/*
			 * Clear scans
			 */
			if(!scanTargetsToClear.isEmpty()) {
				DeleteScanTargetsOperation deleteScanTargetsOperation = new DeleteScanTargetsOperation(getDisplay(), chromatogramSelection, scanTargetsToClear);
				deleteScanTargetsOperation.addContext(UndoContextFactory.getUndoContext());
				try {
					getOperationHistory().execute(deleteScanTargetsOperation, null, null);
				} catch(ExecutionException e) {
					logger.warn(e);
				}
			}
			/*
			 * Clear peaks
			 */
			if(!peaksToDelete.isEmpty()) {
				DeletePeaksOperation deletePeaksOperation = new DeletePeaksOperation(getDisplay(), chromatogramSelection, peaksToDelete);
				deletePeaksOperation.addContext(UndoContextFactory.getUndoContext());
				try {
					getOperationHistory().execute(deletePeaksOperation, null, null);
				} catch(ExecutionException e) {
					logger.warn(e);
				}
			}

			updateChromatogramSelection();
		}
	}

	private void setPeaksActiveForAnalysis(boolean activeForAnalysis) {

		Iterator<?> iterator = tableViewer.get().getStructuredSelection().iterator();
		while(iterator.hasNext()) {
			Object object = iterator.next();
			if(object instanceof IPeak peak) {
				peak.setActiveForAnalysis(activeForAnalysis);
			}
		}
		updateChromatogramSelection();
	}

	private void modifyInternalStandards() {

		MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Internal Standard (ISTD)");
		messageBox.setMessage("Would you like to modify the internal standards?");
		if(messageBox.open() == SWT.YES) {
			Iterator<?> iterator = tableViewer.get().getStructuredSelection().iterator();
			exitloop:
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof IPeak peak) {
					if(peak.getIntegratedArea() > 0) {
						InternalStandardDialog dialog = new InternalStandardDialog(getShell(), peak);
						if(dialog.open() == Window.OK) {
							break exitloop; // Cancel
						}
					}
				}
			}
			/*
			 * Send update.
			 */
			UpdateNotifierUI.update(getDisplay(), chromatogramSelection);
		}
	}

	private void modifyClassifier() {

		MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Classifier");
		messageBox.setMessage("Would you like to modify the classifier?");
		if(messageBox.open() == SWT.YES) {
			Iterator<?> iterator = tableViewer.get().getStructuredSelection().iterator();
			exitloop:
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof IPeak peak) {
					ClassifierDialog dialog = new ClassifierDialog(getShell(), peak);
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
			UpdateNotifierUI.update(getDisplay(), chromatogramSelection);
		}
	}

	private void deleteTargetsAll() {

		for(Object object : tableViewer.get().getStructuredSelection().toList()) {
			if(object instanceof ITargetSupplier targetSupplier) {
				Set<IIdentificationTarget> targetsToDelete = targetSupplier.getTargets();
				DeleteTargetsOperation deleteTargetsOperation = new DeleteTargetsOperation(getDisplay(), chromatogramSelection, targetSupplier, targetsToDelete);
				deleteTargetsOperation.addContext(UndoContextFactory.getUndoContext());

				try {
					getOperationHistory().execute(deleteTargetsOperation, null, null);
				} catch(ExecutionException e) {
					logger.warn(e);
				}

				if(PreferenceSupplier.isAddUnknownAfterDeleteTargetsAll()) {
					IScan scan = getScan(object);
					if(scan != null) {
						IIdentificationTarget identificationTarget = IdentificationTargetSupport.getTargetUnknown(scan);
						targetSupplier.getTargets().add(identificationTarget);
					}
				}
			}
			/*
			 * Send update.
			 */
			tableViewer.get().refresh();
			chromatogramSelection.getChromatogram().setDirty(true);
			UpdateNotifierUI.update(getDisplay(), chromatogramSelection);
			UpdateNotifierUI.update(getDisplay(), IChemClipseEvents.TOPIC_IDENTIFICATION_TARGETS_UPDATE_SELECTION, ExtensionMessages.targetsDeleted);
		}
	}

	/**
	 * May return null.
	 *
	 * @return IScan
	 */
	private IScan getScan(Object object) {

		if(object instanceof IPeak peak) {
			IPeakModel peakModel = peak.getPeakModel();
			return peakModel.getPeakMaximum();
		} else if(object instanceof IScan scan) {
			return scan;
		}
		return null;
	}

	private void addTargetsUnknown() {

		addTargetsUnknown("");
	}

	private void addTargetsUnknown(String name) {

		for(Object object : tableViewer.get().getStructuredSelection().toList()) {
			IScan scan = getScan(object);
			if(scan instanceof ITargetSupplier targetSupplier) {
				IIdentificationTarget identificationTarget = IdentificationTargetSupport.getTargetUnknown(scan);
				if(identificationTarget != null) {
					/*
					 * Modify the identification target.
					 * Set a specific name instead of the default unknown label on demand.
					 */
					if(isModifyIdentificationTarget(name)) {
						targetSupplier.getTargets().clear();
						identificationTarget.getLibraryInformation().setName(name);
					}
					targetSupplier.getTargets().add(identificationTarget);
				}
			}
		}

		/*
		 * Send update.
		 */
		tableViewer.get().refresh();
		UpdateNotifierUI.update(getDisplay(), chromatogramSelection);

		chromatogramSelection.getChromatogram().setDirty(true);
		UpdateNotifierUI.update(getDisplay(), IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "Peaks/Scans unknown targets have been set.");
	}

	private boolean isModifyIdentificationTarget(String name) {

		return name != null && !name.isBlank();
	}

	private void propagateSelection() {

		if(interactionMode != InteractionMode.SOURCE && interactionMode != InteractionMode.BIDIRECTIONAL) {
			return;
		}

		IStructuredSelection selection = tableViewer.get().getStructuredSelection();
		buttonComparison.get().setEnabled(false);
		buttonMerge.get().setEnabled(false);
		buttonDelete.get().setEnabled(false);
		scanIdentifierControl.get().setEnabled(false); // setInput enables/disables the control.

		if(!selection.isEmpty()) {
			buttonDelete.get().setEnabled(true);
			List<?> list = selection.toList();
			if(list.size() > 1) {
				/*
				 * Add in the future to select/display more than one peak.
				 */
				buttonComparison.get().setEnabled(list.size() == 2);
				buttonMerge.get().setEnabled(getSelectedPeaksMSD().size() >= 2);
				/*
				 * Selection Events
				 */
				List<IPeak> selectedPeaks = new ArrayList<>();
				List<IScan> selectedIdentifiedScans = new ArrayList<>();
				List<IScan> scansIdentify = new ArrayList<>();

				for(Object item : list) {
					if(item instanceof IPeak peak) {
						selectedPeaks.add(peak);
						scansIdentify.add(peak.getPeakModel().getPeakMaximum());
					} else if(item instanceof IScan scan) {
						selectedIdentifiedScans.add(scan);
						scansIdentify.add(scan);
					}
				}

				scanIdentifierControl.get().setInput(scansIdentify);

				chromatogramSelection.setSelectedPeaks(selectedPeaks);
				chromatogramSelection.setSelectedIdentifiedScans(selectedIdentifiedScans);
				chromatogramSelection.getChromatogram().setDirty(true);
				UpdateNotifierUI.update(getDisplay(), IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "Peaks/Scans selection via the list.");
			} else {
				/*
				 * Only one object.
				 */
				Object object = list.size() == 1 ? list.get(0) : null;
				if(object instanceof IPeak peak) {
					/*
					 * Fire updates
					 */
					IIdentificationTarget identificationTarget = IIdentificationTarget.getIdentificationTarget(peak);
					if(moveRetentionTimeOnPeakSelection) {
						ChromatogramDataSupport.adjustChromatogramSelection(peak, chromatogramSelection);
					}
					IScan scan = peak.getPeakModel().getPeakMaximum();
					scanIdentifierControl.get().setInput(scan);
					chromatogramSelection.setSelectedPeak(peak);
					List<IScan> selectedIdentifiedScans = new ArrayList<>();
					chromatogramSelection.setSelectedIdentifiedScans(selectedIdentifiedScans);
					chromatogramSelection.setSelectedScan(null);
					UpdateNotifierUI.update(getDisplay(), peak);
					UpdateNotifierUI.update(getDisplay(), identificationTarget);
					if(peak instanceof IPeakMSD peakMSD) {
						UpdateNotifierUI.update(getDisplay(), peakMSD.getPeakModel().getPeakMassSpectrum(), identificationTarget);
					}
				} else if(object instanceof IScan scan) {
					/*
					 * Fire updates
					 */
					IIdentificationTarget identificationTarget = IIdentificationTarget.getIdentificationTarget(scan);
					scanIdentifierControl.get().setInput(scan);
					chromatogramSelection.setSelectedScan(scan);
					chromatogramSelection.setSelectedIdentifiedScan(scan);
					chromatogramSelection.setSelectedPeaks(new ArrayList<>());
					UpdateNotifierUI.update(getDisplay(), scan);
					UpdateNotifierUI.update(getDisplay(), identificationTarget);
					if(scan instanceof IScanMSD scanMSD) {
						UpdateNotifierUI.update(getDisplay(), scanMSD, identificationTarget);
					}
				}
			}
		}
	}

	public void setEditEnabled(boolean editEnabled) {

		tableViewer.get().setEditEnabled(editEnabled);
		updateLabel();
	}

	private void createButtonComparison(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Compare two selected scans/peaks.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COMPARISON_SCAN, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection selection = tableViewer.get().getStructuredSelection();
				if(!selection.isEmpty()) {
					List<?> list = selection.toList();
					if(list.size() == 2) {
						IScanMSD massSpectrum1 = getScanMSD(list.get(0));
						IScanMSD massSpectrum2 = getScanMSD(list.get(1));
						UpdateNotifierUI.update(e.display, massSpectrum1, massSpectrum2);
					}
				}
			}
		});

		buttonComparison.set(button);
	}

	private void createButtonMerge(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Merge the selected peaks into a new peak.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MERGE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				List<IChromatogramPeakMSD> peaksToMerge = getSelectedPeaksMSD();
				if(peaksToMerge.size() >= 2) {
					if(chromatogramSelection instanceof IChromatogramSelectionMSD chromatogramSelectionMSD) {
						/*
						 * Merge Peaks
						 */
						CalculationType calculationType = PreferenceSupplier.getCalculationTypeMerge();
						boolean mergeIdentificationTargets = PreferenceSupplier.isMergeIdentificationTargets();
						IPeakMSD peakMSD = PeakMergerMSD.mergePeaks(peaksToMerge, calculationType, mergeIdentificationTargets);
						/*
						 * Modify the chromatogram
						 */
						IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogram();
						IChromatogramPeakMSD chromatogramPeakMSD = new ChromatogramPeakMSD(peakMSD.getPeakModel(), chromatogramMSD);
						/*
						 * Delete Origins on demand
						 */
						if(PreferenceSupplier.isMergePeaksDeleteOrigins()) {
							chromatogramMSD.getPeaks().removeAll(peaksToMerge);
						}
						/*
						 * Set and update the new peak.
						 */
						chromatogramMSD.getPeaks().add(chromatogramPeakMSD);
						chromatogramSelectionMSD.setSelectedPeak(chromatogramPeakMSD);
						chromatogramMSD.setDirty(true);
						updateChromatogramSelection();
						/*
						 * Update
						 */
						UpdateNotifierUI.update(e.display, chromatogramPeakMSD);
					}
				}
			}
		});

		buttonMerge.set(button);
	}

	private void createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Delete the selected peaks.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deletePeaksOrIdentifications();
			}
		});

		buttonDelete.set(button);
	}

	private void createScanIdentifierUI(Composite parent) {

		ScanIdentifierUI scanIdentifierUI = new ScanIdentifierUI(parent, SWT.NONE);
		scanIdentifierUI.setUpdateListener(display -> {

			if(chromatogramSelection != null) {
				chromatogramSelection.getChromatogram().setDirty(true);
				UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "Peaks/Scans have been identified.");
			}
		});

		scanIdentifierControl.set(scanIdentifierUI);
	}

	private IScanMSD getScanMSD(Object object) {

		IScanMSD massSpectrum;

		if(object instanceof IPeakMSD peak) {
			massSpectrum = peak.getPeakModel().getPeakMassSpectrum();
		} else if(object instanceof IScanMSD scanMSD) {
			massSpectrum = scanMSD;
		} else {
			massSpectrum = null;
		}

		return massSpectrum;
	}

	private void createButtonReset(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the peak/scan list.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset();
			}
		});
	}

	private void createButtonSave(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Save the peak/scan list.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImageProvider.SIZE_16x16));
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

						if(!peaks.isEmpty()) {
							DatabaseFileSupport.savePeaks(getShell(), peaks, chromatogram.getName());
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

						MassSpectra massSpectra = new MassSpectra();
						for(IScan scan : scans) {
							if(scan instanceof IScanMSD scanMSD) {
								massSpectra.addMassSpectrum(scanMSD);
							}
						}

						if(!massSpectra.isEmpty()) {
							DatabaseFileSupport.saveMassSpectra(getShell(), massSpectra, chromatogram.getName());
						}
					}
				} catch(NoConverterAvailableException n) {
					logger.warn(n);
				}
			}
		});

		buttonSave.set(button);
	}

	private void createButtonSettings(Composite parent) {

		createSettingsButton(parent, Arrays.asList( //
				PreferencePageSystem.class, //
				PreferencePageMergePeaks.class, //
				PreferencePageScans.class, //
				PreferencePeakScanPageList.class, //
				PreferencePageUnknownTarget.class //
		), _ -> applySettings());
	}

	private void updateLabel() {

		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() == null) {
			toolbarInfoTop.get().setText(ChromatogramDataSupport.getChromatogramLabel(null));
			toolbarInfoBottom.get().setText("");
		} else {
			/*
			 * Display the selected and total amount of peaks/scans
			 */
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String chromatogramLabel = ChromatogramDataSupport.getChromatogramLabel(chromatogram);

			int peaks = 0;
			int scans = 0;
			for(TableItem tableItem : tableViewer.get().getTable().getItems()) {
				Object object = tableItem.getData();
				if(object instanceof IPeak) {
					peaks++;
				} else if(object instanceof IScan) {
					scans++;
				}
			}

			StringBuilder builder = new StringBuilder();
			builder.append(DESCRIPTION_PEAKS);
			builder.append(" ");
			builder.append(peaks);
			builder.append(" / ");
			builder.append(chromatogram.getPeaks().size());
			builder.append(" | ");
			builder.append(DESCRIPTION_SCANS);
			builder.append(" ");
			builder.append(scans);
			builder.append(" / ");
			builder.append(ChromatogramDataSupport.getIdentifiedScans(chromatogram).size());

			toolbarInfoTop.get().setText(chromatogramLabel);
			toolbarInfoBottom.get().setText(builder.toString());
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
			if(object instanceof IPeak peak) {
				peakList.add(peak);
			}
		}
		return peakList;
	}

	private List<IPeak> getPeakList(Table table, int[] indices) {

		List<IPeak> peakList = new ArrayList<>();
		for(int index : indices) {
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IPeak peak) {
				peakList.add(peak);
			}
		}
		return peakList;
	}

	List<IChromatogramPeakMSD> getSelectedPeaksMSD() {

		Table table = tableViewer.get().getTable();
		int[] indices = table.getSelectionIndices();
		List<IChromatogramPeakMSD> peaksMSD;

		if(indices.length >= 2) {
			peaksMSD = new ArrayList<>();
			for(IPeak peak : getPeakList(table, indices)) {
				if(peak instanceof IChromatogramPeakMSD chromatogramPeakMSD) {
					peaksMSD.add(chromatogramPeakMSD);
				}
			}
		} else {
			peaksMSD = Collections.emptyList();
		}

		return peaksMSD;
	}

	private List<IScan> getScanList(Table table) {

		List<IScan> scanList = new ArrayList<>();
		for(TableItem tableItem : table.getItems()) {
			Object object = tableItem.getData();
			if(object instanceof IScan scan) {
				scanList.add(scan);
			}
		}
		return scanList;
	}

	private List<IScan> getScanList(Table table, int[] indices) {

		List<IScan> scanList = new ArrayList<>();
		for(int index : indices) {
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IScan scan) {
				scanList.add(scan);
			}
		}
		return scanList;
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

	private void updateFromPreferences() {

		showPeaks = PreferenceSupplier.isShowPeaksInList();
		showPeaksInRange = PreferenceSupplier.isShowPeaksInSelectedRange();
		showScans = PreferenceSupplier.isShowScansInList();
		showScansInRange = PreferenceSupplier.isShowScansInSelectedRange();
		moveRetentionTimeOnPeakSelection = PreferenceSupplier.isMoveRetentionTimeOnPeakSelection();
		showPeakProfilesSelectionAll = PreferenceSupplier.isShowPeakProfilesSelectionAll();
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

				enableToolbar(toolbarInfoTop, buttonToolbarInfo.get(), IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, visible);
				enableToolbar(toolbarInfoBottom, buttonToolbarInfo.get(), IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, visible);
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
