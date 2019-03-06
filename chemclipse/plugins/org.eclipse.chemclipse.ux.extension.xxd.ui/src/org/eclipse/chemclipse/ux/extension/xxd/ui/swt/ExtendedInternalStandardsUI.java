/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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

import java.util.Iterator;

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.model.quantitation.InternalStandard;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.ConcentrationValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.NameValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.ResponseFactorValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakDataSupport;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class ExtendedInternalStandardsUI {

	private static final Logger logger = Logger.getLogger(ExtendedInternalStandardsUI.class);
	//
	private static final String MENU_CATEGORY_ISTD = "Internal Standards";
	//
	private static final String ACTION_INITIALIZE = "ACTION_INITIALIZE";
	private static final String ACTION_CANCEL = "ACTION_CANCEL";
	private static final String ACTION_ADD = "ACTION_ADD";
	private static final String ACTION_DELETE = "ACTION_DELETE";
	private static final String ACTION_SELECT = "ACTION_SELECT";
	//
	private Composite toolbarInfo;
	private Composite toolbarModify;
	private Composite toolbarAdd;
	private Label labelPeak;
	private Label labelInputErrors;
	//
	private Text textName;
	private Text textConcentration;
	private Text textResponseFactor;
	private Button buttonInsert;
	//
	private NameValidator nameValidator;
	private ControlDecoration nameControlDecoration;
	private ConcentrationValidator concentrationValidator;
	private ControlDecoration concentrationControlDecoration;
	private ResponseFactorValidator responseFactorValidator;
	private ControlDecoration responseFactorControlDecoration;
	//
	private Button buttonCancel;
	private Button buttonAdd;
	private Button buttonDelete;
	//
	private InternalStandardsListUI internalStandardsListUI;
	private IPeak peak;
	//
	private PeakDataSupport peakDataSupport = new PeakDataSupport();

	@Inject
	public ExtendedInternalStandardsUI(Composite parent) {
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

		String editInformation = internalStandardsListUI.isEditEnabled() ? "Edit is enabled." : "Edit is disabled.";
		labelPeak.setText(peakDataSupport.getPeakLabel(peak) + " - " + editInformation);
		//
		if(peak != null) {
			internalStandardsListUI.setInput(peak.getInternalStandards());
		} else {
			internalStandardsListUI.setInput(null);
		}
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		toolbarModify = createToolbarModify(parent);
		toolbarAdd = createToolbarAdd(parent);
		createInternalStandardsList(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
		PartSupport.setCompositeVisibility(toolbarModify, false);
		PartSupport.setCompositeVisibility(toolbarAdd, false);
		//
		internalStandardsListUI.setEditEnabled(false);
		enableButtonFields(ACTION_INITIALIZE);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarModify(composite);
		createButtonToggleEditModus(composite);
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelPeak = new Label(composite, SWT.NONE);
		labelPeak.setText("");
		labelPeak.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarModify(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(4, false));
		//
		createErrorLabel(composite);
		buttonCancel = createButtonCancel(composite);
		buttonAdd = createButtonAdd(composite);
		buttonDelete = createButtonDelete(composite);
		//
		return composite;
	}

	private void createErrorLabel(Composite parent) {

		labelInputErrors = new Label(parent, SWT.NONE);
		labelInputErrors.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private Button createButtonCancel(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Cancel Operation");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CANCEL, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_CANCEL);
				clearLabelInputErrors();
				PartSupport.setCompositeVisibility(toolbarAdd, false);
			}
		});
		return button;
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add Internal Standard");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(peak != null) {
					if(peak.getIntegratedArea() == 0) {
						setLabelInputError("The peak area is 0. Please integrate the peak(s) first.");
					} else {
						clearLabelInputErrors();
						enableButtonFields(ACTION_ADD);
						PartSupport.setCompositeVisibility(toolbarAdd, true);
					}
				} else {
					setLabelInputError("No peak has been selected yet.");
				}
			}
		});
		return button;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete Internal Standard");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteInternalStandards(e.display.getActiveShell());
			}
		});
		return button;
	}

	private Composite createToolbarAdd(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		createLabelName(composite);
		createTextName(composite);
		createLabelConcentration(composite);
		createTextConcentration(composite);
		createLabelResponseFactor(composite);
		createTextResponseFactor(composite);
		buttonInsert = createButtonInsert(composite);
		//
		return composite;
	}

	private void createLabelName(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Name:");
	}

	private void createTextName(Composite parent) {

		textName = new Text(parent, SWT.BORDER);
		textName.setText("");
		textName.setToolTipText("Name of the internal standard.");
		textName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		nameValidator = new NameValidator();
		nameControlDecoration = new ControlDecoration(textName, SWT.LEFT | SWT.TOP);
		textName.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate(nameValidator, nameControlDecoration, textName);
			}
		});
	}

	private void createLabelConcentration(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Conc.:");
	}

	private void createTextConcentration(Composite parent) {

		textConcentration = new Text(parent, SWT.BORDER);
		textConcentration.setText("");
		textConcentration.setToolTipText("Concentration, e.g. 10 mg/L");
		textConcentration.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		concentrationValidator = new ConcentrationValidator();
		concentrationControlDecoration = new ControlDecoration(textConcentration, SWT.LEFT | SWT.TOP);
		textConcentration.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate(concentrationValidator, concentrationControlDecoration, textConcentration);
			}
		});
	}

	private void createLabelResponseFactor(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("RF:");
	}

	private void createTextResponseFactor(Composite parent) {

		textResponseFactor = new Text(parent, SWT.BORDER);
		textResponseFactor.setText("1.0");
		textResponseFactor.setToolTipText("Response Factor");
		textResponseFactor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		responseFactorValidator = new ResponseFactorValidator();
		responseFactorControlDecoration = new ControlDecoration(textResponseFactor, SWT.LEFT | SWT.TOP);
		textResponseFactor.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate(responseFactorValidator, responseFactorControlDecoration, textResponseFactor);
			}
		});
	}

	private Button createButtonInsert(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Insert Internal Standard");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				addInternalStandard(e.display.getActiveShell());
			}
		});
		//
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
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_DEFAULT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarModify);
				if(!visible) {
					/*
					 * Hide the add toolbar if the modify toolbar is set to hidden.
					 */
					PartSupport.setCompositeVisibility(toolbarAdd, false);
				}
				//
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_ACTIVE, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_DEFAULT, IApplicationImage.SIZE_16x16));
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
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_ENTRY_DEFAULT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean editEnabled = !internalStandardsListUI.isEditEnabled();
				internalStandardsListUI.setEditEnabled(editEnabled);
				button.setImage(ApplicationImageFactory.getInstance().getImage((editEnabled) ? IApplicationImage.IMAGE_EDIT_ENTRY_ACTIVE : IApplicationImage.IMAGE_EDIT_ENTRY_DEFAULT, IApplicationImage.SIZE_16x16));
				updatePeak();
			}
		});
		//
		return button;
	}

	private void createInternalStandardsList(Composite parent) {

		internalStandardsListUI = new InternalStandardsListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = internalStandardsListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_SELECT);
			}
		});
		/*
		 * Add the delete targets support.
		 */
		Shell shell = internalStandardsListUI.getTable().getShell();
		ITableSettings tableSettings = internalStandardsListUI.getTableSettings();
		addDeleteMenuEntry(shell, tableSettings);
		addKeyEventProcessors(shell, tableSettings);
		internalStandardsListUI.applySettings(tableSettings);
	}

	private void addDeleteMenuEntry(Shell shell, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Internal Standard(s)";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY_ISTD;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteInternalStandards(shell);
			}
		});
	}

	private void addKeyEventProcessors(Shell shell, ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					/*
					 * DEL
					 */
					deleteInternalStandards(shell);
				}
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private void deleteInternalStandards(Shell shell) {

		if(peak != null) {
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			messageBox.setText("Delete Internal Standard(s)");
			messageBox.setMessage("Would you like to delete the selected internal standard(s)?");
			if(messageBox.open() == SWT.YES) {
				/*
				 * Delete ISTD
				 */
				enableButtonFields(ACTION_DELETE);
				Iterator iterator = internalStandardsListUI.getStructuredSelection().iterator();
				while(iterator.hasNext()) {
					Object object = iterator.next();
					if(object instanceof IInternalStandard) {
						deleteInternalStandard((IInternalStandard)object);
					}
				}
				updatePeak();
			}
		}
	}

	private void deleteInternalStandard(IInternalStandard internalStandard) {

		if(peak != null) {
			peak.removeInternalStandard(internalStandard);
		}
	}

	private void addInternalStandard(Shell shell) {

		if(peak == null) {
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
			messageBox.setText("Add Internal Standard (ISTD)");
			messageBox.setMessage("No peak has been selected.");
			messageBox.open();
		} else {
			try {
				boolean isInputValid = false;
				String name = "";
				double concentration = 0.0d;
				String concentrationUnit = "";
				double responseFactor = 0.0d;
				//
				isInputValid = validate(nameValidator, nameControlDecoration, textName);
				name = nameValidator.getName();
				//
				if(isInputValid) {
					isInputValid = validate(concentrationValidator, concentrationControlDecoration, textConcentration);
					concentration = concentrationValidator.getConcentration();
					concentrationUnit = concentrationValidator.getUnit();
				}
				//
				if(isInputValid) {
					isInputValid = validate(responseFactorValidator, responseFactorControlDecoration, textResponseFactor);
					responseFactor = responseFactorValidator.getResponseFactor();
				}
				/*
				 * Add
				 */
				if(isInputValid) {
					String chemicalClass = ""; // Use the edit modus to set it.
					IInternalStandard internalStandard = new InternalStandard(name, concentration, concentrationUnit, responseFactor);
					internalStandard.setChemicalClass(chemicalClass);
					//
					if(peak.getInternalStandards().contains(internalStandard)) {
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
						messageBox.setText("Add Internal Standard (ISTD)");
						messageBox.setMessage("The Internal Standard (ISTD) exists already.");
						messageBox.open();
					} else {
						peak.addInternalStandard(internalStandard);
						textName.setText("");
						textConcentration.setText("");
						textResponseFactor.setText("1.0");
						enableButtonFields(ACTION_INITIALIZE);
						updatePeak();
					}
				}
			} catch(Exception e1) {
				logger.warn(e1);
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				messageBox.setText("Add Internal Standard (ISTD)");
				messageBox.setMessage("Please check the content, response factor and unit values.");
				messageBox.open();
			}
		}
	}

	private void enableButtonFields(String action) {

		enableFields(false);
		switch(action) {
			case ACTION_INITIALIZE:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_CANCEL:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_ADD:
				buttonCancel.setEnabled(true);
				textName.setEnabled(true);
				textConcentration.setEnabled(true);
				textResponseFactor.setEnabled(true);
				buttonInsert.setEnabled(true);
				break;
			case ACTION_DELETE:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_SELECT:
				buttonAdd.setEnabled(true);
				buttonCancel.setEnabled(true);
				//
				if(internalStandardsListUI.getTable().getSelectionIndex() >= 0) {
					buttonDelete.setEnabled(true);
				} else {
					buttonDelete.setEnabled(false);
				}
				break;
		}
	}

	private void enableFields(boolean enabled) {

		buttonCancel.setEnabled(enabled);
		buttonDelete.setEnabled(enabled);
		buttonAdd.setEnabled(enabled);
		//
		textName.setEnabled(enabled);
		textConcentration.setEnabled(enabled);
		textResponseFactor.setEnabled(enabled);
		buttonInsert.setEnabled(enabled);
	}

	private void clearLabelInputErrors() {

		labelInputErrors.setText("");
		labelInputErrors.setBackground(null);
	}

	private void setLabelInputError(String message) {

		labelInputErrors.setText(message);
		labelInputErrors.setBackground(Colors.YELLOW);
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
}
