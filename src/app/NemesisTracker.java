package app;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import cxr.cooid.canon.Property;
import cxr.cooid.canon.Thing;
import data.SaveData;

public class NemesisTracker extends JFrame implements ActionListener{
		
		private static final long serialVersionUID = 1L;

		public NemesisTracker(){
			super("Nemesis Tracker 0.1 by Conrad @The_Complexor");
			try {
				this.setIconImage(ImageIO.read(new File("C:\\Users\\Josh\\Desktop\\!drawer\\ffx icon.jpg")));
			} catch (IOException e) {
				e.printStackTrace();
			}

			setLayout(new FlowLayout());
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			setContentPane(constructJTP());
			pack();
			setSize(614, 213);
			setVisible(true);
		}
		
		public JTabbedPane constructJTP(){
			JTabbedPane jtp = new JTabbedPane();
			
			int fiendPaneSize = SaveData.instance.get("Area Conquest").size();
			
			for(Thing t : SaveData.instance){
				
				// Ignore these three
				if(!(t.tag().equals("Area Conquest") || t.tag().equals("Species Conquest") || t.tag().equals("Original Unlocked"))){
					
					// Fiends are filtered into sub-tabs
					if(t.tag().equals("Fiend")){
						JTabbedPane fiendPane = new JTabbedPane();
						JPanel[] jps = new JPanel[fiendPaneSize];
						for(int i = 0; i < fiendPaneSize; i++){ jps[i] = new JPanel(); }
						Thing ac = SaveData.instance.get("Area Conquest");
						System.out.println(ac.cxrString());
						for(Property p : t){
							FiendControllerButton fcb = new FiendControllerButton(SaveData.instance, t.tag(), p.tag());
							
							String area = SaveData.fiends.get(p.tag()).get("area").val();
							int loc = ac.indexOf(ac.get(area));
							jps[loc].add(fcb);
							
							fcb.addActionListener(this);
						}
						for(int i = 0; i < fiendPaneSize; i++){ fiendPane.addTab(ac.get((i+6)%fiendPaneSize).tag(), jps[(i+6)%fiendPaneSize]); }
						fiendPane.setSelectedIndex(7);
						jtp.addTab("Fiend", fiendPane);
					}
					// All others get their own tab
					else{
						JPanel next = new JPanel();
						for(Property p : t){
							FiendControllerButton fcb = new FiendControllerButton(SaveData.instance, t.tag(), p.tag());
							next.add(fcb);
							fcb.addActionListener(this); }
						jtp.addTab(t.tag(), next); }
				}
			}
			return jtp;
		}
		
		@Override public void actionPerformed(ActionEvent ae) { this.repaint(); }
	
	public static void main(String[] args){
		new NemesisTracker();
	}
	
}
