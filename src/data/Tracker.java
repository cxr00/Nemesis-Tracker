package data;

import cxr.cooid.canon.Bill;
import cxr.cooid.canon.Property;
import cxr.cooid.canon.Thing;


public class Tracker extends Bill {
	
	public static String ac = "Area Conquest";
	public static String ac_f = "Area Conquest Fiend";
	public static String f = "Fiend";
	public static String o = "Original";
	public static String o_f = "Original Fiend";
	public static String sc = "Species Conquest";
	public static String sc_f = "Species Conquest Fiend";
	
	public static Tracker instance;
	private Bill fiends;
	private Bill area;
	private Bill species;
	private Bill original;
	
	private Tracker(String root) {
		super("Tracker", Bill.parse(root + Sys.fileSep + "Save" + Sys.ext, "SaveTracker").val());
		root += Sys.fileSep;
		fiends = Bill.parse(root + "Fiends.cxr", "Fiends");
		area = Bill.parse(root + "Area.cxr", "Fiends");
		species= Bill.parse(root + "Species.cxr", "Fiends");
		original = Bill.parse(root + "Original.cxr", "Fiends");
	}
	
	// For increasing the number captured of a particular fiend
	public void countUpFiend(String name){
		if(get(f).contains(name)){
			Property c = get(f).get(name);
			String a = fiends.get(name).get("area").val();
			int v = Integer.parseInt(c.val());
			if(v < 10){ c.setVal(String.valueOf(v + 1)); }
			if(v + 1 == 1 && areaIsConquered(a)) { toggleAreaConquest(a, 1); }
		} }
	
	// Just in case you accidentally count the wrong fiend up, you can count it back down without a problem
	public void countDownFiend(String name){
		if(get(f).contains(name)){
			Property c = get(f).get(name);
			String a = fiends.get(name).get("area").val();
			int v = Integer.parseInt(c.val());
			if(v > 0){ c.setVal(String.valueOf(v - 1)); }
			if(v - 1 == 0) {toggleAreaConquest(a, 0); }
		} }
	
	// Turn unlocks and fiend completion on and off.
	public void toggleAreaConquest(String area, int n){ get(ac).get(area).setVal(String.valueOf(n)); }
	public void toggleAreaConquestFiend(String name, int n){ get(ac_f).get(name).setVal(String.valueOf(n)); }
	
	public void toggleOriginal(String name, int n){ get(o).get(name).setVal(String.valueOf(n)); }
	public void toggleOriginalFiend(String name, int n){ get(o_f).get(name).setVal(String.valueOf(n)); }
	
	public void toggleSpeciesConquest(String name, int n){ get(sc).get(name).setVal(String.valueOf(n)); }
	public void toggleSpeciesConquestFiend(String name, int n){ get(sc_f).get(name).setVal(String.valueOf(n)); }
	
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
		for(Property p : get(ac_f)){ if(!p.val().equals("1")){ out += Integer.parseInt(p.val()); } }
		return out; }
	
	// A condition for unlocking Nemesis
	public boolean allAreaConquestFiendsDefeated(){ return numAreaFiendsDefeated() == area.size(); }
	
	/* ****************** *
	 * ORIGINAL CREATIONS * 
	 * ****************** */
	
	// Determine whether an original fiend is unlocked based on type and requirement parameters
	public boolean originalUnlocked(String name){
		Thing in = original.get(name);
		switch(in.get("type").val()){
		case "area": return numAreasConquered() >= Integer.parseInt(in.get("requirement").val());
		case "species": return numSpeciesConquered() >= Integer.parseInt(in.get("requirement").val());
		case "underwater": return speciesConquered("Underwater", 2);
		case "nemesis": return unlockedNemesis();
		default: return false;
		} }
	
	// For SVP and internal calculations
	public int numOriginalFiendsDefeated(){
		int out = 0;
		for(Property p : get(o_f)){ if(!p.val().equals("1")){ out += Integer.parseInt(p.val()); } }
		return out; }
	
	// A condition for unlocking Nemesis
	public boolean allOriginalFiendsDefeated(){ return numOriginalFiendsDefeated() == original.size(); }
	
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
	public boolean speciesConquered(String type, int n){
		Thing in = getSpecies(type);
		for(Property p : in){ if(Integer.parseInt(p.val()) < n){ return false; } }
		return true; }
	
	// Determine if a species conquest fiend is unlocked
	public boolean speciesFiendUnlocked(String type){
		for(Thing t : species){
			if(t.get("type").val().equals(type)){
				return speciesConquered(type, Integer.parseInt(t.get("requirement").val())); } }
		return false; }
	
	// A condition for unlocking Original fiends
	public int numSpeciesConquered(){
		int out = 0;
		for(Property p : get(sc)){ out += Integer.parseInt(p.val()); }
		return out; }
	
	// Tally for SVP and internal calculation
	public int numSpeciesFiendsDefeated(){
		int out = 0;
		for(Property p : get(sc_f)){ if(!p.val().equals("1")){ out += Integer.parseInt(p.val()); } }
		return out; }
	
	// A condition for unlocking Nemesis
	public boolean allSpeciesConquestFiendsDefeated(){ return numSpeciesFiendsDefeated() == species.size(); }
	
	
	/* ******* *
	 * NEMESIS *
	 * ******* */
	
	// For SVP and internal calculations
	public int numFiendsMaxCaptured(){
		int out = 0;
		for(Property p : get(f)){ out += Integer.parseInt(p.val()) == 10 ? 1 : 0; }
		return out; }
	
	// A condition for unlocking Nemesis
	public boolean allFiendsMaxCaptured(){ return numFiendsMaxCaptured() == fiends.size(); }
	
	// Did you do it all?
	public boolean unlockedNemesis(){
		return allFiendsMaxCaptured() && allAreaConquestFiendsDefeated()
		 && allSpeciesConquestFiendsDefeated() && allOriginalFiendsDefeated();
	}
	
	public static void main(String... strings){
		Tracker t = new Tracker("X:\\! Co-oid\\monster_mgr");
		
		System.out.println(t.cxrString());
		System.out.println(t.fiends.cxrString());
		System.out.println(t.area.cxrString());
		System.out.println(t.species.cxrString());
		System.out.println(t.original.cxrString());
	}

}
