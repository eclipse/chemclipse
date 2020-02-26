/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class MassSpectrumIdentifierRunnable implements IRunnableWithProgress {

	private final IScanMSD scanMSD;
	private final String identifierId;

	public MassSpectrumIdentifierRunnable(IScanMSD scanMSD, String identifierId) {
		this.scanMSD = scanMSD;
		this.identifierId = identifierId;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Scan Identification", IProgressMonitor.UNKNOWN);
			MassSpectrumIdentifier.identify(scanMSD, identifierId, monitor);
		} finally {
			monitor.done();
		}
	}
}