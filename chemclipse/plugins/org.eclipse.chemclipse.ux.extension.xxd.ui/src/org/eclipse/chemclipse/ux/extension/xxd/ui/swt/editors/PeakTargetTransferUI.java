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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.TargetTransferSupport;
import org.eclipse.chemclipse.model.updates.IChromatogramSelectionUpdateListener;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.RetentionTimeValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ChromatogramSourceCombo;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ComboViewer;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PeakTargetTransferUI extends Composite implements IChromatogramSelectionUpdateListener {

	private static final Logger logger = Logger.getLogger(PeakTargetTransferUI.class);
	//
	private static final String DESCRIPTION = "Transfer Targets";
	private static final String TYPE_PEAKS = "Peaks";
	private static final String TYPE_SCANS = "Scans";
	private static final String[] TYPE_ITEMS = new String[]{TYPE_PEAKS, TYPE_SCANS};
	//
	private Label labelSource;
	private ChromatogramSourceCombo chromatogramSourceCombo;
	private ComboViewer comboViewerSink;
	private Combo comboType;
	private Text textTimeDelta;
	private Button checkBoxTransfer;
	private Button buttonExecute;
	//
	@SuppressWarnings("rawtypes")
	private IChromatogramSelection chromatogramSelectionSource;
	//
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public PeakTargetTransferUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void update(IChromatogramSelection chromatogramSelectionSource) {

		this.chromatogramSelectionSource = chromatogramSelectionSource;
		updateInput();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(4, false);
		composite.setLayout(gridLayout);
		//
		createLabelInfo(composite, "Chromatogram:");
		labelSource = createLabelSource(composite);
		//
		createLabelInfo(composite, "Destination:");
		chromatogramSourceCombo = createChromatogramSourceCombo(composite);
		//
		createLabelInfo(composite, "Target:");
		comboViewerSink = createComboViewerSink(composite);
		//
		createLabelInfo(composite, "Type:");
		comboType = createTypeCombo(composite);
		//
		createLabelInfo(composite, "Options:");
		textTimeDelta = createTextTargetDelta(composite);
		checkBoxTransfer = createCheckBoxTransferTargets(composite);
		buttonExecute = createButtonExecute(composite);
		//
		updateInput();
	}

	private void createLabelInfo(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
	}

	private Label createLabelSource(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		label.setLayoutData(gridData);
		label.setText("");
		return label;
	}

	private ChromatogramSourceCombo createChromatogramSourceCombo(Composite parent) {

		ChromatogramSourceCombo chromatogramSourceCombo = new ChromatogramSourceCombo(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		chromatogramSourceCombo.setLayoutData(gridData);
		//
		Combo combo = chromatogramSourceCombo.getCombo();
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateInput();
			}
		});
		//
		return chromatogramSourceCombo;
	}

	private ComboViewer createComboViewerSink(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ListContentProvider());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@SuppressWarnings("rawtypes")
			@Override
			public String getText(Object element) {

				if(element instanceof IChromatogramSelection) {
					/*
					 * Editor
					 */
					IChromatogramSelection chromatogramSelection = (IChromatogramSelection)element;
					String name = chromatogramSelection.getChromatogram().getName();
					String type = ChromatogramDataSupport.getChromatogramType(chromatogramSelection);
					return getChromatogramLabel(name, type, "External");
				} else if(element instanceof IChromatogram) {
					/*
					 * Reference
					 */
					IChromatogram chromatogram = (IChromatogram)element;
					String name = chromatogram.getName();
					String type = ChromatogramDataSupport.getChromatogramType(chromatogram);
					return getChromatogramLabel(name, type, "Internal");
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a sink chromatogram.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateWidgets();
			}
		});
		//
		return comboViewer;
	}

	private String getChromatogramLabel(String name, String type, String defaultName) {

		String label;
		if(name == null || "".equals(name.trim())) {
			label = defaultName + type;
		} else {
			label = name + " " + type;
		}
		//
		return label;
	}

	private Combo createTypeCombo(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setItems(TYPE_ITEMS);
		combo.select(0);
		combo.setToolTipText("Select a type (Peaks or Scans) to be transfered.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateWidgets();
			}
		});
		//
		return combo;
	}

	private Text createTextTargetDelta(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText(Double.toString(preferenceStore.getDouble(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME)));
		text.setToolTipText("Delta retention time in minutes.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		RetentionTimeValidator retentionTimeValidator = new RetentionTimeValidator();
		ControlDecoration controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		//
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * It crashes when the control decoration is created earlier in case
				 * more than one chromatogram is opened simultaneously via the
				 * open selected files button in the supplier file explorer.
				 */
				if(validate(retentionTimeValidator, controlDecoration, text)) {
					preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME, retentionTimeValidator.getRetentionTime());
				}
			}
		});
		//
		return text;
	}

	private Button createCheckBoxTransferTargets(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText("Best Target Only");
		button.setSelection(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY));
		button.setToolTipText("Transfer only the best matching target.");
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY, button.getSelection());
			}
		});
		//
		return button;
	}

	private Button createButtonExecute(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Transfer Targets");
		button.setToolTipText("Transfer the targets from the source to the sink chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("rawtypes")
			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewerSink.getStructuredSelection().getFirstElement();
				IChromatogramSelection chromatogramSelectionSink = chromatogramDataSupport.getChromatogramSelection(object);
				transferTargets(chromatogramSelectionSource, chromatogramSelectionSink, e.display.getActiveShell());
			}
		});
		//
		return button;
	}

	private void updateInput() {

		labelSource.setText(ChromatogramDataSupport.getChromatogramEditorLabel(chromatogramSelectionSource));
		//
		if(chromatogramSourceCombo.isSourceReferences() && chromatogramSelectionSource != null) {
			comboViewerSink.setInput(chromatogramSelectionSource.getChromatogram().getReferencedChromatograms());
		} else if(chromatogramSourceCombo.isSourceEditors()) {
			comboViewerSink.setInput(getFilteredEditorChromatogramSelections());
		} else {
			comboViewerSink.setInput(null);
		}
		//
		updateWidgets();
	}

	private void updateWidgets() {

		textTimeDelta.setEnabled(false);
		checkBoxTransfer.setEnabled(false);
		buttonExecute.setEnabled(false);
		/*
		 * Source and sink selected?
		 */
		chromatogramSourceCombo.setEnabled(chromatogramSelectionSource != null);
		//
		Object object = comboViewerSink.getStructuredSelection().getFirstElement();
		if(object instanceof IChromatogramSelection || object instanceof IChromatogram) {
			buttonExecute.setEnabled(true);
			checkBoxTransfer.setEnabled(true);
		}
		/*
		 * Peaks or Scans selected?
		 */
		textTimeDelta.setEnabled(comboType.getText().equals(TYPE_PEAKS));
	}

	@SuppressWarnings("rawtypes")
	private List<IChromatogramSelection> getFilteredEditorChromatogramSelections() {

		List<IChromatogramSelection> chromatogramSelections = new ArrayList<IChromatogramSelection>();
		if(chromatogramSelectionSource != null) {
			String nameSource = ChromatogramDataSupport.getChromatogramEditorLabel(chromatogramSelectionSource);
			for(IChromatogramSelection chromatogramSelectionTarget : editorUpdateSupport.getChromatogramSelections()) {
				String nameTarget = ChromatogramDataSupport.getChromatogramEditorLabel(chromatogramSelectionTarget);
				if(!nameSource.equals(nameTarget)) {
					chromatogramSelections.add(chromatogramSelectionTarget);
				}
			}
		}
		return chromatogramSelections;
	}

	private boolean validate(IValidator<Object> validator, ControlDecoration controlDecoration, Text text) {

		IStatus status = validator.validate(text.getText());
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

	@SuppressWarnings("rawtypes")
	private void transferTargets(IChromatogramSelection chromatogramSelectionSource, IChromatogramSelection chromatogramSelectionSink, Shell shell) {

		if(chromatogramSelectionSource != null) {
			if(chromatogramSelectionSink != null && chromatogramSelectionSink != chromatogramSelectionSource) {
				/*
				 * Question
				 */
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setText("Transfer Targets");
				messageBox.setMessage("Would you like to transfer the selected targets to chromatogram: " + ChromatogramDataSupport.getChromatogramEditorLabel(chromatogramSelectionSink) + "?");
				if(messageBox.open() == SWT.YES) {
					/*
					 * Transfer the targets.
					 */
					if(comboType.getText().equals(TYPE_PEAKS)) {
						transferPeakTargets(chromatogramSelectionSource, chromatogramSelectionSink, shell);
					} else {
						transferScanTargets(chromatogramSelectionSource, chromatogramSelectionSink, shell);
					}
				}
			} else {
				showWarnMessage(shell, "It's not possible to transfer targets to the same chromatogram.");
			}
		} else {
			showWarnMessage(shell, "A source chromatogram is not available.");
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void transferPeakTargets(IChromatogramSelection chromatogramSelectionSource, IChromatogramSelection chromatogramSelectionSink, Shell shell) {

		TargetTransferSupport targetTransferSupport = new TargetTransferSupport();
		//
		List<? extends IPeak> peaksSource = ChromatogramDataSupport.getPeaks(chromatogramSelectionSource, true);
		List<? extends IPeak> peaksSink = ChromatogramDataSupport.getPeaks(chromatogramSelectionSink, false);
		int retentionTimeDelta = (int)(preferenceStore.getDouble(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
		boolean useBestTargetOnly = preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY);
		//
		String message = targetTransferSupport.transferPeakTargets(peaksSource, peaksSink, retentionTimeDelta, useBestTargetOnly);
		if(message != null) {
			showWarnMessage(shell, message);
		} else {
			MessageDialog.openInformation(shell, DESCRIPTION, "The peak targets have been transfered successfully.");
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void transferScanTargets(IChromatogramSelection chromatogramSelectionSource, IChromatogramSelection chromatogramSelectionSink, Shell shell) {

		TargetTransferSupport targetTransferSupport = new TargetTransferSupport();
		//
		List<IScan> scansSource = ChromatogramDataSupport.getIdentifiedScans(chromatogramSelectionSource.getChromatogram(), chromatogramSelectionSource);
		IChromatogram chromatogramSink = chromatogramSelectionSink.getChromatogram();
		boolean useBestTargetOnly = preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY);
		//
		String message = targetTransferSupport.transferScanTargets(scansSource, chromatogramSink, useBestTargetOnly);
		if(message != null) {
			showWarnMessage(shell, message);
		} else {
			MessageDialog.openInformation(shell, DESCRIPTION, "The scan targets have been transfered successfully.");
		}
	}

	private void showWarnMessage(Shell shell, String message) {

		logger.warn(message);
		MessageDialog.openWarning(shell, DESCRIPTION, message);
	}
}
