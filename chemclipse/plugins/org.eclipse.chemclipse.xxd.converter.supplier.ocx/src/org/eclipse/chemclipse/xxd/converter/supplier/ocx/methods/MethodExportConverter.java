/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.methods;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.chemclipse.converter.core.AbstractExportConverter;
import org.eclipse.chemclipse.converter.methods.IMethodExportConverter;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.methods.IMethodWriter;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.methods.MethodReaderWriter_1003;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.methods.MethodReaderWriter_1004;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.methods.MethodReaderWriter_1401;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.methods.MethodWriter_1000;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.methods.MethodWriter_1001;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support.Format;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class MethodExportConverter extends AbstractExportConverter implements IMethodExportConverter {

	@Override
	public void convert(File file, IProcessMethod processMethod, IMessageConsumer messages, IProgressMonitor monitor) throws IOException {

		IMethodWriter methodWriter = getMethodWriter();
		methodWriter.convert(file, processMethod, messages, monitor);
	}

	@Override
	public void convert(OutputStream stream, String nameHint, IProcessMethod processMethod, IMessageConsumer messages, IProgressMonitor monitor) throws IOException {

		IMethodWriter methodWriter = getMethodWriter();
		methodWriter.convert(stream, nameHint, processMethod, messages, monitor);
	}

	private IMethodWriter getMethodWriter() {

		String methodVersion = PreferenceSupplier.getMethodVersionSave();
		switch(methodVersion) {
			case Format.METHOD_VERSION_0001:
				return new MethodWriter_1000();
			case Format.METHOD_VERSION_0002:
				return new MethodWriter_1001();
			case Format.METHOD_VERSION_0003:
				return new MethodReaderWriter_1003();
			case Format.METHOD_VERSION_1400:
				return new MethodReaderWriter_1004();
			default:
				return new MethodReaderWriter_1401();
		}
	}
}
