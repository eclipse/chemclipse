/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - content-proposal support
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.targets.ITarget;
import org.eclipse.chemclipse.model.updates.ITargetUpdateListener;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.help.HelpContext;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageLists;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTargets;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTargetsList;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.targets.ComboTarget;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swtchart.extensions.core.IKeyboardSupport;
import org.eclipse.ui.PlatformUI;

public class ExtendedTargetsUI extends Composite implements IExtendedPartUI {

	private static final String MENU_CATEGORY_TARGETS = "Targets";
	private static final int INDEX_CHROMATOGRAM = 1;
	//
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private Button buttonToolbarSearch;
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private Button buttonToolbarEdit;
	private AtomicReference<Composite> toolbarEdit = new AtomicReference<>();
	//
	private ComboTarget comboTarget;
	private Button buttonAddTarget;
	private Button buttonDeleteTarget;
	private Button buttonDeleteTargets;
	//
	private Button buttonTableEdit;
	private AtomicReference<TabFolder> tabControl = new AtomicReference<>();
	private AtomicReference<TargetsListUI> targetListOther = new AtomicReference<>();
	private AtomicReference<TargetsListUI> targetListChromatogram = new AtomicReference<>();
	//
	private Object objectCacheChromatogram = null; // IChromatogram
	private Object objectCacheOther = null; // IScan, IPeak, ITargetSupplier
	//
	private ScanDataSupport scanDataSupport = new ScanDataSupport();
	private PeakDataSupport peakDataSupport = new PeakDataSupport();
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private TargetIdentifierUI targetIdentifierUI; // show database link

