/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.swt;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoPeakFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.peak.IPeakFilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.peak.PeakFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences.FilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.internal.support.ISubtractFilterEvents;
import org.eclipse.chemclipse.chromatogram.msd.identifier.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifier;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.ChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.msd.model.notifier.IChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.msd.model.notifier.PeakSelectionUpdateNotifier;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassValueDisplayPrecision;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.SimpleMassSpectrumUI;
import org.eclipse.chemclipse.msd.swt.ui.support.MassSpectrumFileSupport;
import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class PeakMassSpectrumEditUIWithLabel extends Composite implements IChromatogramSelectionMSDUpdateNotifier {

	private static final Logger logger = Logger.getLogger(PeakMassSpectrumEditUIWithLabel.class);
	//
	private SimpleMassSpectrumUI massSpectrumUI;
	private Button addSubtractMassSpectrumButton;
	private Button saveButton;
	private Combo filterCombo;
	private Combo identifierCombo;
	private Label infoLabel;
	//
	private IChromatogramSelectionMSD chromatogramSelection;
	private DecimalFormat decimalFormat;
	private MassValueDisplayPrecision massValueDisplayPrecision;
	//
	private List<String> peakFilterIds;
	private String[] peakFilterNames;
	//
	private List<String> peakIdentifierIds;
	private String[] peakIdentifierNames;
	//
	private IEventBroker eventBroker;

	public PeakMassSpectrumEditUIWithLabel(Composite parent, int style, MassValueDisplayPrecision massValueDisplayPrecision, IEventBroker eventBroker) {
		super(parent, style);
		decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0####");
		this.eventBroker = eventBroker;
		/*
		 * Mass spectrum type, nominal or accurate
		 */
		this.massValueDisplayPrecision = massValueDisplayPrecision;
		/*
		 * Mass Spectrum Filter
		 */
		try {
			IPeakFilterSupport peakFilterSupport = PeakFilter.getPeakFilterSupport();
			peakFilterIds = peakFilterSupport.getAvailableFilterIds();
			peakFilterNames = peakFilterSupport.getFilterNames();
		} catch(NoPeakFilterSupplierAvailableException e) {
			peakFilterIds = new ArrayList<String>();
			peakFilterNames = new String[0];
		}
		/*
		 * Mass Spectrum Identifier
		 */
		try {
			IPeakIdentifierSupport peakIdentifierSupport = PeakIdentifier.getPeakIdentifierSupport();
			peakIdentifierIds = peakIdentifierSupport.getAvailableIdentifierIds();
			peakIdentifierNames = peakIdentifierSupport.getIdentifierNames();
		} catch(NoIdentifierAvailableException e) {
			peakIdentifierIds = new ArrayList<String>();
			peakIdentifierNames = new String[0];
		}
		//
		initialize(parent);
	}

	private void initialize(Composite parent) {

		GridLayout layout;
		GridData gridData;
		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		// -------------------------------------------Label
		Composite labelbar = new Composite(composite, SWT.FILL);
		labelbar.setLayout(new GridLayout(7, false));
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		labelbar.setLayoutData(gridData);
		/*
		 * Set subtract MS button
		 */
		addSubtractMassSpectrumButton = new Button(labelbar, SWT.PUSH);
		addSubtractMassSpectrumButton.setText("");
		addSubtractMassSpectrumButton.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_ADD_SELECTED_SCAN, IApplicationImage.SIZE_16x16));
		addSubtractMassSpectrumButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(chromatogramSelection != null) {
					/*
					 * Show the subtract MS session view.
					 */
					String perspectiveId = IPerspectiveAndViewIds.PERSPECTIVE_PEAKS_MSD;
					String viewId = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.part.sessionSubtractMassSpectrum";
					PerspectiveSwitchHandler.focusPerspectiveAndView(perspectiveId, viewId);
					/*
					 * Clears the currently used subtract mass spectrum
					 */
					PreferenceSupplier.setSessionSubtractMassSpectrum(null);
					/*
					 * Add the selected scan as the subtract mass spectrum.
					 */
					IScanMSD sessionSubtractMassSpectrum = PreferenceSupplier.getSessionSubtractMassSpectrum();
					boolean useNormalize = PreferenceSupplier.isUseNormalize();
					IScanMSD normalizedMassSpectrum = FilterSupport.getCombinedMassSpectrum(sessionSubtractMassSpectrum, chromatogramSelection.getSelectedScan(), null, useNormalize);
					PreferenceSupplier.setSessionSubtractMassSpectrum(normalizedMassSpectrum);
					/*
					 * Update all listeners
					 */
					eventBroker.send(ISubtractFilterEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, true);
				}
			}
		});
		/*
		 * Mass Spectrum Filter
		 */
		filterCombo = new Combo(labelbar, SWT.NONE);
		filterCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		filterCombo.setItems(peakFilterNames);
		Button filterButton = new Button(labelbar, SWT.NONE);
		filterButton.setText("Filter");
		filterButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = filterCombo.getSelectionIndex();
				if(index >= 0 && index < peakFilterIds.size()) {
					String filterId = peakFilterIds.get(index);
					if(chromatogramSelection != null && chromatogramSelection.getSelectedPeak() != null) {
						/*
						 * Clear all identification results and
						 * then run apply the filter.
						 */
						IPeakMSD peakMSD = chromatogramSelection.getSelectedPeak();
						peakMSD.removeAllTargets();
						PeakFilter.applyFilter(peakMSD, filterId, new NullProgressMonitor());
						ChromatogramSelectionMSDUpdateNotifier.fireUpdateChange(chromatogramSelection, true);
					}
				}
			}
		});
		/*
		 * Mass Spectrum Identifier
		 */
		identifierCombo = new Combo(labelbar, SWT.NONE);
		identifierCombo.setItems(peakIdentifierNames);
		identifierCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button identifierButton = new Button(labelbar, SWT.NONE);
		identifierButton.setText("Identify");
		identifierButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = identifierCombo.getSelectionIndex();
				if(index >= 0 && index < peakIdentifierIds.size()) {
					final String identifierId = peakIdentifierIds.get(index);
					if(chromatogramSelection != null && chromatogramSelection.getSelectedPeak() != null) {
						/*
						 * Switch to mass spectrum target view.
						 */
						IPeakMSD peakMSD = chromatogramSelection.getSelectedPeak();
						PerspectiveSwitchHandler.focusPerspectiveAndView(IPerspectiveAndViewIds.PERSPECTIVE_PEAKS_MSD, IPerspectiveAndViewIds.VIEW_PEAK_TARGETS_MSD);
						/*
						 * Run the identification.
						 */
						IRunnableWithProgress runnable = new IRunnableWithProgress() {

							@Override
							public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

								try {
									monitor.beginTask("Peak Identification", IProgressMonitor.UNKNOWN);
									PeakIdentifier.identify(peakMSD, identifierId, monitor);
									PeakSelectionUpdateNotifier.fireUpdateChange(peakMSD, true);
								} finally {
									monitor.done();
								}
							}
						};
						ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
						try {
							/*
							 * Use true, true ... instead of false, true ... if the progress bar
							 * should be shown in action.
							 */
							monitor.run(true, true, runnable);
						} catch(InvocationTargetException e1) {
							logger.warn(e1);
						} catch(InterruptedException e1) {
							logger.warn(e1);
						}
					}
				}
			}
		});
		/*
		 * Save button
		 */
		saveButton = new Button(labelbar, SWT.PUSH);
		saveButton.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImage.SIZE_16x16));
		saveButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					if(chromatogramSelection != null && chromatogramSelection.getSelectedPeak() != null) {
						MassSpectrumFileSupport.saveMassSpectrum(chromatogramSelection.getSelectedPeak().getExtractedMassSpectrum());
					}
				} catch(NoConverterAvailableException e1) {
					logger.warn(e1);
				}
			}
		});
		/*
		 * The label with scan, retention time and retention index.
		 */
		infoLabel = new Label(labelbar, SWT.NONE);
		infoLabel.setText("");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 6;
		infoLabel.setLayoutData(gridData);
		// -------------------------------------------MassSpectrum
		massSpectrumUI = new SimpleMassSpectrumUI(composite, SWT.FILL | SWT.BORDER, massValueDisplayPrecision);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		massSpectrumUI.setLayoutData(gridData);
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		this.chromatogramSelection = chromatogramSelection;
		IPeakMSD peakMSD = chromatogramSelection.getSelectedPeak();
		//
		if(peakMSD != null) {
			setPeakLabel(peakMSD);
			massSpectrumUI.update(peakMSD.getExtractedMassSpectrum(), forceReload);
		}
	}

	private void setPeakLabel(IPeakMSD peakMSD) {

		StringBuilder builder = new StringBuilder();
		if(peakMSD != null) {
			/*
			 * Check if the mass spectrum is a scan.
			 */
			IScanMSD massSpectrum = peakMSD.getExtractedMassSpectrum();
			if(massSpectrum instanceof IPeakMassSpectrum) {
				IPeakMassSpectrum actualMassSpectrum = (IPeakMassSpectrum)massSpectrum;
				builder.append("Peak: ");
				builder.append(actualMassSpectrum.getScanNumber());
				builder.append(" | ");
				builder.append("RT: ");
				builder.append(decimalFormat.format(actualMassSpectrum.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
				builder.append(" | ");
				builder.append("RI: ");
				builder.append(decimalFormat.format(actualMassSpectrum.getRetentionIndex()));
				builder.append(" | ");
				builder.append("Detector: MS");
				builder.append(actualMassSpectrum.getMassSpectrometer());
				builder.append(" | ");
				builder.append("Type: ");
				builder.append(actualMassSpectrum.getMassSpectrumTypeDescription());
				builder.append(" | ");
			}
			builder.append("Signal: ");
			builder.append((int)massSpectrum.getTotalSignal());
		}
		/*
		 * Set the label text.
		 */
		infoLabel.setText(builder.toString());
	}
}
