/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - reimplemented
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.range;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsReset;
import org.eclipse.chemclipse.chromatogram.filter.l10n.Messages;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.swtchart.extensions.menu.IChartMenuCategories;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramFilterResetRange implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.chromatogram.filter.resetChromatogramSelection";
	private static final String NAME = "Chromatogram Selection (1:1)";
	private static final String DESCRIPTION = "This filter reset the chromatogram range.";

	@Override
	public String getCategory() {

		return IChartMenuCategories.RANGE_SELECTION;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<FilterSettingsReset> implements IChromatogramSelectionProcessSupplier<FilterSettingsReset> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, FilterSettingsReset.class, parent, DataCategory.CSD, DataCategory.MSD, DataCategory.WSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, FilterSettingsReset processSettings, ProcessExecutionContext context) throws InterruptedException {

			chromatogramSelection.reset();
			context.addInfoMessage(IChartMenuCategories.RANGE_SELECTION, Messages.chromatogramSelectionReset);
			return chromatogramSelection;
		}
	}
}
