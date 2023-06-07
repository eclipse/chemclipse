/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.updates.IChromatogramSelectionUpdateListener;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.ChromatogramLengthModifier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ChromatogramSourceCombo;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
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

public class ChromatogramAlignmentUI extends Composite implements IChromatogramSelectionUpdateListener {

	private static final Logger logger = Logger.getLogger(ChromatogramAlignmentUI.class);
	//
	private static final String MODIFY_LENGTH_SHORTEST = "MODIFY_LENGTH_SHORTEST";
	private static final String MODIFY_LENGTH_SELECTED = "MODIFY_LENGTH_SELECTED";
	private static final String MODIFY_LENGTH_LONGEST = "MODIFY_LENGTH_LONGEST";
	private static final String MODIFY_LENGTH_ADJUST = "MODIFY_LENGTH_ADJUST";
	//
	private ChromatogramSourceCombo chromatogramSourceCombo;
	private List<Button> buttons = new ArrayList<>();
	//
	private IChromatogramSelection<?, ?>chromatogramSelectionSource = null;
	private List<IChromatogramSelection<?, ?>> chromatogramSelectionsInternal = new ArrayList<>();
	//
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public ChromatogramAlignmentUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public void update(IChromatogramSelection<?, ?>chromatogramSelectionSource) {

		this.chromatogramSelectionSource = chromatogramSelectionSource;
		enableButtons();
	}

