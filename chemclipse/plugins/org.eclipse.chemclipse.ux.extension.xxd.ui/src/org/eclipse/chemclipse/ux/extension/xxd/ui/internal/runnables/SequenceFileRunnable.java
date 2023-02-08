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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.SupplierEditorSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class SequenceFileRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(SequenceFileRunnable.class);
	//
	private ISupplierEditorSupport supplierEditorSupport = new SupplierEditorSupport(DataType.SEQ, () -> Activator.getDefault().getEclipseContext());
	private File file;
	private List<File> files;

	public SequenceFileRunnable(File file) {

		this.file = file;
	}

	public List<File> getSequenceFiles() {

		return files;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try (Stream<Path> xFiles = Files.list(Paths.get(file.toString()))) {
			SubMonitor subMonitor = SubMonitor.convert(monitor, ExtensionMessages.importSequences, 2);
			try {
				subMonitor.worked(1);
				files = getSequenceFiles(file, new ArrayList<>(), subMonitor.split((int)xFiles.count()));
				subMonitor.worked(1);
			} finally {
				subMonitor.done();
			}
		} catch(IOException e) {
			logger.warn(e);
		}
	}

	private List<File> getSequenceFiles(File file, List<File> files, SubMonitor subMonitor) {

		if(subMonitor.isCanceled()) {
			return files;
		} else {
			subMonitor.worked(1);
		}
		//
		if(file.isDirectory()) {
			for(File subfile : file.listFiles()) {
				if(subfile.isDirectory()) {
					files = getSequenceFiles(subfile, files, subMonitor);
				} else {
					if(isSequenceFile(subfile)) {
						files.add(subfile);
					}
				}
			}
		} else {
			if(isSequenceFile(file)) {
				files.add(file);
			}
		}
		return files;
	}

	private boolean isSequenceFile(File file) {

		if(supplierEditorSupport.isSupplierFile(file)) {
			if(supplierEditorSupport.isMatchMagicNumber(file)) {
				return true;
			}
		}
		return false;
	}
}
