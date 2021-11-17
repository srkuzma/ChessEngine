package chess;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

public class TakenPieces extends JPanel {
	
	ArrayList<Image> taken = new ArrayList<>();
	private static final int columns = 6;
	private static final int FIELD_SIZE = 22;
	private static final int OFFSET = 3;
	
	private Map<Image, Integer> values = new HashMap<>();
	
	
	public void addImage(Image i, int val) {
		Image toPut = i.getScaledInstance(FIELD_SIZE, FIELD_SIZE, Image.SCALE_SMOOTH);
		taken.add(toPut);
		values.put(toPut, val);
		sort(taken);
		repaint();
	}
	
	private void sort(ArrayList<Image> list) {
		class CustomComparator implements Comparator<Image> {
		    @Override
		    public int compare(Image o1, Image o2) {
		        return ((values.get(o1) < values.get(o2)) ? 1 : -1);
		    }
		}
		Collections.sort(taken, new CustomComparator());
	}
	public TakenPieces() {
		// TODO Auto-generated constructor stub
		setSize(new Dimension(180, 90));
	}
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		//
		Graphics2D g2D = (Graphics2D)g;
		
		int col = 0;
		int row = 0;
		for (Image image:taken) {
			g.drawImage(image, col * (FIELD_SIZE + OFFSET) + OFFSET, row * (FIELD_SIZE + OFFSET) + OFFSET, null);
			
			col++;
			if (col >= columns) {
				col = 0;
				row++;
			}
		}
	}
}
