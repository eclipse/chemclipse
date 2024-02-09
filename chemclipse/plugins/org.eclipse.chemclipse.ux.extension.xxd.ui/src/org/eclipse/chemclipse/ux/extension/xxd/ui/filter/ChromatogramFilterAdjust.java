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
package org.eclipse.chemclipse.ux.extension.xxd.ui.filter;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.extensions.menu.IChartMenuCategories;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramFilterAdjust implements IProcessTypeSupplier {

	/*
	 * Legacy Prefix:
	 * ChromatogramFilter
	 */
	private static final String ID = "ChromatogramFilter.org.eclipse.chemclipse.ux.extension.xxd.ui.filter.adjustChromatogramSelection";
	private static final String NAME = "Chromatogram Editor UI (Adjust)";
	private static final String DESCRIPTION = "This filter adjusts the chromatogram editor range.";

	@Override
	public String getCategory() {

		return IChartMenuCategories.RANGE_SELECTION;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<FilterSettingsAdjust> implements IChromatogramSelectionProcessSupplier<FilterSettingsAdjust> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, FilterSettingsAdjust.class, parent, DataCategory.CSD, DataCategory.MSD, DataCategory.WSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, FilterSettingsAdjust processSettings, ProcessExecutionContext context) throws InterruptedException {

			UpdateNotifierUI.update(Display.getDefault(), IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_ADJUST, ExtensionMessages.adjustChromatogramEditor);
			context.addInfoMessage(IChartMenuCategories.RANGE_SELECTION, ExtensionMessages.chromatogramEditorReset);
			return chromatogramSelection;
		}
	}
}