/*******************************************************************************
 * Copyright (c) 2017, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - content-proposal support
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.app.undo.UndoContextFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSystem;
import org.eclipse.chemclipse.ux.extension.ui.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.help.HelpContext;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.operations.DeleteTargetsOperation;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTargets;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTargetsChromatography;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTargetsList;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageUnknownTarget;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.ChromatogramUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.IdentificationTargetSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakSelectionSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.targets.ComboTarget;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.e4.ui.services.EContextService;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

import jakarta.inject.Inject;

public class ExtendedTargetsUI extends Composite implements IExtendedPartUI {

	private static final String MENU_CATEGORY_TARGETS = "Targets";
	private static final int INDEX_CHROMATOGRAM = 1;

	private static final String KEY_CLASS_PREFIX = "org.eclipse.chemclipse.ux.extension.xxd.ui.TargetsList.";
	private static final String COMMAND_PEAK_SELECTION_NEXT = "org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedChromatogramUI.PeakSelectionNext";
	private static final String COMMAND_PEAK_SELECTION_PREVIOUS = "org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedChromatogramUI.PeakSelectionPrevious";

	private EContextService contextService = Activator.getDefault().getApplication().getContext().get(EContextService.class);
	private IBindingService bindingService = PlatformUI.getWorkbench().getService(IBindingService.class);

	private AtomicReference<Button> buttonToolbarInfo = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarSearch = new AtomicReference<>();
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarEdit = new AtomicReference<>();
	private AtomicReference<Composite> toolbarEdit = new AtomicReference<>();
	private AtomicReference<ComboTarget> comboTargetControl = new AtomicReference<>();
	private AtomicReference<Button> buttonAddTargetControl = new AtomicReference<>();
	private AtomicReference<Button> buttonDeleteTargetControl = new AtomicReference<>();
	private AtomicReference<Button> buttonDeleteTargetsControl = new AtomicReference<>();
	private AtomicReference<Button> buttonTableEditControl = new AtomicReference<>();
	private AtomicReference<TabFolder> tabControl = new AtomicReference<>();
	private AtomicReference<TargetsListUI> targetListOther = new AtomicReference<>();
	private AtomicReference<TargetsListUI> targetListChromatogram = new AtomicReference<>();
	private AtomicReference<TargetWebIdentifierUI> targetWebIdentifierControl = new AtomicReference<>();
	private AtomicReference<ScanIdentifierUI> scanIdentifierControl = new AtomicReference<>();

	private Object objectCacheChromatogram = null; // IChromatogram
	private Object objectCacheOther = null; // IScan, IPeak, ITargetSupplier
	private IChromatogramSelection chromatogramSelection;

	private ScanDataSupport scanDataSupport = new ScanDataSupport();
	private PeakDataSupport peakDataSupport = new PeakDataSupport();

	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private IUpdateListener updateListener = null;

	@Inject
	public ExtendedTargetsUI(Composite parent, int style) {

		super(parent, style);
		createControl();
		contextService.activateContext("org.eclipse.chemclipse.ux.extension.xxd.ui.TargetsList");
		contextService.activateContext("org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedChromatogramUI"); // reuse the peak selection hotkeys
		createPeakSelectionFilter();
	}

	// not attached to the table to account for unidentified peaks
	private void createPeakSelectionFilter() {

		Display display = getDisplay();
		Listener listener = event -> {

			if(event.widget instanceof Control control && isChildControl(control)) {
				KeyEvent keyEvent = new KeyEvent(event);
				if(matchesCommand(COMMAND_PEAK_SELECTION_NEXT, keyEvent)) {
					PeakSelectionSupport.selectNeighborPeak(display, chromatogramSelection, true);
				} else if(matchesCommand(COMMAND_PEAK_SELECTION_PREVIOUS, keyEvent)) {
					PeakSelectionSupport.selectNeighborPeak(display, chromatogramSelection, false);
				}
			}
		};

		display.addFilter(SWT.KeyUp, listener);
		addDisposeListener(_ -> display.removeFilter(SWT.KeyUp, listener));
	}

	private boolean isChildControl(Control control) {

		Control parent = control;
		while(parent != null) {
			if(parent == this) {
				return true;
			}
			parent = parent.getParent();
		}

		return false;
	}

	@Override
	public boolean setFocus() {

		updateOnFocus();
		return true;
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void clear() {

		objectCacheChromatogram = null;
		objectCacheOther = null;
		updateTargets(getDisplay());
	}

	public void updatePart() {

		targetListOther.get().refresh();
		targetListChromatogram.get().refresh();
	}

	public void updateChromatogram(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
		if(chromatogramSelection != null) {
			/*
			 * Update Peak/Scan
			 */
			if(objectCacheOther instanceof IPeak) {
				updateOther(chromatogramSelection.getSelectedPeak());
			} else if(objectCacheOther instanceof IScan) {
				updateOther(chromatogramSelection.getSelectedIdentifiedScan());
			}
			/*
			 * Update Chromatogram
			 */
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(objectCacheChromatogram != chromatogram) {
				objectCacheChromatogram = chromatogram;
			}

			if(isChromatogramActive()) {
				updateTargets(getDisplay());
			}
		}
	}

	public void updateOther(Object object) {

		if(object != null) {
			/*
			 * Update Cache
			 */
			if(objectCacheOther != object) {
				this.objectCacheOther = object;
			}

			if(!isChromatogramActive()) {
				updateTargets(getDisplay());
			}
		}
	}

	public void updateTarget(IIdentificationTarget identificationTarget) {

		if(identificationTarget == null) {
			return;
		}

		TargetsListUI targetListUI = getActiveTargetList().get();
		Table table = targetListUI.getTable();
		/*
		 * Skip the echo of the own propagation, otherwise the refresh
		 * would cancel a cell editor that has just been opened.
		 */
		int selectionIndex = table.getSelectionIndex();
		if(selectionIndex >= 0 && table.getItem(selectionIndex).getData() == identificationTarget) {
			return;
		}

		updateInput(getRetentionIndex());
		targetListUI.sortTable(); // Sort again as targets verification was updated.

		for(int index = 0; index < table.getItemCount(); index++) {
			if(table.getItem(index).getData() == identificationTarget) {
				table.setSelection(index);
				table.showSelection();
				targetWebIdentifierControl.get().setInput(identificationTarget.getLibraryInformation());
				break;
			}
		}

		updateWidgets();
		// The target is not propagated again to avoid an update loop.
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);

		createToolbarMain(this);
		createToolbarInfo(this);
		createToolbarSearch(this);
		createToolbarEdit(this);
		createTableSection(this);

		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo.get(), IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarSearch, buttonToolbarSearch.get(), IMAGE_SEARCH, TOOLTIP_SEARCH, false);
		enableToolbar(toolbarEdit, buttonToolbarEdit.get(), IMAGE_EDIT, TOOLTIP_EDIT, false);

		setHelp(this, HelpContext.TARGETS);
		enableEdit(Arrays.asList(targetListOther, targetListChromatogram), buttonTableEditControl.get(), IMAGE_EDIT_ENTRY, false);
		applySettings();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(8, false));

		createButtonToggleToolbarInfo(composite);
		createScanIdentifierUI(composite);
		createTargetIdentifierUI(composite);
		createButtonToggleToolbarSearch(composite);
		createButtonToggleToolbarEdit(composite);
		createButtonDeleteAll(composite);
		createButtonHelp(composite, HelpContext.TARGETS);
		createButtonSettings(composite);
	}

	private void createButtonToggleToolbarInfo(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		buttonToolbarInfo.set(button);
	}

	private void createButtonToggleToolbarSearch(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH);
		buttonToolbarSearch.set(button);
	}

	private void createButtonToggleToolbarEdit(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT);
		buttonToolbarEdit.set(button);
	}

	private void createButtonDeleteAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Delete all targets");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteTargetsAll(e.display);
			}
		});

		buttonDeleteTargetsControl.set(button);
	}

	private void createTargetIdentifierUI(Composite parent) {

		targetWebIdentifierControl.set(new TargetWebIdentifierUI(parent, SWT.NONE));
	}

	private void createScanIdentifierUI(Composite parent) {

		ScanIdentifierUI scanIdentifierUI = new ScanIdentifierUI(parent, SWT.NONE);
		scanIdentifierUI.setUpdateListener(display -> {

			IScan scan = getScan();
			if(scan != null) {
				updateTargetsModify(display);
				UpdateNotifierUI.update(display, scan);
				UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "Scan Chart identification has been performed.");
				ChromatogramUpdateSupport.fireUpdateChromatogramSelection(display, scan);
			}
		});

		scanIdentifierControl.set(scanIdentifierUI);
	}

	private void createButtonSettings(Composite parent) {

		createSettingsButton(parent, Arrays.asList( //
				PreferencePageTargets.class, //
				PreferencePageTargetsList.class, //
				PreferencePageTargetsChromatography.class, //
				PreferencePageSystem.class, //
				PreferencePageUnknownTarget.class //
		), _ -> applySettings());
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		toolbarInfo.set(informationUI);
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener((searchText, caseSensitive) -> {

			targetListOther.get().setSearchText(searchText, caseSensitive);
			targetListChromatogram.get().setSearchText(searchText, caseSensitive);
		});

		toolbarSearch.set(searchSupportUI);
	}

	private void createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(4, false));

		createComboTarget(composite);
		createButtonAdd(composite);
		createButtonDelete(composite);
		createButtonToggleEditTable(composite);

		toolbarEdit.set(composite);
	}

	private void createButtonToggleEditTable(Composite parent) {

		Button button = createButtonToggleEditTable(parent, Arrays.asList(targetListOther, targetListChromatogram), IMAGE_EDIT_ENTRY);
		buttonTableEditControl.set(button);
	}

	private void createComboTarget(Composite parent) {

		ComboTarget comboTarget = new ComboTarget(parent, SWT.NONE);
		comboTarget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboTarget.setTargetUpdateListener(identificationTarget -> {

			if(identificationTarget != null) {
				addTarget(comboTarget.getDisplay(), identificationTarget);
			}
		});

		comboTargetControl.set(comboTarget);
	}

	private void createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add the target.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IIdentificationTarget identificationTarget = comboTargetControl.get().createTarget();
				if(identificationTarget != null) {
					addTarget(e.display, identificationTarget);
				}
			}
		});

		buttonAddTargetControl.set(button);
	}

	private void createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Delete the selected targets.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteTargetsSelected(e.display);
			}
		});

		buttonDeleteTargetControl.set(button);
	}

	private void createTableSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, true));

		TabFolder tabFolder = new TabFolder(composite, SWT.BOTTOM);
		tabFolder.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateOnFocus();
			}
		});

		createTargetTableTabItem(tabFolder, targetListOther);
		createTargetTableTabItem(tabFolder, targetListChromatogram);

		tabControl.set(tabFolder);
	}

	private void createTargetTableTabItem(TabFolder tabFolder, AtomicReference<TargetsListUI> listControl) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(listControl == targetListOther ? "Auto" : "Chromatogram");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		tabItem.setControl(composite);

		createTargetTable(composite, listControl);
	}

	private void createTargetTable(Composite parent, AtomicReference<TargetsListUI> listControl) {

		TargetsListUI targetListUI = new TargetsListUI(parent, SWT.BORDER);
		targetListUI.setEditingSupport();
		Table table = targetListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateWidgets();
			}
		});

		targetListUI.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				propagateTarget(e.display);
			}
		});

		targetListUI.setUpdateListener(this::fireUpdate);
		/*
		 * Sort the table
		 */
		targetListUI.setComparator(preferenceStore.getBoolean(PreferenceSupplier.P_TARGETS_TABLE_SORTABLE));
		/*
		 * Add the delete targets support.
		 */
		Display display = targetListUI.getTable().getDisplay();
		ITableSettings tableSettings = targetListUI.getTableSettings();
		addDeleteMenuEntry(display, tableSettings);
		addVerifyTargetsMenuEntry(display, tableSettings);
		addUnverifyTargetsMenuEntry(display, tableSettings);
		addKeyEventProcessors(display, tableSettings);
		targetListUI.applySettings(tableSettings);

		listControl.set(targetListUI);
	}

	private void addDeleteMenuEntry(Display display, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Targets";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY_TARGETS;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteTargetsSelected(display);
			}
		});
	}

	private void addVerifyTargetsMenuEntry(Display display, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Verify Targets Check";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY_TARGETS;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				verifyTargets(true, display);
			}
		});
	}

	private void addUnverifyTargetsMenuEntry(Display display, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Verify Targets Uncheck";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY_TARGETS;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				verifyTargets(false, display);
			}
		});
	}

	private void addKeyEventProcessors(Display display, ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor((_, e) -> {

			if(e.keyCode == SWT.DEL) {
				deleteTargetsSelected(display);
			} else if(matchesKeyPress("VerifyTarget", e)) {
				verifyTargets(true, display);
			} else if(matchesKeyPress("UnverifyTarget", e)) {
				verifyTargets(false, display);
			} else if(matchesKeyPress("DeleteTargets", e)) {
				deleteTargetsAll(display);
			} else if(matchesKeyPress("Unknown", e)) {
				addTargetUnknown(e.display);
			} else if(matchesKeyPress("Query", e)) {
				scanIdentifierControl.get().runIdentification();
			} else if(!matchesPeakSelection(e)) {
				propagateTarget(display);
			}
		});
	}

	private boolean matchesPeakSelection(KeyEvent e) {

		return matchesCommand(COMMAND_PEAK_SELECTION_NEXT, e) || matchesCommand(COMMAND_PEAK_SELECTION_PREVIOUS, e);
	}

	private boolean matchesKeyPress(String commandSuffix, KeyEvent e) {

		return matchesCommand(KEY_CLASS_PREFIX + commandSuffix, e);
	}

	private boolean matchesCommand(String commandId, KeyEvent e) {

		TriggerSequence triggerSequence = bindingService.getBestActiveBindingFor(commandId);
		if(triggerSequence instanceof KeySequence keySequence) {
			KeyStroke[] bindingStrokes = keySequence.getKeyStrokes();
			int accelerator = SWTKeySupport.convertEventToUnmodifiedAccelerator(e);
			KeyStroke eventStroke = SWTKeySupport.convertAcceleratorToKeyStroke(accelerator);
			return bindingStrokes.length > 0 && bindingStrokes[0].equals(eventStroke);
		}
		return false;
	}

	private void verifyTargets(boolean verified, Display display) {

		AtomicReference<TargetsListUI> targetList = getActiveTargetList();
		Iterator<?> iterator = targetList.get().getStructuredSelection().iterator();
		while(iterator.hasNext()) {
			Object object = iterator.next();
			if(object instanceof IIdentificationTarget identificationTarget) {
				identificationTarget.setVerified(verified);
				UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "Target has been manually verified.");
			}
		}
		updateTargets(display);
	}

	private void applySettings() {

		comboTargetControl.get().updateContentProposals();

		targetListOther.get().setComparator(preferenceStore.getBoolean(PreferenceSupplier.P_TARGETS_TABLE_SORTABLE));
		targetListChromatogram.get().setComparator(preferenceStore.getBoolean(PreferenceSupplier.P_TARGETS_TABLE_SORTABLE));
	}

	private void updateInput(float retentionIndex) {

		AtomicReference<TargetsListUI> targetList = getActiveTargetList();
		if(targetList.get() == null) {
			return;
		}

		Object object = getObject();
		if(object instanceof ITargetSupplier targetSupplier) {
			List<IIdentificationTarget> identificationTargets = IIdentificationTarget.getTargetsSorted(targetSupplier.getTargets(), retentionIndex);
			targetList.get().setInput(identificationTargets);
		} else {
			targetList.get().setInput(null);
			enableToolbar(toolbarEdit, buttonToolbarEdit.get(), IMAGE_EDIT, TOOLTIP_EDIT, false);
		}

		updateLabelInfo();
	}

	private void updateRetentionInfo() {

		if(isChromatogramActive()) {
			updateRetentionInfo(targetListChromatogram);
		} else {
			updateRetentionInfo(targetListOther);
		}
	}

	private void updateRetentionInfo(AtomicReference<TargetsListUI> targetList) {

		if(targetList.get() == null) {
			return;
		}

		Object object = getObject();
		if(object instanceof ITargetSupplier) {
			if(object instanceof IChromatogram) {
				targetList.get().updateSourceRange(null, null);
			} else if(object instanceof IPeak peak) {
				IScan scan = peak.getPeakModel().getPeakMaximum();
				targetList.get().updateSourceRange(scan.getRetentionTime(), scan.getRetentionIndex());
			} else if(object instanceof IScan scan) {
				targetList.get().updateSourceRange(scan.getRetentionTime(), scan.getRetentionIndex());
			} else {
				targetList.get().updateSourceRange(null, null);
			}
		} else {
			targetList.get().updateSourceRange(null, null);
		}
	}

	private float getRetentionIndex() {

		Object object = getObject();

		if(object instanceof IPeak peak) {
			IScan scan = peak.getPeakModel().getPeakMaximum();
			return scan.getRetentionIndex();
		} else if(object instanceof IScan scan) {
			return scan.getRetentionIndex();
		} else {
			return 0.0f;
		}
	}

	private void updateLabelInfo() {

		Object object = getObject();

		if(object instanceof ITargetSupplier) {
			String dataDescription;
			if(object instanceof IChromatogram chromatogram) {
				dataDescription = ChromatogramDataSupport.getChromatogramLabel(chromatogram);
			} else if(object instanceof IPeak peak) {
				dataDescription = peakDataSupport.getPeakLabel(peak);
			} else if(object instanceof IScan scan) {
				dataDescription = scanDataSupport.getScanLabel(scan);
			} else {
				dataDescription = "Target Supplier";
			}
			toolbarInfo.get().setText(dataDescription);
		} else {
			toolbarInfo.get().setText("No target data has been selected yet.");
		}
	}

	private void updateWidgets() {

		Object object = getObject();

		boolean enabled = object != null;
		comboTargetControl.get().setEnabled(enabled);
		buttonAddTargetControl.get().setEnabled(enabled);
		AtomicReference<TargetsListUI> targetList = getActiveTargetList();

		if(object instanceof ITargetSupplier targetSupplier) {
			buttonDeleteTargetControl.get().setEnabled(targetList.get().getTable().getSelectionIndex() >= 0);
			buttonDeleteTargetsControl.get().setEnabled(!targetSupplier.getTargets().isEmpty());
		} else {
			buttonDeleteTargetControl.get().setEnabled(false);
			buttonDeleteTargetsControl.get().setEnabled(false);
		}
	}

	@SuppressWarnings("unchecked")
	private void deleteTargetsSelected(Display display) {

		AtomicReference<TargetsListUI> targetList = getActiveTargetList();
		List<IIdentificationTarget> selection = targetList.get().getStructuredSelection().toList();
		if(getObject() instanceof ITargetSupplier targetSupplier) {
			DeleteTargetsOperation deleteTargetsOperation = new DeleteTargetsOperation(getDisplay(), chromatogramSelection, targetSupplier, selection);
			deleteTargetsOperation.addContext(UndoContextFactory.getUndoContext());
			try {
				getOperationHistory().execute(deleteTargetsOperation, null, null);
			} catch(ExecutionException e) {
				logger.warn(e);
			}
			updateTargetsModify(display);
		}
	}

	private void deleteTargetsAll(Display display) {

		if(chromatogramSelection != null && getObject() instanceof ITargetSupplier targetSupplier) {
			Set<IIdentificationTarget> targetsToDelete = targetSupplier.getTargets();
			DeleteTargetsOperation deleteTargetsOperation = new DeleteTargetsOperation(getDisplay(), chromatogramSelection, targetSupplier, targetsToDelete);
			deleteTargetsOperation.addContext(UndoContextFactory.getUndoContext());
			try {
				getOperationHistory().execute(deleteTargetsOperation, null, null);
			} catch(ExecutionException e) {
				logger.warn(e);
			}
			updateTargetsModify(display);
			if(preferenceStore.getBoolean(PreferenceSupplier.P_ADD_UNKNOWN_AFTER_DELETE_TARGETS_ALL)) {
				addTargetUnknown(display);
			}
		}
	}

	private IOperationHistory getOperationHistory() {

		return OperationHistoryFactory.getOperationHistory();
	}

	private void updateTargetsModify(Display display) {

		setChromatogramDirty();
		updateTargets(display);
		UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, ExtensionMessages.targetsDeleted);
	}

	private void addTarget(Display display, IIdentificationTarget identificationTarget) {

		if(getObject() instanceof ITargetSupplier targetSupplier) {
			targetSupplier.getTargets().add(identificationTarget);
			setChromatogramDirty();
			comboTargetControl.get().setText("");
			updateTargets(display);
			UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "Target has been set.");
		}
	}

	private void addTargetUnknown(Display display) {

		IIdentificationTarget identificationTarget = IdentificationTargetSupport.getTargetUnknown(getScan());
		addTarget(display, identificationTarget);
	}

	private void setChromatogramDirty() {

		if(objectCacheChromatogram instanceof IChromatogram chromatogram) {
			chromatogram.setDirty(true);
		}
	}

	private void updateTargets(Display display) {

		updateRetentionInfo();
		updateInput(getRetentionIndex());
		updateWidgets();

		AtomicReference<TargetsListUI> targetList = getActiveTargetList();
		TargetsListUI targetListUI = targetList.get();
		targetListUI.sortTable();
		Table table = targetListUI.getTable();
		if(table.getItemCount() > 0) {
			table.setSelection(0);
			boolean propagateTargetOnUpdate = preferenceStore.getBoolean(PreferenceSupplier.P_PROPAGATE_TARGET_ON_UPDATE);
			if(propagateTargetOnUpdate) {
				propagateTarget(display);
			}
		}

		enableScanIdentifierUI();
	}

	private void enableScanIdentifierUI() {

		IScan scan = getScan();
		scanIdentifierControl.get().setInput(scan);
		scanIdentifierControl.get().setEnabled(scan != null);
	}

	private void propagateTarget(Display display) {

		AtomicReference<TargetsListUI> targetList = getActiveTargetList();
		Table table = targetList.get().getTable();
		int index = table.getSelectionIndex();
		if(index >= 0) {
			TableItem tableItem = table.getItem(index);
			Object data = tableItem.getData();
			if(data instanceof IIdentificationTarget identificationTarget) {
				/*
				 * First update the mass spectrum.
				 */
				IScanMSD massSpectrum = getMassSpectrum();
				if(massSpectrum != null) {
					UpdateNotifierUI.update(display, massSpectrum, identificationTarget);
				}
				/*
				 * Update the target and weblink button.
				 */
				UpdateNotifierUI.update(display, identificationTarget);
				targetWebIdentifierControl.get().setInput(identificationTarget.getLibraryInformation());
			}
		}
	}

	private AtomicReference<TargetsListUI> getActiveTargetList() {

		return isChromatogramActive() ? targetListChromatogram : targetListOther;
	}

	private void updateOnFocus() {

		DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
		List<Object> objects = dataUpdateSupport.getUpdates(getLastTopic(dataUpdateSupport.getTopics()));

		if(!objects.isEmpty()) {
			Object object = objects.get(0);
			if(object instanceof IChromatogramSelection chromatogramSelection) {
				updateChromatogram(chromatogramSelection);
			} else {
				updateOther(object);
			}
		}

		enableScanIdentifierUI();
	}

	private String getLastTopic(List<String> topics) {

		Collections.reverse(topics);
		for(String topic : topics) {
			if(topic.equals(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION)) {
				return topic;
			}
			if(topic.equals(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION)) {
				return topic;
			}
			if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION)) {
				return topic;
			}
			if(topic.equals(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGETS_UPDATE_SELECTION)) {
				return topic;
			}
		}

		return "";
	}

	/**
	 * May return null.
	 *
	 * @return IScan
	 */
	private IScan getScan() {

		Object object = getObject();

		if(object instanceof IPeak peak) {
			IPeakModel peakModel = peak.getPeakModel();
			return peakModel.getPeakMaximum();
		} else if(object instanceof IScan scan) {
			return scan;
		}

		return null;
	}

	/**
	 * May return null.
	 *
	 * @return IScanMSD
	 */
	private IScanMSD getMassSpectrum() {

		Object object = getObject();
		if(object instanceof IScanMSD scanMSD) {
			return scanMSD;
		} else if(object instanceof IPeakMSD peakMSD) {
			return peakMSD.getExtractedMassSpectrum();
		} else if(object instanceof IChromatogram chromatogram) {
			if(chromatogram.getProcessDataMap().get(IChemClipseEvents.KEY_PROCESS_DATA_COMBINED_MASS_SPECTRUM) instanceof IScanMSD scanMSD) {
				return scanMSD;
			}
		}

		return null;
	}

	private Object getObject() {

		return isChromatogramActive() ? objectCacheChromatogram : objectCacheOther;
	}

	private boolean isChromatogramActive() {

		TabFolder tabFolder = tabControl.get();
		if(tabFolder == null) {
			return false;
		}
		return tabFolder.getSelectionIndex() == INDEX_CHROMATOGRAM;
	}

	private void fireUpdate() {

		if(updateListener != null) {
			updateListener.update();
		}
	}
}