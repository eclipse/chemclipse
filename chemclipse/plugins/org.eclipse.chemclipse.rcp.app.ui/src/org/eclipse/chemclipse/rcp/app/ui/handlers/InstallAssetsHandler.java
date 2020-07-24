/*******************************************************************************
 * Copyright (c) 2020 Christoph Läubrich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.handlers;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createColumn;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createDefault;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createTable;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.maximize;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.top;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.eclipse.chemclipse.logging.support.Settings;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.swt.columns.SimpleColumnDefinition;
import org.eclipse.chemclipse.support.ui.wizards.SinglePageWizard;
import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Service;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public class InstallAssetsHandler {

	enum AssetType {

		Configuration(".cfg"), Method(".ocm"), Plugin(".jar");

		private final String extension;

		private AssetType(String extension) {

			this.extension = extension;
		}

		private File getDirectory() {

			switch(this) {
				case Configuration:
					return Settings.getSystemConfigDirectory();
				case Method:
					return Settings.getSystemMethodDirectory();
				case Plugin:
					return Settings.getSystemPluginDirectory();
				default:
					return Settings.getSystemDirectory();
			}
		}

		public String getDescription() {

			switch(this) {
				case Method:
					return "Process Method File";
				case Configuration:
					return "Service Configuration File";
				case Plugin:
					return "Plugin Extension";
				default:
					return "";
			}
		}
	};

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, @Service(filterExpression = "(action=ConfigReader)") Runnable configReader, @Service(filterExpression = "(action=BundleReader)") Runnable bundleReader, IEventBroker eventBroker) throws IOException {

		AssetInstallPage page = new AssetInstallPage();
		SinglePageWizard wizard = new SinglePageWizard("Install / Manage Assets", true, page);
		if(wizard.open(shell, 800, 600)) {
			for(AssetItem delete : page.deletedItems) {
				File directory = delete.getType().getDirectory();
				File deleteFile = new File(directory, delete.file.getName());
				File backupFile = new File(directory, delete.file.getName() + "." + System.currentTimeMillis() + ".bak");
				deleteFile.renameTo(backupFile);
			}
			for(AssetItem create : page.newItems) {
				File directory = create.getType().getDirectory();
				File destinationFile = new File(directory, create.file.getName());
				Files.copy(create.file.toPath(), destinationFile.toPath());
			}
			// notify method listeners (e.g. chromatogram editor menus)
			eventBroker.post(IChemClipseEvents.TOPIC_METHOD_UPDATE, null);
			// perform update of configurations
			configReader.run();
			// rescan plugins
			bundleReader.run();
		}
	}

	private static final class AssetInstallPage extends WizardPage {

		private List<AssetItem> deletedItems = new ArrayList<>();
		private List<AssetItem> newItems = new ArrayList<>();

		public AssetInstallPage() {

			super(AssetInstallPage.class.getName());
			setTitle("Manage additional assets");
			setDescription("Here you can add or remove assets to your installation, for example new configurations, global available methods and even extension plugins");
		}

		@Override
		public void createControl(Composite parent) {

			List<AssetItem> assets = new ArrayList<>();
			Composite composite = createDefault(parent, 2);
			TableViewer table = createTable(composite, false);
			createColumn(table, new SimpleColumnDefinition<>("Type", 100, new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {

					if(element instanceof AssetItem) {
						return ((AssetItem)element).getType().toString();
					}
					return super.getText(element);
				}

				@Override
				public Image getImage(Object element) {

					if(element instanceof AssetItem) {
						switch(((AssetItem)element).getType()) {
							case Configuration:
								return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PREPROCESSING, IApplicationImage.SIZE_16x16);
							case Method:
								return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD, IApplicationImage.SIZE_16x16);
							case Plugin:
								return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PLUGINS, IApplicationImage.SIZE_16x16);
							default:
								break;
						}
					}
					return super.getImage(element);
				}
			}));
			createColumn(table, new SimpleColumnDefinition<>("Asset", 400, AssetItem::getName));
			createColumn(table, new SimpleColumnDefinition<>("Description", 300, AssetItem::getDescription));
			maximize(table.getControl());
			ToolBarManager toolBarManager = new ToolBarManager(SWT.VERTICAL | SWT.FLAT);
			toolBarManager.add(new Action("Add a new asset", ApplicationImageFactory.getInstance().getIcon(IApplicationImage.IMAGE_IMPORT)) {

				private FileDialog fileDialog;

				@Override
				public void run() {

					if(fileDialog == null) {
						fileDialog = new FileDialog(getShell());
						fileDialog.setText("Select an Asset");
						List<String> names = new ArrayList<>();
						List<String> extensions = new ArrayList<>();
						for(AssetType type : AssetType.values()) {
							names.add(type.toString());
							extensions.add("*" + type.extension);
						}
						names.add(0, String.join("; ", names));
						extensions.add(0, String.join(";", extensions));
						fileDialog.setFilterNames(names.toArray(new String[0]));
						fileDialog.setFilterExtensions(extensions.toArray(new String[0]));
					}
					String open = fileDialog.open();
					if(open != null) {
						File file = new File(open);
						for(AssetType type : AssetType.values()) {
							if(file.getName().toLowerCase().endsWith(type.extension)) {
								AssetItem newItem = new AssetItem(file, type);
								if(new File(type.getDirectory(), file.getName()).exists()) {
									MessageDialog.openError(getShell(), "Item already exits", "This item is already installed");
									return;
								}
								assets.add(newItem);
								newItems.add(newItem);
								table.add(newItem);
							}
						}
					}
				}
			});
			Action deleteAction = new Action("Delete selected asset(s)", ApplicationImageFactory.getInstance().getIcon(IApplicationImage.IMAGE_DELETE)) {

				@Override
				public void run() {

					for(Object item : table.getStructuredSelection().toArray()) {
						if(item instanceof AssetItem) {
							AssetItem assetItem = (AssetItem)item;
							assets.remove(assetItem);
							table.remove(assetItem);
							if(newItems.remove(assetItem)) {
								// the item was never persisted so we do not need to delete it...
								return;
							}
							deletedItems.add(assetItem);
						}
					}
				}
			};
			deleteAction.setEnabled(false);
			table.addSelectionChangedListener(e -> deleteAction.setEnabled(!e.getSelection().isEmpty()));
			toolBarManager.add(deleteAction);
			top(toolBarManager.createControl(composite));
			for(AssetType type : AssetType.values()) {
				File directory = type.getDirectory();
				File[] listFiles = directory.listFiles();
				if(listFiles != null) {
					for(File file : listFiles) {
						if(file.getName().toLowerCase().endsWith(type.extension)) {
							assets.add(new AssetItem(file, type));
						}
					}
				}
			}
			table.setInput(assets);
			setControl(composite);
		}
	}

	private static final class AssetItem {

		private File file;
		private AssetType type;

		public AssetItem(File file, AssetType type) {

			this.file = file;
			this.type = type;
		}

		public String getName() {

			return file.getName();
		}

		public AssetType getType() {

			return type;
		}

		public String getDescription() {

			return type.getDescription();
		}
	}

	public static final class VisibleWhen {

		@Evaluate
		public boolean isVisible() {

			return !Boolean.getBoolean("chemclipse.assets.install.disabled");
		}
	}
}