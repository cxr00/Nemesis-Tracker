package app;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cxr.cooid.canon.Property;
import cxr.cooid.canon.Thing;
import data.SaveData;
import app.FiendControllerButton.JointFCBListener;
import app.viewport.StreamViewport;

public class NemesisTracker extends JFrame implements ActionListener{
	
	public static ArrayList<FiendControllerButton> all_of_em;
	
	/** Keeps StreamViewport directly above NemesisTracker control panel */
	private static class ViewportTie implements ComponentListener{

		private NemesisTracker main;
		private StreamViewport attache;
		
		public ViewportTie(NemesisTracker main, StreamViewport attache){
			this.main = main;
			this.attache = attache; }

		@Override
		public void componentMoved(ComponentEvent ce) {
			Dimension d = new Dimension(main.getLocation().x, main.getLocation().y-attache.getHeight()-5);
			attache.setLocation(d.width, d.height); }

		// unused
		@Override public void componentHidden(ComponentEvent arg0) {}
		@Override public void componentResized(ComponentEvent ce) {}
		@Override public void componentShown(ComponentEvent ce) {}
		
	}
	
	private static class ViewportFiendPanelListener implements ChangeListener{

		private JTabbedPane source;
		private StreamViewport attache;
		
		public ViewportFiendPanelListener(JTabbedPane source, StreamViewport attache){
			this.source = source;
			this.attache = attache; }
		
		@Override public void stateChanged(ChangeEvent c) {
				int index = source.getSelectedIndex();
				JTabbedPane ss = (JTabbedPane)c.getSource();
				if(index == 0 && ss.getName().equals("JTP")){
					JTabbedPane sel = (JTabbedPane)source.getComponent(0);
					int index2 = sel.getSelectedIndex();
					FiendTab ft = (FiendTab)sel.getComponent(index2);
					attache.updateFiendIcons("Fiend", ft.contents());
					for(FiendControllerButton fcb : all_of_em){ fcb.updateLabel(); }
				}
				else{
					FiendTab sel = (FiendTab)((JTabbedPane) c.getSource()).getComponent(index);
					System.out.println(sel.getName());
					attache.updateFiendIcons(sel.getName(), sel.contents());
					
				}
		}
		
	}
	
	public static class FiendTab extends JPanel{
		
		private static final long serialVersionUID = 1L;

