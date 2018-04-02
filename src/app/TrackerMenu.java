package app;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import data.SaveData;

public class TrackerMenu extends JMenuBar{
	
	private static final long serialVersionUID = 7694750673044468565L;
	
	public TrackerMenu(SaveData data, NemesisTracker source){
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		
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
	}
	
}
