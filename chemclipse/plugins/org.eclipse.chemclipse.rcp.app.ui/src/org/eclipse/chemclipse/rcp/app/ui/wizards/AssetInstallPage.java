/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.wizards;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createColumn;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createDefault;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createTable;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.maximize;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.top;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.rcp.app.assets.AssetItem;
import org.eclipse.chemclipse.rcp.app.assets.AssetType;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.ui.swt.columns.SimpleColumnDefinition;
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

public class AssetInstallPage extends WizardPage {

	private static final Logger logger = Logger.getLogger(AssetInstallPage.class);
	//
	private TableViewer tableViewer;
	//
	private List<AssetItem> assets = new ArrayList<>();
	private List<AssetItem> deletedItems = new ArrayList<>();
	private List<AssetItem> newItems = new ArrayList<>();

	public AssetInstallPage() {

		super(AssetInstallPage.class.getName());
		setTitle("Manage Assets");
		setDescription("Manage additional assets of the installation, for example new configurations, globally available methods and plugins.");
	}

	public List<AssetItem> getDeletedItems() {

		return Collections.unmodifiableList(deletedItems);
	}

	public List<AssetItem> getNewItems() {

		return Collections.unmodifiableList(newItems);
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = createDefault(parent, 2);
		//
		tableViewer = createTableViewer(composite);
		createToolBarManager(composite);
		updateInput();
		//
		setControl(composite);
	}

	private TableViewer createTableViewer(Composite parent) {

		TableViewer tableViewer = createTable(parent, false);
		createColumns(tableViewer);
		maximize(tableViewer.getControl());
		//
		return tableViewer;
	}