	@Inject
	public ExtendedTargetsUI(Composite parent, int style) {

		super(parent, style);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, HelpContext.TARGETS);
		createControl();
	}

	@Override
	public boolean setFocus() {

		updateOnFocus();
		return true;
	}

	public void clear() {

		objectCacheChromatogram = null;
		objectCacheOther = null;
		updateTargets(getDisplay());
	}

	public void updateChromatogram(IChromatogramSelection<?, ?> chromatogramSelection) {

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
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			if(objectCacheChromatogram != chromatogram) {
				objectCacheChromatogram = chromatogram;
			}
			//
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
			//
			if(!isChromatogramActive()) {
				updateTargets(getDisplay());
			}
		}
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createToolbarSearch(this);
		createToolbarEdit(this);
		createTableSection(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarSearch, buttonToolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH, false);
		enableToolbar(toolbarEdit, buttonToolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT, false);
		//
		enableEdit(Arrays.asList(targetListOther, targetListChromatogram), buttonTableEdit, IMAGE_EDIT_ENTRY, false);
		applySettings();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		targetIdentifierUI = createTargetIdentifierUI(composite);
		buttonToolbarSearch = createButtonToggleToolbar(composite, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH);
		buttonToolbarEdit = createButtonToggleToolbar(composite, toolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT);
		buttonDeleteTargets = createButtonDeleteAll(composite);
		createButtonHelp(composite);
		createButtonSettings(composite);
	}

	private Button createButtonDeleteAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Delete all targets");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(openQuestion(e.display.getActiveShell(), "Do you want to delete all targets?")) {
					deleteAllTargets();
					updateTargets(e.display);
					UpdateNotifierUI.update(e.display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "Targets have been deleted.");
				}
			}
		});
		//
		return button;
	}

	private TargetIdentifierUI createTargetIdentifierUI(Composite parent) {

		TargetIdentifierUI targetIdentifierUI = new TargetIdentifierUI(parent, SWT.NONE);
		return targetIdentifierUI;
	}

	private boolean openQuestion(Shell shell, String text) {

		return MessageDialog.openQuestion(shell, MENU_CATEGORY_TARGETS, text);
	}

	private void createButtonSettings(Composite parent) {

		createSettingsButton(parent, Arrays.asList( //
				PreferencePageTargets.class, //
				PreferencePageTargetsList.class, //
				PreferencePageSystem.class, //
				PreferencePageLists.class //
		), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				targetListOther.get().setSearchText(searchText, caseSensitive);
				targetListChromatogram.get().setSearchText(searchText, caseSensitive);
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private void createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(4, false));
		//
		comboTarget = createComboTarget(composite);
		buttonAddTarget = createButtonAdd(composite);
		buttonDeleteTarget = createButtonDelete(composite);
		buttonTableEdit = createButtonToggleEditTable(composite, Arrays.asList(targetListOther, targetListChromatogram), IMAGE_EDIT_ENTRY);
		//
		toolbarEdit.set(composite);
	}

	private ComboTarget createComboTarget(Composite parent) {

		ComboTarget comboTarget = new ComboTarget(parent, SWT.NONE);
		comboTarget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboTarget.setTargetUpdateListener(new ITargetUpdateListener() {

			@Override
			public void update(IIdentificationTarget identificationTarget) {

				if(identificationTarget != null) {
					setTarget(comboTarget.getDisplay(), identificationTarget);
				}
			}
		});
		return comboTarget;
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add the target.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IIdentificationTarget identificationTarget = comboTarget.createTarget();
				if(identificationTarget != null) {
					setTarget(e.display, identificationTarget);
				}
			}
		});
		return button;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete the selected targets.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteTargets(e.display);
			}
		});
		return button;
	}

	private void createTableSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, true));
		//
		TabFolder tabFolder = new TabFolder(composite, SWT.BOTTOM);
		tabFolder.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateOnFocus();
			}
		});
		//
		createTargetTableTabItem(tabFolder, targetListOther);
		createTargetTableTabItem(tabFolder, targetListChromatogram);
		//
		tabControl.set(tabFolder);
	}

	private void createTargetTableTabItem(TabFolder tabFolder, AtomicReference<TargetsListUI> listControl) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(listControl == targetListOther ? "Auto" : "Chromatogram");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		tabItem.setControl(composite);
		//
		createTargetTable(composite, listControl);
	}

	private void createTargetTable(Composite parent, AtomicReference<TargetsListUI> listControl) {

		TargetsListUI targetListUI = new TargetsListUI(parent, SWT.BORDER);
		targetListUI.setEditingSupport();
		Table table = targetListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateWidgets();
			}
		});
		//
		targetListUI.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				propagateTarget(e.display);
			}
		});
		/*
		 * Set/Save the column order.
		 */
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String preferenceColumnOrder = PreferenceConstants.P_COLUMN_ORDER_TARGET_LIST;
		String preferenceColumnWidth = PreferenceConstants.P_COLUMN_WIDTH_TARGET_LIST;
		targetListUI.setColumnMoveWidthSupport(preferenceStore, preferenceColumnOrder, preferenceColumnWidth);
		//
		targetListUI.setComparator(preferenceStore.getBoolean(PreferenceConstants.P_TARGETS_TABLE_SORTABLE));
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
		//
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

				deleteTargets(display);
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

		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					/*
					 * DEL
					 */
					deleteTargets(display);
				} else if(e.keyCode == IKeyboardSupport.KEY_CODE_LC_I && (e.stateMask & SWT.MOD1) == SWT.MOD1) {
					if((e.stateMask & SWT.MOD3) == SWT.MOD3) {
						/*
						 * CTRL + ALT + I
						 */
						verifyTargets(false, display);
					} else {
						/*
						 * CTRL + I
						 */
						verifyTargets(true, display);
					}
				} else {
					propagateTarget(display);
				}
			}
		});
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

		comboTarget.updateContentProposals();
		//
		targetListOther.get().setComparator(preferenceStore.getBoolean(PreferenceConstants.P_TARGETS_TABLE_SORTABLE));
		targetListChromatogram.get().setComparator(preferenceStore.getBoolean(PreferenceConstants.P_TARGETS_TABLE_SORTABLE));
	}

	private void updateInput(float retentionIndex) {

		AtomicReference<TargetsListUI> targetList = getActiveTargetList();
		Object object = getObject();
		//
		if(object instanceof ITargetSupplier targetSupplier) {
			List<IIdentificationTarget> identificationTargets = new ArrayList<>(targetSupplier.getTargets());
			IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(SortOrder.DESC, retentionIndex);
			Collections.sort(identificationTargets, identificationTargetComparator);
			targetList.get().setInput(identificationTargets);
		} else {
			targetList.get().setInput(null);
			enableToolbar(toolbarEdit, buttonToolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT, false);
		}
		//
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

		Object object = getObject();
		//
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
		//
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
		//
		if(object instanceof ITargetSupplier) {
			String dataDescription;
			if(object instanceof IChromatogram) {
				dataDescription = ChromatogramDataSupport.getChromatogramLabel((IChromatogram<?>)object);
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
		//
		boolean enabled = object != null;
		comboTarget.setEnabled(enabled);
		buttonAddTarget.setEnabled(enabled);
		AtomicReference<TargetsListUI> targetList = getActiveTargetList();
		//
		if(object instanceof ITargetSupplier targetSupplier) {
			buttonDeleteTarget.setEnabled(targetList.get().getTable().getSelectionIndex() >= 0);
			buttonDeleteTargets.setEnabled(!targetSupplier.getTargets().isEmpty());
		} else {
			buttonDeleteTarget.setEnabled(false);
			buttonDeleteTargets.setEnabled(false);
		}
	}

	private void deleteTargets(Display display) {

		if(openQuestion(display.getActiveShell(), "Would you like to delete the selected targets?")) {
			/*
			 * Delete Target
			 */
			AtomicReference<TargetsListUI> targetList = getActiveTargetList();
			Iterator<?> iterator = targetList.get().getStructuredSelection().iterator();
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof ITarget target) {
					deleteTarget(target);
				}
			}
			updateTargets(display);
			UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "Targets have been deleted.");
		}
	}

	private void deleteTarget(ITarget target) {

		Object object = getObject();
		//
		if(object instanceof ITargetSupplier targetSupplier) {
			targetSupplier.getTargets().remove(target);
			IChromatogram<?> chromatogram = ((IChromatogram<?>)objectCacheChromatogram);
			if(chromatogram != null) {
				chromatogram.setDirty(true);
			}
		}
		/*
		 * Don't do an table update here, cause this method could be called several times in a loop.
		 */
	}

	private void deleteAllTargets() {

		Object object = getObject();
		//
		if(object instanceof ITargetSupplier targetSupplier) {
			targetSupplier.getTargets().clear();
			IChromatogram<?> chromatogram = ((IChromatogram<?>)objectCacheChromatogram);
			if(chromatogram != null) {
				chromatogram.setDirty(true);
			}
		}
	}

	private void setTarget(Display display, IIdentificationTarget identificationTarget) {

		Object object = getObject();
		//
		if(object instanceof ITargetSupplier targetSupplier) {
			targetSupplier.getTargets().add(identificationTarget);
			IChromatogram<?> chromatogram = ((IChromatogram<?>)objectCacheChromatogram);
			if(chromatogram != null) {
				chromatogram.setDirty(true);
			}
		}
		//
		comboTarget.setText("");
		updateTargets(display);
		UpdateNotifierUI.update(display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "Target has been set.");
	}

	/**
	 * May return null.
	 * 
	 * @return IScanMSD
	 */
	private IScanMSD getMassSpectrum() {

		Object object = getObject();
		//
		if(object instanceof IScanMSD scanMSD) {
			return scanMSD;
		} else if(object instanceof IPeakMSD peakMSD) {
			return peakMSD.getExtractedMassSpectrum();
		} else {
			return null;
		}
	}

	private void updateTargets(Display display) {

		updateRetentionInfo();
		updateInput(getRetentionIndex());
		updateWidgets();
		//
		AtomicReference<TargetsListUI> targetList = getActiveTargetList();
		TargetsListUI targetListUI = targetList.get();
		targetListUI.sortTable();
		Table table = targetListUI.getTable();
		if(table.getItemCount() > 0) {
			table.setSelection(0);
			boolean propagateTargetOnUpdate = preferenceStore.getBoolean(PreferenceConstants.P_PROPAGATE_TARGET_ON_UPDATE);
			if(propagateTargetOnUpdate) {
				propagateTarget(display);
			}
		}
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
				UpdateNotifierUI.update(display, identificationTarget);
				/*
				 * Then update the weblink button.
				 */
				targetIdentifierUI.setInput(identificationTarget.getLibraryInformation());
			}
		}
	}

	private AtomicReference<TargetsListUI> getActiveTargetList() {

		return isChromatogramActive() ? targetListChromatogram : targetListOther;
	}

	@SuppressWarnings("rawtypes")
	private void updateOnFocus() {

		DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
		List<Object> objects = dataUpdateSupport.getUpdates(getLastTopic(dataUpdateSupport.getTopics()));
		//
		if(!objects.isEmpty()) {
			Object object = objects.get(0);
			if(object instanceof IChromatogramSelection chromatogramSelection) {
				updateChromatogram(chromatogramSelection);
			} else {
				updateOther(object);
			}
		}
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
		//
		return "";
	}

	private Object getObject() {

		return isChromatogramActive() ? objectCacheChromatogram : objectCacheOther;
	}

	private boolean isChromatogramActive() {

		return tabControl.get().getSelectionIndex() == INDEX_CHROMATOGRAM;
	}
}
