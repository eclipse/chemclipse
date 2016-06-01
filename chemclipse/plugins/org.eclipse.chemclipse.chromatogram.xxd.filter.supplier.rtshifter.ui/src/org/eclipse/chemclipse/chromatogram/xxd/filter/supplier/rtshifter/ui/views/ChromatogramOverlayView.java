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

	@Inject
	private Composite composite;
	private MultipleChromatogramOffsetUI chromatogramOverlayUI;
	private Shell shell;
	private Cursor cursor;
	private Button buttonLockOffset;
	private Offset lockOffset;

	@Inject
	public ChromatogramOverlayView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
		shell = Display.getCurrent().getActiveShell();
		cursor = shell.getCursor();
		lockOffset = new Offset(0.0d, 0.0d);
	}

	@PostConstruct
	private void createControl() {

		composite.setLayout(new GridLayout(2, true));
		//
		createButtonBar(composite);
		createOverlayChart(composite);
	}

	private void createButtonBar(Composite composite) {

		Composite compositeButtonsLeft = new Composite(composite, SWT.NONE);
		GridData gridDataCompositeLeft = new GridData(GridData.FILL_HORIZONTAL);
		gridDataCompositeLeft.horizontalAlignment = SWT.BEGINNING;
		compositeButtonsLeft.setLayoutData(gridDataCompositeLeft);
		compositeButtonsLeft.setLayout(new GridLayout(3, false));
		//
		Composite compositeButtonsRight = new Composite(composite, SWT.NONE);
		GridData gridDataCompositeRight = new GridData(GridData.FILL_HORIZONTAL);
		gridDataCompositeRight.horizontalAlignment = SWT.END;
		compositeButtonsRight.setLayoutData(gridDataCompositeRight);
		compositeButtonsRight.setLayout(new GridLayout(7, false));
		/*
		 * LEFT
		 */
		//
		Label label = new Label(compositeButtonsLeft, SWT.BORDER);
		label.setText("DISPLAY MODE");
		label.setBackground(Colors.YELLOW);
		//
		Button buttonCheck = new Button(compositeButtonsLeft, SWT.CHECK);
		buttonCheck.setText("Shift Master");
		//
		Button buttonApply = new Button(compositeButtonsLeft, SWT.PUSH);
		buttonApply.setText("Apply");
		/*
		 * RIGHT
		 */
		buttonLockOffset = new Button(compositeButtonsRight, SWT.CHECK);
		buttonLockOffset.setText("Lock Offset");
		buttonLockOffset.setSelection(false);
		buttonLockOffset.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setLockOffset(buttonLockOffset.getSelection());
				update(getChromatogramSelection(), true);
			}
		});
		//
		Button buttonSettings = new Button(compositeButtonsRight, SWT.PUSH);
		buttonSettings.setText("");
		buttonSettings.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		buttonSettings.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new SWTPreferencePage();
				preferencePage.setTitle("Display Settings");
				//
				IPreferencePage preferencePageOverlay = new PreferencePage();
				preferencePageOverlay.setTitle("Overlay Settings");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageOverlay));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getCurrent().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Overlay Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					update(getChromatogramSelection(), false);
				}
			}
		});
		//
		Button buttonResetAll = new Button(compositeButtonsRight, SWT.PUSH);
		buttonResetAll.setText("");
		buttonResetAll.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		buttonResetAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceSupplier.resetOffset();
				resetLockedOffset();
				update(getChromatogramSelection(), false);
			}
		});
		//
		Button buttonOffsetLeft = new Button(compositeButtonsRight, SWT.PUSH);
		buttonOffsetLeft.setText("");
		buttonOffsetLeft.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_LEFT, IApplicationImage.SIZE_16x16));
		buttonOffsetLeft.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setWaitCursor();
				PreferenceSupplier.decreaseXOffset();
				if(buttonLockOffset.getSelection()) {
					setLockedOffsetX();
				}
				update(getChromatogramSelection(), false);
				setDefaultCursor();
			}
		});
		//
		Button buttonOffsetRight = new Button(compositeButtonsRight, SWT.PUSH);
		buttonOffsetRight.setText("");
		buttonOffsetRight.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_RIGHT, IApplicationImage.SIZE_16x16));
		buttonOffsetRight.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setWaitCursor();
				PreferenceSupplier.increaseXOffset();
				if(buttonLockOffset.getSelection()) {
					setLockedOffsetX();
				}
				update(getChromatogramSelection(), false);
				setDefaultCursor();
			}
		});
		//
		Button buttonOffsetUp = new Button(compositeButtonsRight, SWT.PUSH);
		buttonOffsetUp.setText("");
		buttonOffsetUp.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_UP, IApplicationImage.SIZE_16x16));
		buttonOffsetUp.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setWaitCursor();
				PreferenceSupplier.increaseYOffset();
				if(buttonLockOffset.getSelection()) {
					setLockedOffsetY();
				}
				update(getChromatogramSelection(), false);
				setDefaultCursor();
			}
		});
		//
		Button buttonOffsetDown = new Button(compositeButtonsRight, SWT.PUSH);
		buttonOffsetDown.setText("");
		buttonOffsetDown.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_DOWN, IApplicationImage.SIZE_16x16));
		buttonOffsetDown.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setWaitCursor();
				PreferenceSupplier.decreaseYOffset();
				if(buttonLockOffset.getSelection()) {
					setLockedOffsetY();
				}
				update(getChromatogramSelection(), false);
				setDefaultCursor();
			}
		});
	}

	private void createOverlayChart(Composite composite) {

		IOffset offset = getOffset();
		chromatogramOverlayUI = new MultipleChromatogramOffsetUI(composite, SWT.NONE, offset, new AxisTitlesIntensityScale());
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
			if(buttonLockOffset.getSelection()) {
				chromatogramOverlayUI.setOffset(lockOffset);
			} else {
				chromatogramOverlayUI.setOffset(getOffset());
			}
			//
			List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(chromatogramSelection, false);
			chromatogramOverlayUI.updateSelection(chromatogramSelections, forceReload);
		}
	}

	private void setWaitCursor() {

		shell.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_WAIT));
	}

	private void setDefaultCursor() {

		shell.setCursor(cursor);
	}

	private void setLockedOffsetX() {

		int xOffset = PreferenceSupplier.getOverlayXOffset();
		List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(getChromatogramSelection(), false);
		int x = 0;
		for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
			chromatogramSelection.getOffset().setX(x);
			x += xOffset;
		}
	}

	private void setLockedOffsetY() {

		int yOffset = PreferenceSupplier.getOverlayYOffset();
		List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(getChromatogramSelection(), false);
		int y = 0;
		for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
			chromatogramSelection.getOffset().setY(y);
			y += yOffset;
		}
	}

	private void setLockOffset(boolean lockOffset) {

		List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(getChromatogramSelection(), false);
		for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
			chromatogramSelection.setLockOffset(lockOffset);
		}
	}

	private void resetLockedOffset() {

		List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(getChromatogramSelection(), false);
		for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
			chromatogramSelection.resetOffset();
		}
	}

	/**
	 * Returns an offset instance.
	 * 
	 * @return {@link IOffset}import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
	 */
	public IOffset getOffset() {

		int xOffset = PreferenceSupplier.getOverlayXOffset();
		int yOffset = PreferenceSupplier.getOverlayYOffset();
		IOffset offset = new Offset(xOffset, yOffset);
		return offset;
	}
}