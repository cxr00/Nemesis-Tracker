package app;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import data.SaveData;

public class FiendControllerButton extends JButton {

	private static final long serialVersionUID = 1L;

	public static class FCPListener implements MouseListener{

		// Can be Fiend, Area, Species, or Original
		private SaveData data;
		private FiendControllerButton source;
		
		public FCPListener(SaveData data, FiendControllerButton source){
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
				case "Area":
					data.toggleAreaConquestFiend(source.name, o);
					break;
				case "Species":
					data.toggleSpeciesConquestFiend(source.name, o);
					break;
				case "Original":
					data.toggleOriginalFiend(source.name, o);
					break;
				}
				source.setText(source.name + progress());
				source.repaint();
			}
		}

		@Override public void mouseEntered(MouseEvent m) {
		}
		@Override public void mouseExited(MouseEvent m) {
		}
		
		private String progress(){
			if(source.type.equals("Fiend")){
				if(data.get(source.type).get(source.name).val().equals("10")){
					return " (DONE)";
				}
				else{
					return " (" + data.get("Fiend").get(source.name).val() + " / 10" + ")";
				}
			}
			else{
				if(data.get(source.type).get(source.name).val().equals("1")){
					return " (DONE)";
				}
			}
			return "";
		}
		
		@Override public void mousePressed(MouseEvent m) {}
		@Override public void mouseReleased(MouseEvent m) {}
	}
	
	private String type;
	private String name;
	
	public FiendControllerButton(SaveData data, String type, String name){
		this.type = type;
		this.name = name;
		this.setPreferredSize(new Dimension(175, 19));
		this.setHorizontalTextPosition(JButton.CENTER);
		this.setVerticalTextPosition(JButton.CENTER);
		this.addMouseListener(new FCPListener(data, this));
	}
	
	public String name(){ return name; }
	public String type(){ return type; }
	
}
