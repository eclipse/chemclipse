/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
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
import java.util.List;

import org.eclipse.chemclipse.chromatogram.wsd.identifier.wavespectrum.WaveSpectrumIdentifier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.IExtensionMessages;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class WaveSpectrumIdentifierRunnable implements IRunnableWithProgress {

	private final List<IScanWSD> scans;
	private final String identifierId;

	public WaveSpectrumIdentifierRunnable(List<IScanWSD> scans, String identifierId) {

		this.scans = scans;
		this.identifierId = identifierId;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.SCAN_IDENTIFICATION), IProgressMonitor.UNKNOWN);
			WaveSpectrumIdentifier.identify(scans, identifierId, monitor);
		} finally {
			monitor.done();
		}
	}
}
