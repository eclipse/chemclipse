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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.calibration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.CalibrationNameValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.RetentionIndexValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.RetentionTimeValidator;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class CalibrationEditUI extends Composite {

	private static final String ACTION_INITIALIZE = "ACTION_INITIALIZE";
	private static final String ACTION_CANCEL = "ACTION_CANCEL";
	private static final String ACTION_DELETE = "ACTION_DELETE";
	private static final String ACTION_ADD = "ACTION_ADD";
	private static final String ACTION_SELECT = "ACTION_SELECT";
	//
	private Text textRetentionTime;
	private Text textRetentionIndex;
	private Combo comboReferences;
	private Button buttonSet;
	private Button buttonCancel;
	private Button buttonDelete;
	private Button buttonAdd;
	//
	private RetentionTimeValidator retentionTimeValidator;
	private ControlDecoration controlDecorationRetentionTime;
	private RetentionIndexValidator retentionIndexValidator;
	private ControlDecoration controlDecorationRetentionIndex;
	private CalibrationNameValidator calibrationNameValidator;
	private ControlDecoration controlDecorationName;
	//
	private ICalibrationEditListener calibrationEditListener = null;
	private List<IRetentionIndexEntry> retentionIndexEntries = new ArrayList<>();

	public CalibrationEditUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setCalibrationEditListener(ICalibrationEditListener calibrationEditListener) {

		this.calibrationEditListener = calibrationEditListener;
	}

	public void clearRetentionIndexEntries() {

		this.retentionIndexEntries.clear();
	}

	public void addRetentionIndexEntries(List<IRetentionIndexEntry> retentionIndexEntries) {

		this.retentionIndexEntries.addAll(retentionIndexEntries);
	}

	public void selectRetentionIndices() {

		enableButtonFields(ACTION_SELECT);
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(7, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		textRetentionTime = createTextRetentionTime(composite);
		textRetentionIndex = createTextRetentionIndex(composite);
		comboReferences = createComboReferences(composite);
		buttonSet = createButtonSet(composite);
		buttonCancel = createButtonCancel(composite);
		buttonDelete = createButtonDelete(composite);
		buttonAdd = createButtonAdd(composite);
		//
		enableButtonFields(ACTION_INITIALIZE);
	}

	private Text createTextRetentionTime(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Set the retention time in minutes.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		retentionTimeValidator = new RetentionTimeValidator();
		controlDecorationRetentionTime = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate();
			}
		});
		//
		return text;
	}

	private Text createTextRetentionIndex(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Set the retention index.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		retentionIndexValidator = new RetentionIndexValidator();
		controlDecorationRetentionIndex = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate();
			}
		});
		//
		return text;
	}

	private Combo createComboReferences(Composite parent) {

		Combo combo = new Combo(parent, SWT.BORDER);
		combo.setText("");
		combo.setItems(getAvailableStandards());
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		calibrationNameValidator = new CalibrationNameValidator();
		controlDecorationName = new ControlDecoration(combo, SWT.LEFT | SWT.TOP);
		//
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String name = combo.getText().trim();
				IRetentionIndexEntry retentionIndexEntry = getRetentionIndexEntry(name);
				if(retentionIndexEntry != null) {
					textRetentionIndex.setText(Float.toString(retentionIndexEntry.getRetentionIndex()));
				} else {
					textRetentionIndex.setText("");
				}
				validate();
			}
		});
		//
		combo.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate();
			}
		});
		//
		return combo;
	}

	private Button createButtonSet(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Set the retention index.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IRetentionIndexEntry retentionIndexEntry = createRetentionIndexEntry();
				if(retentionIndexEntry != null) {
					fireUpdateAdd(retentionIndexEntry);
					clearFields();
					enableButtonFields(ACTION_INITIALIZE);
				}
			}
		});
		//
		return button;
	}

	private Button createButtonCancel(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Cancel the current operation.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CANCEL, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				clearFields();
				enableButtonFields(ACTION_CANCEL);
			}
		});
		//
		return button;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete the selected calibration entrie(s).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				fireUpdateDelete();
			}
		});
		//
		return button;
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Activate the add operation.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_ADD);
			}
		});
		//
		return button;
	}

	private String[] getAvailableStandards() {

		int size = retentionIndexEntries.size();
		String[] availableStandards = new String[size];
		for(int i = 0; i < size; i++) {
			availableStandards[i] = retentionIndexEntries.get(i).getName();
		}
		return availableStandards;
	}

	private IRetentionIndexEntry getRetentionIndexEntry(String name) {

		for(IRetentionIndexEntry retentionIndexEntry : retentionIndexEntries) {
			if(retentionIndexEntry.getName().equals(name)) {
				return retentionIndexEntry;
			}
		}
		return null;
	}

	private void enableButtonFields(String action) {

		enableFields(false);
		//
		switch(action) {
			case ACTION_INITIALIZE:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_CANCEL:
				buttonAdd.setEnabled(true);
				hideControlDecorations();
				break;
			case ACTION_DELETE:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_ADD:
				buttonCancel.setEnabled(true);
				comboReferences.setEnabled(true);
				textRetentionTime.setEnabled(true);
				textRetentionIndex.setEnabled(true);
				validate();
				break;
			case ACTION_SELECT:
				buttonAdd.setEnabled(true);
				buttonDelete.setEnabled(true);
				break;
		}
	}

	private void enableFields(boolean enabled) {

		textRetentionTime.setEnabled(enabled);
		textRetentionIndex.setEnabled(enabled);
		comboReferences.setEnabled(enabled);
		buttonSet.setEnabled(enabled);
		buttonCancel.setEnabled(enabled);
		buttonDelete.setEnabled(enabled);
		buttonAdd.setEnabled(enabled);
	}

	private void clearFields() {

		textRetentionTime.setText("");
		textRetentionIndex.setText("");
		comboReferences.setText("");
	}

	private boolean validate() {

		buttonSet.setEnabled(false);
		//
		if(validate(retentionTimeValidator, controlDecorationRetentionTime, textRetentionTime.getText())) {
			if(validate(retentionIndexValidator, controlDecorationRetentionIndex, textRetentionIndex.getText())) {
				if(validate(calibrationNameValidator, controlDecorationName, comboReferences.getText())) {
					buttonSet.setEnabled(true);
					return true;
				}
			}
		}
		//
		return false;
	}

	private IRetentionIndexEntry createRetentionIndexEntry() {

		IRetentionIndexEntry retentionIndexEntry = null;
		if(validate()) {
			int retentionTime = (int)(retentionTimeValidator.getRetentionTime() * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
			float retentionIndex = retentionIndexValidator.getRetentionIndex();
			String name = calibrationNameValidator.getName();
			retentionIndexEntry = new RetentionIndexEntry(retentionTime, retentionIndex, name);
		}
		return retentionIndexEntry;
	}

	private boolean validate(IValidator<Object> validator, ControlDecoration controlDecoration, String text) {

		IStatus status = validator.validate(text);
		if(status.isOK()) {
			controlDecoration.hide();
			return true;
		} else {
			controlDecoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
			controlDecoration.showHoverText(status.getMessage());
			controlDecoration.show();
			return false;
		}
	}

	private void hideControlDecorations() {

		controlDecorationRetentionTime.hide();
		controlDecorationRetentionIndex.hide();
		controlDecorationName.hide();
	}

	private void fireUpdateDelete() {

		enableButtonFields(ACTION_DELETE);
		if(calibrationEditListener != null) {
			calibrationEditListener.delete();
		}
	}

	private void fireUpdateAdd(IRetentionIndexEntry retentionIndexEntry) {

		if(calibrationEditListener != null) {
			calibrationEditListener.add(retentionIndexEntry);
		}
	}
}