	public void update(List<IChromatogramSelection<?, ?>> chromatogramSelectionsInternal) {

		this.chromatogramSelectionsInternal.clear();
		if(chromatogramSelectionsInternal != null) {
			this.chromatogramSelectionsInternal.addAll(chromatogramSelectionsInternal);
		}
		enableButtons();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridLayout gridLayout = new GridLayout(8, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		chromatogramSourceCombo = createChromatogramSourceCombo(composite);
		createVerticalSeparator(composite);
		buttons.add(createButtonShrinkChromatograms(composite));
		buttons.add(createButtonAlignChromatograms(composite));
		buttons.add(createButtonStretchChromatograms(composite));
		buttons.add(createButtonAdjustChromatograms(composite));
		createVerticalSeparator(composite);
		buttons.add(createButtonSetRanges(composite));
	}

	private ChromatogramSourceCombo createChromatogramSourceCombo(Composite parent) {

		ChromatogramSourceCombo chromatogramSourceCombo = new ChromatogramSourceCombo(parent, SWT.NONE);
		chromatogramSourceCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Combo combo = chromatogramSourceCombo.getCombo();
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtons();
			}
		});
		//
		return chromatogramSourceCombo;
	}

	private Button createButtonShrinkChromatograms(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Shrink the chromatograms to the smallest chromatogram of all open editors.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHRINK_CHROMATOGRAMS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifyChromatogramLength(e.display.getActiveShell(), MODIFY_LENGTH_SHORTEST);
			}
		});
		//
		return button;
	}

	private Button createButtonAlignChromatograms(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Align the chromatograms to the length of the selection.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ALIGN_CHROMATOGRAMS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifyChromatogramLength(e.display.getActiveShell(), MODIFY_LENGTH_SELECTED);
			}
		});
		//
		return button;
	}

	private Button createButtonStretchChromatograms(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Stretch the chromatograms to the widest chromatogram of all open editors.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STRETCH_CHROMATOGRAMS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifyChromatogramLength(e.display.getActiveShell(), MODIFY_LENGTH_LONGEST);
			}
		});
		//
		return button;
	}

	private Button createButtonAdjustChromatograms(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Adjust the chromatograms using the settings");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADJUST_CHROMATOGRAMS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifyChromatogramLength(e.display.getActiveShell(), MODIFY_LENGTH_ADJUST);
			}
		});
		//
		return button;
	}

	private Button createButtonSetRanges(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Set the time range for all editors.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(setRanges()) {
					MessageDialog.openInformation(e.display.getActiveShell(), "Range Selection", "The selected editor range has been set successfully to all opened chromatograms.");
				}
			}
		});
		//
		return button;
	}

	private void enableButtons() {

		boolean enabled = isActionValid();
		for(Button button : buttons) {
			button.setEnabled(enabled);
		}
	}

	private void createVerticalSeparator(Composite parent) {

		Label label = new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gridData = new GridData();
		gridData.heightHint = 35;
		label.setLayoutData(gridData);
	}

	private boolean setRanges() {

		if(chromatogramSelectionSource != null) {
			int startRetentionTime = chromatogramSelectionSource.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelectionSource.getStopRetentionTime();
			float startAbundance = chromatogramSelectionSource.getStartAbundance();
			float stopAbundance = chromatogramSelectionSource.getStopAbundance();
			boolean setChromatogramIntensityRange = preferenceStore.getBoolean(PreferenceConstants.P_SET_CHROMATOGRAM_INTENSITY_RANGE);
			/*
			 * Editor
			 */
			for(IChromatogramSelection<?, ?>selection : getTargetChromatogramSelections()) {
				if(selection != chromatogramSelectionSource) {
					/*
					 * Don't fire an update. The next time the selection is on focus,
					 * the correct range will be loaded.
					 * selection.fireUpdateChange(true);
					 */
					selection.setRangeRetentionTime(startRetentionTime, stopRetentionTime);
					if(setChromatogramIntensityRange) {
						selection.setStopAbundance(stopAbundance);
						selection.setStartAbundance(startAbundance);
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private void modifyChromatogramLength(Shell shell, String modifyLengthType) {

		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Modify Chromatogram Length");
		messageBox.setMessage("Would you like to modify the length of all opened chromatograms? Peaks will be deleted.");
		if(messageBox.open() == SWT.YES) {
			IChromatogram<?> chromatogram = getChromatogram(modifyLengthType);
			if(chromatogram != null) {
				/*
				 * Settings
				 */
				int scanDelay;
				int chromatogramLength;
				if(MODIFY_LENGTH_ADJUST.equals(modifyLengthType)) {
					scanDelay = preferenceStore.getInt(PreferenceConstants.P_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY);
					chromatogramLength = preferenceStore.getInt(PreferenceConstants.P_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH);
				} else {
					scanDelay = chromatogram.getScanDelay();
					chromatogramLength = chromatogram.getStopRetentionTime();
					preferenceStore.setValue(PreferenceConstants.P_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY, scanDelay);
					preferenceStore.setValue(PreferenceConstants.P_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH, chromatogramLength);
				}
				/*
				 * Modify chromatograms.
				 */
				for(IChromatogramSelection<?, ?> chromatogramSelection : getTargetChromatogramSelections()) {
					if(realignChromatogram(modifyLengthType, chromatogramSelection, chromatogram)) {
						IRunnableWithProgress runnable = new ChromatogramLengthModifier(chromatogramSelection, scanDelay, chromatogramLength);
						ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
						try {
							monitor.run(true, true, runnable);
						} catch(InvocationTargetException e) {
							logger.warn(e);
						} catch(InterruptedException e) {
							logger.warn(e);
							Thread.currentThread().interrupt();
						}
					}
					/*
					 * Refresh the master chromatogram on length change.
					 */
					if(!MODIFY_LENGTH_SELECTED.equals(modifyLengthType)) {
						update();
					}
				}
			}
		}
	}

	private boolean realignChromatogram(String modifyLengthType, IChromatogramSelection<?, ?> chromatogramSelection, IChromatogram<?> chromatogram) {

		/*
		 * Don't re-align the template chromatogram.
		 */
		if(MODIFY_LENGTH_ADJUST.equals(modifyLengthType)) {
			return true;
		} else {
			if(chromatogramSelection.getChromatogram() != chromatogram) {
				return true;
			}
			return false;
		}
	}

	private IChromatogram<?> getChromatogram(String modifyLengthType) {

		IChromatogram<?> chromatogram;
		switch(modifyLengthType) {
			case MODIFY_LENGTH_SHORTEST:
				chromatogram = getShortestChromatogram();
				break;
			case MODIFY_LENGTH_SELECTED:
			case MODIFY_LENGTH_ADJUST:
				if(chromatogramSelectionSource != null) {
					chromatogram = chromatogramSelectionSource.getChromatogram();
				} else {
					chromatogram = null;
				}
				break;
			case MODIFY_LENGTH_LONGEST:
				chromatogram = getLongestChromatogram();
				break;
			default:
				chromatogram = null;
				break;
		}
		return chromatogram;
	}

	/**
	 * May return null.
	 * 
	 * @return IChromatogram
	 */
	private IChromatogram<?> getShortestChromatogram() {

		IChromatogram<?> chromatogram = null;
		int maxRetentionTime = Integer.MAX_VALUE;
		for(IChromatogramSelection<?, ?> chromatogramSelection : getTargetChromatogramSelections()) {
			if(chromatogramSelection.getChromatogram().getStopRetentionTime() < maxRetentionTime) {
				maxRetentionTime = chromatogramSelection.getChromatogram().getStopRetentionTime();
				chromatogram = chromatogramSelection.getChromatogram();
			}
		}
		return chromatogram;
	}

	/**
	 * May return null.
	 * 
	 * @return IChromatogram
	 */
	private IChromatogram<?> getLongestChromatogram() {

		IChromatogram<?> chromatogram = null;
		int minRetentionTime = Integer.MIN_VALUE;
		for(IChromatogramSelection<?, ?> chromatogramSelection : getTargetChromatogramSelections()) {
			if(chromatogramSelection.getChromatogram().getStopRetentionTime() > minRetentionTime) {
				minRetentionTime = chromatogramSelection.getChromatogram().getStopRetentionTime();
				chromatogram = chromatogramSelection.getChromatogram();
			}
		}
		return chromatogram;
	}

	private boolean isActionValid() {

		boolean enabled = false;
		if(chromatogramSelectionSource != null) {
			/*
			 * Referenced / editor chromatogram available?
			 * Size > 1, because the master is also contained.
			 */
			if(chromatogramSourceCombo.isSourceReferences()) {
				enabled = chromatogramSelectionsInternal.size() > 1;
			} else if(chromatogramSourceCombo.isSourceEditors()) {
				enabled = editorUpdateSupport.getChromatogramSelections().size() > 1;
			}
		}
		return enabled;
	}

	@SuppressWarnings("rawtypes")
	private List<IChromatogramSelection> getTargetChromatogramSelections() {

		List<IChromatogramSelection> selections = new ArrayList<>();
		if(chromatogramSelectionSource != null) {
			/*
			 * Fetch referenced / editor chromatogram selections.
			 */
			if(chromatogramSourceCombo.isSourceReferences()) {
				selections.addAll(chromatogramSelectionsInternal);
			} else if(chromatogramSourceCombo.isSourceEditors()) {
				selections.addAll(editorUpdateSupport.getChromatogramSelections());
			}
		}
		//
		return selections;
	}
}
