/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.util.QuantReferencesListUtil;
import org.eclipse.chemclipse.support.validators.QuantReferenceValidator;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageQuantitation;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakDataSupport;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class ExtendedPeakQuantReferencesUI {

	private Composite toolbarInfo;
	private Label labelInfo;
	private Composite toolbarModify;
	private Label labelInputErrors;
	private Combo comboQuantReferences;
	private Button buttonAddReference;
	private Button buttonDeleteReference;
	private QuantReferencesListUI quantReferencesListUI;
	private QuantReferencesListUtil quantReferencesUtil = new QuantReferencesListUtil();
	//
	private QuantReferenceValidator validator;
	private ControlDecoration controlDecoration;
	//
	private IPeak peak;
	private PeakDataSupport peakDataSupport = new PeakDataSupport();

	@Inject
	public ExtendedPeakQuantReferencesUI(Composite parent) {
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updatePeak();
	}

	public void update(IPeak peak) {

		this.peak = peak;
		updatePeak();
	}

	private void updatePeak() {

		updateReferences();
		updateLabel();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		toolbarModify = createToolbarModify(parent);
		quantReferencesListUI = createTable(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
		PartSupport.setCompositeVisibility(toolbarModify, false);
		//
		quantReferencesListUI.setEditEnabled(false);
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
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarModify(composite);
		createButtonToggleEditModus(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfo = new Label(composite, SWT.NONE);
		labelInfo.setText("");
		labelInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarModify(Composite parent) {

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
		return composite;
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

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfo);
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

	private Button createButtonToggleToolbarModify(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle modify toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarModify);
				if(visible) {
					setComboQuantReferenceItems();
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
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
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_ENTRY, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean editEnabled = !quantReferencesListUI.isEditEnabled();
				quantReferencesListUI.setEditEnabled(editEnabled);
				updateLabel();
			}
		});
		//
		return button;
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new PreferencePageQuantitation();
				preferencePage.setTitle("Quantitiation");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private QuantReferencesListUI createTable(Composite parent) {

		QuantReferencesListUI listUI = new QuantReferencesListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = listUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		return listUI;
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
			Iterator iterator = quantReferencesListUI.getStructuredSelection().iterator();
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
			quantReferencesListUI.setInput(peak.getQuantitationReferences());
		} else {
			quantReferencesListUI.clear();
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

		labelInfo.setText(peakDataSupport.getPeakLabel(peak));
		String editInformation = quantReferencesListUI.isEditEnabled() ? "Edit is enabled." : "Edit is disabled.";
		labelInfo.setText(labelInfo.getText() + " - " + editInformation);
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
