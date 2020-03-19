package app.viewport;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import app.FiendControllerButton;
import app.NemesisTracker;
import cxr.cooid.canon.Thing;
import data.SaveData;

public class StreamViewport extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static Color BUTTON_COLOR = new Color(141, 151, 248);
	
	public static boolean isVisible = true;
	
	public static class FiendSummary{

		public ArrayList<String> name;
		public ArrayList<ImageIcon> icon;
		
		public FiendSummary(){
			name = new ArrayList<String>();
			icon = new ArrayList<ImageIcon>();
		}
		
		public FiendSummary(ArrayList<String> name, ArrayList<ImageIcon> icon){
			this.name = name;
			this.icon = icon;
		}
		
		public int indexOf(String k){
			return name.indexOf(k);
		}
		
	}
	
	public JLabel fiendCount;
	private static String f = "Fiend: ";
	
	public JLabel areaCount;
	private static String a = "Area: ";
	
	public JLabel speciesCount;
	private static String s = "Species: ";
	
	public JLabel originalCount;
	private static String o = "Original: ";
	
	public JLabel nemesisDown;
	private static String n = "Nemesis: ";
	
	public static StreamViewport instance = new StreamViewport();
	private JPanel summary;
	private JPanel icons;
	
	private FiendSummary fs = new FiendSummary();
	
	private StreamViewport() {
		super("Nemesis Tracker Stream Viewport");
		setLayout(new GridBagLayout());
		
		URL resource = SaveData.class.getResource("/data/ffx.png");
		try {
			InputStream is = resource.openStream();
			Image icon = ImageIO.read(is);
			this.setIconImage(icon);
		} catch (IOException ioe) { ioe.printStackTrace(); }
		catch (NullPointerException npe){}
		
		summary = new JPanel();
		summary.setLayout(new GridLayout(5,1));
		fiendCount = new JLabel(f + SaveData.instance.numFiendsMaxCaptured() + " / " + SaveData.fiends.size());
		fiendCount.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		summary.add(fiendCount);
		
		areaCount = new JLabel(a + SaveData.instance.numAreaFiendsDefeated() + " / " + SaveData.area.size());
		areaCount.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		summary.add(areaCount);
		
		speciesCount = new JLabel(s + SaveData.instance.numSpeciesConquered() + " / " + SaveData.species.size());
		speciesCount.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		summary.add(speciesCount);
		
		originalCount = new JLabel(o + SaveData.instance.numOriginalFiendsDefeated() + " / " + SaveData.original.size());
		originalCount.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		summary.add(originalCount);
		
		nemesisDown = new JLabel(n + (Integer.valueOf(SaveData.instance.get("Nemesis").get(0).val()) == 1 ? "DEFEATED" : "ALIVE"));
		nemesisDown.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		summary.add(nemesisDown);
		
		icons = new JPanel();
		icons.setLayout(new GridLayout(3, 0));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		add(summary, gbc);
		gbc.gridwidth = 3;
		add(icons, gbc);
		setUndecorated(true);
		setSize(NemesisTracker.win_x, 150);
		pack();
		if(isVisible){
			setVisible(true);
		}
	}
	
	public void updateFiendCount(Thing info){
		fiendCount.setText(f + info.get("Fiend").val() + " / " + SaveData.fiends.size());
		fiendCount.repaint();
		
		areaCount.setText(a + info.get("Area").val() + " / " + SaveData.area.size());
		areaCount.repaint();
		
		speciesCount.setText(s + info.get("Species").val() + " / " + SaveData.species.size());
		speciesCount.repaint();
		
		originalCount.setText(o + info.get("Original").val() + " / " + SaveData.original.size());
		originalCount.repaint();
		
		nemesisDown.setText(n + (Integer.valueOf(SaveData.instance.get("Nemesis").get(0).val()) == 1 ? "DEFEATED" : "ALIVE"));
		nemesisDown.repaint();
		
		pack();
		summary.repaint();
		repaint();
	}
	
	public void updateFiendIcons(String objective, ArrayList<FiendControllerButton> fcblist){
		setVisible(false);
		icons.removeAll();
		ArrayList<String> name_list = new ArrayList<String>();
		ArrayList<ImageIcon> icon_list = new ArrayList<ImageIcon>();
		if(objective.equals("Nemesis")){
			nemesisDown.setText(n + (Integer.valueOf(SaveData.instance.get("Nemesis").get(0).val()) == 1 ? "DEFEATED" : "ALIVE"));
			nemesisDown.repaint();
		}
		else{
			for(FiendControllerButton fcb : fcblist){
				URL resource = SaveData.class.getResource("/data/ico/" + fcb.name() + ".png");
				try {
					InputStream is = resource.openStream();
					ImageIcon icon = new ImageIcon(ImageIO.read(is));
					name_list.add(fcb.name());
					icon_list.add(icon);
					JLabel to_add = new JLabel(icon);
					to_add.setBackground(BUTTON_COLOR);
					to_add.setOpaque(true);
					to_add.setText(FiendControllerButton.progress(objective, fcb.name()));
					String s = SaveData.instance.get(objective).get(fcb.name()).val();
					if(objective.equals("Fiend")){
						if(s.equals("10")){ to_add.setBackground(Color.GRAY); }
					}
					else if(s.equals("1")){
						to_add.setBackground(Color.GRAY);
					}
					icons.add(to_add);
					
					icons.validate();
				} catch (IOException ioe) { ioe.printStackTrace();
				} catch (NullPointerException npe){}
			}
			fs = new FiendSummary(name_list, icon_list);
		}
		icons.repaint();
		summary.repaint();
		pack();
		repaint();
		if(isVisible){
			setVisible(true);
		}
	}
	
	public void toggle(String name, String label_text, int v){
		int loc = fs.indexOf(name);
		if(loc != -1){
			((JLabel)icons.getComponent(loc)).setText(label_text);
			icons.getComponent(loc).setBackground(v == 0 ? BUTTON_COLOR : Color.GRAY);
		}
		icons.repaint();
		pack();
		repaint();
	}
	
}
