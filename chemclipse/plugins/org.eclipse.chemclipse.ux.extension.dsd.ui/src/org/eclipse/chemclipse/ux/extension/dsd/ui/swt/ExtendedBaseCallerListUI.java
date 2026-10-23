/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.dsd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSystem;
import org.eclipse.chemclipse.ux.extension.dsd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.ui.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.IExtendedPartUI;
import org.eclipse.e4.ui.services.EContextService;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

public class ExtendedBaseCallerListUI extends Composite implements IExtendedPartUI {

	private static final String MENU_CATEGORY = "Base Caller";

	private static final String KEY_CLASS_PREFIX = "org.eclipse.chemclipse.ux.extension.xxd.ui.PeakScanList."; // reuse hotkeys

	private EContextService contextService = Activator.getDefault().getApplication().getContext().get(EContextService.class);
	private IBindingService bindingService = PlatformUI.getWorkbench().getService(IBindingService.class);

	private AtomicReference<Composite> toolbarMain = new AtomicReference<>();
	private AtomicReference<Button> buttonDelete = new AtomicReference<>();
	private AtomicReference<BaseCallerListUI> tableViewer = new AtomicReference<>();

	private IChromatogramSelection chromatogramSelection;

	private int currentModCount;

	public ExtendedBaseCallerListUI(Composite parent, int style) {

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
		return true;
	}

	public void updateChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		if(hasChanged(chromatogramSelection)) {
			this.chromatogramSelection = chromatogramSelection;
			updateChromatogramSelection();
		}
	}

	public void updateChromatogramSelection() {

		if(chromatogramSelection == null) {
			tableViewer.get().clear();
			currentModCount = -1;
		} else {
			currentModCount = chromatogramSelection.getChromatogram().getModCount();
			tableViewer.get().setInput(chromatogramSelection);
		}
	}

	public void updateSelection() {

		List<Object> selection = new ArrayList<>(2);
		if(chromatogramSelection != null) {
			IScan selectedScan = chromatogramSelection.getSelectedIdentifiedScan();
			if(selectedScan != null) {
				selection.add(selectedScan);
			}
		}
		tableViewer.get().setSelection(new StructuredSelection(selection), true);
	}

	public void refreshTableViewer() {

		tableViewer.get().refresh(true);
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));

		createToolbarMain(this);
		createBaseCallerTable(this);

		initialize();
	}

	private void initialize() {

		contextService.activateContext("org.eclipse.chemclipse.ux.extension.xxd.ui.TargetsList"); // reuse hotkeys
		buttonDelete.get().setEnabled(false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));

		createButtonDelete(composite);
		createButtonReset(composite);
		createButtonSettings(composite);

		toolbarMain.set(composite);
	}

	private void createBaseCallerTable(Composite parent) {

		BaseCallerListUI baseCallerListUI = new BaseCallerListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = baseCallerListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {

				propagateSelection();
			}
		});
		ITableSettings tableSettings = baseCallerListUI.getTableSettings();
		addDeleteMenuItem(tableSettings, "Delete Nucleobase");
		addKeyEventProcessors(tableSettings);
		baseCallerListUI.applySettings(tableSettings);

		tableViewer.set(baseCallerListUI);
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

				deleteIdentifications();
			}
		});
	}

	private void addKeyEventProcessors(ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor((_, e) -> {

			if(matchesKeyPress("DeleteTargets", e)) {
				deleteTargetsAll();
			} else if(e.keyCode == SWT.DEL) {
				deleteIdentifications();
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

	private void deleteIdentifications() {

		Iterator<?> iterator = tableViewer.get().getStructuredSelection().iterator();
		while(iterator.hasNext()) {
			Object object = iterator.next();
			if(object instanceof ITargetSupplier supplier) {
				supplier.getTargets().clear(); // TODO: undo
			}
		}
		updateChromatogramSelection();
	}

	private void deleteTargetsAll() {

		for(Object object : tableViewer.get().getStructuredSelection().toList()) {
			if(object instanceof ITargetSupplier supplier) {
				supplier.getTargets().clear(); // TODO: undo
			}
			tableViewer.get().refresh();
			chromatogramSelection.getChromatogram().setDirty(true);
			UpdateNotifierUI.update(getDisplay(), chromatogramSelection);
			UpdateNotifierUI.update(getDisplay(), IChemClipseEvents.TOPIC_IDENTIFICATION_TARGETS_UPDATE_SELECTION, "Nucleobase deleted.");
		}
	}

	private void propagateSelection() {

		IStructuredSelection selection = tableViewer.get().getStructuredSelection();
		buttonDelete.get().setEnabled(false);

		if(!selection.isEmpty()) {
			buttonDelete.get().setEnabled(true);
			List<?> list = selection.toList();
			if(list.size() > 1) {
				/*
				 * Selection Events
				 */
				List<IScan> selectedIdentifiedScans = new ArrayList<>();

				for(Object item : list) {
					if(item instanceof IScan scan) {
						selectedIdentifiedScans.add(scan);
					}
				}

				chromatogramSelection.setSelectedIdentifiedScans(selectedIdentifiedScans);
				chromatogramSelection.getChromatogram().setDirty(true);
				UpdateNotifierUI.update(getDisplay(), IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "Scans selection via the list.");
			} else {
				/*
				 * Only one object.
				 */
				Object object = list.size() == 1 ? list.get(0) : null;
				if(object instanceof IScan scan) {
					/*
					 * Fire updates
					 */
					IIdentificationTarget identificationTarget = IIdentificationTarget.getIdentificationTarget(scan);
					chromatogramSelection.setSelectedScan(scan);
					chromatogramSelection.setSelectedIdentifiedScan(scan);
					UpdateNotifierUI.update(getDisplay(), scan);
					UpdateNotifierUI.update(getDisplay(), identificationTarget);
				}
			}
		}
	}

	public void setEditEnabled(boolean editEnabled) {

		tableViewer.get().setEditEnabled(editEnabled);
	}

	private void createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Delete the selected scan.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteIdentifications();
			}
		});

		buttonDelete.set(button);
	}

	private void createButtonReset(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the base caller list.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset();
			}
		});
	}

	private void createButtonSettings(Composite parent) {

		createSettingsButton(parent, Arrays.asList( //
				PreferencePageSystem.class //
		), _ -> applySettings());
	}

	private void applySettings() {

		updateChromatogramSelection();
	}

	private void reset() {

		updateChromatogramSelection();
	}

	private boolean hasChanged(IChromatogramSelection chromatogramSelection) {

		boolean referenceChanged = this.chromatogramSelection != chromatogramSelection;
		if(!referenceChanged && chromatogramSelection != null) {
			if(chromatogramSelection.getChromatogram().getModCount() != currentModCount) {
				return true;
			}
		}

		return referenceChanged;
	}
}
