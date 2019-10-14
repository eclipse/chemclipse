/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.methods;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.chemclipse.converter.core.AbstractExportConverter;
import org.eclipse.chemclipse.converter.methods.IMethodExportConverter;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.IMethodWriter;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.methods.MethodFormat_0003;
import org.eclipse.core.runtime.IProgressMonitor;

public class MethodExportConverter extends AbstractExportConverter implements IMethodExportConverter {

	private static final IMethodWriter WRITER = new MethodFormat_0003();

	@Override
	public void convert(File file, IProcessMethod processMethod, MessageConsumer messages, IProgressMonitor monitor) throws IOException {

		WRITER.convert(file, processMethod, messages, monitor);
	}

	@Override
	public void convert(OutputStream stream, String nameHint, IProcessMethod processMethod, MessageConsumer messages, IProgressMonitor monitor) throws IOException {

		WRITER.convert(stream, nameHint, processMethod, messages, monitor);
	}
}
