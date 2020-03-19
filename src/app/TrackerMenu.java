package app;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import data.SaveData;

import app.viewport.StreamViewport;

public class TrackerMenu extends JMenuBar{
	
	private static final long serialVersionUID = 7694750673044468565L;
	
	private static String helpMessage = helpMessage();
	
	public TrackerMenu(SaveData data, NemesisTracker source){
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		
		JButton toggleViewport = new JButton("Hide Viewport");
		toggleViewport.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(toggleViewport.getText().equals("Hide Viewport")){
					toggleViewport.setText("Show Viewport");
					StreamViewport.isVisible = false;
					StreamViewport.instance.setVisible(false);
				}
				else{
					toggleViewport.setText("Hide Viewport");
					StreamViewport.isVisible = true;
					StreamViewport.instance.setVisible(true);
				}
			}
		});
		this.add(toggleViewport);
		
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				data.save();
				JOptionPane.showMessageDialog(source, "Save Complete");
			}
		});
		this.add(save);
		
		JButton revert = new JButton("Revert");
		revert.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				data.revert();
				source.refreshJTP();
				JOptionPane.showMessageDialog(source, "Reverted to last save. If you did this by accident, click \"Load Backup\"");
			}
		});
		this.add(revert);
		
		JButton loadBackup = new JButton("Load Backup");
		loadBackup.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				data.loadFromBackup();
				source.refreshJTP();
				JOptionPane.showMessageDialog(source, "Loaded from backup.");
			}
		});
		this.add(loadBackup);
		
		JButton clearData = new JButton("Reset");
		clearData.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				int o = JOptionPane.showConfirmDialog(source, "This will clear all your recorded progress. Are you sure?\n(This cannot be undone.)");
				if(o == JOptionPane.YES_OPTION){
					SaveData.clearSaveData();
					source.refreshJTP();
					JOptionPane.showMessageDialog(source, "All Data has been cleared.");
				}
			}
		});
		this.add(clearData);
		
		JButton help = new JButton("Help");
		help.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				JOptionPane.showMessageDialog(source, helpMessage);
			}
		});
		this.add(help);
	}

	private static String helpMessage() {
		String out = "@@ How to use this tool @@\n\n";
		
		out += "Left-click a fiend button to increase its capture count, and right-click to decrease it.\n";
		out += "For arena objectives, left click to mark them as done, right click to unmark them.\n";
		out += "After all objectives are met, the Nemesis tab will unlock.\n\n";
		
		out += "@@ Buttons @@\n\n";
		out += "The tracker automatically backs up after every change you make, so if the program crashes or you close it accidentally, you can hit Load Backup to pick up where you left off.\n\n";

		out += "Only use the Save button whenever you save your game during a run. If you die, click Revert to reset progress to your last save.\n This will save you the time of backtracking to the Monster Arena just to figure out how much progress you've lost.\n\n";

		out += "When resetting, use Reset. BE CAREFUL. This will reset the backup as well, so only use this when you are done with your current run or are starting a new one.\n\n";
		
		out += "@@ The Viewport @@\n\n";

		out += "To provide a cleaner view for your stream, a secondary \"Stream Viewport\" window shows a summary of your progress. ";
		out += "This is a separate window from the control panel you will use to track objectives.\n";
		out += "Hopefully this makes it easier to showcase progress to your viewers without the bulky control panel messing with your stream's vibe.\n\n";
		
		out += "To add the viewport to your scene, select the \"Nemesis Tracker Stream Viewport\" window.";
		
		return out;
	}
	
}