		public ArrayList<FiendControllerButton> contents(){
			ArrayList<FiendControllerButton> out = new ArrayList<FiendControllerButton>(); 
			for(int i = 0; i < getComponentCount(); i++){
				if(getComponent(i) instanceof FiendControllerButton){
					out.add((FiendControllerButton) getComponent(i)); }
			}
			return out;
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	private JTabbedPane jtp;
	private FiendTab nemesisTab;
	private JTabbedPane fiendPane;
	
	public static final int win_x = 604;
	public static final int win_y = 495;
	
	public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	public NemesisTracker(){
		super("Nemesis Tracker 0.1 by Conrad @The_Complexor");
		
		URL resource = SaveData.class.getResource("/data/cxr.png");
		try {
			InputStream is = resource.openStream();
			Image icon = ImageIO.read(is);
			this.setIconImage(icon);
		} catch (IOException ioe) { ioe.printStackTrace(); }
		catch (NullPointerException npe){}

		setLayout(new FlowLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		refreshJTP();
		
		setJMenuBar(new TrackerMenu(SaveData.instance, this));
		setLocation((screenSize.height-win_x)/2, (screenSize.height-win_y)/2);
		addComponentListener(new ViewportTie(this, StreamViewport.instance));
		pack();
		
		setResizable(false);
		setVisible(true);
	}
	
	public JTabbedPane constructJTP(){
		fiendPane = new JTabbedPane();
		JTabbedPane jtp = new JTabbedPane();
		jtp.setName("JTP");
		
		int fiendPaneSize = SaveData.instance.get("Area Conquest Unlocked").size();
		
		for(Thing t : SaveData.instance){
			
			// Ignore these three
			if(!t.tag().contains("Unlocked")){
				
				// Fiends are filtered into sub-tabs
				if(t.tag().equals("Fiend")){
					
					Thing ac = SaveData.instance.get("Area Conquest Unlocked");
					FiendTab[] jps = new FiendTab[fiendPaneSize];
					for(int i = 0; i < fiendPaneSize; i++){
						jps[i] = new FiendTab();
						jps[i].setLayout(new FlowLayout());
						jps[i].setName("Fiend");
					}
					all_of_em = new ArrayList<FiendControllerButton>();
					for(Property p : t){
						Thing fiend = SaveData.fiends.get(p.tag());
						FiendControllerButton fcb = new FiendControllerButton(SaveData.instance, t.tag(), p.tag());
						
						String area = fiend.get("area").val();
						int loc = ac.indexOf(ac.get(area));
						jps[loc].add(fcb);
						fcb.addActionListener(this);
						all_of_em.add(fcb);
						
						Property found = fiend.get("found");
						if(found != null){
							FiendControllerButton fcb2 = new FiendControllerButton(SaveData.instance, t.tag(), p.tag());
							loc = ac.indexOf(ac.get(found.val()));
							jps[loc].add(fcb2);
							fcb.addActionListener(new JointFCBListener(SaveData.instance, fcb2));
							fcb2.addActionListener(new JointFCBListener(SaveData.instance, fcb));
							all_of_em.add(fcb2);
						}
					}
					for(int i = 0; i < fiendPaneSize; i++){
						fiendPane.addTab(ac.get(i).tag(), jps[i]);
					}
					fiendPane.setName("FiendPane");
					fiendPane.addChangeListener(new  ViewportFiendPanelListener(fiendPane, StreamViewport.instance));
					StreamViewport.instance.updateFiendIcons("Fiend", ((FiendTab)fiendPane.getComponent(0)).contents());
					jtp.addTab("Fiend", fiendPane);
				}
				else if(t.tag().equals("Nemesis")){
					nemesisTab = new FiendTab();
					nemesisTab.setLayout(new FlowLayout(FlowLayout.CENTER));
					FiendControllerButton nem = new FiendControllerButton(SaveData.instance, "Nemesis", "Nemesis");
					nem.setPreferredSize(new Dimension(380, 320));
					nem.addActionListener(this);
					nemesisTab.setName("Nemesis");
					nemesisTab.add(nem);
					if(SaveData.instance.unlockedNemesis() && nemesisTab.getParent() == null){ jtp.addTab("Nemesis", nemesisTab); }
				}
				// All others get their own tab
				else{
					FiendTab next = new FiendTab();
					next.setLayout(new FlowLayout(FlowLayout.CENTER));
					for(Property p : t){
						FiendControllerButton fcb = new FiendControllerButton(SaveData.instance, t.tag(), p.tag());
						next.add(fcb);
						next.setName(t.tag());
						fcb.addActionListener(this);
//						fcb.updateLabel();
					}
					jtp.addTab(t.tag(), next); }
			}
		}
		jtp.addChangeListener(new ViewportFiendPanelListener(jtp, StreamViewport.instance));
		
		for(FiendControllerButton fcb : all_of_em){ fcb.updateLabel(); }
		return jtp;
	}
	
	public void refreshJTP(){
		int index = jtp != null ? jtp.getSelectedIndex() : 0;
		int f_index = fiendPane != null ? fiendPane.getSelectedIndex() : 0;
		jtp = constructJTP();
		jtp.setSelectedIndex(index);
		fiendPane.setSelectedIndex(f_index);
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
			if(SaveData.instance.unlockedNemesis()){ jtp.addTab("Nemesis", nemesisTab); }
			else{ jtp.remove(nemesisTab); }
			jtp.repaint(); }
		this.repaint(); }
	
	public static void main(String[] args){ new NemesisTracker(); }
	
}
