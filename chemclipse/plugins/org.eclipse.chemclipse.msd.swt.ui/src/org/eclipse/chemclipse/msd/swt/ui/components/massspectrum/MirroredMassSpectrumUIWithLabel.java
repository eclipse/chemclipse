/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.notifier.IMassSpectrumSelectionUpdateNotifier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * TODO merge with ProfileMassSpectrumUIWithLabel and ScanMassSpectrumUIWithLabel
 */
public class MirroredMassSpectrumUIWithLabel extends Composite implements IMassSpectrumSelectionUpdateNotifier {

	private static final Logger logger = Logger.getLogger(MirroredMassSpectrumUIWithLabel.class);
	//
	private Button pinButton;
	private Label infoLabelPinned;
	private SimpleMirroredMassSpectrumUI mirroredMassSpectrumUI;
	private Label infoLabelMirrored;
	//
	private IScanMSD pinnedMassSpectrum;
	private IScanMSD mirroredMassSpectrum;
	private DecimalFormat decimalFormat;
	private MassValueDisplayPrecision massValueDisplayPrecision;
	private boolean isPinned = false;

	public MirroredMassSpectrumUIWithLabel(Composite parent, int style, MassValueDisplayPrecision massValueDisplayPrecision) {
		super(parent, style);
		decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0####");
		/*
		 * Mass spectrum type, nominal or accurate
		 */
		this.massValueDisplayPrecision = massValueDisplayPrecision;
		initialize(parent);
	}

	private void initialize(Composite parent) {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		/*
		 * Pinned bar top
		 */
		Composite labelbar = new Composite(composite, SWT.FILL);
		labelbar.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData();
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
		infoLabelPinned = new Label(labelbar, SWT.NONE);
		infoLabelPinned.setText("");
		infoLabelPinned.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		/*
		 * Mass Spectrum
		 */
		mirroredMassSpectrumUI = new SimpleMirroredMassSpectrumUI(composite, SWT.FILL | SWT.BORDER, massValueDisplayPrecision);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		mirroredMassSpectrumUI.setLayoutData(gridData);
		//
		infoLabelMirrored = new Label(composite, SWT.NONE);
		infoLabelMirrored.setText("");
		infoLabelMirrored.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
			mirroredMassSpectrum = massSpectrum;
			//
			try {
				IScanMSD pinnedMassSpectrumCopy = pinnedMassSpectrum.makeDeepCopy().normalize(1000.0f);
				IScanMSD mirroredMassSpectrumCopy = mirroredMassSpectrum.makeDeepCopy().normalize(1000.0f);
				setMassSpectrumLabel(pinnedMassSpectrumCopy, mirroredMassSpectrumCopy);
				mirroredMassSpectrumUI.update(pinnedMassSpectrumCopy, mirroredMassSpectrumCopy, forceReload);
			} catch(CloneNotSupportedException e) {
				logger.warn(e);
			}
		} else {
			mirroredMassSpectrumUI.update(null, null, true);
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

		setMassSpectrumLabel(pinnedMassSpectrum, "PINNED MS = ", infoLabelPinned);
		setMassSpectrumLabel(mirroredMassSpectrum, "REFERENCE MS = ", infoLabelMirrored);
	}

	private void setMassSpectrumLabel(IScanMSD massSpectrum, String title, Label label) {

		StringBuilder builder = new StringBuilder();
		builder.append(title);
		if(massSpectrum instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)massSpectrum;
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			builder.append("NAME: ");
			builder.append(libraryInformation.getName());
			builder.append(" | ");
			builder.append("CAS: ");
			builder.append(libraryInformation.getCasNumber());
			builder.append(" | ");
		}
		builder.append("RT: ");
		builder.append(decimalFormat.format(massSpectrum.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
		builder.append(" | ");
		builder.append("RI: ");
		if(PreferenceSupplier.showRetentionIndexWithoutDecimals()) {
			builder.append(Integer.toString((int)massSpectrum.getRetentionIndex()));
		} else {
			builder.append(decimalFormat.format(massSpectrum.getRetentionIndex()));
		}
		label.setText(builder.toString());
	}
}
