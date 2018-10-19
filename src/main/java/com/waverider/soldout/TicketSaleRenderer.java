/**
 * (C) Copyright Waverider LLC, 2018
 */
package com.waverider.soldout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Label;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

public class TicketSaleRenderer implements TableCellRenderer {

	
	ArrayList<Color> colors = new ArrayList<Color>();
	
	public TicketSaleRenderer() {
		colors.add(Color.LIGHT_GRAY);
		colors.add(Color.RED);
		colors.add(Color.ORANGE);
		colors.add(Color.GREEN);
		colors.add(Color.YELLOW);
		colors.add(Color.cyan);
		colors.add(Color.PINK);
		
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		
		AccessTokenDisplay ad = (AccessTokenDisplay)value;
		
//		String text = (String)value;
		JLabel label = new JLabel("H: " + ad.value);
		label.setFont(table.getFont());
		label.setOpaque(true);
		
		Color c = colors.get(ad.ownerId);
//		if (ad.ownerId==6) {
//			label.setForeground(Color.WHITE);
//		}
		
		if (ad.isListing) {
			label.setBorder(new LineBorder(Color.blue,2));
//			c = c.darker();
		}
		
		label.setBackground(c);

//		label.setHorizontalAlignment(Label.CENTER);
		
//		String[] splits = text.split(" ");
//		label.setText(splits[1]);
//		
//		if (text.contains("S")) {
//			label.setBackground(Color.YELLOW);
//		}
//		else if (text.contains("L")) {
//			label.setBackground(Color.green);
//		}
		return label;
	}

}
