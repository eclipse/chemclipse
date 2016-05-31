/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
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

import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.IMassSpectrumFilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.MassSpectrumFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.exceptions.NoMassSpectrumFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences.FilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.internal.support.ISubtractFilterEvents;
import org.eclipse.chemclipse.chromatogram.msd.identifier.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.notifier.IMassSpectrumSelectionUpdateNotifier;
import org.eclipse.chemclipse.msd.model.notifier.MassSpectrumSelectionUpdateNotifier;
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
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
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

/**
 * TODO merge with ProfileMassSpectrumUIWithLabel and ScanMassSpectrumUIWithLabel, Pinned
 */
public class PinnedMassSpectrumEditUIWithLabel extends Composite implements IMassSpectrumSelectionUpdateNotifier {

	private static final Logger logger = Logger.getLogger(PinnedMassSpectrumEditUIWithLabel.class);
	private SimpleMassSpectrumUI massSpectrumUI;
	private Button pinButton;
	private Button addSubtractMassSpectrumButton;
	private Button saveButton;
	private Combo filterCombo;
	private Combo identifierCombo;
	private Label infoLabel;
	private IScanMSD originalMassSpectrum;
	private IScanMSD clonedMassSpectrum;
	private DecimalFormat decimalFormat;
	private MassValueDisplayPrecision massValueDisplayPrecision;
	private boolean isPinned = false;
	//
	private List<String> massSpectrumFilterIds;
	private String[] massSpectrumFilterNames;
	//
	private List<String> massSpectrumIdentifierIds;
	private String[] massSpectrumIdentifierNames;
	//
	private IEventBroker eventBroker;

