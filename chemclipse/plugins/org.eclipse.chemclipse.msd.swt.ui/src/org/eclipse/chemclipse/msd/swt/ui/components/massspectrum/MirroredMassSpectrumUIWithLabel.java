/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.notifier.IMassSpectrumSelectionUpdateNotifier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;

/**
 * TODO merge with ProfileMassSpectrumUIWithLabel and ScanMassSpectrumUIWithLabel
 */
public class MirroredMassSpectrumUIWithLabel extends Composite implements IMassSpectrumSelectionUpdateNotifier {

	private SimpleMirroredMassSpectrumUI mirroredMassSpectrumUI;
	private Button pinButton;
	private Label infoLabel;
	private IScanMSD pinnedMassSpectrum;
	private IScanMSD mirroredMassSpectrum;
	private DecimalFormat decimalFormat;
	private MassValueDisplayPrecision massValueDisplayPrecision;
	private boolean isPinned = false;

	public MirroredMassSpectrumUIWithLabel(Composite parent, int style, MassValueDisplayPrecision massValueDisplayPrecision) {
		super(parent, style);
		decimalFormat = new DecimalFormat("0.0####");
		/*
		 * Mass spectrum type, nominal or accurate
		 */
		this.massValueDisplayPrecision = massValueDisplayPrecision;
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
		labelbar.setLayout(new GridLayout(2, false));
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
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
		 * The label with scan, retention time and retention index.
		 */
		infoLabel = new Label(labelbar, SWT.NONE);
		infoLabel.setText("");
		infoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// -------------------------------------------MassSpectrum
		mirroredMassSpectrumUI = new SimpleMirroredMassSpectrumUI(composite, SWT.FILL | SWT.BORDER, massValueDisplayPrecision);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		mirroredMassSpectrumUI.setLayoutData(gridData);
	}

	@Override
	public void update(IScanMSD massSpectrum, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(massSpectrum != null) {
			/*
			 * Do not load the same mass spectrum twice if it has already been
			 * loaded.
			 */
			if(!isPinned) {
				if(pinnedMassSpectrum != massSpectrum) {
					pinnedMassSpectrum = massSpectrum;
				}
			}
			//
			mirroredMassSpectrum = massSpectrum;
			setMassSpectrumLabel(pinnedMassSpectrum, mirroredMassSpectrum);
			/*
			 * Update
			 */
			mirroredMassSpectrumUI.update(pinnedMassSpectrum, mirroredMassSpectrum, forceReload);
		}
	}

	private void setPinButtonText() {

		pinButton.setText("");
		if(isPinned) {
			pinButton.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_UNPIN_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		} else {
			pinButton.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PIN_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		}
	}

	private void setMassSpectrumLabel(IScanMSD pinnedMassSpectrum, IScanMSD mirroredMassSpectrum) {

		StringBuilder builder = new StringBuilder();
		/*
		 * Check if the mass spectrum is a scan.
		 */
		addMassSpectrumLabelData(pinnedMassSpectrum, builder);
		builder.append(" vs. ");
		addMassSpectrumLabelData(mirroredMassSpectrum, builder);
		/*
		 * Set the label text.
		 */
		infoLabel.setText(builder.toString());
	}

	private void addMassSpectrumLabelData(IScanMSD massSpectrum, StringBuilder builder) {

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
		}
	}
}
