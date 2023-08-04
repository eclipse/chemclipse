/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.model.identifier.IIdentifierSettings;
import org.eclipse.chemclipse.model.identifier.PenaltyCalculation;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.PenaltyCalculationModel;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class PenaltyCalculationUI extends Composite {

	private Button buttonClipboard;
	private ComboViewer comboViewerPenaltyCalculation;
	private Text textReferenceValue;
	private Text textPenaltyWindow;
	private Spinner spinnerPenaltyLevelFactor;
	private Spinner spinnerMaxPenalty;
	private Button buttonCalculate;
	//
	private PenaltyCalculationModel penaltyCalculationModel;
	//
	private IUpdateListener updateListener;

	public PenaltyCalculationUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public PenaltyCalculationModel getPenaltyCalculationModel() {

		return penaltyCalculationModel;
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(7, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		buttonClipboard = createButtonClipboard(this);
		comboViewerPenaltyCalculation = createComboViewer(this);
		textReferenceValue = createText(this, "Reference", 0.0d);
		textPenaltyWindow = createText(this, "Penalty Window", 10.0d);
		spinnerPenaltyLevelFactor = createSpinnerPenaltyLevelFactor(this);
		spinnerMaxPenalty = createSpinnerMaxPenalty(this);
		buttonCalculate = createButtonCalculate(this);
		//
		initialize();
	}

	private void initialize() {

		comboViewerPenaltyCalculation.setInput(PenaltyCalculation.values());
		comboViewerPenaltyCalculation.getCombo().select(0);
		//
		updateWidgets();
	}

	private Button createButtonClipboard(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Copy settings to clipboard");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COPY_CLIPBOARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String lineDelimiter = OperatingSystemUtils.getLineDelimiter();
				StringBuilder builder = new StringBuilder();
				PenaltyCalculation penaltyCalculation = getPenaltySelection();
				//
				builder.append("Penalty Calculation: ");
				builder.append(penaltyCalculation.label());
				builder.append(lineDelimiter);
				//
				builder.append("Penalty Window: ");
				String penaltyWindow;
				switch(penaltyCalculation) {
					case RETENTION_TIME_MS:
						int retentionTimeMilliseconds = (int)getValue(textPenaltyWindow);
						penaltyWindow = Integer.toString(retentionTimeMilliseconds);
						break;
					case RETENTION_TIME_MIN:
						double retentionTimeMinutes = getValue(textPenaltyWindow);
						penaltyWindow = Double.toString(retentionTimeMinutes);
						break;
					case RETENTION_INDEX:
						double retentionIndex = getValue(textPenaltyWindow);
						penaltyWindow = Double.toString(retentionIndex);
						break;
					default:
						penaltyWindow = "0";
						break;
				}
				builder.append(penaltyWindow);
				builder.append(lineDelimiter);
				//
				builder.append("Penalty Calculation Level Factor: ");
				builder.append(getValue(spinnerPenaltyLevelFactor));
				builder.append(lineDelimiter);
				//
				builder.append("Max Penalty: ");
				builder.append(getValue(spinnerMaxPenalty));
				//
				Object[] data = new Object[]{builder.toString()};
				//
				TextTransfer textTransfer = TextTransfer.getInstance();
				Transfer[] dataTypes = new Transfer[]{textTransfer};
				Clipboard clipboard = new Clipboard(Display.getDefault());
				clipboard.setContents(data, dataTypes);
				clipboard.dispose();
			}
		});
		//
		return button;
	}

	private ComboViewer createComboViewer(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof PenaltyCalculation) {
					return ((PenaltyCalculation)element).label();
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select the penalty calculation type.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateWidgets();
				calculate();
			}
		});
		//
		return comboViewer;
	}

	private Text createText(Composite parent, String tooltip, double selection) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText(Double.toString(selection));
		text.setToolTipText(tooltip);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				updateButtons();
				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					calculate();
				}
			}
		});
		//
		return text;
	}

	private Spinner createSpinnerPenaltyLevelFactor(Composite parent) {

		int min = (int)IIdentifierSettings.MIN_PENALTY_LEVEL_FACTOR;
		int max = (int)IIdentifierSettings.MAX_PENALTY_LEVEL_FACTOR;
		int selection = (int)IIdentifierSettings.DEF_PENALTY_LEVEL_FACTOR;
		return createSpinner(this, "Penalty Level Factor", min, max, selection);
	}

	private Spinner createSpinnerMaxPenalty(Composite parent) {

		int min = (int)IIdentifierSettings.MIN_PENALTY_MATCH_FACTOR;
		int max = (int)IIdentifierSettings.MAX_PENALTY_MATCH_FACTOR;
		int selection = (int)IIdentifierSettings.DEF_PENALTY_MATCH_FACTOR;
		return createSpinner(this, "Max Penalty", min, max, selection);
	}

	private Spinner createSpinner(Composite parent, String tooltip, int min, int max, int selection) {

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setToolTipText(tooltip);
		spinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		spinner.setIncrement(1);
		spinner.setMinimum(min);
		spinner.setMaximum(max);
		spinner.setSelection(selection);
		//
		spinner.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				calculate();
			}
		});
		//
		spinner.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				updateButtons();
				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					calculate();
				}
			}
		});
		//
		return spinner;
	}

	private Button createButtonCalculate(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Calculate");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				calculate();
			}
		});
		//
		return button;
	}

	private PenaltyCalculation getPenaltySelection() {

		Object object = comboViewerPenaltyCalculation.getStructuredSelection().getFirstElement();
		if(object instanceof PenaltyCalculation) {
			return (PenaltyCalculation)object;
		}
		//
		return PenaltyCalculation.NONE;
	}

	private boolean validate() {

		return getValue(textReferenceValue) != -1 && getValue(textPenaltyWindow) != -1 && getValue(spinnerPenaltyLevelFactor) != -1 && getValue(spinnerMaxPenalty) != -1;
	}

	private void updateWidgets() {

		boolean enabled = true;
		PenaltyCalculation penaltyCalculation = getPenaltySelection();
		//
		switch(penaltyCalculation) {
			case RETENTION_TIME_MS:
				textReferenceValue.setToolTipText("Retention Time Reference [min]");
				break;
			case RETENTION_TIME_MIN:
				textReferenceValue.setToolTipText("Retention Time Reference [min]");
				break;
			case RETENTION_INDEX:
				textReferenceValue.setToolTipText("Retention Index Reference");
				break;
			default:
				enabled = false;
				break;
		}
		//
		updateWidgets(enabled);
		updateButtons();
	}

	private void updateWidgets(boolean enabled) {

		buttonClipboard.setEnabled(enabled);
		textReferenceValue.setEnabled(enabled);
		textPenaltyWindow.setEnabled(enabled);
		spinnerPenaltyLevelFactor.setEnabled(enabled);
		spinnerMaxPenalty.setEnabled(enabled);
		buttonCalculate.setEnabled(enabled);
	}

	private boolean updateButtons() {

		boolean enabled = validate();
		buttonClipboard.setEnabled(enabled);
		buttonCalculate.setEnabled(enabled);
		return enabled;
	}

	private void calculate() {

		penaltyCalculationModel = null;
		if(updateButtons()) {
			PenaltyCalculation penaltyCalculation = getPenaltySelection();
			double referenceValue = getValue(textReferenceValue);
			double penaltyWindow = getValue(textPenaltyWindow);
			double penaltyLevelFactor = getValue(spinnerPenaltyLevelFactor);
			double maxPenalty = getValue(spinnerMaxPenalty);
			penaltyCalculationModel = new PenaltyCalculationModel(penaltyCalculation, referenceValue, penaltyWindow, penaltyLevelFactor, maxPenalty);
			fireUpdate();
		}
	}

	private double getValue(Text text) {

		try {
			return Double.parseDouble(text.getText().trim());
		} catch(NumberFormatException e) {
			return -1;
		}
	}

	private double getValue(Spinner spinner) {

		return spinner.getSelection();
	}

	private void fireUpdate() {

		if(updateListener != null) {
			updateListener.update();
		}
	}
}