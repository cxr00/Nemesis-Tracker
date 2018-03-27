package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import cxr.cooid.canon.Thing;
import data.SaveData;

public class FiendControllerButton extends JButton implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static Color BUTTON_COLOR = new Color(141, 151, 248);
	
	public static class JointFCBListener implements ActionListener{
		
		private SaveData data;
		private FiendControllerButton source;
		
		public JointFCBListener(SaveData data, FiendControllerButton source){
			this.data = data;
			this.source = source;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			switch(arg0.getActionCommand()){
			case "Update":
				if(!(source.type.equals("Fiend") || source.type.equals("Nemesis"))){
					switch(source.type){
					case "Area Conquest":
						source.setEnabled(data.areaIsConquered(SaveData.getCXR("Fiend").get(source.name).get("area").val()));
						break;
					case "Species Conquest":
						Thing tmp = SaveData.getCXR("Species Conquest").get(source.name);
						source.setEnabled(data.speciesIsConquered(tmp.get("type").val(), Integer.parseInt(tmp.get("requirement").val())));
						break;
					case "Original Creations":
						source.setEnabled(data.originalUnlocked(SaveData.getCXR("Original Creations").get(source.name)));
						break;
					}
				}
				break;
			default: source.setText(source.name + progress());
			}
			source.repaint();
		}
		
		private String progress(){
			if(source.type.equals("Fiend")){
				if(data.get(source.type).get(source.name).val().equals("10")){ 
					source.setBackground(Color.DARK_GRAY);
					return " (DONE)"; }
				else{
					source.setBackground(BUTTON_COLOR);
					return " (" + data.get("Fiend").get(source.name).val() + " / 10" + ")"; } }
			else if(data.get(source.type).get(source.name).val().equals("1")){
				source.setBackground(Color.DARK_GRAY);
				return " (DONE)"; }
			return "";
		}
		
	}
	
	public static class FCBListener implements MouseListener{

		// Can be Fiend, Area, Species, or Original
		private SaveData data;
		private FiendControllerButton source;
		
		public FCBListener(SaveData data, FiendControllerButton source){
			this.data = data;
			this.source = source;
			source.setText(source.name + " " + progress());
		}

		@Override
		public void mouseClicked(MouseEvent m) {
			int o = SwingUtilities.isLeftMouseButton(m) ? 1 : SwingUtilities.isRightMouseButton(m) ? 0 : -1;
			if(o != -1){
				switch(source.type){
				case "Fiend":
					if(o == 1){ data.countUpFiend(source.name); }
					else{ data.countDownFiend(source.name); }
					break;
				case "Area Conquest":
					data.toggleAreaConquestFiend(source.name, o);
					break;
				case "Species Conquest":
					data.toggleSpeciesConquestFiend(source.name, o);
					break;
				case "Original Creations":
					data.toggleOriginalFiend(source.name, o);
					break;
				case "Nemesis":
					data.toggleNemesis(o);
					break;
				}
				source.setText(source.name + " " + progress());
				source.repaint();
			}
			source.fireActionPerformed(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, "Joint Call"));
		}

		@Override public void mouseEntered(MouseEvent m) {
		}
		@Override public void mouseExited(MouseEvent m) {
		}
		
		private String progress(){
			if(source.type.equals("Fiend")){
				if(data.get(source.type).get(source.name).val().equals("10")){
					source.setBackground(Color.DARK_GRAY);
					return " (DONE)"; }
				else{
					source.setBackground(BUTTON_COLOR);
					return " (" + data.get("Fiend").get(source.name).val() + " / 10" + ")"; } }
			else if(data.get(source.type).get(source.name).val().equals("1")){
				source.setBackground(Color.DARK_GRAY);
				return " (DONE)"; }
			source.setBackground(BUTTON_COLOR);
			return "";
		}
		
		@Override public void mousePressed(MouseEvent m) {}
		@Override public void mouseReleased(MouseEvent m) {}
	}
	
	private SaveData data;
	private String type;
	private String name;
	private int cost;
	private ImageIcon icon;
	
	private static int g = 34;
	
	public FiendControllerButton(SaveData data, String type, String name){
		this.data = data;
		this.type = type;
		this.name = name;
		
		URL resource = SaveData.class.getResource("/data/ico/" + name + ".png");
		try {
			InputStream is = resource.openStream();
			icon = new ImageIcon(ImageIO.read(is));
			this.setIcon(icon);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (NullPointerException npe){
		}
		
		this.setBackground(BUTTON_COLOR);
		this.setPreferredSize(new Dimension(175, 19+g));
		this.setHorizontalTextPosition(JButton.CENTER);
		this.setVerticalTextPosition(JButton.BOTTOM);
		FCBListener fbcl = new FCBListener(data, this);
		this.addMouseListener(fbcl);
		this.cost = Integer.parseInt(SaveData.getCXR(type).get(name).get("cost").val());
	}
	
	public String name(){ return name; }
	public String type(){ return type; }
	
	public int getCost(){ return cost; }

	@Override
	public void actionPerformed(ActionEvent e) {
		setText(name + " " + progress());
		repaint();
	}
	
	private String progress(){
		if(type.equals("Fiend")){
			if(data.get(type).get(name).val().equals("10")){
				setBackground(Color.DARK_GRAY);
				return " (DONE)"; }
			else{
				setBackground(BUTTON_COLOR);
				return " (" + data.get("Fiend").get(name).val() + " / 10" + ")"; } }
		else if(data.get(type).get(name).val().equals("1")){
			setBackground(Color.DARK_GRAY);
			return " (DONE)"; }
		setBackground(BUTTON_COLOR);
		return "";
	}
	
}
