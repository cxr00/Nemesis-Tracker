package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

import cxr.cooid.canon.Bill;
import cxr.cooid.canon.Property;
import cxr.cooid.canon.Thing;


public class SaveData extends Bill{
	
	public static final String ext = ".cxr";
	public static final int len = ext.length();
	
	public static final String userDir = System.getProperty("user.dir");
	public static final String fileSep = System.getProperty("file.separator");
	
	public static final String appData = System.getenv("localappdata") + fileSep + "Nemesis-Tracker.cxr";
	
	public static String ac = "Area Conquest Unlocked";
	public static String ac_f = "Area Conquest";
	public static String f = "Fiend";
	public static String o = "Original Creation Unlocked";
	public static String o_f = "Original Creations";
	public static String sc = "Species Conquest Unlocked";
	public static String sc_f = "Species Conquest";
	
	public static final SaveData instance = loadSaveData();
	public static Bill template;
	public static Bill fiends;
	public static Bill area;
	public static Bill species;
	public static Bill original;
	public static Bill nemesis;
	
	public static Bill getCXR(String type){
		switch(type){
		case "Fiend": return fiends;
		case "Area Conquest": return area;
		case "Species Conquest": return species;
		case "Original Creations": return original;
		case "Nemesis": return nemesis;
		default: return null;
		}
	}
	
	private SaveData() {
		super("SaveData", getSave());
		this.save();
	}
	
	/* ***************** *
	 * SAVE/LOAD METHODS *
	 * ***************** */
	
	private static SaveData loadSaveData(){
		File f = new File(appData);
		if(!f.exists()){ f.mkdir(); }
		try{
			InputStream is = null;
			URL resource = null;
			List<String> pList = null;
			String resourcePath = "/data/";
			
			resource = SaveData.class.getResource(resourcePath + "Fiends.cxr");
			is = resource.openStream();
			pList = Arrays.asList(IOUtils.toString(is, StandardCharsets.UTF_8).split("\n"));
			fiends = Bill.parse("Fiends", pList);
		
			resource = SaveData.class.getResource(resourcePath + "Area Conquest.cxr");
			is = resource.openStream();
			pList = Arrays.asList(IOUtils.toString(is, StandardCharsets.UTF_8).split("\n"));
			area = Bill.parse("Area", pList);
			
			resource = SaveData.class.getResource(resourcePath + "Species Conquest.cxr");
			is = resource.openStream();
			pList = Arrays.asList(IOUtils.toString(is, StandardCharsets.UTF_8).split("\n"));
			species= Bill.parse("Species", pList);
			
			resource = SaveData.class.getResource(resourcePath + "Original Creations.cxr");
			is = resource.openStream();
			pList = Arrays.asList(IOUtils.toString(is, StandardCharsets.UTF_8).split("\n"));
			original = Bill.parse("Original", pList);
			
			resource = SaveData.class.getResource(resourcePath + "Template.cxr");
			is = resource.openStream();
			pList = Arrays.asList(IOUtils.toString(is, StandardCharsets.UTF_8).split("\n"));
			template = Bill.parse("Template", pList);
			
			resource = SaveData.class.getResource(resourcePath + "Nemesis.cxr");
			is = resource.openStream();
			pList = Arrays.asList(IOUtils.toString(is, StandardCharsets.UTF_8).split("\n"));
			nemesis = Bill.parse("Nemesis", pList);
			
			return new SaveData();
		}catch(IOException ioe){ioe.printStackTrace(); return null; }
	}
	
	// Saves when you explicitly press the "save" button
	public boolean save(){
		try{
			FileWriter fw = new FileWriter(new File(appData + fileSep + tag() + ext));
			fw.write(cxrString());
			fw.close();
			return true;
		}catch(IOException ioe){ ioe.printStackTrace(); return false; }
	}
	
	public void revert(){ this.val = getSave(); }
	