	public PinnedMassSpectrumEditUIWithLabel(Composite parent, int style, MassValueDisplayPrecision massValueDisplayPrecision, IEventBroker eventBroker) {
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
			IMassSpectrumFilterSupport massSpectrumFilterSupport = MassSpectrumFilter.getMassSpectrumFilterSupport();
			massSpectrumFilterIds = massSpectrumFilterSupport.getAvailableFilterIds();
			massSpectrumFilterNames = massSpectrumFilterSupport.getFilterNames();
		} catch(NoMassSpectrumFilterSupplierAvailableException e) {
			massSpectrumFilterIds = new ArrayList<String>();
			massSpectrumFilterNames = new String[0];
		}
		/*
		 * Mass Spectrum Identifier
		 */
		try {
			IMassSpectrumIdentifierSupport massSpectrumIdentifierSupport = MassSpectrumIdentifier.getMassSpectrumIdentifierSupport();
			massSpectrumIdentifierIds = massSpectrumIdentifierSupport.getAvailableIdentifierIds();
			massSpectrumIdentifierNames = massSpectrumIdentifierSupport.getIdentifierNames();
		} catch(NoIdentifierAvailableException e) {
			massSpectrumIdentifierIds = new ArrayList<String>();
			massSpectrumIdentifierNames = new String[0];
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
		 * Pin button
		 */
		pinButton = new Button(labelbar, SWT.PUSH);
		setPinButtonText();
		pinButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				isPinned = isPinned ? false : true;
				setPinButtonText();
			}
		});
		/*
		 * Set subtract MS button
		 */
		addSubtractMassSpectrumButton = new Button(labelbar, SWT.PUSH);
		addSubtractMassSpectrumButton.setText("");
		addSubtractMassSpectrumButton.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SUBTRACT_ADD_SELECTED_SCAN, IApplicationImage.SIZE_16x16));
		addSubtractMassSpectrumButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(clonedMassSpectrum != null) {
					/*
					 * Show the subtract MS session view.
					 */
					String perspectiveId = IPerspectiveAndViewIds.PERSPECTIVE_MSD;
					String viewId = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.part.sessionSubtractMassSpectrum";
					PerspectiveSwitchHandler.focusPerspectiveAndView(perspectiveId, viewId);
					/*
					 * Clears the currently used subtract mass spectrum
					 */
					PreferenceSupplier.setSessionSubtractMassSpectrum(null);
					/*
					 * Add the selected scan as the subtract mass spectrum.
					 */
					IScanMSD massSpectrum1 = PreferenceSupplier.getSessionSubtractMassSpectrum();
					boolean useNormalize = PreferenceSupplier.isUseNormalize();
					IScanMSD normalizedMassSpectrum = FilterSupport.getCombinedMassSpectrum(massSpectrum1, clonedMassSpectrum, null, useNormalize);
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
		filterCombo.setItems(massSpectrumFilterNames);
		Button filterButton = new Button(labelbar, SWT.NONE);
		filterButton.setText("Filter");
		filterButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = filterCombo.getSelectionIndex();
				if(index >= 0 && index < massSpectrumFilterIds.size()) {
					String filterId = massSpectrumFilterIds.get(index);
					if(clonedMassSpectrum != null) {
						/*
						 * Clear all identification results and
						 * then run apply the filter.
						 */
						clonedMassSpectrum.getTargets().clear();
						MassSpectrumFilter.applyFilter(clonedMassSpectrum, filterId, new NullProgressMonitor());
						updateAfterProcessing();
					}
				}
			}
		});
		/*
		 * Mass Spectrum Identifier
		 */
		identifierCombo = new Combo(labelbar, SWT.NONE);
		identifierCombo.setItems(massSpectrumIdentifierNames);
		identifierCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button identifierButton = new Button(labelbar, SWT.NONE);
		identifierButton.setText("Identify");
		identifierButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = identifierCombo.getSelectionIndex();
				if(index >= 0 && index < massSpectrumIdentifierIds.size()) {
					final String identifierId = massSpectrumIdentifierIds.get(index);
					if(clonedMassSpectrum != null) {
						/*
						 * Switch to mass spectrum target view.
						 */
						PerspectiveSwitchHandler.focusPerspectiveAndView(IPerspectiveAndViewIds.PERSPECTIVE_MSD, IPerspectiveAndViewIds.VIEW_MASS_SPECTRUM_TARGETS);
						/*
						 * Run the identification.
						 */
						IRunnableWithProgress runnable = new IRunnableWithProgress() {

							@Override
							public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

								try {
									monitor.beginTask("Mass Spectrum Identification", IProgressMonitor.UNKNOWN);
									MassSpectrumIdentifier.identify(clonedMassSpectrum, identifierId, monitor);
									MassSpectrumSelectionUpdateNotifier.fireUpdateChange(clonedMassSpectrum, true);
									originalMassSpectrum.setOptimizedMassSpectrum(clonedMassSpectrum);
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
					MassSpectrumFileSupport.saveMassSpectrum(clonedMassSpectrum);
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
		massSpectrumUI.getPlotArea().addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {

				MassSpectrumSelectionUpdateNotifier.fireUpdateChange(clonedMassSpectrum, true);
			}
		});
	}

	@Override
	public void update(IScanMSD massSpectrum, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(massSpectrum != null && !isPinned) {
			/*
			 * Do not load the same mass spectrum twice if it has already been
			 * loaded.
			 */
			if(clonedMassSpectrum != massSpectrum) {
				try {
					originalMassSpectrum = massSpectrum;
					clonedMassSpectrum = massSpectrum.makeDeepCopy();
					setMassSpectrumLabel(clonedMassSpectrum);
					massSpectrumUI.update(clonedMassSpectrum, forceReload);
				} catch(CloneNotSupportedException e) {
					infoLabel.setText("It's not possible to clone the mass spectrum.");
				}
			}
		}
	}

	private void updateAfterProcessing() {

		massSpectrumUI.update(clonedMassSpectrum, true);
		setMassSpectrumLabel(clonedMassSpectrum);
	}

	private void setPinButtonText() {

		pinButton.setText("");
		if(isPinned) {
			pinButton.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_UNPIN_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		} else {
			pinButton.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PIN_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		}
	}

	private void setMassSpectrumLabel(IScanMSD massSpectrum) {

		StringBuilder builder = new StringBuilder();
		/*
		 * Check if the mass spectrum is a scan.
		 */
		if(massSpectrum instanceof IVendorMassSpectrum) {
			IVendorMassSpectrum actualMassSpectrum = (IVendorMassSpectrum)massSpectrum;
			builder.append("Scan: ");
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
		/*
		 * Set the label text.
		 */
		infoLabel.setText(builder.toString());
	}
}
