/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - support process method resume option
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.util.function.BiConsumer;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier.SupplierType;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.menu.AbstractChartMenuEntry;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class ProcessorSupplierMenuEntry<T> extends AbstractChartMenuEntry implements IChartMenuEntry {

	private final IProcessSupplier<T> processorSupplier;
	private final BiConsumer<IProcessSupplier<T>, IProcessSupplierContext> executionConsumer;
	private final IProcessSupplierContext context;

	public ProcessorSupplierMenuEntry(IProcessSupplier<T> processorSupplier, IProcessSupplierContext context, BiConsumer<IProcessSupplier<T>, IProcessSupplierContext> executionConsumer) {

		this.executionConsumer = executionConsumer;
		this.processorSupplier = processorSupplier;
		this.context = context;
	}

	@Override
	public String getCategory() {

		return processorSupplier.getCategory();
	}

	@Override
	public String getName() {

		String name = processorSupplier.getName().replace("&", "&&");
		if(processorSupplier.getType() == SupplierType.INTERACTIVE) {
			return name + " ...";
		}
		return name;
	}

	@Override
	public String getToolTipText() {

		return processorSupplier.getDescription();
	}

	@Override
	public void execute(Shell shell, ScrollableChart scrollableChart) {

		executionConsumer.accept(processorSupplier, context);
	}
}
