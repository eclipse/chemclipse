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
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.ui.views;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.ui.modifier.FilterModifierShift;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.components.chromatogram.MultipleChromatogramOffsetUI;
import org.eclipse.chemclipse.swt.ui.preferences.SWTPreferencePage;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesIntensityScale;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.views.AbstractChromatogramOverlayView;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class ChromatogramShiftView extends AbstractChromatogramOverlayView {

	private static final Logger logger = Logger.getLogger(ChromatogramShiftView.class);
	//
	@Inject
	private Composite composite;
	//
	private MultipleChromatogramOffsetUI chromatogramOverlayUI;
	//
	private Button buttonShiftLeft;
	private Button buttonShiftLeftFast;
	private Button buttonShiftRight;
	private Button buttonShiftRightFast;
	//
	private Label labelStatusDataDisplay;

	@Inject
	public ChromatogramShiftView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		composite.setLayout(new GridLayout(2, false));
		//
		createButtonBar(composite);
		createOverlayChart(composite);
	}

	private void createButtonBar(Composite composite) {

		Composite compositeLeft = new Composite(composite, SWT.NONE);
		compositeLeft.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeLeft.setLayout(new GridLayout(1, false));
		//
		Composite compositeRight = new Composite(composite, SWT.NONE);
		GridData gridDataCompositeRight = new GridData(GridData.FILL_HORIZONTAL);
		gridDataCompositeRight.horizontalAlignment = SWT.END;
		compositeRight.setLayoutData(gridDataCompositeRight);
		compositeRight.setLayout(new GridLayout(5, false));
		//
		createHeaderLeft(compositeLeft);
		createHeaderRight(compositeRight);
		//
		setWidgetStatus();
	}

	private void createHeaderLeft(Composite composite) {

		labelStatusDataDisplay = new Label(composite, SWT.NONE);
		labelStatusDataDisplay.setText("DATA EDIT MODUS");
		labelStatusDataDisplay.setBackground(Colors.YELLOW);
		labelStatusDataDisplay.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createHeaderRight(Composite composite) {

		createButtonSettings(composite);
		createButtonOffsetLeft(composite);
		createButtonOffsetLeftFast(composite);
		createButtonOffsetRight(composite);
		createButtonOffsetRightFast(composite);
	}

	private void createButtonSettings(Composite composite) {

		Button buttonSettings = new Button(composite, SWT.PUSH);
		buttonSettings.setText("");
		buttonSettings.setToolTipText("Open Settings");
		buttonSettings.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		buttonSettings.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new SWTPreferencePage();
				preferencePage.setTitle("Display Settings");
				//
				IPreferencePage preferencePageOverlay = new PreferencePage();
				preferencePageOverlay.setTitle("RT Shifter/Offset settings");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageOverlay));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getCurrent().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Overlay/Shifter Settings");
				if(preferenceDialog.open() == Window.OK) {
					/*
					 * Update the chromatogram.
					 */
					update(getChromatogramSelection(), false);
				}
			}
		});
	}

	private void createButtonOffsetLeft(Composite composite) {

		buttonShiftLeft = new Button(composite, SWT.PUSH);
		buttonShiftLeft.setText("");
		buttonShiftLeft.setToolTipText("Shift left");
		buttonShiftLeft.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_LEFT, IApplicationImage.SIZE_16x16));
		buttonShiftLeft.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				shiftChromatogram(PreferenceSupplier.getMillisecondsToShiftBackward());
			}
		});
	}

	private void createButtonOffsetLeftFast(Composite composite) {

		buttonShiftLeftFast = new Button(composite, SWT.PUSH);
		buttonShiftLeftFast.setText("");
		buttonShiftLeftFast.setToolTipText("Shift left fast");
		buttonShiftLeftFast.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_LEFT_FAST, IApplicationImage.SIZE_16x16));
		buttonShiftLeftFast.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				shiftChromatogram(PreferenceSupplier.getMillisecondsToShiftFastBackward());
			}
		});
	}

	private void createButtonOffsetRight(Composite composite) {

		buttonShiftRight = new Button(composite, SWT.PUSH);
		buttonShiftRight.setText("");
		buttonShiftRight.setToolTipText("Shift right");
		buttonShiftRight.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_RIGHT, IApplicationImage.SIZE_16x16));
		buttonShiftRight.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				shiftChromatogram(PreferenceSupplier.getMillisecondsToShiftForward());
			}
		});
	}

	private void createButtonOffsetRightFast(Composite composite) {

		buttonShiftRightFast = new Button(composite, SWT.PUSH);
		buttonShiftRightFast.setText("");
		buttonShiftRightFast.setToolTipText("Shift right fast");
		buttonShiftRightFast.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_RIGHT_FAST, IApplicationImage.SIZE_16x16));
		buttonShiftRightFast.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				shiftChromatogram(PreferenceSupplier.getMillisecondsToShiftFastForward());
			}
		});
	}

	private void createOverlayChart(Composite composite) {

		chromatogramOverlayUI = new MultipleChromatogramOffsetUI(composite, SWT.NONE, new AxisTitlesIntensityScale());
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		chromatogramOverlayUI.setLayoutData(gridData);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		chromatogramOverlayUI.setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void update(IChromatogramSelection chromatogramSelection, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(chromatogramSelection)) {
			/*
			 * Update the offset of the view. It necessary, the user must
			 * restart the workbench in case of a change otherwise.
			 */
			setWidgetStatus();
			List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(chromatogramSelection, false);
			chromatogramOverlayUI.updateSelection(chromatogramSelections, forceReload);
		}
	}

	private void setWidgetStatus() {

		buttonShiftLeft.setEnabled(true);
		buttonShiftLeftFast.setEnabled(true);
		buttonShiftRight.setEnabled(true);
		buttonShiftRightFast.setEnabled(true);
	}

	/*
	 * Reset
	 */
	private void resetOffsets() {

		PreferenceSupplier.resetOffset();
		//
		List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(getChromatogramSelection(), false);
		for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
			chromatogramSelection.resetOffset();
		}
	}

	private void shiftChromatogram(int millisecondsToShift) {

		/*
		 * Reset Offsets before running the filter.
		 */
		resetOffsets();
		runShiftChromatogram(getChromatogramSelection(), millisecondsToShift);
	}

	private void runShiftChromatogram(IChromatogramSelection chromatogramSelection, int millisecondsToShift) {

		StatusLineLogger.setInfo(InfoType.MESSAGE, "Start RTShifter Filter");
		/*
		 * Do the operation.<br/> Open a progress monitor dialog.
		 */
		final Display display = Display.getCurrent();
		IRunnableWithProgress runnable = new FilterModifierShift(chromatogramSelection, millisecondsToShift);
		ProgressMonitorDialog monitor = new ProgressMonitorDialog(display.getActiveShell());
		try {
			/*
			 * Use true, true ... instead of false, true ... if the progress bar
			 * should be shown in action.
			 */
			monitor.run(true, true, runnable);
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(InterruptedException e) {
			logger.warn(e);
		}
		StatusLineLogger.setInfo(InfoType.MESSAGE, "RTShifter Filter finished");
	}
}