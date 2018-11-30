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
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.ChromatogramLengthModifier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;

public class ChromatogramAlignmentUI extends Composite implements IChromatogramSelectionUpdateListener {

	private static final Logger logger = Logger.getLogger(ChromatogramAlignmentUI.class);
	//
	private static final String MODIFY_LENGTH_SHORTEST = "MODIFY_LENGTH_SHORTEST";
	private static final String MODIFY_LENGTH_SELECTED = "MODIFY_LENGTH_SELECTED";
	private static final String MODIFY_LENGTH_LONGEST = "MODIFY_LENGTH_LONGEST";
	private static final String MODIFY_LENGTH_ADJUST = "MODIFY_LENGTH_ADJUST";
	//
	private List<Button> buttons = new ArrayList<>();
	//
	@SuppressWarnings("rawtypes")
	private IChromatogramSelection chromatogramSelectionSource;
	//
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public ChromatogramAlignmentUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void update(IChromatogramSelection chromatogramSelectionSource) {

		this.chromatogramSelectionSource = chromatogramSelectionSource;
		enableButtons();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		GridLayout gridLayout = new GridLayout(8, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		// TODO Option Box references, editors
		createLabel(composite, "Use this methods to shrink/expand the editor chromatograms:");
		buttons.add(createButtonShrinkChromatograms(composite));
		buttons.add(createButtonAlignChromatograms(composite));
		buttons.add(createButtonStretchChromatograms(composite));
		buttons.add(createButtonAdjustChromatograms(composite));
		createVerticalSeparator(composite);
		createLabel(composite, "Set selected Range:");
		buttons.add(createButtonSetRanges(composite));
	}

	private void createLabel(Composite parent, String message) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(message);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private Button createButtonShrinkChromatograms(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Shrink the chromatograms to the smallest chromatogram of all open editors.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHRINK_CHROMATOGRAMS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifyChromatogramLength(MODIFY_LENGTH_SHORTEST);
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

				modifyChromatogramLength(MODIFY_LENGTH_SELECTED);
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

				modifyChromatogramLength(MODIFY_LENGTH_LONGEST);
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

				modifyChromatogramLength(MODIFY_LENGTH_ADJUST);
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
					MessageDialog.openInformation(button.getShell(), "Range Selection", "The selected editor range has been set successfully to all opened chromatograms.");
				}
			}
		});
		//
		return button;
	}

	private void enableButtons() {

		boolean enabled = chromatogramSelectionSource != null;
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

	@SuppressWarnings("rawtypes")
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
			for(IChromatogramSelection selection : getTargetChromatogramSelections()) {
				if(selection != chromatogramSelectionSource) {
					/*
					 * Don't fire an update. The next time the selection is on focus,
					 * the correct range will be loaded.
					 * selection.fireUpdateChange(true);
					 */
					selection.setStartRetentionTime(startRetentionTime);
					selection.setStopRetentionTime(stopRetentionTime);
					if(setChromatogramIntensityRange) {
						selection.setStartAbundance(startAbundance);
						selection.setStopAbundance(stopAbundance);
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("rawtypes")
	private void modifyChromatogramLength(String modifyLengthType) {

		MessageBox messageBox = new MessageBox(DisplayUtils.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Modify Chromatogram Length");
		messageBox.setMessage("Would you like to modify the length of all opened chromatograms? Peaks will be deleted.");
		if(messageBox.open() == SWT.YES) {
			IChromatogram chromatogram = getChromatogram(modifyLengthType);
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
				for(IChromatogramSelection chromatogramSelection : getTargetChromatogramSelections()) {
					if(realignChromatogram(modifyLengthType, chromatogramSelection, chromatogram)) {
						IRunnableWithProgress runnable = new ChromatogramLengthModifier(chromatogramSelection, scanDelay, chromatogramLength);
						ProgressMonitorDialog monitor = new ProgressMonitorDialog(DisplayUtils.getShell());
						try {
							monitor.run(true, true, runnable);
						} catch(InvocationTargetException e) {
							logger.warn(e);
						} catch(InterruptedException e) {
							logger.warn(e);
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

	@SuppressWarnings("rawtypes")
	private boolean realignChromatogram(String modifyLengthType, IChromatogramSelection chromatogramSelection, IChromatogram chromatogram) {

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

	@SuppressWarnings("rawtypes")
	private IChromatogram getChromatogram(String modifyLengthType) {

		IChromatogram chromatogram;
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
	@SuppressWarnings("rawtypes")
	private IChromatogram getShortestChromatogram() {

		IChromatogram chromatogram = null;
		int maxRetentionTime = Integer.MAX_VALUE;
		for(IChromatogramSelection chromatogramSelection : getTargetChromatogramSelections()) {
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
	@SuppressWarnings("rawtypes")
	private IChromatogram getLongestChromatogram() {

		IChromatogram chromatogram = null;
		int minRetentionTime = Integer.MIN_VALUE;
		for(IChromatogramSelection chromatogramSelection : getTargetChromatogramSelections()) {
			if(chromatogramSelection.getChromatogram().getStopRetentionTime() > minRetentionTime) {
				minRetentionTime = chromatogramSelection.getChromatogram().getStopRetentionTime();
				chromatogram = chromatogramSelection.getChromatogram();
			}
		}
		return chromatogram;
	}

	@SuppressWarnings("rawtypes")
	private List<IChromatogramSelection> getTargetChromatogramSelections() {

		List<IChromatogramSelection> selections = new ArrayList<>();
		boolean useReferencedChromatograms = false;
		if(useReferencedChromatograms && chromatogramSelectionSource != null) {
			//
		} else {
			selections.addAll(editorUpdateSupport.getChromatogramSelections());
		}
		//
		return selections;
	}
}
