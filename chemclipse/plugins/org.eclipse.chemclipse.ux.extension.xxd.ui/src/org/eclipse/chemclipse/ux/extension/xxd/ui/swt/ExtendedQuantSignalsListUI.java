/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.QuantitationSignalValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePeaksAxes;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ExtendedQuantSignalsListUI extends Composite implements IExtendedPartUI {

	private static final String MENU_CATEGORY = "Signals";
	//
	private IQuantitationCompound quantitationCompound;
	//
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private Button buttonToolbarEdit;
	private AtomicReference<Composite> toolbarEdit = new AtomicReference<>();
	private Label labelInputErrors;
	private Text textSignal;
	private Button buttonAdd;
	private Button buttonDelete;
	private AtomicReference<QuantSignalsListUI> tableViewer = new AtomicReference<>();
	private Button buttonTableEdit;
	//
	private QuantitationSignalValidator validator = new QuantitationSignalValidator();
	private ControlDecoration controlDecoration;

	public ExtendedQuantSignalsListUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IQuantitationCompound quantitationCompound) {

		this.quantitationCompound = quantitationCompound;
		updateInput();
		updateWidgets();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(composite);
		createToolbarInfo(composite);
		createToolbarEdit(composite);
		createTable(composite);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarEdit, buttonToolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT, false);
		enableEdit(tableViewer, buttonTableEdit, IMAGE_EDIT_ENTRY, false);
		clearLabelInputErrors();
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

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePagePeaksAxes.class), new ISettingsHandler() {

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

	private void createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		int columns = 3;
		composite.setLayout(new GridLayout(columns, false));
		//
		labelInputErrors = createLabel(composite, columns);
		//
		textSignal = createTextSignal(composite);
		buttonAdd = createButtonAdd(composite);
		buttonDelete = createButtonDelete(composite);
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

	private Text createTextSignal(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Type in a new signal.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate(validator, controlDecoration, text);
				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					addSignal(e.display.getActiveShell());
				}
			}
		});
		//
		return text;
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add a new signal.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				addSignal(e.display.getActiveShell());
			}
		});
		//
		return button;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Delete the selected signal(s).");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteSignals(e.display.getActiveShell());
			}
		});
		//
		return button;
	}

	private void createTable(Composite parent) {

		QuantSignalsListUI quantSignalsListUI = new QuantSignalsListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		quantSignalsListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Add the delete support.
		 */
		Shell shell = quantSignalsListUI.getTable().getShell();
		ITableSettings tableSettings = quantSignalsListUI.getTableSettings();
		addDeleteMenuEntry(shell, tableSettings);
		addKeyEventProcessors(shell, tableSettings);
		quantSignalsListUI.applySettings(tableSettings);
		//
		tableViewer.set(quantSignalsListUI);
	}

	private void addDeleteMenuEntry(Shell shell, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Signal(s)";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteSignals(shell);
			}
		});
	}

	private void addKeyEventProcessors(Shell shell, ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					deleteSignals(shell);
				}
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private void deleteSignals(Shell shell) {

		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Delete Signal(s)");
		messageBox.setMessage("Would you like to delete the selected signal(s)?");
		if(messageBox.open() == SWT.YES) {
			/*
			 * Delete
			 */
			Iterator iterator = tableViewer.get().getStructuredSelection().iterator();
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof IQuantitationSignal) {
					quantitationCompound.getQuantitationSignals().remove((IQuantitationSignal)object);
				}
			}
			updateInput();
		}
	}

	private void addSignal(Shell shell) {

		boolean isInputValid = validate(validator, controlDecoration, textSignal);
		if(isInputValid) {
			setSignal(validator);
		} else {
			MessageDialog.openError(shell, "Add Signal", "The given signal is invalid.");
		}
	}

	private void setSignal(QuantitationSignalValidator validator) {

		if(quantitationCompound != null) {
			IQuantitationSignal quantitationSignal = validator.getQuantitationSignal();
			if(quantitationSignal != null) {
				quantitationCompound.getQuantitationSignals().add(quantitationSignal);
				textSignal.setText("");
				updateInput();
			}
		}
	}

	private void applySettings() {

		updateInput();
	}

	private void updateInput() {

		if(quantitationCompound != null) {
			toolbarInfo.get().setText("Quantitation Compound: " + quantitationCompound.getName());
			tableViewer.get().setInput(quantitationCompound.getQuantitationSignals());
		} else {
			toolbarInfo.get().setText("");
			tableViewer.get().clear();
		}
	}

	private boolean validate(IValidator validator, ControlDecoration controlDecoration, Text text) {

		IStatus status = validator.validate(text.getText());
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

		labelInputErrors.setText("Example: " + QuantitationSignalValidator.DEMO);
		labelInputErrors.setBackground(null);
	}

	private void setLabelInputError(String message) {

		labelInputErrors.setText(message);
		labelInputErrors.setBackground(Colors.YELLOW);
	}

	private void updateWidgets() {

		boolean enabled = (quantitationCompound == null) ? false : true;
		textSignal.setEnabled(enabled);
		buttonAdd.setEnabled(enabled);
		buttonDelete.setEnabled(enabled);
	}
}
