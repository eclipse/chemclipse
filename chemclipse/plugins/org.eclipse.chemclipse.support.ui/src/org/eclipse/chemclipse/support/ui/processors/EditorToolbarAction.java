/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - support for sorting / icons
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.processors;

import java.util.function.BiConsumer;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public final class EditorToolbarAction extends Action {

	private static final int MAX_TEXT_LENGTH = 10;
	//
	private final Processor processor;
	private final BiConsumer<IProcessSupplier<?>, IProcessSupplierContext> executionListener;
	private final IProcessSupplierContext context;

	public EditorToolbarAction(Processor processor, BiConsumer<IProcessSupplier<?>, IProcessSupplierContext> executionListener, IProcessSupplierContext context) {

		super(shortenName(processor.getProcessSupplier().getName()));
		//
		this.processor = processor;
		IProcessSupplier<?> supplier = processor.getProcessSupplier();
		this.executionListener = executionListener;
		this.context = context;
		//
		StringBuilder builder = new StringBuilder(supplier.getName());
		String description = supplier.getDescription();
		if(description != null && !description.isEmpty()) {
			builder.append(": ");
			builder.append(description);
		}
		//
		setToolTipText(builder.toString());
		setId(supplier.getId());
	}

	@Override
	public ImageDescriptor getImageDescriptor() {

		return ApplicationImageFactory.getInstance().getImageDescriptor(processor.getImageFileName(), IApplicationImage.SIZE_16x16);
	}

	@Override
	public void run() {

		executionListener.accept(processor.getProcessSupplier(), context);
	}

	private static String shortenName(String name) {

		if(name.length() > MAX_TEXT_LENGTH) {
			return name.substring(0, MAX_TEXT_LENGTH - 1) + '…';
		}
		return name;
	}
}