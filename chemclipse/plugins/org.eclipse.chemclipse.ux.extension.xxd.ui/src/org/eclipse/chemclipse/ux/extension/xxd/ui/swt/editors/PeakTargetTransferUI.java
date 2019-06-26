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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.updates.IChromatogramSelectionUpdateListener;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
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
	private static final String DESCRIPTION = "Transfer Peak Target(s)";
	//
	private Label labelSource;
	private ChromatogramSourceCombo chromatogramSourceCombo;
	private ComboViewer comboViewerSink;
	private Button buttonExecute;
	//
	@SuppressWarnings("rawtypes")
	private IChromatogramSelection chromatogramSelectionSource;
	//
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private TargetExtendedComparator comparator = new TargetExtendedComparator(SortOrder.DESC);

	public PeakTargetTransferUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void update(IChromatogramSelection chromatogramSelectionSource) {

		this.chromatogramSelectionSource = chromatogramSelectionSource;
		updateWidgets();
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
		createLabelInfo(composite, "Options:");
		createTextTargetDelta(composite);
		createCheckBoxTransferTargets(composite);
		buttonExecute = createButtonExecute(composite);
		//
		updateWidgets();
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

				updateWidgets();
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

				enableButtons();
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

	private void createTextTargetDelta(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText(Double.toString(preferenceStore.getDouble(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME)));
		text.setToolTipText("Delta retention time in minutes.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		RetentionTimeValidator retentionTimeValidator = new RetentionTimeValidator();
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * It crashes when the control decoration is created earlier in case
				 * more than one chromatogram is opened simultaneously via the
				 * open selected files button in the supplier file explorer.
				 */
				ControlDecoration controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
				if(validate(retentionTimeValidator, controlDecoration, text)) {
					preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME, retentionTimeValidator.getRetentionTime());
				}
			}
		});
	}

	private void createCheckBoxTransferTargets(Composite parent) {

		Button checkBox = new Button(parent, SWT.CHECK);
		checkBox.setText("Best Target Only");
		checkBox.setSelection(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY));
		checkBox.setToolTipText("Transfer only the best matching target.");
		checkBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		checkBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				preferenceStore.setValue(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY, checkBox.getSelection());
			}
		});
	}

	private Button createButtonExecute(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Transfer Peaks");
		button.setToolTipText("Transfer the peak targets from the source to the sink chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewerSink.getStructuredSelection().getFirstElement();
				if(object instanceof IChromatogramSelection) {
					transferPeakTargets(e.display.getActiveShell());
				} else if(object instanceof IChromatogram) {
					transferPeakTargets(e.display.getActiveShell());
				}
			}
		});
		//
		return button;
	}

	private void updateWidgets() {

		labelSource.setText(ChromatogramDataSupport.getChromatogramEditorLabel(chromatogramSelectionSource));
		chromatogramSourceCombo.setEnabled(chromatogramSelectionSource != null);
		//
		if(chromatogramSourceCombo.isSourceReferences() && chromatogramSelectionSource != null) {
			comboViewerSink.setInput(chromatogramSelectionSource.getChromatogram().getReferencedChromatograms());
		} else if(chromatogramSourceCombo.isSourceEditors()) {
			comboViewerSink.setInput(getFilteredEditorChromatogramSelections());
		} else {
			comboViewerSink.setInput(null);
		}
		//
		enableButtons();
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

	private void enableButtons() {

		buttonExecute.setEnabled(false);
		//
		Object object = comboViewerSink.getStructuredSelection().getFirstElement();
		if(object instanceof IChromatogramSelection) {
			buttonExecute.setEnabled(true);
		} else if(object instanceof IChromatogram) {
			buttonExecute.setEnabled(true);
		}
	}

	private boolean validate(IValidator validator, ControlDecoration controlDecoration, Text text) {

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

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void transferPeakTargets(Shell shell) {

		if(chromatogramSelectionSource != null) {
			Object object = comboViewerSink.getStructuredSelection().getFirstElement();
			IChromatogramSelection chromatogramSelectionSink = chromatogramDataSupport.getChromatogramSelection(object);
			if(chromatogramSelectionSink != null && chromatogramSelectionSink != chromatogramSelectionSource) {
				/*
				 * Question
				 */
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setText("Transfer Peak Target(s)");
				messageBox.setMessage("Would you like to transfer the selected peak target(s) to chromatogram: " + chromatogramSelectionSink.getChromatogram().getName() + "?");
				if(messageBox.open() == SWT.YES) {
					/*
					 * Transfer the peak targets.
					 */
					int retentionTimeDelta = (int)(preferenceStore.getDouble(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					boolean useBestTargetOnly = preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY);
					//
					List<? extends IPeak> peaksSource = ChromatogramDataSupport.getPeaks(chromatogramSelectionSource, true);
					List<? extends IPeak> peaksSink = ChromatogramDataSupport.getPeaks(chromatogramSelectionSink, false);
					/*
					 * Are peaks available?
					 */
					if(peaksSource.size() > 0) {
						if(peaksSink.size() > 0) {
							/*
							 * Transfer OK
							 */
							for(IPeak peakSink : peaksSink) {
								for(IPeak peakSource : peaksSource) {
									int retentionTimePeakSink = peakSink.getPeakModel().getRetentionTimeAtPeakMaximum();
									int retentionTimePeakSource = peakSource.getPeakModel().getRetentionTimeAtPeakMaximum();
									if(isPeakInFocus(retentionTimePeakSink, retentionTimePeakSource, retentionTimeDelta)) {
										/*
										 * Best target or all?
										 */
										if(useBestTargetOnly) {
											IIdentificationTarget peakTarget = IIdentificationTarget.getBestIdentificationTarget(peakSource.getTargets(), comparator);
											transferPeakTarget(peakTarget, peakSink);
										} else {
											for(IIdentificationTarget peakTarget : peakSource.getTargets()) {
												transferPeakTarget(peakTarget, peakSink);
											}
										}
									}
								}
							}
							MessageDialog.openInformation(shell, DESCRIPTION, "The peak target(s) have been transfered successfully.");
						} else {
							showWarnMessage(shell, "The sink chromatogram contains no peaks.");
						}
					} else {
						showWarnMessage(shell, "The source chromatogram contains no peaks.");
					}
				}
			} else {
				showWarnMessage(shell, "It's not possible to transfer targets to the same chromatogram.");
			}
		} else {
			showWarnMessage(shell, "A source chromatogram is not available.");
		}
	}

	private void showWarnMessage(Shell shell, String message) {

		logger.warn(message);
		MessageDialog.openWarning(shell, DESCRIPTION, message);
	}

	private boolean isPeakInFocus(int retentionTimePeakSink, int retentionTimePeakSource, int retentionTimeDelta) {

		if(retentionTimePeakSink >= (retentionTimePeakSource - retentionTimeDelta) && retentionTimePeakSink <= (retentionTimePeakSource + retentionTimeDelta)) {
			return true;
		} else {
			return false;
		}
	}

	private void transferPeakTarget(IIdentificationTarget identificationTargetSource, IPeak peakSink) {

		ILibraryInformation libraryInformation = new LibraryInformation(identificationTargetSource.getLibraryInformation());
		IComparisonResult comparisonResult = new ComparisonResult(identificationTargetSource.getComparisonResult());
		IIdentificationTarget peakTargetSink = new IdentificationTarget(libraryInformation, comparisonResult);
		peakSink.getTargets().add(peakTargetSink);
	}
}
