/*******************************************************************************
 * Copyright (c) 2015, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.ocx.model.chromatogram;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.io.ProxyReaderMSD;
import org.eclipse.chemclipse.msd.model.core.AbstractVendorMassSpectrumProxy;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransitionSettings;
import org.eclipse.core.runtime.NullProgressMonitor;

public class VendorScanProxy extends AbstractVendorMassSpectrumProxy implements IVendorScanProxy {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 8949336542952337617L;
	private static final Logger logger = Logger.getLogger(VendorScanProxy.class);
	//
	private File file;
	private int offset;
	private String version;
	private IIonTransitionSettings ionTransitionSettings;

	public VendorScanProxy(File file, int offset, String version, IIonTransitionSettings ionTransitionSettings) {

		this.file = file;
		this.offset = offset;
		this.version = version;
		this.ionTransitionSettings = ionTransitionSettings;
	}

	@Override
	public int getMaxPossibleIons() {

		return MAX_IONS;
	}

	@Override
	public int getMinPossibleRetentionTime() {

		return MIN_RETENTION_TIME;
	}

	@Override
	public int getMaxPossibleRetentionTime() {

		return MAX_RETENTION_TIME;
	}

	@Override
	public void importIons() {

		try {
			ProxyReaderMSD scanProxyReaderMSD = new ProxyReaderMSD();
			scanProxyReaderMSD.readMassSpectrum(file, offset, version, this, ionTransitionSettings, new NullProgressMonitor());
		} catch(IOException e) {
			logger.warn(e);
		}
	}

	// -------------------------------IMassSpectrumCloneable
	/**
	 * Keep in mind, it is a covariant return.<br/>
	 * IMassSpectrum is needed. IMassSpectrum is a subtype of
	 * ISupplierMassSpectrum is a subtype of IMassSpectrum.
	 */
	@Override
	public IVendorScan makeDeepCopy() throws CloneNotSupportedException {

		IVendorScanProxy massSpectrum = (IVendorScanProxy)super.clone();
		IVendorIon chemClipseIon;
		/*
		 * The instance variables have been copied by super.clone();.<br/> The
		 * ions in the ion list need not to be removed via
		 * removeAllIons as the method super.clone() has created a new
		 * list.<br/> It is necessary to fill the list again, as the abstract
		 * super class does not know each available type of ion.<br/>
		 * Make a deep copy of all ions.
		 */
		for(IIon ion : getIons()) {
			chemClipseIon = new VendorIon(ion.getIon(), ion.getAbundance());
			massSpectrum.addIon(chemClipseIon);
		}
		return massSpectrum;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		return makeDeepCopy();
	}
	// -------------------------------IMassSpectrumCloneable
}
