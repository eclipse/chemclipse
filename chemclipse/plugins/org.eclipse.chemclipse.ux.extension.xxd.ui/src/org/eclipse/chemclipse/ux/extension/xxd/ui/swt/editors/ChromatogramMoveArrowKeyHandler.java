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

import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.selection.ChromatogramSelectionSupport;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.selection.MoveDirection;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IKeyboardSupport;
import org.eclipse.swtchart.extensions.events.AbstractHandledEventProcessor;
import org.eclipse.swtchart.extensions.events.IHandledEventProcessor;

public class ChromatogramMoveArrowKeyHandler extends AbstractHandledEventProcessor implements IHandledEventProcessor {

	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private ExtendedChromatogramUI extendedChromatogramUI;
	private int keyCode;

	public ChromatogramMoveArrowKeyHandler(ExtendedChromatogramUI extendedChromatogramUI, int keyCode) {

		this.extendedChromatogramUI = extendedChromatogramUI;
		this.keyCode = keyCode;
	}

	@Override
	public int getEvent() {

		return IKeyboardSupport.EVENT_KEY_UP;
	}

	@Override
	public int getButton() {

		return keyCode;
	}

	@Override
	public int getStateMask() {

		return SWT.NONE;
	}

	@Override
	public void handleEvent(BaseChart baseChart, Event event) {

		handleArrowMoveWindowSelection(keyCode);
	}

	
	private void handleArrowMoveWindowSelection(int keyCode) {

		IChromatogramSelection<?, ?>chromatogramSelection = extendedChromatogramUI.getChromatogramSelection();
		if(chromatogramSelection != null) {
			if(keyCode == SWT.ARROW_RIGHT || keyCode == SWT.ARROW_LEFT) {
				/*
				 * Left, Right
				 * (Retention Time)
				 */
				boolean useAlternateWindowMoveDirection = preferenceStore.getBoolean(PreferenceConstants.P_ALTERNATE_WINDOW_MOVE_DIRECTION);
				//
				if(keyCode == SWT.ARROW_RIGHT) {
					MoveDirection moveDirection = (useAlternateWindowMoveDirection) ? MoveDirection.LEFT : MoveDirection.RIGHT;
					ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, moveDirection, 20);
				} else {
					MoveDirection moveDirection = (useAlternateWindowMoveDirection) ? MoveDirection.RIGHT : MoveDirection.LEFT;
					ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, moveDirection, 20);
				}
				extendedChromatogramUI.updateSelection();
				//
			} else if(keyCode == SWT.ARROW_UP || keyCode == SWT.ARROW_DOWN) {
				/*
				 * Up, Down
				 * (Abundance)
				 * Doesn't work if auto adjust signals is enabled.
				 */
				float stopAbundance = chromatogramSelection.getStopAbundance();
				float newStopAbundance;
				if(PreferenceSupplier.useAlternateWindowMoveDirection()) {
					newStopAbundance = (keyCode == SWT.ARROW_UP) ? stopAbundance - stopAbundance / 20.0f : stopAbundance + stopAbundance / 20.0f;
				} else {
					newStopAbundance = (keyCode == SWT.ARROW_UP) ? stopAbundance + stopAbundance / 20.0f : stopAbundance - stopAbundance / 20.0f;
				}
				//
				int startRetentionTime = chromatogramSelection.getStartRetentionTime();
				int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
				float startAbundance = chromatogramSelection.getStartAbundance();
				extendedChromatogramUI.setChromatogramSelectionRange(startRetentionTime, stopRetentionTime, startAbundance, newStopAbundance);
				extendedChromatogramUI.updateSelection();
			}
		}
	}
}
