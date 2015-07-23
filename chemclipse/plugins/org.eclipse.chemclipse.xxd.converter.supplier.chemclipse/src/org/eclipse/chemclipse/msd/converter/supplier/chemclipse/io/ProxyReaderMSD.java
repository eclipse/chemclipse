/*******************************************************************************
 * Copyright (c) 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.chemclipse.io;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ReaderProxy_1003;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.ReaderProxy_1004;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.IVendorScanProxy;
import org.eclipse.chemclipse.msd.model.core.IIonTransitionSettings;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.core.runtime.IProgressMonitor;

public class ProxyReaderMSD {

	public void readMassSpectrum(File file, int offset, String version, IVendorScanProxy massSpectrum, IIonTransitionSettings ionTransitionSettings, IProgressMonitor monitor) throws IOException {

		IReaderProxy scanReaderProxy = null;
		if(version.equals(IFormat.VERSION_1003)) {
			scanReaderProxy = new ReaderProxy_1003();
		} else if(version.equals(IFormat.VERSION_1004)) {
			scanReaderProxy = new ReaderProxy_1004();
		}
		//
		if(scanReaderProxy != null) {
			scanReaderProxy.readMassSpectrum(file, offset, massSpectrum, ionTransitionSettings, monitor);
		}
	}
}
