/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.system;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.system.AbstractSystemProcessSettings;
import org.eclipse.chemclipse.processing.system.AbstractSystemProcessSupplier;
import org.eclipse.chemclipse.processing.system.ISystemProcessSettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class TargetsProcessSupplier extends AbstractSystemProcessSettings {

	private static final String ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.system.targets";
	private static final String NAME = "Targets Part";
	private static final String DESCRIPTION = "Settings of the targets part";

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractSystemProcessSupplier<TargetsProcessSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, TargetsProcessSettings.class, parent);
		}

		@Override
		public void executeUserSettings(ISystemProcessSettings settings, ProcessExecutionContext context) throws Exception {

			if(settings instanceof TargetsProcessSettings processSettings) {
				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				//
				preferenceStore.setValue(PreferenceConstants.P_USE_TARGET_LIST, processSettings.isUseTargetList());
				preferenceStore.setValue(PreferenceConstants.P_PROPAGATE_TARGET_ON_UPDATE, processSettings.isPropagateTargetOnUpdate());
				preferenceStore.setValue(PreferenceConstants.P_TARGETS_TABLE_SORTABLE, processSettings.isTargetsTableSortable());
				preferenceStore.setValue(PreferenceConstants.P_TARGETS_TABLE_SHOW_DEVIATION_RT, processSettings.isShowDeviationRT());
				preferenceStore.setValue(PreferenceConstants.P_TARGETS_TABLE_SHOW_DEVIATION_RI, processSettings.isShowDeviationRI());
				//
				preferenceStore.setValue(PreferenceConstants.P_USE_ABSOLUTE_DEVIATION_RETENTION_TIME, processSettings.isUseAbsoluteDeviationRetentionTime());
				preferenceStore.setValue(PreferenceConstants.P_RETENTION_TIME_DEVIATION_REL_OK, processSettings.getRetentionTimeDeviationRelOK());
				preferenceStore.setValue(PreferenceConstants.P_RETENTION_TIME_DEVIATION_REL_WARN, processSettings.getRetentionTimeDeviationRelWarn());
				preferenceStore.setValue(PreferenceConstants.P_RETENTION_TIME_DEVIATION_ABS_OK, processSettings.getRetentionTimeDeviationAbsOK());
				preferenceStore.setValue(PreferenceConstants.P_RETENTION_TIME_DEVIATION_ABS_WARN, processSettings.getRetentionTimeDeviationAbsWarn());
				//
				preferenceStore.setValue(PreferenceConstants.P_USE_ABSOLUTE_DEVIATION_RETENTION_INDEX, processSettings.isUseAbsoluteDeviationRetentionIndex());
				preferenceStore.setValue(PreferenceConstants.P_RETENTION_INDEX_DEVIATION_REL_OK, processSettings.getRetentionIndexDeviationRelOK());
				preferenceStore.setValue(PreferenceConstants.P_RETENTION_INDEX_DEVIATION_REL_WARN, processSettings.getRetentionIndexDeviationRelWarn());
				preferenceStore.setValue(PreferenceConstants.P_RETENTION_INDEX_DEVIATION_ABS_OK, processSettings.getRetentionIndexDeviationAbsOK());
				preferenceStore.setValue(PreferenceConstants.P_RETENTION_INDEX_DEVIATION_ABS_WARN, processSettings.getRetentionIndexDeviationAbsWarn());
			}
		}
	}
}