	private void createColumns(TableViewer tableViewer) {

		createColumn(tableViewer, new SimpleColumnDefinition<>("Type", 100, new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof AssetItem) {
					return ((AssetItem)element).getAssetType().getLabel();
				}
				return super.getText(element);
			}

			@Override
			public Image getImage(Object element) {

				if(element instanceof AssetItem) {
					switch(((AssetItem)element).getAssetType()) {
						case CONFIGURATION:
							return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PREPROCESSING, IApplicationImage.SIZE_16x16);
						case METHOD:
							return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD, IApplicationImage.SIZE_16x16);
						case PLUGIN:
							return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PLUGINS, IApplicationImage.SIZE_16x16);
						default:
							break;
					}
				}
				return super.getImage(element);
			}
		}));
		//
		createColumn(tableViewer, new SimpleColumnDefinition<>("Asset", 400, AssetItem::getName));
		createColumn(tableViewer, new SimpleColumnDefinition<>("Description", 300, AssetItem::getDescription));
	}

	private ToolBarManager createToolBarManager(Composite parent) {

		ToolBarManager toolBarManager = new ToolBarManager(SWT.VERTICAL | SWT.FLAT);
		//
		Action actionAddAsset = createActionAddAsset();
		Action actionAddAssets = createActionAddAssets();
		Action actionDeleteAsset = createActionDeleteAsset();
		actionDeleteAsset.setEnabled(false);
		Action actionDeleteAssets = createActionDeleteAssets();
		//
		toolBarManager.add(actionAddAsset);
		toolBarManager.add(actionAddAssets);
		toolBarManager.add(actionDeleteAsset);
		toolBarManager.add(actionDeleteAssets);
		//
		tableViewer.addSelectionChangedListener(e -> actionDeleteAsset.setEnabled(!e.getSelection().isEmpty()));
		tableViewer.addSelectionChangedListener(e -> actionDeleteAssets.setEnabled(assets.size() > 0));
		//
		top(toolBarManager.createControl(parent));
		//
		return toolBarManager;
	}

	private Action createActionAddAsset() {

		Action action = new Action("Add New Asset", ApplicationImageFactory.getInstance().getIcon(IApplicationImage.IMAGE_IMPORT)) {

			@Override
			public void run() {

				FileDialog fileDialog = new FileDialog(getShell());
				fileDialog.setText("Select an Asset");
				/*
				 * Display available options.
				 */
				List<String> filterNames = new ArrayList<>();
				List<String> filterExtensions = new ArrayList<>();
				for(AssetType assetType : AssetType.values()) {
					String extension = "*" + assetType.getExtension();
					filterNames.add(assetType.getLabel() + " (" + extension + ")");
					filterExtensions.add(extension);
				}
				//
				String extensionsAll = String.join(";", filterExtensions);
				filterNames.add(0, "All Assets (*.*)");
				filterExtensions.add(0, extensionsAll);
				//
				fileDialog.setFilterNames(filterNames.toArray(new String[filterNames.size()]));
				fileDialog.setFilterExtensions(filterExtensions.toArray(new String[filterExtensions.size()]));
				//
				String open = fileDialog.open();
				if(open != null) {
					File file = new File(open);
					AssetItem assetItem = getAssetItem(file);
					if(assetItem != null) {
						String message = addNewAsset(assetItem);
						if(message != null) {
							MessageDialog.openError(getShell(), "Add Asset", message);
						} else {
							tableViewer.add(assetItem);
						}
					}
				}
			}
		};
		//
		return action;
	}

	private Action createActionAddAssets() {

		Action action = new Action("Add New Assets (ZIP)", ApplicationImageFactory.getInstance().getIcon(IApplicationImage.IMAGE_ZIP_FILE)) {

			@Override
			public void run() {

				FileDialog fileDialog = new FileDialog(getShell());
				fileDialog.setText("Select Zipped Assets");
				/*
				 * Display available options.
				 */
				fileDialog.setFilterNames(new String[]{"Zipped Assets (*.zip)"});
				fileDialog.setFilterExtensions(new String[]{"*.zip"});
				//
				String open = fileDialog.open();
				if(open != null) {
					/*
					 * ZIP contains methods, ...
					 */
					File directoryTmp = new File(System.getProperty("java.io.tmpdir"));
					if(directoryTmp.exists()) {
						File file = new File(open);
						try (ZipFile zipFile = new ZipFile(file)) {
							/*
							 * Available asset extension(s).
							 */
							Set<String> assetExtensions = new HashSet<>();
							for(AssetType assetType : AssetType.values()) {
								assetExtensions.add(assetType.getExtension());
							}
							/*
							 * Messages / AssetItems
							 */
							List<String> messages = new ArrayList<>();
							List<AssetItem> assetItems = new ArrayList<>();
							//
							Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
							while(zipEntries.hasMoreElements()) {
								ZipEntry zipEntry = zipEntries.nextElement();
								if(!zipEntry.isDirectory()) {
									String name = zipEntry.getName();
									if(assetExtensionMatches(name, assetExtensions)) {
										/*
										 * Create a tmp copy of the zip entry.
										 */
										byte[] bytes = extractFileBytes(zipFile, zipEntry);
										if(bytes.length > 0) {
											File fileAsset = writeTempFile(directoryTmp, name, bytes);
											if(fileAsset.exists()) {
												AssetItem assetItem = getAssetItem(fileAsset);
												if(assetItem != null) {
													String message = addNewAsset(assetItem);
													if(message != null) {
														messages.add(message);
													} else {
														assetItems.add(assetItem);
													}
												}
											}
										}
									}
								}
							}
							/*
							 * Add items to the table viewer.
							 */
							if(assetItems.size() > 0) {
								tableViewer.add(assetItems.toArray());
							}
							/*
							 * Something went wrong with some entries.
							 */
							if(messages.size() > 0) {
								StringBuilder builder = new StringBuilder();
								for(String message : messages) {
									builder.append(message);
									builder.append(OperatingSystemUtils.getLineDelimiter());
								}
								MessageDialog.openError(getShell(), "Add Asset(s)", builder.toString());
							}
						} catch(ZipException e) {
							logger.warn(e);
						} catch(IOException e) {
							logger.warn(e);
						}
					}
				}
			}
		};
		//
		return action;
	}

	/**
	 * Get the file content.
	 * 
	 * @param zipFile
	 * @param zipEntry
	 * @return byte[]
	 */
	private byte[] extractFileBytes(ZipFile zipFile, ZipEntry zipEntry) {

		byte[] bytes = new byte[0];
		try (DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(zipFile.getInputStream(zipEntry)))) {
			bytes = new byte[(int)zipEntry.getSize()];
			dataInputStream.readFully(bytes);
		} catch(IOException e) {
			logger.warn(e);
		}
		//
		return bytes;
	}

	/**
	 * Writes a tmp file with the given content.
	 * 
	 * @param directoryTmp
	 * @param name
	 * @param bytes
	 * @return
	 */
	private File writeTempFile(File directory, String name, byte[] bytes) {

		File file = new File(directory.getAbsolutePath() + File.separator + name);
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			fileOutputStream.write(bytes);
			fileOutputStream.flush();
		} catch(IOException e) {
			logger.warn(e);
		}
		//
		return file;
	}

	/**
	 * Returns true if the extension is available, e.g.:
	 * MyMethod.ocm -> [.ocm] matches
	 * 
	 * @param name
	 * @param assetExtensions
	 * @return
	 */
	private boolean assetExtensionMatches(String name, Set<String> assetExtensions) {

		for(String assetExtension : assetExtensions) {
			if(name.endsWith(assetExtension)) {
				return true;
			}
		}
		//
		return false;
	}

	/**
	 * Adds the asset. If a problem occurs, a message is returned. If everything is ok, the message is null.
	 * 
	 * @param file
	 * @return {String}
	 */
	private String addNewAsset(AssetItem assetItem) {

		String message = null;
		//
		AssetType assetType = assetItem.getAssetType();
		String name = assetItem.getName();
		//
		if(new File(assetType.getDirectory(), name).exists()) {
			message = "The asset is installed already: " + name + ".";
		} else {
			/*
			 * Add the asset.
			 */
			if(isNewAssetItemAvailable(assetItem)) {
				message = "The asset has been added already: " + name + ".";
			} else {
				assets.add(assetItem);
				newItems.add(assetItem);
			}
		}
		//
		return message;
	}

	private boolean isNewAssetItemAvailable(AssetItem assetItem) {

		for(AssetItem item : newItems) {
			if(item.getName().equals(assetItem.getName())) {
				return true;
			}
		}
		//
		return false;
	}

	/**
	 * Create an asset item. Null, if none is available.
	 * 
	 * @param file
	 * @return {AssetItem}
	 */
	private AssetItem getAssetItem(File file) {

		AssetItem assetItem = null;
		for(AssetType assetType : AssetType.values()) {
			if(file.getName().toLowerCase().endsWith(assetType.getExtension())) {
				assetItem = new AssetItem(file, assetType);
			}
		}
		//
		return assetItem;
	}

	/**
	 * Deletes the given asset.
	 * 
	 * @param assetItem
	 */
	private void deleteAsset(AssetItem assetItem) {

		assets.remove(assetItem);
		if(newItems.remove(assetItem)) {
			/*
			 * The item was never persisted so we do not need to delete it...
			 */
			return;
		}
		deletedItems.add(assetItem);
	}

	private Action createActionDeleteAsset() {

		Action action = new Action("Delete Selected Asset(s)", ApplicationImageFactory.getInstance().getIcon(IApplicationImage.IMAGE_DELETE)) {

			@Override
			public void run() {

				if(MessageDialog.openQuestion(getShell(), "Delete Selected Asset(s)", "Would you like to delete the selected asset(s)?")) {
					/*
					 * Delete
					 */
					List<AssetItem> assetItems = new ArrayList<>();
					for(Object item : tableViewer.getStructuredSelection().toArray()) {
						if(item instanceof AssetItem) {
							AssetItem assetItem = (AssetItem)item;
							assetItems.add(assetItem);
							deleteAsset(assetItem);
						}
					}
					//
					if(assetItems.size() > 0) {
						tableViewer.remove(assetItems.toArray());
					}
				}
			}
		};
		//
		return action;
	}

	private Action createActionDeleteAssets() {

		Action action = new Action("Delete All Asset(s)", ApplicationImageFactory.getInstance().getIcon(IApplicationImage.IMAGE_DELETE_ALL)) {

			@Override
			public void run() {

				if(MessageDialog.openQuestion(getShell(), "Delete Asset(s)", "Would you really like to delete all asset(s)?")) {
					/*
					 * Delete
					 */
					List<AssetItem> assetItems = new ArrayList<>();
					Object object = tableViewer.getInput();
					if(object instanceof List) {
						/*
						 * Create a new list to avoid a
						 * java.util.ConcurrentModificationException
						 */
						List<Object> items = new ArrayList<>();
						items.addAll((List<?>)object);
						//
						for(Object item : items) {
							if(item instanceof AssetItem) {
								AssetItem assetItem = (AssetItem)item;
								assetItems.add(assetItem);
								deleteAsset((AssetItem)item);
							}
						}
					}
					/*
					 * Clear
					 */
					if(assetItems.size() > 0) {
						tableViewer.remove(assetItems.toArray());
					}
				}
			}
		};
		//
		return action;
	}

	private void updateInput() {

		assets.clear();
		//
		for(AssetType type : AssetType.values()) {
			File directory = type.getDirectory();
			File[] listFiles = directory.listFiles();
			if(listFiles != null) {
				for(File file : listFiles) {
					if(file.getName().toLowerCase().endsWith(type.getExtension())) {
						assets.add(new AssetItem(file, type));
					}
				}
			}
		}
		//
		tableViewer.setInput(assets);
	}
}
