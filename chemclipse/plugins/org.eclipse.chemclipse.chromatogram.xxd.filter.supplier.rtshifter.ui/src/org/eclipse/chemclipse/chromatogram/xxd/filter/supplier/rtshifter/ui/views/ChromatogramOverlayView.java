/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
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
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Shell;

public class ChromatogramOverlayView extends AbstractChromatogramOverlayView {

	private static final Offset LOCK_OFFSET = new Offset(0.0d, 0.0d);
	//
	@Inject
	private Composite composite;
	//
	private MultipleChromatogramOffsetUI chromatogramOverlayUI;
	private Shell shell;
	private Cursor cursor;
	//
	private Button buttonLockOffset;
	private Button buttonOffsetLeft;
	private Button buttonOffsetLeftFast;
	private Button buttonOffsetRight;
	private Button buttonOffsetRightFast;
	private Button buttonOffsetUp;
	private Button buttonOffsetDown;
	//
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

		Composite compositeLeft = new Composite(composite, SWT.NONE);
		compositeLeft.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeLeft.setLayout(new GridLayout(1, false));
		//
		Composite compositeCenter = new Composite(composite, SWT.NONE);
		compositeCenter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeCenter.setLayout(new GridLayout(1, false));
		//
		Composite compositeRight = new Composite(composite, SWT.NONE);
		GridData gridDataCompositeRight = new GridData(GridData.FILL_HORIZONTAL);
		gridDataCompositeRight.horizontalAlignment = SWT.END;
		compositeRight.setLayoutData(gridDataCompositeRight);
		compositeRight.setLayout(new GridLayout(9, false));
		//
		createButtonsCenterLeft(compositeLeft);
		createButtonsCenterRight(compositeCenter);
		createButtonsRight(compositeRight);
		//
		setWidgetStatus();
	}

	private void createButtonsCenterLeft(Composite composite) {

		labelStatusDataDisplay = new Label(composite, SWT.NONE);
		labelStatusDataDisplay.setText("");
		labelStatusDataDisplay.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createButtonsCenterRight(Composite composite) {

		labelStatusLockOffset = new Label(composite, SWT.NONE);
		labelStatusLockOffset.setText("");
		labelStatusLockOffset.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createButtonsRight(Composite composite) {

		createButtonLockOffset(composite);
		createButtonSettings(composite);
		createButtonReset(composite);
		createButtonOffsetLeft(composite);
		createButtonOffsetLeftFast(composite);
		createButtonOffsetRight(composite);
		createButtonOffsetRightFast(composite);
		createButtonOffsetUp(composite);
		createButtonOffsetDown(composite);
	}

	private void createButtonLockOffset(Composite composite) {

		buttonLockOffset = new Button(composite, SWT.PUSH);
		buttonLockOffset.setText("");
		buttonLockOffset.setToolTipText("Lock the offset");
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

	private void createButtonReset(Composite composite) {

		Button buttonReset = new Button(composite, SWT.PUSH);
		buttonReset.setText("");
		buttonReset.setToolTipText("Reset Settings");
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
		buttonOffsetLeft.setToolTipText("Move left");
		buttonOffsetLeft.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_LEFT, IApplicationImage.SIZE_16x16));
		buttonOffsetLeft.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setWaitCursor();
				PreferenceSupplier.decreaseXOffset();
				setXLockedOffsetConditionally();
				update(getChromatogramSelection(), false);
				setDefaultCursor();
			}
		});
	}

	private void createButtonOffsetLeftFast(Composite composite) {

		buttonOffsetLeftFast = new Button(composite, SWT.PUSH);
		buttonOffsetLeftFast.setText("");
		buttonOffsetLeftFast.setToolTipText("Move left fast");
		buttonOffsetLeftFast.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_LEFT_FAST, IApplicationImage.SIZE_16x16));
		buttonOffsetLeftFast.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setWaitCursor();
				PreferenceSupplier.decreaseXOffsetFast();
				setXLockedOffsetConditionally();
				update(getChromatogramSelection(), false);
				setDefaultCursor();
			}
		});
	}

	private void createButtonOffsetRight(Composite composite) {

		buttonOffsetRight = new Button(composite, SWT.PUSH);
		buttonOffsetRight.setText("");
		buttonOffsetRight.setToolTipText("Move right");
		buttonOffsetRight.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_RIGHT, IApplicationImage.SIZE_16x16));
		buttonOffsetRight.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setWaitCursor();
				PreferenceSupplier.increaseXOffset();
				setXLockedOffsetConditionally();
				update(getChromatogramSelection(), false);
				setDefaultCursor();
			}
		});
	}

	private void createButtonOffsetRightFast(Composite composite) {

		buttonOffsetRightFast = new Button(composite, SWT.PUSH);
		buttonOffsetRightFast.setText("");
		buttonOffsetRightFast.setToolTipText("Move right fast");
		buttonOffsetRightFast.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_RIGHT_FAST, IApplicationImage.SIZE_16x16));
		buttonOffsetRightFast.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setWaitCursor();
				PreferenceSupplier.increaseXOffsetFast();
				setXLockedOffsetConditionally();
				update(getChromatogramSelection(), false);
				setDefaultCursor();
			}
		});
	}

	private void createButtonOffsetUp(Composite composite) {

		buttonOffsetUp = new Button(composite, SWT.PUSH);
		buttonOffsetUp.setText("");
		buttonOffsetUp.setToolTipText("Move up");
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
		buttonOffsetDown.setToolTipText("Move down");
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

	private void createOverlayChart(Composite composite) {

		chromatogramOverlayUI = new MultipleChromatogramOffsetUI(composite, SWT.NONE, new AxisTitlesIntensityScale());
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
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
		//
		if((xOffset == 0 && yOffset == 0)) {
			labelStatusDataDisplay.setText("DISPLAY ORIGINAL");
			labelStatusDataDisplay.setBackground(Colors.GREEN);
		} else {
			labelStatusDataDisplay.setText("DISPLAY SHIFTED");
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
		buttonOffsetLeft.setEnabled(true);
		buttonOffsetLeftFast.setEnabled(true);
		buttonOffsetRight.setEnabled(true);
		buttonOffsetRightFast.setEnabled(true);
		//
		buttonLockOffset.setEnabled(true);
		buttonOffsetUp.setEnabled(true);
		buttonOffsetDown.setEnabled(true);
		labelStatusLockOffset.setEnabled(true);
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
}