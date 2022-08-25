/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import java.util.Collection;

import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.ITargetReference;
import org.eclipse.chemclipse.model.targets.LibraryField;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class TargetSettingEditor {

	private Button checkBoxPeaks;
	private Button checkBoxScans;
	private ComboViewer libraryFieldComboViewer;
	private Label labelRotation;
	private TargetReferenceListUI targetReferenceListUI;
	//
	private ITargetDisplaySettings targetDisplaySettings;
	private TargetDisplaySettingsPage targetDisplaySettingsPage;
	private Collection<? extends ITargetReference> targetReferences;

	public TargetSettingEditor(Composite parent, ITargetDisplaySettings targetDisplaySettings, TargetDisplaySettingsPage settingsPage, Collection<? extends ITargetReference> targetReferences) {

		this.targetDisplaySettings = targetDisplaySettings;
		this.targetDisplaySettingsPage = settingsPage;
		this.targetReferences = targetReferences;
		//
		createControl(parent);
	}

	private void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackgroundMode(SWT.INHERIT_FORCE);
		composite.setLayout(new GridLayout(7, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Section
		 */
		checkBoxPeaks = createCheckBoxPeaks(composite);
		checkBoxScans = createCheckBoxScans(composite);
		createLabel(composite, " | ");
		createLabel(composite, "Display Field: ");
		libraryFieldComboViewer = createComboViewerLibraryField(composite);
		createLabel(composite, "Collision Detection Depth: ");
		createComboViewerCollision(composite);
		/*
		 * Section
		 */
		labelRotation = createLabel(composite, getRotationText(targetDisplaySettings.getRotation()));
		createScaleRotation(composite);
		/*
		 * Section
		 */
		createSectionTableEdit(composite);
		/*
		 * Section
		 */
		targetReferenceListUI = createTargetReferenceListUI(composite);
		/*
		 * Add actions.
		 */
		addActions();
		targetReferenceListUI.setTargetDisplaySettings(targetDisplaySettings);
		targetReferenceListUI.setInput(targetReferences);
	}

	private void addActions() {

		ISelectionChangedListener comboListener = new ISelectionChangedListener() {

			private LibraryField currentField;

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				LibraryField field = targetDisplaySettings.getLibraryField();
				if(field != currentField) {
					currentField = field;
					targetReferenceListUI.refresh();
					fireUpdate();
				}
			}
		};
		//
		libraryFieldComboViewer.addSelectionChangedListener(comboListener);
	}

	private Button createCheckBoxPeaks(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText("Show Peak Labels");
		button.setToolTipText("Show the best target of the identified peaks.");
		button.setSelection(targetDisplaySettings.isShowPeakLabels());
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				targetDisplaySettings.setShowPeakLabels(checkBoxPeaks.getSelection());
				fireUpdate();
			}
		});
		//
		return button;
	}

	private Button createCheckBoxScans(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText("Show Scan Labels");
		button.setToolTipText("Show the best target of the identified scan(s).");
		button.setSelection(targetDisplaySettings.isShowScanLabels());
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				targetDisplaySettings.setShowScanLabels(checkBoxScans.getSelection());
				fireUpdate();
			}
		});
		//
		return button;
	}

	private Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	private ComboViewer createComboViewerLibraryField(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setInput(LibraryField.values());
		comboViewer.setSelection(new StructuredSelection(targetDisplaySettings.getLibraryField()));
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				targetDisplaySettings.setLibraryField((LibraryField)comboViewer.getStructuredSelection().getFirstElement());
				fireUpdate();
			}
		});
		//
		Combo combo = comboViewer.getCombo();
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return comboViewer;
	}

	private ComboViewer createComboViewerCollision(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setInput(new Object[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
		comboViewer.setSelection(new StructuredSelection(targetDisplaySettings.getCollisionDetectionDepth()));
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				targetDisplaySettings.setCollisionDetectionDepth(((Integer)comboViewer.getStructuredSelection().getFirstElement()));
				fireUpdate();
			}
		});
		//
		return comboViewer;
	}

	private Scale createScaleRotation(Composite parent) {

		Scale scale = new Scale(parent, SWT.HORIZONTAL);
		scale.setMinimum(0);
		scale.setMaximum(90);
		scale.setIncrement(1);
		scale.setPageIncrement(15);
		scale.setSelection(targetDisplaySettings.getRotation());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 6;
		scale.setLayoutData(gridData);
		//
		scale.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int selection = scale.getSelection();
				targetDisplaySettings.setRotation(selection);
				labelRotation.setText(getRotationText(selection));
				fireUpdate();
			}
		});
		//
		return scale;
	}

	private void createSectionTableEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackgroundMode(SWT.INHERIT_FORCE);
		composite.setLayout(new GridLayout(3, false));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 7;
		composite.setLayoutData(gridData);
		//
		createButtonCheckAll(composite);
		createButtonUncheckAll(composite);
		createSearchSupportUI(composite);
	}

	private SearchSupportUI createSearchSupportUI(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				targetReferenceListUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		return searchSupportUI;
	}

	private Button createButtonCheckAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Set all target reference(s) visible.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHECK_ALL, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifyTargetVisibility(targetReferenceListUI, true);
				targetReferenceListUI.refresh();
				fireUpdate();
			}
		});
		//
		return button;
	}

	private Button createButtonUncheckAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Set all target reference(s) invisible.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_UNCHECK_ALL, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifyTargetVisibility(targetReferenceListUI, false);
				targetReferenceListUI.refresh();
				fireUpdate();
			}
		});
		//
		return button;
	}

	private void modifyTargetVisibility(TableViewer tableViewer, boolean visible) {

		for(TableItem item : tableViewer.getTable().getItems()) {
			Object data = item.getData();
			if(data instanceof ITargetReference targetReference) {
				targetDisplaySettings.setVisible(targetReference, visible);
			}
		}
	}

	private TargetReferenceListUI createTargetReferenceListUI(Composite parent) {

		TargetReferenceListUI targetReferenceListUI = new TargetReferenceListUI(parent, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 7;
		Table table = targetReferenceListUI.getTable();
		table.setLayoutData(gridData);
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				fireUpdate();
			}
		});
		//
		return targetReferenceListUI;
	}

	private String getRotationText(int value) {

		StringBuilder builder = new StringBuilder();
		builder.append("Rotation (");
		if(value < 10) {
			builder.append(' ');
		}
		builder.append(value);
		builder.append("°) ");
		//
		return builder.toString();
	}

	private void fireUpdate() {

		targetDisplaySettingsPage.notifyListener();
	}
}