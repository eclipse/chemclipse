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
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.ui.modifier.FilterModifierStretch;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.components.chromatogram.MultipleChromatogramOffsetUI;
import org.eclipse.chemclipse.swt.ui.preferences.SWTPreferencePage;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesIntensityScale;
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
import org.eclipse.swt.widgets.MessageBox;

public class ChromatogramStretchView extends AbstractChromatogramOverlayView {

	private static final Logger logger = Logger.getLogger(ChromatogramStretchView.class);
	//
	private static final String TYPE_SHORTEST = "TYPE_SHORTEST";
	private static final String TYPE_SELECTED = "TYPE_SELECTED";
	private static final String TYPE_LONGEST = "TYPE_LONGEST";
	//
	@Inject
	private Composite composite;
	//
	private MultipleChromatogramOffsetUI chromatogramOverlayUI;
	//
	private Button buttonShrinkChromatograms;
	private Button buttonAlignToSelectedChromatogram;
	private Button buttonStretchChromatograms;

	@Inject
	public ChromatogramStretchView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		composite.setLayout(new GridLayout(1, false));
		//
		createButtonBar(composite);
		createOverlayChart(composite);
	}

	private void createButtonBar(Composite composite) {

		Composite compositeButtons = new Composite(composite, SWT.NONE);
		GridData gridDataCompositeRight = new GridData(GridData.FILL_HORIZONTAL);
		gridDataCompositeRight.horizontalAlignment = SWT.END;
		compositeButtons.setLayoutData(gridDataCompositeRight);
		compositeButtons.setLayout(new GridLayout(4, false));
		createButtonsRight(compositeButtons);
	}

	private void createButtonsRight(Composite composite) {

		createButtonSettings(composite);
		createButtonShrinkChromatograms(composite);
		createButtonAlignToSelectedChromatogram(composite);
		createButtonStretchChromatograms(composite);
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
				if(preferenceDialog.open() == Window.OK) {
					update(getChromatogramSelection(), false);
				}
			}
		});
	}

	private void createButtonShrinkChromatograms(Composite composite) {

		buttonShrinkChromatograms = new Button(composite, SWT.PUSH);
		buttonShrinkChromatograms.setText("");
		buttonShrinkChromatograms.setToolTipText("Shrink chromatograms");
		buttonShrinkChromatograms.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SHRINK_CHROMATOGRAMS, IApplicationImage.SIZE_16x16));
		buttonShrinkChromatograms.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.YES | SWT.NO | SWT.ICON_WARNING);
				messageBox.setText("Shrink to shortest chromatogram");
				messageBox.setMessage("Would you like to shrink all chromatograms?");
				if(messageBox.open() == SWT.YES) {
					stretchChromatograms(TYPE_SHORTEST);
				}
			}
		});
	}

	private void createButtonAlignToSelectedChromatogram(Composite composite) {

		buttonAlignToSelectedChromatogram = new Button(composite, SWT.PUSH);
		buttonAlignToSelectedChromatogram.setText("");
		buttonAlignToSelectedChromatogram.setToolTipText("Align to selected chromatogram");
		buttonAlignToSelectedChromatogram.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ALIGN_CHROMATOGRAMS, IApplicationImage.SIZE_16x16));
		buttonAlignToSelectedChromatogram.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.YES | SWT.NO | SWT.ICON_WARNING);
				messageBox.setText("Align to selected chromatogram");
				messageBox.setMessage("Would you like to align all chromatograms?");
				if(messageBox.open() == SWT.YES) {
					stretchChromatograms(TYPE_SELECTED);
				}
			}
		});
	}

	private void createButtonStretchChromatograms(Composite composite) {

		buttonStretchChromatograms = new Button(composite, SWT.PUSH);
		buttonStretchChromatograms.setText("");
		buttonStretchChromatograms.setToolTipText("Stretch chromatograms");
		buttonStretchChromatograms.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STRETCH_CHROMATOGRAMS, IApplicationImage.SIZE_16x16));
		buttonStretchChromatograms.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.YES | SWT.NO | SWT.ICON_WARNING);
				messageBox.setText("Stretch to longest chromatogram");
				messageBox.setMessage("Would you like to stretch all chromatograms?");
				if(messageBox.open() == SWT.YES) {
					stretchChromatograms(TYPE_LONGEST);
				}
			}
		});
	}

	private void createOverlayChart(Composite composite) {

		chromatogramOverlayUI = new MultipleChromatogramOffsetUI(composite, SWT.NONE, new AxisTitlesIntensityScale());
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
			List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(chromatogramSelection, false);
			chromatogramOverlayUI.updateSelection(chromatogramSelections, forceReload);
		}
	}

	private void stretchChromatograms(String type) {

		IChromatogram chromatogram = getChromatogram(type);
		if(chromatogram != null) {
			/*
			 * Settings
			 */
			PreferenceSupplier.setStretchScanDelay(chromatogram.getScanDelay());
			PreferenceSupplier.setStretchLength(chromatogram.getStopRetentionTime());
			/*
			 * Shift all chromatograms.
			 */
			List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(getChromatogramSelection(), false);
			for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
				/*
				 * Don't re-align the template chromatogram.
				 */
				if(chromatogramSelection.getChromatogram() != chromatogram) {
					runStretchChromatogram(chromatogramSelection);
				}
			}
		}
	}

	private void runStretchChromatogram(IChromatogramSelection chromatogramSelection) {

		StatusLineLogger.setInfo(InfoType.MESSAGE, "Start RTStretcher Filter");
		/*
		 * Do the operation.<br/> Open a progress monitor dialog.
		 */
		final Display display = Display.getCurrent();
		IRunnableWithProgress runnable = new FilterModifierStretch(chromatogramSelection);
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
		StatusLineLogger.setInfo(InfoType.MESSAGE, "RTStretcher Filter finished");
	}

	/*
	 * May return null;
	 */
	private IChromatogram getChromatogram(String type) {

		IChromatogram chromatogram;
		switch(type) {
			case TYPE_SHORTEST:
				chromatogram = getShortestChromatogram();
				break;
			case TYPE_SELECTED:
				IChromatogramSelection chromatogramSelection = getChromatogramSelection();
				chromatogram = chromatogramSelection.getChromatogram();
				break;
			case TYPE_LONGEST:
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
	private IChromatogram getShortestChromatogram() {

		List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(getChromatogramSelection(), false);
		IChromatogram chromatogram = null;
		int maxRetentionTime = Integer.MAX_VALUE;
		for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
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
	private IChromatogram getLongestChromatogram() {

		List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(getChromatogramSelection(), false);
		IChromatogram chromatogram = null;
		int minRetentionTime = Integer.MIN_VALUE;
		for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
			if(chromatogramSelection.getChromatogram().getStopRetentionTime() > minRetentionTime) {
				minRetentionTime = chromatogramSelection.getChromatogram().getStopRetentionTime();
				chromatogram = chromatogramSelection.getChromatogram();
			}
		}
		return chromatogram;
	}
}