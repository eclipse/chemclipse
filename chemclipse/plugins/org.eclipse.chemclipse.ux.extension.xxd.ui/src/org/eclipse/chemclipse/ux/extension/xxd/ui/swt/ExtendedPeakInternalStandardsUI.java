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

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.model.quantitation.InternalStandard;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.components.peaks.InternalStandardsListUI;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PeakSupport;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class ExtendedPeakInternalStandardsUI {

	private static final Logger logger = Logger.getLogger(ExtendedPeakInternalStandardsUI.class);
	//
	private static final String ACTION_INITIALIZE = "ACTION_INITIALIZE";
	private static final String ACTION_CANCEL = "ACTION_CANCEL";
	private static final String ACTION_ADD = "ACTION_ADD";
	private static final String ACTION_DELETE = "ACTION_DELETE";
	private static final String ACTION_SELECT = "ACTION_SELECT";
	//
	private Composite toolbarInfo;
	private Composite toolbarModify;
	private Label labelPeak;
	//
	private InternalStandardsListUI peakInternalStandardsListUI;
	//
	private Button buttonCancel;
	private Button buttonDelete;
	private Button buttonAdd;
	//
	private Text textName;
	private Text textConcentration;
	private Text textConcentrationUnit;
	private Text textResponseFactor;
	private Text textChemicalClass;
	private Button buttonInternalStandardAdd;
	//
	private IPeak peak;

	@Inject
	public ExtendedPeakInternalStandardsUI(Composite parent) {
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updatePeak();
	}

	public void update(IPeak peak) {

		this.peak = peak;
		labelPeak.setText(PeakSupport.getPeakLabel(peak));
		updatePeak();
	}

	private void updatePeak() {

		if(peak != null) {
			peakInternalStandardsListUI.setInput(peak.getInternalStandards());
		} else {
			peakInternalStandardsListUI.setInput(null);
		}
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		toolbarModify = createToolbarModify(parent);
		createInternalStandardsList(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
		PartSupport.setCompositeVisibility(toolbarModify, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(2, false));
		//
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarModify(composite);
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		composite.setVisible(false);
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
		composite.setLayout(new GridLayout(11, false));
		//
		createLabelName(composite);
		createTextName(composite);
		createLabelContent(composite);
		createTextConcentration(composite);
		createLabelConcentrationUnit(composite);
		createTextConcentrationUnit(composite);
		createLabelResponseFactor(composite);
		createTextResponseFactor(composite);
		createLabelChemicalClass(composite);
		createTextChemicalClass(composite);
		createButtonAddISTD(composite);
		//
		return composite;
	}

	private void createLabelName(Composite parent) {

		Label labelName = new Label(parent, SWT.NONE);
		labelName.setText("Name");
	}

	private void createTextName(Composite parent) {

		textName = new Text(parent, SWT.BORDER);
		textName.setText("");
		textName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createLabelContent(Composite parent) {

		Label labelContent = new Label(parent, SWT.NONE);
		labelContent.setText("Conc.");
	}

	private void createTextConcentration(Composite parent) {

		textConcentration = new Text(parent, SWT.BORDER);
		textConcentration.setText("");
		textConcentration.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createLabelConcentrationUnit(Composite parent) {

		Label labelConcentrationUnit = new Label(parent, SWT.NONE);
		labelConcentrationUnit.setText("Unit");
	}

	private void createTextConcentrationUnit(Composite parent) {

		textConcentrationUnit = new Text(parent, SWT.BORDER);
		textConcentrationUnit.setText("");
		textConcentrationUnit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createLabelResponseFactor(Composite parent) {

		Label labelResponseFactor = new Label(parent, SWT.NONE);
		labelResponseFactor.setText("RF");
	}

	private void createTextResponseFactor(Composite parent) {

		textResponseFactor = new Text(parent, SWT.BORDER);
		textResponseFactor.setText("1.0");
		textResponseFactor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createLabelChemicalClass(Composite parent) {

		Label labelChemicalClass = new Label(parent, SWT.NONE);
		labelChemicalClass.setText("Class");
	}

	private void createTextChemicalClass(Composite parent) {

		textChemicalClass = new Text(parent, SWT.BORDER);
		textChemicalClass.setText("");
		textChemicalClass.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createButtonAddISTD(Composite parent) {

		buttonInternalStandardAdd = new Button(parent, SWT.PUSH);
		buttonInternalStandardAdd.setText("");
		buttonInternalStandardAdd.setToolTipText("Add Internal Standard");
		buttonInternalStandardAdd.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		buttonInternalStandardAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Shell shell = Display.getCurrent().getActiveShell();
				if(peak == null) {
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
					messageBox.setText("Add ISTD");
					messageBox.setMessage("No peak has been selected.");
					messageBox.open();
				} else {
					try {
						String name = textName.getText().trim();
						double concentration = Double.parseDouble(textConcentration.getText().trim());
						String concentrationUnit = textConcentrationUnit.getText().trim();
						double responseFactor = Double.parseDouble(textResponseFactor.getText().trim());
						String chemicalClass = textChemicalClass.getText().trim();
						IInternalStandard internalStandard = new InternalStandard(name, concentration, concentrationUnit, responseFactor);
						internalStandard.setChemicalClass(chemicalClass);
						//
						if(peak.getInternalStandards().contains(internalStandard)) {
							MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
							messageBox.setText("Add ISTD");
							messageBox.setMessage("The ISTD exists already.");
							messageBox.open();
						} else {
							peak.addInternalStandard(internalStandard);
							textName.setText("");
							textConcentration.setText("");
							textConcentrationUnit.setText("");
							textResponseFactor.setText("1.0");
							textChemicalClass.setText("");
							enableButtonFields(ACTION_INITIALIZE);
							peakInternalStandardsListUI.update(peak.getInternalStandards(), true);
						}
					} catch(Exception e1) {
						logger.warn(e1);
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
						messageBox.setText("Add ISTD");
						messageBox.setMessage("Please check the content, response factor and unit values.");
						messageBox.open();
					}
				}
			}
		});
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
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void createInternalStandardsList(Composite parent) {

		peakInternalStandardsListUI = new InternalStandardsListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = peakInternalStandardsListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_SELECT);
			}
		});
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
				textConcentrationUnit.setEnabled(true);
				textResponseFactor.setEnabled(true);
				textChemicalClass.setEnabled(true);
				buttonInternalStandardAdd.setEnabled(true);
				break;
			case ACTION_DELETE:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_SELECT:
				buttonAdd.setEnabled(true);
				//
				if(peakInternalStandardsListUI.getTable().getSelectionIndex() >= 0) {
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
		textConcentrationUnit.setEnabled(enabled);
		textResponseFactor.setEnabled(enabled);
		textChemicalClass.setEnabled(enabled);
		buttonInternalStandardAdd.setEnabled(enabled);
	}
}
