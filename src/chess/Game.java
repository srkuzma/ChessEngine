package chess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class Game extends JFrame {
	
	Board board = new Board(this);
	Panel sideBar = new Panel(new GridLayout(5,1, 0, 2));
	JLabel playerLabel = new JLabel("05:00"), computerLabel = new JLabel("05:00");
	JLabel empty = new JLabel("");
	JLabel winner = new JLabel("");
	Timer playerTimer = new Timer(playerLabel, true, 5, 3, board), computerTimer = new Timer(computerLabel, false, 5, 3, board);
	JPanel movesHistory;
	JPanel listOfMoves;
	JPanel moveNumbers;
	JScrollPane scroll;
	
	public Game() {
		
		setLayout(new BorderLayout());
		setTitle("Chess");
		//setBounds(100,50,100,100);
		this.getContentPane().add(board);
		//add(board, BorderLayout.CENTER);
		//pack();
		
		sideBar.setPreferredSize(new Dimension(160, 480));
		sideBar.setBackground(new Color(220,220,220));
		
		playerLabel.setHorizontalAlignment(JLabel.CENTER);
		computerLabel.setHorizontalAlignment(JLabel.CENTER);
		
		playerLabel.setFont(new Font("Verdana", Font.BOLD, 26));
		computerLabel.setFont(new Font("Verdana", Font.BOLD, 26));
		playerLabel.setBackground(new Color(30, 144, 255));
		computerLabel.setBackground(new Color(30, 144, 255));
		
		playerLabel.setOpaque(true);
		computerLabel.setOpaque(true);
		
		playerLabel.setForeground(Color.WHITE);
		computerLabel.setForeground(Color.WHITE);
		
		winner.setFont(new Font("Calibri", Font.BOLD, 15));
		winner.setHorizontalAlignment(JLabel.CENTER);
		
		sideBar.add(empty);
		sideBar.add(computerLabel);
		
		
		movesHistory = new JPanel(new BorderLayout());
		
		playerTimer.start();
		computerTimer.start();
		setResizable(false);
		
		listOfMoves = new JPanel(new GridLayout(0,2, 0, 0));
		moveNumbers = new JPanel(new GridLayout(0,1, 0, 0));
		//listOfMoves.setPreferredSize(new Dimension(150, 100));
		//listOfMoves.setColumns(4);
		
		JPanel middlePanel = new JPanel(new BorderLayout());
		//middlePanel.setBorder(new TitledBorder(new EtchedBorder()));
		JPanel numbersAndList = new JPanel(new BorderLayout());
		numbersAndList.add(listOfMoves, BorderLayout.EAST);
		numbersAndList.add(moveNumbers, BorderLayout.WEST);
		
		scroll = new JScrollPane(listOfMoves);
		scroll.getVerticalScrollBar().setUnitIncrement(2);
		//scroll.setPreferredSize(new Dimension(120,70));
		
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setEnabled(true);
		middlePanel.add(scroll, BorderLayout.NORTH);
		
		
		
		JButton left = new JButton("<");
		JButton right = new JButton(">");
		
		Game g = this;
		Color defaultBackground = left.getBackground();
		Color defaultForeground = left.getForeground();
		
		left.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				left.setBackground(new Color(30, 144, 255));
				left.setForeground(Color.WHITE);
				left.setText("<");
			}
			@Override
			public void mouseExited(MouseEvent e) {
				left.setBackground(defaultBackground);
				left.setForeground(defaultForeground);
				left.setText("<");
			}
		});
		
		right.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				right.setBackground(new Color(30, 144, 255));
				right.setForeground(Color.WHITE);
				right.setText(">");
			}
			@Override
			public void mouseExited(MouseEvent e) {
				right.setBackground(defaultBackground);
				right.setForeground(defaultForeground);
				right.setText(">");
			}
		});
		
		left.addActionListener((ae) -> { board.requestFocus(); board.setFocusable(true); board.decMove(); });
		right.addActionListener((ae) -> {board.requestFocus(); board.setFocusable(true); board.incMove(); });
		
		JPanel arrows = new JPanel(new GridLayout(1,2));
		arrows.add(left);
		arrows.add(right);
		
		movesHistory.add(middlePanel, BorderLayout.CENTER);
		movesHistory.add(arrows, BorderLayout.SOUTH);
		
		sideBar.add(movesHistory);
		sideBar.add(playerLabel);
		
		sideBar.add(winner);
		
		add(sideBar, BorderLayout.EAST);
		
		addMenu();
		
		pack();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		
	}
	
	private void addMenu() {
		JMenuBar mb = new JMenuBar();
		JMenu m = new JMenu("Options");
		JMenuItem m1 = new JMenu("New game");
		JMenuItem m2 = new JMenu("Change color");
		JMenuItem m3 = new JMenuItem("Exit");
		
		JMenuItem ng15  = new JMenuItem("15+10");
		JMenuItem ng10 = new JMenuItem("10+0");
		JMenuItem ng5 = new JMenuItem("5+3");
		JMenuItem ng3 = new JMenuItem("3+2");
		JMenuItem ng1 = new JMenuItem("1+0");
		
		
		ng15.addActionListener((ae) -> { setNewGame(15, 10);});
		ng10.addActionListener((ae) -> { setNewGame(10, 0);});
		ng5.addActionListener((ae) -> { setNewGame(5,3);});
		ng3.addActionListener((ae) -> { setNewGame(3,2);});
		ng1.addActionListener((ae) -> { setNewGame(1, 0);});
		
		m1.add(ng15);
		m1.add(ng10);
		m1.add(ng5);
		m1.add(ng3);
		m1.add(ng1); 
		
		JMenuItem green  = new JMenuItem("Green");
		JMenuItem red = new JMenuItem("Red");
		JMenuItem orange = new JMenuItem("Orange");
		JMenuItem brown = new JMenuItem("Brown");
		JMenuItem pink = new JMenuItem("Pink");
		
		green.addActionListener((ae) -> { board.setColor(new Color(34, 139, 34)); board.setFocusable(true); board.repaint();});
		red.addActionListener((ae) -> { board.setColor(new Color(210, 43, 43)); board.setFocusable(true); board.repaint();});
		orange.addActionListener((ae) -> { board.setColor(new Color(227, 150, 62)); board.setFocusable(true); board.repaint();});
		brown.addActionListener((ae) -> { board.setColor(new Color(123, 63, 0)); board.setFocusable(true); board.repaint();});
		pink.addActionListener((ae) -> { board.setColor(new Color(222, 49, 99)); board.setFocusable(true); board.repaint();});
		
		m2.add(green);
		m2.add(red);
		m2.add(orange);
		m2.add(brown);
		m2.add(pink);
		
		m3.addActionListener((ae) -> {
			playerTimer.interrupt();
			computerTimer.interrupt();
			dispose();
		});
		
		m.add(m1);
		m.add(m2);
		m.addSeparator();
		m.add(m3);
		
		mb.add(m);
		
		setJMenuBar(mb);
	}
	
	void setNewGame(int minutes, int increment){
		playerTimer.interrupt();
		computerTimer.interrupt();
		
		playerLabel.setText(String.format("%02d", minutes) + ":" +  String.format("%02d", 0));
		computerLabel.setText(String.format("%02d", minutes) + ":" +  String.format("%02d", 0));
		
		playerTimer = new Timer(playerLabel, true, minutes, increment, board);
		computerTimer = new Timer(computerLabel, false, minutes, increment, board);
		
		playerTimer.start();
		computerTimer.start();
		
		board.restart();
		
	}
	
	public static void main(String[] args) {
		new Game();
	}
	int a = 0;

	public void addButton(JButton button) {
		
		int sz = board.tables.size();
		
		if (sz % 2 == 1) {
			Integer num  = sz / 2 + 1;
			Label l = new Label(num.toString());
			//l.setPreferredSize(new Dimension(20,23));
			moveNumbers.add(l);
		}
		
		a++;
		if (a == 3) {
			scroll.setPreferredSize(new Dimension(120, 46));
		}
		if (a == 5) {
			scroll.setPreferredSize(new Dimension(120, 69));
		}
		
		button.setFont(new Font(null, Font.BOLD, 10));
		button.setPreferredSize(new Dimension(60,23));
		
		button.addActionListener((ae) -> {
			board.requestFocus(); 
			board.setFocusable(true);
			board.setMove(sz);
		});
		
		listOfMoves.add(button);
		
		JScrollBar sb = scroll.getVerticalScrollBar();
		sb.setValue( sb.getMaximum());
		
		
		pack();
	}
}
