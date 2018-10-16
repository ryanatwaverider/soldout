package com.waverider.soldout;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class TicketSaleRenderer implements TableCellRenderer {

	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		String text = (String)value;
		JLabel label = new JLabel(text);
		label.setFont(table.getFont());
		label.setOpaque(true);
		String[] splits = text.split(" ");
		label.setText(splits[1]);
		
		if (text.contains("S")) {
			label.setBackground(Color.YELLOW);
		}
		else if (text.contains("L")) {
			label.setBackground(Color.green);
		}
		return label;
	}

}
