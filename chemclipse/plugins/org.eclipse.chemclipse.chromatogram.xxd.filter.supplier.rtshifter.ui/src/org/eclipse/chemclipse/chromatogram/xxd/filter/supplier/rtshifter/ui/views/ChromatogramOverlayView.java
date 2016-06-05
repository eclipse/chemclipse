/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
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
import org.eclipse.chemclipse.swt.ui.support.IOffset;
import org.eclipse.chemclipse.swt.ui.support.Offset;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class ChromatogramOverlayView extends AbstractChromatogramOverlayView {

	private static final Logger logger = Logger.getLogger(ChromatogramOverlayView.class);
	private static final Offset LOCK_OFFSET = new Offset(0.0d, 0.0d);
	//
	@Inject
	private Composite composite;
	//
	private MultipleChromatogramOffsetUI chromatogramOverlayUI;
	private Shell shell;
	private Cursor cursor;
	//
	private Button buttonEditSelectedChromatogram;
	private Button buttonLockOffset;
	private Button buttonOffsetLeft;
	private Button buttonOffsetLeftFast;
	private Button buttonOffsetRight;
	private Button buttonOffsetRightFast;
	private Button buttonOffsetUp;
	private Button buttonOffsetDown;
	private Button buttonApplyOffset;
	//
	private Label labelStatusEditChromatogram;
	private Label labelStatusDataDisplay;
	private Label labelStatusLockOffset;

	@Inject
	public ChromatogramOverlayView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
		shell = Display.getCurrent().getActiveShell();
		cursor = shell.getCursor();
	}

	@PostConstruct
	private void createControl() {

		composite.setLayout(new GridLayout(4, false));
		//
		createButtonBar(composite);
		createOverlayChart(composite);
	}

	private void createButtonBar(Composite composite) {

		Composite compositeButtonsLeft = new Composite(composite, SWT.NONE);
		compositeButtonsLeft.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeButtonsLeft.setLayout(new GridLayout(1, false));
		//
		Composite compositeButtonsCenterLeft = new Composite(composite, SWT.NONE);
		compositeButtonsCenterLeft.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeButtonsCenterLeft.setLayout(new GridLayout(1, false));
		//
		Composite compositeButtonsCenterRight = new Composite(composite, SWT.NONE);
		compositeButtonsCenterRight.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeButtonsCenterRight.setLayout(new GridLayout(1, false));
		//
		Composite compositeButtonsRight = new Composite(composite, SWT.NONE);
		GridData gridDataCompositeRight = new GridData(GridData.FILL_HORIZONTAL);
		gridDataCompositeRight.horizontalAlignment = SWT.END;
		compositeButtonsRight.setLayoutData(gridDataCompositeRight);
		compositeButtonsRight.setLayout(new GridLayout(11, false));
		//
		createButtonsLeft(compositeButtonsLeft);
		createButtonsCenterLeft(compositeButtonsCenterLeft);
		createButtonsCenterRight(compositeButtonsCenterRight);
		createButtonsRight(compositeButtonsRight);
		//
		setWidgetStatus();
	}

	private void createButtonsLeft(Composite composite) {

		labelStatusEditChromatogram = new Label(composite, SWT.BORDER);
		labelStatusEditChromatogram.setText("");
		labelStatusEditChromatogram.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createButtonsCenterLeft(Composite composite) {

		labelStatusDataDisplay = new Label(composite, SWT.BORDER);
		labelStatusDataDisplay.setText("");
		labelStatusDataDisplay.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createButtonsCenterRight(Composite composite) {

		labelStatusLockOffset = new Label(composite, SWT.BORDER);
		labelStatusLockOffset.setText("");
		labelStatusLockOffset.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createButtonsRight(Composite composite) {

		createButtonEditSelectedChromatogram(composite);
		createButtonLockOffset(composite);
		createButtonSettings(composite);
		createButtonReset(composite);
		createButtonOffsetLeft(composite);
		createButtonOffsetLeftFast(composite);
		createButtonOffsetRight(composite);
		createButtonOffsetRightFast(composite);
		createButtonOffsetUp(composite);
		createButtonOffsetDown(composite);
		createButtonApplyOffset(composite);
	}

	private void createButtonEditSelectedChromatogram(Composite composite) {

		buttonEditSelectedChromatogram = new Button(composite, SWT.PUSH);
		buttonEditSelectedChromatogram.setText("");
		buttonEditSelectedChromatogram.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PIN_CHROMATOGRAM, IApplicationImage.SIZE_16x16));
		buttonEditSelectedChromatogram.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceSupplier.toggleEditSelectedChromatogram();
				if(PreferenceSupplier.isEditSelectedChromatogram()) {
					resetOffsets();
				}
				update(getChromatogramSelection(), true);
			}
		});
	}

	private void createButtonLockOffset(Composite composite) {

		buttonLockOffset = new Button(composite, SWT.PUSH);
		buttonLockOffset.setText("");
		buttonLockOffset.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_LOCK_OFFSET, IApplicationImage.SIZE_16x16));
		buttonLockOffset.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceSupplier.toggleLockOffset();
				setLockOffset();
				update(getChromatogramSelection(), true);
			}
		});
	}

	private void createButtonSettings(Composite composite) {

		Button buttonSettings = new Button(composite, SWT.PUSH);
		buttonSettings.setText("");
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
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					/*
					 * Update the chromatogram.
					 */
					update(getChromatogramSelection(), false);
				}
			}
		});
	}

	private void createButtonReset(Composite composite) {

		Button buttonReset = new Button(composite, SWT.PUSH);
		buttonReset.setText("");
		buttonReset.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		buttonReset.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				resetOffsets();
				update(getChromatogramSelection(), false);
			}
		});
	}

	private void createButtonOffsetLeft(Composite composite) {

		buttonOffsetLeft = new Button(composite, SWT.PUSH);
		buttonOffsetLeft.setText("");
		buttonOffsetLeft.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_LEFT, IApplicationImage.SIZE_16x16));
		buttonOffsetLeft.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(PreferenceSupplier.isEditSelectedChromatogram()) {
					shiftChromatogram(PreferenceSupplier.getMillisecondsToShiftBackward());
				} else {
					setWaitCursor();
					PreferenceSupplier.decreaseXOffset();
					setXLockedOffsetConditionally();
					update(getChromatogramSelection(), false);
					setDefaultCursor();
				}
			}
		});
	}

	private void createButtonOffsetLeftFast(Composite composite) {

		buttonOffsetLeftFast = new Button(composite, SWT.PUSH);
		buttonOffsetLeftFast.setText("");
		buttonOffsetLeftFast.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_LEFT_FAST, IApplicationImage.SIZE_16x16));
		buttonOffsetLeftFast.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(PreferenceSupplier.isEditSelectedChromatogram()) {
					shiftChromatogram(PreferenceSupplier.getMillisecondsToShiftFastBackward());
				} else {
					setWaitCursor();
					PreferenceSupplier.decreaseXOffsetFast();
					setXLockedOffsetConditionally();
					update(getChromatogramSelection(), false);
					setDefaultCursor();
				}
			}
		});
	}

	private void createButtonOffsetRight(Composite composite) {

		buttonOffsetRight = new Button(composite, SWT.PUSH);
		buttonOffsetRight.setText("");
		buttonOffsetRight.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_RIGHT, IApplicationImage.SIZE_16x16));
		buttonOffsetRight.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(PreferenceSupplier.isEditSelectedChromatogram()) {
					shiftChromatogram(PreferenceSupplier.getMillisecondsToShiftForward());
				} else {
					setWaitCursor();
					PreferenceSupplier.increaseXOffset();
					setXLockedOffsetConditionally();
					update(getChromatogramSelection(), false);
					setDefaultCursor();
				}
			}
		});
	}

	private void createButtonOffsetRightFast(Composite composite) {

		buttonOffsetRightFast = new Button(composite, SWT.PUSH);
		buttonOffsetRightFast.setText("");
		buttonOffsetRightFast.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_RIGHT_FAST, IApplicationImage.SIZE_16x16));
		buttonOffsetRightFast.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(PreferenceSupplier.isEditSelectedChromatogram()) {
					shiftChromatogram(PreferenceSupplier.getMillisecondsToShiftFastForward());
				} else {
					setWaitCursor();
					PreferenceSupplier.increaseXOffsetFast();
					setXLockedOffsetConditionally();
					update(getChromatogramSelection(), false);
					setDefaultCursor();
				}
			}
		});
	}

	private void createButtonOffsetUp(Composite composite) {

		buttonOffsetUp = new Button(composite, SWT.PUSH);
		buttonOffsetUp.setText("");
		buttonOffsetUp.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_UP, IApplicationImage.SIZE_16x16));
		buttonOffsetUp.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setWaitCursor();
				PreferenceSupplier.increaseYOffset();
				setYLockedOffsetConditionally();
				update(getChromatogramSelection(), false);
				setDefaultCursor();
			}
		});
	}

	private void createButtonOffsetDown(Composite composite) {

		buttonOffsetDown = new Button(composite, SWT.PUSH);
		buttonOffsetDown.setText("");
		buttonOffsetDown.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_DOWN, IApplicationImage.SIZE_16x16));
		buttonOffsetDown.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setWaitCursor();
				PreferenceSupplier.decreaseYOffset();
				setYLockedOffsetConditionally();
				update(getChromatogramSelection(), false);
				setDefaultCursor();
			}
		});
	}

	private void createButtonApplyOffset(Composite composite) {

		buttonApplyOffset = new Button(composite, SWT.PUSH);
		buttonApplyOffset.setText("");
		buttonApplyOffset.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE_ADD, IApplicationImage.SIZE_16x16));
		buttonApplyOffset.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.YES | SWT.NO | SWT.ICON_WARNING);
				messageBox.setText("Apply Offset");
				messageBox.setMessage("Would you like to apply the offset to all chromatograms?");
				if(messageBox.open() == SWT.YES) {
					shiftChromatograms();
				}
			}
		});
	}

	private void createOverlayChart(Composite composite) {

		chromatogramOverlayUI = new MultipleChromatogramOffsetUI(composite, SWT.NONE, new AxisTitlesIntensityScale());
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 4;
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
			setChromatogramOverlayOffset();
			List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(chromatogramSelection, false);
			chromatogramOverlayUI.updateSelection(chromatogramSelections, forceReload);
		}
	}

	private void setWidgetStatus() {

		int xOffset = PreferenceSupplier.getOverlayXOffset();
		int yOffset = PreferenceSupplier.getOverlayYOffset();
		boolean isLockOffset = PreferenceSupplier.isLockOffset();
		boolean isEditSelectedChromatogram = PreferenceSupplier.isEditSelectedChromatogram();
		//
		if((xOffset == 0 && yOffset == 0) || isEditSelectedChromatogram) {
			labelStatusDataDisplay.setText("REAL MODE");
			labelStatusDataDisplay.setBackground(Colors.GREEN);
		} else {
			labelStatusDataDisplay.setText("DISPLAY MODE");
			labelStatusDataDisplay.setBackground(Colors.YELLOW);
		}
		/*
		 * Lock Offset
		 */
		if(isLockOffset) {
			buttonLockOffset.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_UNLOCK_OFFSET, IApplicationImage.SIZE_16x16));
			labelStatusLockOffset.setText("LOCKED OFFSET");
			labelStatusLockOffset.setBackground(Colors.YELLOW);
		} else {
			buttonLockOffset.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_LOCK_OFFSET, IApplicationImage.SIZE_16x16));
			labelStatusLockOffset.setText("DYNAMIC OFFSET");
			labelStatusLockOffset.setBackground(Colors.GREEN);
		}
		/*
		 * Edit Selected Chromatogram
		 */
		buttonEditSelectedChromatogram.setEnabled(true);
		buttonOffsetLeft.setEnabled(true);
		buttonOffsetLeftFast.setEnabled(true);
		buttonOffsetRight.setEnabled(true);
		buttonOffsetRightFast.setEnabled(true);
		labelStatusEditChromatogram.setEnabled(true);
		//
		if(isEditSelectedChromatogram) {
			buttonEditSelectedChromatogram.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_UNPIN_CHROMATOGRAM, IApplicationImage.SIZE_16x16));
			labelStatusEditChromatogram.setText("EDIT CHROMATOGRAM");
			labelStatusEditChromatogram.setBackground(Colors.YELLOW);
			//
			buttonLockOffset.setEnabled(false);
			buttonOffsetUp.setEnabled(false);
			buttonOffsetDown.setEnabled(false);
			buttonApplyOffset.setEnabled(false);
			labelStatusLockOffset.setEnabled(false);
		} else {
			buttonEditSelectedChromatogram.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PIN_CHROMATOGRAM, IApplicationImage.SIZE_16x16));
			labelStatusEditChromatogram.setText("SHOW CHROMATOGRAM");
			labelStatusEditChromatogram.setBackground(Colors.GREEN);
			//
			buttonLockOffset.setEnabled(true);
			buttonOffsetUp.setEnabled(true);
			buttonOffsetDown.setEnabled(true);
			buttonApplyOffset.setEnabled(true);
			labelStatusLockOffset.setEnabled(true);
		}
	}

	private void setChromatogramOverlayOffset() {

		if(buttonLockOffset.getSelection()) {
			chromatogramOverlayUI.setOffset(LOCK_OFFSET);
		} else {
			int xOffset = PreferenceSupplier.getOverlayXOffset();
			int yOffset = PreferenceSupplier.getOverlayYOffset();
			IOffset offset = new Offset(xOffset, yOffset);
			//
			chromatogramOverlayUI.setOffset(offset);
		}
	}

	private void setWaitCursor() {

		shell.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_WAIT));
	}

	private void setDefaultCursor() {

		shell.setCursor(cursor);
	}

	private void setXLockedOffsetConditionally() {

		if(PreferenceSupplier.isLockOffset()) {
			int xOffset = PreferenceSupplier.getOverlayXOffset();
			List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(getChromatogramSelection(), false);
			int x = 0;
			for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
				chromatogramSelection.getOffset().setX(x);
				x += xOffset;
			}
		}
	}

	private void setYLockedOffsetConditionally() {

		if(PreferenceSupplier.isLockOffset()) {
			int yOffset = PreferenceSupplier.getOverlayYOffset();
			List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(getChromatogramSelection(), false);
			int y = 0;
			for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
				chromatogramSelection.getOffset().setY(y);
				y += yOffset;
			}
		}
	}

	/*
	 * Lock the offset
	 */
	private void setLockOffset() {

		boolean isLockOffset = PreferenceSupplier.isLockOffset();
		List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(getChromatogramSelection(), false);
		for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
			chromatogramSelection.setLockOffset(isLockOffset);
		}
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

	private void shiftChromatograms() {

		List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(getChromatogramSelection(), false);
		for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
			/*
			 * Get the retention time shift.
			 */
			int millisecondsToShift = 0;
			if(PreferenceSupplier.isLockOffset()) {
				millisecondsToShift = (int)chromatogramSelection.getOffset().getX();
			} else {
				millisecondsToShift = PreferenceSupplier.getOverlayXOffset();
			}
			runShiftChromatogram(chromatogramSelection, millisecondsToShift);
		}
		/*
		 * Finally, reset all offsets.
		 */
		resetOffsets();
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