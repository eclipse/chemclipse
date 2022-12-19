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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.targets.DisplayOption;
import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.ITargetReference;
import org.eclipse.chemclipse.model.targets.LibraryField;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;

public class TargetSettingEditor {

	private AtomicReference<Label> rotationLabelControl = new AtomicReference<>();
	private AtomicReference<TargetReferenceListUI> listControl = new AtomicReference<>();
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
		composite.setLayout(new GridLayout(9, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		createLabel(composite, "Display Option: ");
		createComboViewerDisplayOption(composite);
		createCheckBoxPeaks(composite);
		createCheckBoxScans(composite);
		new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
		createLabel(composite, "Display Field: ");
		createComboViewerLibraryField(composite);
		createLabel(composite, "Collision Detection Depth: ");
		createComboViewerCollision(composite);
		//
		createSectionRotation(composite);
		//
		createSectionTableEdit(composite);
		//
		createSectionListUI(composite);
		/*
		 * Input
		 */
		initialize();
	}

	private void initialize() {

		TargetReferenceListUI targetReferenceListUI = listControl.get();
		targetReferenceListUI.setTargetDisplaySettings(targetDisplaySettings);
		targetReferenceListUI.setInput(targetReferences);
	}

	private void createCheckBoxPeaks(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText("Peaks");
		button.setToolTipText("Show peak labels.");
		button.setSelection(targetDisplaySettings.isShowPeakLabels());
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				targetDisplaySettings.setShowPeakLabels(button.getSelection());
				fireUpdate();
			}
		});
	}

	private void createCheckBoxScans(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText("Scans");
		button.setToolTipText("Show scan labels");
		button.setSelection(targetDisplaySettings.isShowScanLabels());
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				targetDisplaySettings.setShowScanLabels(button.getSelection());
				fireUpdate();
			}
		});
	}

	private ComboViewer createComboViewerDisplayOption(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof DisplayOption displayOption) {
					return displayOption.label();
				}
				return null;
			}
		});
		//
		comboViewer.setInput(DisplayOption.values());
		comboViewer.setSelection(new StructuredSelection(targetDisplaySettings.getDisplayOption()));
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				targetDisplaySettings.setDisplayOption((DisplayOption)comboViewer.getStructuredSelection().getFirstElement());
				fireUpdate();
			}
		});
		/*
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=567652
		 */
		Combo combo = comboViewer.getCombo();
		if(OperatingSystemUtils.isLinux()) {
			combo.setBackground(combo.getBackground());
		}
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return comboViewer;
	}

	private Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		//
		return label;
	}

	private ComboViewer createComboViewerLibraryField(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof LibraryField libraryField) {
					return libraryField.label();
				}
				return null;
			}
		});
		//
		comboViewer.setInput(LibraryField.values());
		comboViewer.setSelection(new StructuredSelection(targetDisplaySettings.getLibraryField()));
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				targetDisplaySettings.setLibraryField((LibraryField)comboViewer.getStructuredSelection().getFirstElement());
				listControl.get().refresh();
				fireUpdate();
			}
		});
		/*
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=567652
		 */
		Combo combo = comboViewer.getCombo();
		if(OperatingSystemUtils.isLinux()) {
			combo.setBackground(combo.getBackground());
		}
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
		/*
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=567652
		 */
		Combo combo = comboViewer.getCombo();
		if(OperatingSystemUtils.isLinux()) {
			combo.setBackground(combo.getBackground());
		}
		//
		return comboViewer;
	}

	private void createSectionRotation(Composite parent) {

		rotationLabelControl.set(createLabel(parent, getRotationText(targetDisplaySettings.getRotation())));
		createScaleRotation(parent);
	}

	private Scale createScaleRotation(Composite parent) {

		Scale scale = new Scale(parent, SWT.HORIZONTAL);
		scale.setMinimum(0);
		scale.setMaximum(90);
		scale.setIncrement(1);
		scale.setPageIncrement(15);
		scale.setSelection(targetDisplaySettings.getRotation());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 8;
		scale.setLayoutData(gridData);
		//
		scale.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int selection = scale.getSelection();
				targetDisplaySettings.setRotation(selection);
				rotationLabelControl.get().setText(getRotationText(selection));
				fireUpdate();
			}
		});
		//
		return scale;
	}

	private void createSectionTableEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 9;
		composite.setLayoutData(gridData);
		composite.setBackgroundMode(SWT.INHERIT_FORCE);
		composite.setLayout(new GridLayout(4, false));
		//
		createButtonCheckOperation(composite, true);
		createButtonCheckOperation(composite, false);
		createSpinnerSelectHighestPeaks(composite);
		createSearchSupportUI(composite);
	}

	private SearchSupportUI createSearchSupportUI(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				listControl.get().setSearchText(searchText, caseSensitive);
			}
		});
		//
		return searchSupportUI;
	}

	private Button createButtonCheckOperation(Composite parent, boolean setVisible) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Set all target references " + (setVisible ? "visible" : "invisible") + ".");
		button.setImage(ApplicationImageFactory.getInstance().getImage(setVisible ? IApplicationImage.IMAGE_CHECK_ALL : IApplicationImage.IMAGE_UNCHECK_ALL, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setTargetVisibility(setVisible);
				listControl.get().refresh();
				fireUpdate();
			}
		});
		//
		return button;
	}

	private Spinner createSpinnerSelectHighestPeaks(Composite parent) {

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setMinimum(1);
		spinner.setMaximum(Integer.MAX_VALUE);
		spinner.setPageIncrement(1);
		spinner.setSelection(10);
		spinner.setToolTipText("Select the n-highest peaks.");
		GridData gridData = new GridData();
		gridData.widthHint = 80;
		spinner.setLayoutData(gridData);
		//
		spinner.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				selectHighestTargets(spinner.getSelection());
				listControl.get().refresh();
				fireUpdate();
			}
		});
		//
		return spinner;
	}

	private void selectHighestTargets(int selection) {

		setTargetVisibility(false);
		List<ITargetReference> targetReferencesSorted = new ArrayList<>(targetReferences);
		Collections.sort(targetReferencesSorted, (r1, r2) -> Double.compare(r2.getSignal().getY(), r1.getSignal().getY()));
		//
		for(int i = 0; i < selection; i++) {
			ITargetReference targetReference = targetReferencesSorted.get(i);
			targetDisplaySettings.setVisible(targetReference, true);
		}
	}

	private void setTargetVisibility(boolean visible) {

		for(ITargetReference targetReference : targetReferences) {
			targetDisplaySettings.setVisible(targetReference, visible);
		}
	}

	private void createSectionListUI(Composite parent) {

		TargetReferenceListUI targetReferenceListUI = new TargetReferenceListUI(parent, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 9;
		Table table = targetReferenceListUI.getTable();
		table.setLayoutData(gridData);
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				fireUpdate();
			}
		});
		//
		listControl.set(targetReferenceListUI);
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