	private static ArrayList<Thing> getSave(){
		File f = new File(appData + fileSep + "SaveData" + ext);
		if(!f.exists()){ return template.val(); }
		else{
			List<String> out = new ArrayList<String>();
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String l = br.readLine();
				while(l != null){
					out.add(l);
					l = br.readLine();
				}
				br.close();
				return Bill.parse("Save", out).val();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	// Saves after every action, Just in case.
	public boolean backup(){
		try{
			FileWriter fw = new FileWriter(new File(appData + fileSep + tag() + ext + ".bak"));
			fw.write(cxrString());
			fw.close();
			return true;
		}catch(IOException ioe){ ioe.printStackTrace(); return false; }
	}
	
	public void loadFromBackup(){
		Bill out = Bill.parse(appData + fileSep + tag() + ext + ".bak", "BACKUP");
		this.val = out.val();
	}
	
	/* ******************** *
	 * MODIFICATION METHODS *
	 * ******************** */
	
	// For increasing the number captured of a particular fiend
	public void countUpFiend(String name){
		if(get(f).contains(name)){
			Property c = get(f).get(name);
			// Change value
			int v = Integer.parseInt(c.val());
			if(v < 10){ c.setVal(String.valueOf(v + 1)); }
			// Area Conquest
			String a = fiends.get(name).get("area").val();
			if(v + 1 == 1 && areaIsConquered(a)) { toggleAreaConquest(a, 1); }
			// Species Conquest
			String s = fiends.get(name).get("type").val();
			if(v + 1 == 1 && speciesIsConquered(s, 1)) { toggleSpeciesConquest(s, 1); }
		} backup(); }
	
	// Just in case you accidentally count the wrong fiend up, you can count it back down without a problem
	public void countDownFiend(String name){
		if(get(f).contains(name)){
			Property c = get(f).get(name);
			// Change value
			int v = Integer.parseInt(c.val());
			if(v > 0){ c.setVal(String.valueOf(v - 1)); }
			// Area Conquest
			String a = fiends.get(name).get("area").val();
			if(v - 1 == 0) {toggleAreaConquest(a, 0); }
			// Species Conquest
			String s = fiends.get(name).get("type").val();
			if(v - 1 == 0 && !(s.equals("Underwater") || s.equals("Miscellaneous") )) {toggleSpeciesConquest(s, 0); }
		} backup(); }
	
	// Turn unlocks and fiend completion on and off.
	public void toggleAreaConquest(String area, int n){ get(ac).get(area).setVal(String.valueOf(n)); backup(); }
	public void toggleAreaConquestFiend(String name, int n){ get(ac_f).get(name).setVal(String.valueOf(n)); backup(); }
	
	public void toggleOriginal(String name, int n){ get(o).get(name).setVal(String.valueOf(n)); backup(); }
	public void toggleOriginalFiend(String name, int n){ get(o_f).get(name).setVal(String.valueOf(n)); backup(); }
	
	public void toggleSpeciesConquest(String name, int n){ get(sc).get(name).setVal(String.valueOf(n)); backup(); }
	public void toggleSpeciesConquestFiend(String name, int n){ get(sc_f).get(name).setVal(String.valueOf(n)); backup(); }
	
	/* ************* *
	 * AREA CONQUEST *
	 * ************* */
	
	// For checking whether an area is conquered
	public Thing getArea(String area){
		Thing out = new Thing(area);
		for(Thing t : fiends){
			// Check each fiend in area
			if(t.get("area").val().equals(area)){
				out.add(new Property(t.tag(), get(f).get(t.tag()).val())); } }
		return out; }
	
	// Areas are only conquered when at least one of every fiend is captured
	public boolean areaIsConquered(String area){
		for(Property e : getArea(area)){ if(Integer.parseInt(e.val()) == 0){ return false; } }
		return true; }
	
	// A condition for unlocking Original fiends
	public int numAreasConquered(){
		int out = 0;
		for(Property p : get(ac)){ out += Integer.parseInt(p.val()); }
		return out; }
	
	// Tally for SVP and internal calculation
	public int numAreaFiendsDefeated(){
		int out = 0;
		for(Property p : get(ac_f)){ if(p.val().equals("1")){ out += Integer.parseInt(p.val()); } }
		return out; }
	
	// A condition for unlocking Nemesis
	public boolean allAreaConquestFiendsDefeated(){
		int out = numAreaFiendsDefeated();
		System.out.println("AreaConquestFiendsDefeated: " + out);
		return out == area.size(); }
	
	/* ****************** *
	 * ORIGINAL CREATIONS * 
	 * ****************** */
	
	// check whether any new original fiends are unlocked
	public void updateOriginalUnlocks(){ for(Thing e : original){ toggleOriginal(e.tag(), originalUnlocked(e) ? 1 : 0); } }
	
	// Determine whether an original fiend is unlocked based on type and requirement parameters
	public boolean originalUnlocked(Thing orig){
		switch(orig.get("type").val()){
		case "area": return numAreasConquered() >= Integer.parseInt(orig.get("requirement").val());
		case "species": return numSpeciesConquered() >= Integer.parseInt(orig.get("requirement").val());
		case "underwater": return speciesIsConquered("Underwater", 2);
		default: return false;
		} }
	
	public boolean originalUnlocked(String name){ return originalUnlocked(original.get(name)); }
	
	// For SVP and internal calculations
	public int numOriginalFiendsDefeated(){
		int out = 0;
		for(Property p : get(o_f)){ if(p.val().equals("1")){ out += Integer.parseInt(p.val()); } }
		return out; }
	
	// A condition for unlocking Nemesis
	public boolean allOriginalFiendsDefeated(){
		int out = numOriginalFiendsDefeated();
		System.out.println("OriginalCreationsDefeated: " + out);
		return out == original.size(); }
	
	/* **************** *
	 * SPECIES CONQUEST *
	 * **************** */
	
	// For checking whether species conquest conditions are met
	public Thing getSpecies(String type){
		Thing out = new Thing(type);
		for(Thing t : fiends){
			// Check each fiend in area
			if(t.get("type").val().equals(type)){
				out.add(new Property(t.tag(), get(f).get(t.tag()).val())); } }
		return out; }
	
	// Determine if a species type has been conquered to n amount 
	public boolean speciesIsConquered(String type, int n){
		Thing in = getSpecies(type);
		for(Property p : in){ if(Integer.parseInt(p.val()) < n){ return false; } }
		return true; }
	
	// Determine if a species conquest fiend is unlocked
	public boolean speciesFiendUnlocked(String type){
		for(Thing t : species){
			if(t.get("type").val().equals(type)){
				return speciesIsConquered(type, Integer.parseInt(t.get("requirement").val())); } }
		return false; }
	
	// A condition for unlocking Original fiends
	public int numSpeciesConquered(){
		int out = 0;
		for(Property p : get(sc)){ out += Integer.parseInt(p.val()); }
		return out; }
	
	// Tally for SVP and internal calculation
	public int numSpeciesFiendsDefeated(){
		int out = 0;
		for(Property p : get(sc_f)){ if(p.val().equals("1")){ out += Integer.parseInt(p.val()); } }
		return out; }
	
	// A condition for unlocking Nemesis
	public boolean allSpeciesConquestFiendsDefeated(){
		int out = numSpeciesFiendsDefeated();
		System.out.println("SpeciesConquestFiendsDefeated: " + out);
		return out == species.size(); }
	
	
	/* ******* *
	 * NEMESIS *
	 * ******* */
	
	// For SVP and internal calculations
	public int numFiendsMaxCaptured(){
		int out = 0;
		for(Property p : get(f)){ out += Integer.parseInt(p.val()) == 10 ? 1 : 0; }
		return out; }
	
	// A condition for unlocking Nemesis
	public boolean allFiendsMaxCaptured(){
		int out = numFiendsMaxCaptured();
		System.out.println("Fiends Max Captured: " + out);
		return out == fiends.size(); }
	
	// Did you do it all?
	public boolean unlockedNemesis(){
		boolean a = allFiendsMaxCaptured();
		boolean b = allAreaConquestFiendsDefeated();
		boolean c = allSpeciesConquestFiendsDefeated();
		boolean d = allOriginalFiendsDefeated();
		System.out.println();
		return a && b && c && d;
	}
	
	public void toggleNemesis(int o){ get("Nemesis").get(0).setVal(String.valueOf(o)); }
}
