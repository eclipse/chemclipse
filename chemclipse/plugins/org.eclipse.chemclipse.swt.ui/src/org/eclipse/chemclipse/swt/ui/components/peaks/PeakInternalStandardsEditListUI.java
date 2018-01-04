/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.components.peaks;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.model.quantitation.InternalStandard;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class PeakInternalStandardsEditListUI extends Composite {

	private static final Logger logger = Logger.getLogger(PeakInternalStandardsEditListUI.class);
	//
	private static final String ACTION_INITIALIZE = "ACTION_INITIALIZE";
	private static final String ACTION_CANCEL = "ACTION_CANCEL";
	private static final String ACTION_ADD = "ACTION_ADD";
	private static final String ACTION_DELETE = "ACTION_DELETE";
	private static final String ACTION_SELECT = "ACTION_SELECT";
	//
	private InternalStandardsListUI peakInternalStandardsListUI;
	//
	private Button buttonCancel;
	private Button buttonDelete;
	private Button buttonAdd;
	//
	private Label labelPeakInfo;
	private Text textName;
	private Text textConcentration;
	private Text textConcentrationUnit;
	private Text textResponseFactor;
	private Text textChemicalClass;
	private Button buttonInternalStandardAdd;
	//
	private IPeak peak;
	//
	private DecimalFormat decimalFormat;

	public PeakInternalStandardsEditListUI(Composite parent, int style) {
		super(parent, style);
		initialize();
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
	}

	public void update(IPeak peak, boolean forceReload) {

		this.peak = peak;
		setPeakLabel(peak);
		//
		if(peak != null) {
			peakInternalStandardsListUI.setInput(peak.getInternalStandards());
		} else {
			peakInternalStandardsListUI.setInput(null);
		}
	}

	private void initialize() {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		createLabelAndActionField(composite);
		createInputField(composite);
		createButtonField(composite);
		createTableField(composite);
		//
		enableButtonFields(ACTION_INITIALIZE);
	}

	private void createLabelAndActionField(Composite composite) {

		Composite compositeLeft = new Composite(composite, SWT.NONE);
		compositeLeft.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeLeft.setLayout(new GridLayout(1, false));
		//
		Composite compositeRight = new Composite(composite, SWT.NONE);
		GridData gridDataCompositeRight = new GridData(GridData.FILL_HORIZONTAL);
		gridDataCompositeRight.horizontalAlignment = SWT.END;
		compositeRight.setLayoutData(gridDataCompositeRight);
		compositeRight.setLayout(new GridLayout(3, false));
		//
		createHeaderLeft(compositeLeft);
		createHeaderRight(compositeRight);
	}

	private void createHeaderLeft(Composite composite) {

		labelPeakInfo = new Label(composite, SWT.NONE);
		labelPeakInfo.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 9;
		labelPeakInfo.setLayoutData(gridData);
	}

	private void createHeaderRight(Composite composite) {

		buttonCancel = new Button(composite, SWT.PUSH);
		buttonCancel.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CANCEL, IApplicationImage.SIZE_16x16));
		buttonCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_CANCEL);
			}
		});
		//
		buttonDelete = new Button(composite, SWT.PUSH);
		buttonDelete.setEnabled(false);
		buttonDelete.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		buttonDelete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(peak != null) {
					Table table = peakInternalStandardsListUI.getTable();
					int index = table.getSelectionIndex();
					if(index >= 0) {
						MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
						messageBox.setText("Delete ion(s)?");
						messageBox.setMessage("Would you like to delete the ISTD(s)?");
						if(messageBox.open() == SWT.OK) {
							//
							enableButtonFields(ACTION_DELETE);
							TableItem[] tableItems = table.getSelection();
							for(TableItem tableItem : tableItems) {
								Object object = tableItem.getData();
								if(object instanceof IInternalStandard) {
									IInternalStandard internalStandard = (IInternalStandard)object;
									peak.removeInternalStandard(internalStandard);
								}
							}
							peakInternalStandardsListUI.update(peak.getInternalStandards(), true);
						}
					}
				}
			}
		});
		//
		buttonAdd = new Button(composite, SWT.PUSH);
		buttonAdd.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		buttonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_ADD);
			}
		});
	}

	private void createInputField(Composite composite) {

		Composite compositeInput = new Composite(composite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		compositeInput.setLayoutData(gridData);
		compositeInput.setLayout(new GridLayout(10, false));
		//
		Label labelName = new Label(compositeInput, SWT.NONE);
		labelName.setText("Name");
		//
		textName = new Text(compositeInput, SWT.BORDER);
		textName.setText("");
		textName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Label labelContent = new Label(compositeInput, SWT.NONE);
		labelContent.setText("Conc.");
		//
		textConcentration = new Text(compositeInput, SWT.BORDER);
		textConcentration.setText("");
		textConcentration.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Label labelConcentrationUnit = new Label(compositeInput, SWT.NONE);
		labelConcentrationUnit.setText("Unit");
		//
		textConcentrationUnit = new Text(compositeInput, SWT.BORDER);
		textConcentrationUnit.setText("");
		textConcentrationUnit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Label labelResponseFactor = new Label(compositeInput, SWT.NONE);
		labelResponseFactor.setText("RF");
		//
		textResponseFactor = new Text(compositeInput, SWT.BORDER);
		textResponseFactor.setText("1.0");
		textResponseFactor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Label labelChemicalClass = new Label(compositeInput, SWT.NONE);
		labelChemicalClass.setText("Class");
		//
		textChemicalClass = new Text(compositeInput, SWT.BORDER);
		textChemicalClass.setText("");
		textChemicalClass.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createButtonField(Composite composite) {

		buttonInternalStandardAdd = new Button(composite, SWT.PUSH);
		buttonInternalStandardAdd.setText("Add");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		buttonInternalStandardAdd.setLayoutData(gridData);
		buttonInternalStandardAdd.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
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

	private void createTableField(Composite composite) {

		Composite compositeTable = new Composite(composite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		compositeTable.setLayoutData(gridData);
		compositeTable.setLayout(new FillLayout());
		//
		peakInternalStandardsListUI = new InternalStandardsListUI(compositeTable, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		peakInternalStandardsListUI.getTable().addSelectionListener(new SelectionAdapter() {

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

	private void setPeakLabel(IPeak peak) {

		if(peak != null) {
			IPeakModel peakModel = peak.getPeakModel();
			StringBuilder builder = new StringBuilder();
			builder.append("RT: ");
			builder.append(decimalFormat.format(peakModel.getRetentionTimeAtPeakMaximum() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
			builder.append(" | ");
			builder.append("Tailing: ");
			builder.append(decimalFormat.format(peakModel.getTailing()));
			builder.append(" | ");
			builder.append("Area: ");
			builder.append(Integer.valueOf(((int)peak.getIntegratedArea())).toString());
			labelPeakInfo.setText(builder.toString());
		} else {
			labelPeakInfo.setText("No peak has been selected yet.");
		}
	}
}
