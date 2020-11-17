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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.util.QuantReferencesListUtil;
import org.eclipse.chemclipse.support.validators.QuantReferenceValidator;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageQuantitation;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakDataSupport;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class ExtendedPeakQuantReferencesUI extends Composite implements IExtendedPartUI {

	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private Button buttonToolbarEdit;
	private AtomicReference<Composite> toolbarEdit = new AtomicReference<>();
	private Label labelInputErrors;
	private Combo comboQuantReferences;
	private Button buttonAddReference;
	private Button buttonDeleteReference;
	private AtomicReference<QuantReferencesListUI> tableViewer = new AtomicReference<>();
	private Button buttonTableEdit;
	//
	private QuantReferenceValidator validator;
	private ControlDecoration controlDecoration;
	//
	private IPeak peak;
	private PeakDataSupport peakDataSupport = new PeakDataSupport();
	private QuantReferencesListUtil quantReferencesUtil = new QuantReferencesListUtil();

	public ExtendedPeakQuantReferencesUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public boolean setFocus() {

		updatePeak();
		return true;
	}

	public void update(IPeak peak) {

		this.peak = peak;
		updatePeak();
	}

	private void updatePeak() {

		updateReferences();
		updateLabel();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createToolbarEdit(this);
		createTable(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarEdit, buttonToolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT, false);
		enableEdit(tableViewer, buttonTableEdit, IMAGE_EDIT_ENTRY, false);
		//
		clearLabelInputErrors();
		applySettings();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(4, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		buttonToolbarEdit = createButtonToggleToolbar(composite, toolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT);
		buttonTableEdit = createButtonToggleEditTable(composite, tableViewer, IMAGE_EDIT_ENTRY);
		createSettingsButton(composite);
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		int columns = 3;
		composite.setLayout(new GridLayout(columns, false));
		//
		labelInputErrors = createLabel(composite, columns);
		//
		comboQuantReferences = createComboQuantReferences(composite);
		buttonAddReference = createButtonAdd(composite);
		buttonDeleteReference = createButtonDelete(composite);
		//
		toolbarEdit.set(composite);
	}

	private Label createLabel(Composite parent, int horizontalSpan) {

		Label label = new Label(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = horizontalSpan;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		//
		return label;
	}

	private Combo createComboQuantReferences(Composite parent) {

		Combo combo = new Combo(parent, SWT.NONE);
		combo.setText("");
		combo.setToolTipText("Select a quantitation reference or type in a new reference name.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		validator = new QuantReferenceValidator();
		controlDecoration = new ControlDecoration(combo, SWT.LEFT | SWT.TOP);
		//
		combo.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate(validator, controlDecoration, combo);
				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					addReference(e.display.getActiveShell());
				}
			}
		});
		//
		return combo;
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add the quantitation reference.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				addReference(e.display.getActiveShell());
			}
		});
		return button;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete the selected quantitation reference(s).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteReferences(e.display.getActiveShell());
			}
		});
		return button;
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageQuantitation.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void createTable(Composite parent) {

		QuantReferencesListUI quantReferencesListUI = new QuantReferencesListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = quantReferencesListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		tableViewer.set(quantReferencesListUI);
	}

	private void applySettings() {

		setComboQuantReferenceItems();
	}

	private void addReference(Shell shell) {

		boolean isInputValid = validate(validator, controlDecoration, comboQuantReferences);
		if(isInputValid) {
			setReference(validator);
		} else {
			MessageDialog.openError(shell, "Add Quantitation Reference", "The given quantitation reference is invalid.");
		}
	}

	private void deleteReference(String quantitationReference) {

		if(peak != null) {
			peak.removeQuantitationReference(quantitationReference);
			updateReferences();
		}
	}

	@SuppressWarnings("rawtypes")
	private void deleteReferences(Shell shell) {

		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Delete Quantitation Reference(s)");
		messageBox.setMessage("Would you like to delete the selected quantitation(s)?");
		if(messageBox.open() == SWT.YES) {
			//
			Iterator iterator = tableViewer.get().getStructuredSelection().iterator();
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof String) {
					deleteReference((String)object);
				}
			}
			updateReferences();
		}
	}

	private void setReference(QuantReferenceValidator validator) {

		if(peak != null) {
			peak.addQuantitationReference(validator.getName());
		}
		comboQuantReferences.setText("");
		updateReferences();
	}

	private void updateReferences() {

		updateLabel();
		updateWidgets();
		//
		if(peak != null) {
			tableViewer.get().setInput(peak.getQuantitationReferences());
		} else {
			tableViewer.get().clear();
		}
	}

	private void setComboQuantReferenceItems() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		boolean useTargetList = preferenceStore.getBoolean(PreferenceConstants.P_USE_QUANTITATION_REFERENCE_LIST);
		//
		String[] items;
		if(useTargetList) {
			items = quantReferencesUtil.parseString(preferenceStore.getString(PreferenceConstants.P_QUANTITATION_REFERENCE_LIST));
			Arrays.sort(items);
		} else {
			items = new String[]{};
		}
		//
		comboQuantReferences.setItems(items);
	}

	private void updateLabel() {

		toolbarInfo.get().setText(peakDataSupport.getPeakLabel(peak));
	}

	private void updateWidgets() {

		boolean enabled = (peak == null) ? false : true;
		comboQuantReferences.setEnabled(enabled);
		buttonAddReference.setEnabled(enabled);
		buttonDeleteReference.setEnabled(enabled);
	}

	private boolean validate(IValidator validator, ControlDecoration controlDecoration, Combo combo) {

		IStatus status = validator.validate(combo.getText());
		if(status.isOK()) {
			controlDecoration.hide();
			clearLabelInputErrors();
			return true;
		} else {
			setLabelInputError(status.getMessage());
			controlDecoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
			controlDecoration.showHoverText("Input Error");
			controlDecoration.show();
			return false;
		}
	}

	private void clearLabelInputErrors() {

		labelInputErrors.setText("Example: Styrene");
		labelInputErrors.setBackground(null);
	}

	private void setLabelInputError(String message) {

		labelInputErrors.setText(message);
		labelInputErrors.setBackground(Colors.YELLOW);
	}
}
