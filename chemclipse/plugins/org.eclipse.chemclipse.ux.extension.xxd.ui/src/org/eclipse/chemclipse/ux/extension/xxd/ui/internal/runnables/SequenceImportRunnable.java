/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.converter.model.reports.ISequence;
import org.eclipse.chemclipse.converter.model.reports.ISequenceRecord;
import org.eclipse.chemclipse.converter.sequence.SequenceConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.IExtensionMessages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class SequenceImportRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(SequenceImportRunnable.class);
	//
	private File file;
	private ISequence<? extends ISequenceRecord> sequence;

	public SequenceImportRunnable(File file) {

		this.file = file;
	}

	public ISequence<? extends ISequenceRecord> getSequence() {

		return sequence;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		SubMonitor subMonitor = SubMonitor.convert(monitor, ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.READ_SEQUENCE), 2);
		try {
			subMonitor.worked(1);
			IProcessingInfo<ISequence<? extends ISequenceRecord>> processingInfo = SequenceConverter.convert(file, monitor);
			sequence = processingInfo.getProcessingResult();
			subMonitor.worked(2);
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		} finally {
			subMonitor.done();
		}
	}
}
