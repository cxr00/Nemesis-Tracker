package app;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JMenuBar;

import data.SaveData;

public class TrackerMenu extends JMenuBar{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7694750673044468565L;
	
	public TrackerMenu(SaveData data){
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				System.out.println("SAVING");
				data.save();
			}
		});
		this.add(save);
		
		JButton revert = new JButton("Revert");
		revert.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				data.revert();
			}
		});
		this.add(revert);
		
		JButton loadBackup= new JButton("Load Backup");
		revert.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				data.loadFromBackup();
			}
		});
		this.add(loadBackup);
	}
	
}
