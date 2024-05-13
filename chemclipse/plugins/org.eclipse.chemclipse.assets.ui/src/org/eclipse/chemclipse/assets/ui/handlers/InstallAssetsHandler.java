/*******************************************************************************
 * Copyright (c) 2020, 2024 Christoph Läubrich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - code refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.assets.ui.handlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.eclipse.chemclipse.assets.core.AssetItem;
import org.eclipse.chemclipse.assets.ui.wizards.AssetInstallPage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.wizards.SinglePageWizard;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Service;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;

import jakarta.inject.Named;

public class InstallAssetsHandler {

	@Execute
	public void execute(IWorkbench workbench, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell, @Service(filterExpression = "(action=ConfigReader)") Runnable configReader, @Service(filterExpression = "(action=BundleReader)") Runnable bundleReader, IEventBroker eventBroker) throws IOException {

		AssetInstallPage assetInstallPage = new AssetInstallPage();
		SinglePageWizard wizard = new SinglePageWizard("Install / Manage Assets", true, assetInstallPage);
		if(wizard.open(shell, 800, 600)) {
			/*
			 * Handle deleted items.
			 */
			List<AssetItem> deletedItems = assetInstallPage.getDeletedItems();
			for(AssetItem delete : deletedItems) {
				File directory = delete.getAssetType().directory();
				File deleteFile = new File(directory, delete.getName());
				File backupFile = new File(directory, delete.getName() + "." + System.currentTimeMillis() + ".bak");
				deleteFile.renameTo(backupFile);
			}
			/*
			 * Handle new items.
			 */
			List<AssetItem> newItems = assetInstallPage.getNewItems();
			for(AssetItem create : newItems) {
				File directory = create.getAssetType().directory();
				File destinationFile = new File(directory, create.getName());
				Files.copy(create.getFile().toPath(), destinationFile.toPath());
			}
			/*
			 * Notify all method listeners (e.g. chromatogram editor menus).
			 * Perform an update of the configuration.
			 * Scan the plugins.
			 */
			eventBroker.post(IChemClipseEvents.TOPIC_METHOD_UPDATE, null);
			configReader.run();
			bundleReader.run();
			//
			MessageBox messageBox = new MessageBox(shell, SWT.YES | SWT.NO);
			messageBox.setText("Update Assets");
			messageBox.setMessage("A restart is required. Restart now?");
			if(SWT.YES == messageBox.open()) {
				workbench.restart();
			}
		}
	}

	@CanExecute
	public boolean canExecute(@Optional @Service(filterExpression = "(action=ConfigReader)") Runnable configReader, @Optional @Service(filterExpression = "(action=BundleReader)") Runnable bundleReader, @Optional IEventBroker eventBroker) {

		return configReader != null && bundleReader != null && eventBroker != null;
	}
}
