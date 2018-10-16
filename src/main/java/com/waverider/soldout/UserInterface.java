package com.waverider.soldout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.waverider.soldout.messages.SoldOutEntityUpdate;

public class UserInterface extends JFrame implements SoldOutEntityUpdateSubscriber {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7332486379800750898L;
	
	private final GlobalInformationProvider globalInformationProvider;

	private VenueTableModel venueTableModel;
	
	Font f = new Font("Arial", Font.BOLD, 16);

	public UserInterface(GlobalInformationProvider gip) {
		super("Lady Gaga - United Center, Nov 11, 2018");
		
		globalInformationProvider = gip;
		
		venueTableModel = new VenueTableModel(gip);
		JTable venueDisplayTable = new JTable(venueTableModel);
		
		venueDisplayTable.setRowHeight(30);
		venueDisplayTable.setFont(f);
		venueDisplayTable.setGridColor(Color.gray);
		
		venueDisplayTable.setDefaultRenderer(String.class, new TicketSaleRenderer());

		
		JPanel contentPane = new JPanel(new BorderLayout());
		this.setContentPane(contentPane);
		
		JScrollPane scroller = new JScrollPane(venueDisplayTable);
		contentPane.add(new JLabel("    U N I T E D    C E N T E R   S E A T   M A P"),BorderLayout.NORTH);
		contentPane.add(scroller,BorderLayout.CENTER);
	}

	@Override
	public void onNewMessage(SoldOutEntityUpdate soeu) {
		venueTableModel.onNewMessage(soeu);
	}

	@Override
	public void start(Random random) {
		setVisible(true);

	}
}
