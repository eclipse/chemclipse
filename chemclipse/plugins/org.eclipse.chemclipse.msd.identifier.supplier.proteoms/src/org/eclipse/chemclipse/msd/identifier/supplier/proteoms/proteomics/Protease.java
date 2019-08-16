package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.proteomics;

public class Protease {

	public static final Protease TRYPSIN = new Protease("Trypsin");
	private String name;

	public Protease(String name) {
		this.name = name;
	}

	public String getName() {

		return name;
	}
}
