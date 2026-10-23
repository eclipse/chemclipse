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
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - adapted for DSD
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.dsd.ui.swt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.dsd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.ui.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.ui.swt.IExtendedPartUI;
import org.eclipse.e4.ui.services.EContextService;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

import jakarta.inject.Inject;

public class ExtendedElectropherogramTargetsUI extends Composite implements IExtendedPartUI {

	private static final String MENU_CATEGORY_TARGETS = "Targets";

	private static final String KEY_CLASS_PREFIX = "org.eclipse.chemclipse.ux.extension.xxd.ui.TargetsList.";

	private EContextService contextService = Activator.getDefault().getApplication().getContext().get(EContextService.class);
	private IBindingService bindingService = PlatformUI.getWorkbench().getService(IBindingService.class);

	private AtomicReference<Button> buttonToolbarSearch = new AtomicReference<>();
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private AtomicReference<ElectropherogramTargetsListUI> targetList = new AtomicReference<>();

	private Object objectCacheChromatogram = null;

	private IUpdateListener updateListener = null;

	@Inject
	public ExtendedElectropherogramTargetsUI(Composite parent, int style) {

		super(parent, style);
		createControl();
		contextService.activateContext("org.eclipse.chemclipse.ux.extension.xxd.ui.TargetsList");
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
		updateTargets(getDisplay());
	}

	public void updatePart() {

		targetList.get().refresh();
	}

	public void updateChromatogram(IChromatogramSelection chromatogramSelection) {

		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(objectCacheChromatogram != chromatogram) {
				objectCacheChromatogram = chromatogram;
			}
			updateTargets(getDisplay());
		}
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);

		createToolbarMain(this);
		createToolbarSearch(this);
		createTableSection(this);

		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarSearch, buttonToolbarSearch.get(), IMAGE_SEARCH, TOOLTIP_SEARCH, false);
		applySettings();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));

		createButtonToggleToolbarSearch(composite);
	}

	private void createButtonToggleToolbarSearch(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH);
		buttonToolbarSearch.set(button);
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener((searchText, caseSensitive) -> {
			targetList.get().setSearchText(searchText, caseSensitive);
		});

		toolbarSearch.set(searchSupportUI);
	}

	private void createTableSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, true));
		createTargetTable(composite, targetList);
	}

	private void createTargetTable(Composite parent, AtomicReference<ElectropherogramTargetsListUI> listControl) {

		ElectropherogramTargetsListUI targetListUI = new ElectropherogramTargetsListUI(parent, SWT.BORDER);
		Table table = targetListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		targetListUI.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				propagateTarget(e.display);
			}
		});

		targetListUI.setUpdateListener(this::fireUpdate);
		targetListUI.setComparator(true); // sort the table
		Display display = targetListUI.getTable().getDisplay();
		ITableSettings tableSettings = targetListUI.getTableSettings();
		addVerifyTargetsMenuEntry(display, tableSettings);
		addUnverifyTargetsMenuEntry(display, tableSettings);
		addKeyEventProcessors(display, tableSettings);
		targetListUI.applySettings(tableSettings);

		listControl.set(targetListUI);
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

			if(matchesKeyPress("VerifyTarget", e)) {
				verifyTargets(true, display);
			} else if(matchesKeyPress("UnverifyTarget", e)) {
				verifyTargets(false, display);
			} else {
				propagateTarget(display);
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

	private void verifyTargets(boolean verified, Display display) {

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

		targetList.get().setComparator(true);
	}

	private void updateTargets(Display display) {

		updateInput();

		ElectropherogramTargetsListUI targetListUI = targetList.get();
		targetListUI.sortTable();
		Table table = targetListUI.getTable();
		if(table.getItemCount() > 0) {
			table.setSelection(0);
			propagateTarget(display);
		}
	}

	private void updateInput() {

		if(objectCacheChromatogram instanceof ITargetSupplier targetSupplier) {
			List<IIdentificationTarget> identificationTargets = getTargetsSorted(targetSupplier.getTargets());
			targetList.get().setInput(identificationTargets);
		} else {
			targetList.get().setInput(null);
		}
	}

	private static List<IIdentificationTarget> getTargetsSorted(Collection<IIdentificationTarget> targets) {

		List<IIdentificationTarget> identificationTargets = new ArrayList<>();
		if(targets != null) {
			identificationTargets.addAll(targets);
			IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(SortOrder.DESC);
			Collections.sort(identificationTargets, identificationTargetComparator);
		}

		return identificationTargets;
	}

	private void propagateTarget(Display display) {

		Table table = targetList.get().getTable();
		int index = table.getSelectionIndex();
		if(index >= 0) {
			TableItem tableItem = table.getItem(index);
			Object data = tableItem.getData();
			if(data instanceof IIdentificationTarget identificationTarget) {
				UpdateNotifierUI.update(display, identificationTarget);
			}
		}
	}

	private void updateOnFocus() {

		DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
		List<Object> objects = dataUpdateSupport.getUpdates(getLastTopic(dataUpdateSupport.getTopics()));

		if(!objects.isEmpty()) {
			Object object = objects.get(0);
			if(object instanceof IChromatogramSelection chromatogramSelection) {
				updateChromatogram(chromatogramSelection);
			}
		}
	}

	private String getLastTopic(List<String> topics) {

		Collections.reverse(topics);
		for(String topic : topics) {
			if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION)) {
				return topic;
			}
			if(topic.equals(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGETS_UPDATE_SELECTION)) {
				return topic;
			}
		}

		return "";
	}

	private void fireUpdate() {

		if(updateListener != null) {
			updateListener.update();
		}
	}
}