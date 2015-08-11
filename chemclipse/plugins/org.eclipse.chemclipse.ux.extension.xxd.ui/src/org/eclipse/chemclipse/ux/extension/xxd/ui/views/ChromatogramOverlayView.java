/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.views;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.components.chromatogram.MultipleChromatogramOffsetUI;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesIntensityScale;
import org.eclipse.chemclipse.swt.ui.support.IOffset;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ChromatogramOverlayView extends AbstractChromatogramOverlayView {

	@Inject
	private Composite composite;
	private MultipleChromatogramOffsetUI chromatogramOverlayUI;
	private Shell shell;
	private Cursor cursor;

	@Inject
	public ChromatogramOverlayView(EPartService partService, MPart part, IEventBroker eventBroker) {

		super(part, partService, eventBroker);
		shell = Display.getCurrent().getActiveShell();
		cursor = shell.getCursor();
	}

	@PostConstruct
	private void createControl() {

		IOffset offset = getOffset();
		composite.setLayout(new GridLayout(1, true));
		//
		Composite compositeButtons = new Composite(composite, SWT.NONE);
		GridData gridDataComposite = new GridData(GridData.FILL_HORIZONTAL);
		gridDataComposite.horizontalAlignment = SWT.END;
		compositeButtons.setLayoutData(gridDataComposite);
		compositeButtons.setLayout(new GridLayout(5, false));
		//
		Button buttonResetAll = new Button(compositeButtons, SWT.PUSH);
		buttonResetAll.setText("");
		buttonResetAll.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		buttonResetAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceSupplier.resetOffset();
				update(getChromatogramSelection(), false);
			}
		});
		//
		Button buttonOffsetLeft = new Button(compositeButtons, SWT.PUSH);
		buttonOffsetLeft.setText("");
		buttonOffsetLeft.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_LEFT, IApplicationImage.SIZE_16x16));
		buttonOffsetLeft.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setWaitCursor();
				PreferenceSupplier.decreaseXOffset();
				update(getChromatogramSelection(), false);
				setDefaultCursor();
			}
		});
		//
		Button buttonOffsetRight = new Button(compositeButtons, SWT.PUSH);
		buttonOffsetRight.setText("");
		buttonOffsetRight.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_RIGHT, IApplicationImage.SIZE_16x16));
		buttonOffsetRight.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setWaitCursor();
				PreferenceSupplier.increaseXOffset();
				update(getChromatogramSelection(), false);
				setDefaultCursor();
			}
		});
		//
		Button buttonOffsetUp = new Button(compositeButtons, SWT.PUSH);
		buttonOffsetUp.setText("");
		buttonOffsetUp.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_UP, IApplicationImage.SIZE_16x16));
		buttonOffsetUp.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setWaitCursor();
				PreferenceSupplier.increaseYOffset();
				update(getChromatogramSelection(), false);
				setDefaultCursor();
			}
		});
		//
		Button buttonOffsetDown = new Button(compositeButtons, SWT.PUSH);
		buttonOffsetDown.setText("");
		buttonOffsetDown.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_OFFSET_DOWN, IApplicationImage.SIZE_16x16));
		buttonOffsetDown.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setWaitCursor();
				PreferenceSupplier.decreaseYOffset();
				update(getChromatogramSelection(), false);
				setDefaultCursor();
			}
		});
		//
		chromatogramOverlayUI = new MultipleChromatogramOffsetUI(composite, SWT.NONE, offset, new AxisTitlesIntensityScale());
		chromatogramOverlayUI.setLayoutData(new GridData(GridData.FILL_BOTH));
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
			List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(chromatogramSelection);
			IOffset offset = getOffset();
			chromatogramOverlayUI.setOffset(offset);
			chromatogramOverlayUI.updateSelection(chromatogramSelections, forceReload);
		}
	}

	private void setWaitCursor() {

		shell.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_WAIT));
	}

	private void setDefaultCursor() {

		shell.setCursor(cursor);
	}
}