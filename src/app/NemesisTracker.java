package app;

import java.awt.Dimension;
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
import app.FiendControllerButton.JointFCBListener;

public class NemesisTracker extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	
	private JTabbedPane jtp;
	private JPanel nemesisTab;
	private JTabbedPane fiendPane;
	
	private static int win_x = 604;
	private static int win_y = 460;
	
	public NemesisTracker(){
		super("Nemesis Tracker 0.1 by Conrad @The_Complexor");
		try {
			this.setIconImage(ImageIO.read(new File("C:\\Users\\Josh\\Desktop\\!drawer\\ffx icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		setLayout(new FlowLayout(FlowLayout.LEFT));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		jtp = constructJTP();
		setContentPane(jtp);
		
		this.setJMenuBar(new TrackerMenu(SaveData.instance, this));
		
		pack();
		setResizable(false);
		setVisible(true);
	}
	
	public JTabbedPane constructJTP(){
		fiendPane = new JTabbedPane();
		JTabbedPane jtp = new JTabbedPane();
		
		int fiendPaneSize = SaveData.instance.get("Area Conquest Unlocked").size();
		
		for(Thing t : SaveData.instance){
			
			// Ignore these three
			if(!t.tag().contains("Unlocked")){
				
				// Fiends are filtered into sub-tabs
				if(t.tag().equals("Fiend")){
					
					JPanel[] jps = new JPanel[fiendPaneSize];
					for(int i = 0; i < fiendPaneSize; i++){
						jps[i] = new JPanel();
						jps[i].setLayout(new FlowLayout(FlowLayout.LEFT));
					}
					Thing ac = SaveData.instance.get("Area Conquest Unlocked");
					for(Property p : t){
						Thing fiend = SaveData.fiends.get(p.tag());
						FiendControllerButton fcb = new FiendControllerButton(SaveData.instance, t.tag(), p.tag());
						
						String area = fiend.get("area").val();
						int loc = ac.indexOf(ac.get(area));
						jps[loc].add(fcb);
						fcb.addActionListener(this);
						
						Property found = fiend.get("found");
						if(found != null){
							FiendControllerButton fcb2 = new FiendControllerButton(SaveData.instance, t.tag(), p.tag());
							loc = ac.indexOf(ac.get(found.val()));
							jps[loc].add(fcb2);
							fcb.addActionListener(new JointFCBListener(SaveData.instance, fcb2));
							fcb2.addActionListener(new JointFCBListener(SaveData.instance, fcb));
						}
					}
					int offset = 0;
					for(int i = 0; i < fiendPaneSize; i++){ fiendPane.addTab(ac.get((i+offset)%fiendPaneSize).tag(), jps[(i+offset)%fiendPaneSize]); }
					fiendPane.setSelectedIndex(offset);
					jtp.addTab("Fiend", fiendPane);
				}
				else if(t.tag().equals("Nemesis")){
					nemesisTab = new JPanel();
					nemesisTab.setLayout(new FlowLayout(FlowLayout.CENTER));
					FiendControllerButton nem = new FiendControllerButton(SaveData.instance, "Nemesis", "Nemesis");
					nem.setPreferredSize(new Dimension(380, 320));
					nem.addActionListener(this);
					nemesisTab.add(nem);
					if(SaveData.instance.unlockedNemesis() && nemesisTab.getParent() == null){ jtp.addTab("Nemesis", nemesisTab); }
				}
				// All others get their own tab
				else{
					JPanel next = new JPanel();
					next.setLayout(new FlowLayout(FlowLayout.LEFT));
					for(Property p : t){
						FiendControllerButton fcb = new FiendControllerButton(SaveData.instance, t.tag(), p.tag());
						next.add(fcb);
						fcb.addActionListener(this);
					}
					jtp.addTab(t.tag(), next); }
			}
		}
		return jtp;
	}
	
	public void refreshJTP(){
		jtp = constructJTP();
		setContentPane(jtp);
		pack();
		repaint();
	}
	
	public void pack(){
		super.pack();
		setSize(win_x, win_y);
	}
	
	@Override public void actionPerformed(ActionEvent ae) {
		if(nemesisTab.getParent() == null){
			if(SaveData.instance.unlockedNemesis()){
				jtp.addTab("Nemesis", nemesisTab); jtp.repaint(); } }
		else{ jtp.remove(nemesisTab); jtp.repaint(); }
		this.repaint();
	}
	
	public static void main(String[] args){
		new NemesisTracker();
	}
	
}
