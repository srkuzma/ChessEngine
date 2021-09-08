package chess;

import java.awt.AlphaComposite;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Board extends JPanel implements Runnable{
	
	final int INFINITY = 10000000;
	
	int fieldSize = 60;
	int w = 480;
	int h = 480;
	char[][] pieces = new char[8][8], tempBoard = new char[8][8];
	/*char[][] init = {
			{'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'},
			{'P','P','P','P','P','P','P','P'},
			{'.','.','.','.','.','.','.','.'},
			{'.','.','.','.','.','.','.','.'},
			{'.','.','.','.','.','.','.','.'},
			{'.','.','.','.','.','.','.','.'},
			{'p','p','p','p','p','p','p','p'},
			{'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'}
	};*/
	
	char[][] init = {
			{'.','.','.','.','.','.','.','.'},
			{'.','.','.','.','.','.','.','.'},
			{'.','.','.','.','.','.','.','.'},
			{'.','.','.','.','.','.','.','.'},
			{'.','Q','.','.','.','.','.','.'},
			{'.','.','.','.','.','.','.','.'},
			{'.','.','.','.','.','.','.','.'},
			{'k','.','.','K','.','.','.','.'}
	};
	
	Image k;
	Image q;
	Image b;
	Image n;
	Image r;
	Image p;
	Image K;
	Image Q;
	Image B;
	Image N;
	Image R;
	Image P;
	
	Color color;

	Context pieceContext = new Context();
	Context tempContext = new Context();
	PieceColor turn = PieceColor.WHITE;
	PieceColor tempTurn;
	Field current = new Field(0,0);
	Field currentEnd = new Field(0,0);
	boolean clicked = false;
	ArrayList<Field> attacked;
	ArrayList<Field> blocked;
	ArrayList<Field> tempAttacked;
	ArrayList<Field> tempBlocked;
	
	boolean checked = false;
	boolean castledWhite = false;
	boolean castledBlack = false;
	boolean kingMovedWhite = false;
	boolean kingMovedBlack = false;
	boolean rook00Moved = false;
	boolean rook07Moved = false;
	boolean rook70Moved = false;
	boolean rook77Moved = false;
	boolean gameOver = false;
	boolean stalemate = false;
	
	Field tempCurrent = new Field(1,1);
	Field animated = null;
	Field animatedStart = null;
	Field animatedEnd = null;
	Field animatedPiece = null;
	
	Field draggedStart = null;
	Field dragged = null;
	Field draggedEnd = null;
	boolean dragging = false;
	int xoff = 0;
	int yoff = 0;
	
	boolean premoved;
	Move premove = null;
	
	ArrayList<Move> moves = new ArrayList<Move>();
	
	Thread animatedThread;
	
	Thread computerMove = new Thread(this);
	
	int pieceCount = 32;
	
	int currLen;
	int currMove = 0;
	
	ArrayList<char[][]> tables = new ArrayList<>();
	
	Game owner;
	
	public enum PieceColor{
		BLACK, WHITE;
	}
	
	private PieceColor getColor(char c) {
		return (c >= 'a' && c <= 'z') ? PieceColor.BLACK: PieceColor.WHITE;
	}
	
	public Board(Game owner) {
		
		this.owner = owner;
		
		setPreferredSize(new Dimension(w,h));
		
		k = new ImageIcon(getClass().getClassLoader().getResource("k.png")).getImage().getScaledInstance(w/8, h/8, Image.SCALE_SMOOTH);
		k = new ImageIcon(k).getImage();
		
		q = new ImageIcon(getClass().getClassLoader().getResource("q.png")).getImage().getScaledInstance(w/8, h/8, Image.SCALE_SMOOTH);
		q = new ImageIcon(q).getImage();
		
		b = new ImageIcon(getClass().getClassLoader().getResource("b.png")).getImage().getScaledInstance(w/8, h/8, Image.SCALE_SMOOTH);
		b = new ImageIcon(b).getImage();
		
		n = new ImageIcon(getClass().getClassLoader().getResource("n.png")).getImage().getScaledInstance(w/8, h/8, Image.SCALE_SMOOTH);
		n = new ImageIcon(n).getImage();
		
		r = new ImageIcon(getClass().getClassLoader().getResource("r.png")).getImage().getScaledInstance(w/8, h/8, Image.SCALE_SMOOTH);
		r = new ImageIcon(r).getImage();
		
		p = new ImageIcon(getClass().getClassLoader().getResource("p.png")).getImage().getScaledInstance(w/8, h/8, Image.SCALE_SMOOTH);
		p = new ImageIcon(p).getImage();
		
		K = new ImageIcon(getClass().getClassLoader().getResource("wk.png")).getImage().getScaledInstance(w/8, h/8, Image.SCALE_SMOOTH);
		K = new ImageIcon(K).getImage();
		
		Q = new ImageIcon(getClass().getClassLoader().getResource("wq.png")).getImage().getScaledInstance(w/8, h/8, Image.SCALE_SMOOTH);
		Q = new ImageIcon(Q).getImage();
		
		B = new ImageIcon(getClass().getClassLoader().getResource("wb.png")).getImage().getScaledInstance(w/8, h/8, Image.SCALE_SMOOTH);
		B = new ImageIcon(B).getImage();
		
		N = new ImageIcon(getClass().getClassLoader().getResource("wn.png")).getImage().getScaledInstance(w/8, h/8, Image.SCALE_SMOOTH);
		N = new ImageIcon(N).getImage();
		
		R = new ImageIcon(getClass().getClassLoader().getResource("wr.png")).getImage().getScaledInstance(w/8, h/8, Image.SCALE_SMOOTH);
		R = new ImageIcon(R).getImage();
		
		P = new ImageIcon(getClass().getClassLoader().getResource("wp.png")).getImage().getScaledInstance(w/8, h/8, Image.SCALE_SMOOTH);
		P = new ImageIcon(P).getImage();
		
		setColor(new Color(34, 139, 34));
		
		restart();
		/*for(int i = 0; i <8; i++) {
			pieces[i] = init[i].clone();
		}*/
		addListeners();
		
		//drawTable(getGraphics());
		
		repaint();
	}
	
	private void drawTable(Graphics g) {
		Graphics2D g2D = (Graphics2D)g;
		fieldSize = w/8;
		g2D.setColor(color);
		
		for(int i = 0; i<8; i++)
			for(int j = 0; j<8; j++) {
				if ((i+j) % 2 == 1) {
					g2D.fillRect(j * fieldSize, i * fieldSize, fieldSize, fieldSize);
				}
			}
	}
	
	
	private void doMove(int x, int y, char[][] pieces, Context s, boolean animate) {
		
		try {
			if (animatedThread != null)
				animatedThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Timer playerTimer = owner.playerTimer;
		Timer computerTimer = owner.computerTimer;
		
		String moveText = new String("");
		
		if (pieces == this.pieces) {
			
			char p = pieces[current.y][current.x];
			if (p != 'P' && p != 'p') {
				Character UpperCase = (char)(p + ((p >= 'a') ? 'A' - 'a': 0));
				moveText = moveText.concat(UpperCase.toString());
				currLen++;
			}
			
			if (pieces[y][x] != '.') {
				if (p == 'P' || p == 'p')
					moveText = moveText.concat(((Character)((char) ('a' + current.x))).toString());
				moveText = moveText.concat("x");
			}
			moveText = moveText.concat(((Character)((char) ('a' + x))).toString() + ((Integer)(y + 1)).toString() + " ");
			owner.addButton(new JButton(moveText));
			
			moves.add(new Move(new Field(current.x,current.y), new Field(x,y)));
			
			if (turn.equals(PieceColor.WHITE)) {
				playerTimer.tick = false;
				computerTimer.tick = true;
				playerTimer.addIncrement();
			}
			else {
				playerTimer.tick = true;
				computerTimer.tick = false;
				computerTimer.addIncrement();
			}
		}
		
		
		//castle
		if (pieces[current.y][current.x] == 'K') {
			
			if (x - current.x == 2 && !s.kingMovedWhite) {
				pieces[0][7] = '.';
				pieces[0][5] = 'R';
			}
			else if (x - current.x == -2 && !s.kingMovedWhite) {
				pieces[0][0] = '.';
				pieces[0][3] = 'R';
			}
			s.kingMovedWhite = true;
		}
		if (pieces[current.y][current.x] == 'k') {
			if (x - current.x == -2 && !s.kingMovedBlack) {
				pieces[7][0] = '.';
				pieces[7][3] = 'r';
			}
			else if (x - current.x == 2 && !s.kingMovedBlack) {
				pieces[7][7] = '.';
				pieces[7][5] = 'r';
			}
			s.kingMovedBlack = true;
		}
		
		if (pieces[current.y][current.x] == 'R' ) {
			if (current.y == 0 && current.x == 0)
				s.rook00Moved = true;
			if (current.y == 0 && current.x == 7)
				s.rook07Moved = true;
			
		}
		if (pieces[current.y][current.x] == 'r' ) {
			if (current.y == 7 && current.x == 0)
				s.rook70Moved = true;
			if (current.y == 7 && current.x == 7)
				s.rook77Moved = true;
			
		}
		
		if (pieces == this.pieces && pieces[y][x] != '.')
			pieceCount--;
		
		pieces[y][x] = pieces[current.y][current.x];
		pieces[current.y][current.x] = '.';
		currentEnd = new Field(y,x);
		
		if (pieces == this.pieces)
			turn = (turn == PieceColor.BLACK) ? PieceColor.WHITE: PieceColor.BLACK;
		else 
			tempTurn = (tempTurn == PieceColor.BLACK) ? PieceColor.WHITE: PieceColor.BLACK;
		
		if (pieces[y][x] == 'P' && y == 7) {
			pieces[y][x] = 'Q';
		}
		if (pieces[y][x] == 'p' && y == 0) {
			pieces[y][x] = 'q';
		}
		
		if (pieces == this.pieces) {
			addCurrentTable();
			
			if (animate && !premoved) {
				animatedPiece = new Field(x, y);
				//System.out.println(current.x + " " + current.y);
				animated = new Field(current.x * fieldSize, current.y * fieldSize);
				animatedStart = new Field(current.x * fieldSize, current.y * fieldSize);
				animatedEnd = new Field(x * fieldSize, y * fieldSize);
				animatedThread = new Thread(animation);
				animatedThread.start();
			}
			
		}
		
		if (pieces == this.pieces && gameOver()) {
			if (threefold()) {
				owner.winner.setText("Draw by repetition");
			}
			else if (stalemate()) {
				owner.winner.setText("Stalemate");
			}
			else if (turn.equals(PieceColor.WHITE)) {
				owner.winner.setText("Black has won");
			}
			else {
				owner.winner.setText("White has won");
			}
			gameOver = true;
			playerTimer.tick = false;
			computerTimer.tick = false;
			//System.out.println("GAME OVER!");
		}
		
		repaint();
			
	}
	
	private boolean equal(char[][] mat1, char[][] mat2) {
		for(int i = 0; i<8; i++)
			for(int j = 0; j<8; j++) {
				if (mat1[i][j] != mat2[i][j])
					return false;
			}
		return true;
	}
	
	private boolean threefold() {
		if (currMove < 9)
			return false;
		for(int i = 0; i < 2; i++)
			if (!(equal(tables.get(currMove - i), tables.get(currMove - 4 - i))
					&& equal(tables.get(currMove - i), tables.get(currMove - 8 - i))))
				return false;
		return true;
	}

	private boolean stalemate() {
		Field kingPos = null;
		PieceColor oppTurn = (turn.equals(PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE);
		for(int i = 0; i<8; i++)
			for(int j = 0; j<8; j++) {
				if ((turn.equals(PieceColor.WHITE) && pieces[i][j] == 'K') || (turn.equals(PieceColor.BLACK) && pieces[i][j] == 'k')) {
					kingPos = new Field(j,i);
				}
			}
		if (!getAttackedByOpponent(turn, pieces, pieceContext).contains(kingPos))
			return true;
		else
			return false;
	}

	class Animation implements Runnable{

		@Override
		public void run() {
			while (!animated.equals(animatedEnd)) {
				//System.out.println("HERE");
				animated.x += animatedPiece.x - animatedStart.x / fieldSize;
				animated.y += animatedPiece.y - animatedStart.y / fieldSize;
				
				//System.out.println("G" + animated.y);
				
				repaint();
				try {
					Thread.sleep(3);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			animatedPiece = null;
			repaint();
			
		}
		
	}
	
	Animation animation = new Animation();
	
	private Context saveContext(Context toSave) {
		Context s = new Context();
		s.castledBlack = toSave.castledBlack;
		s.castledWhite = toSave.castledWhite;
		s.kingMovedBlack = toSave.kingMovedBlack;
		s.kingMovedWhite = toSave.kingMovedWhite;
		s.rook00Moved = toSave.rook00Moved;
		s.rook07Moved = toSave.rook07Moved;
		s.rook70Moved = toSave.rook70Moved;
		s.rook77Moved = toSave.rook77Moved;
		
		return s;
	}
	
	private void restoreContext(Context toRestore, Context s) {
		toRestore.castledBlack = s.castledBlack;
		toRestore.castledWhite = s.castledWhite;
		toRestore.kingMovedBlack = s.kingMovedBlack;
		toRestore.kingMovedWhite = s.kingMovedWhite;
		toRestore.rook00Moved = s.rook00Moved;
		toRestore.rook07Moved = s.rook07Moved;
		toRestore.rook70Moved = s.rook70Moved;
		toRestore.rook77Moved = s.rook77Moved;
	}
		
	private void addListeners() {
		
		setFocusable(true);
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_LEFT: decMove(); break;
				case KeyEvent.VK_RIGHT: incMove(); break;
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (!SwingUtilities.isLeftMouseButton(e)) return;
				
				int x,y;
				x = e.getX()/fieldSize;
				y = 7 - e.getY()/fieldSize;
				
				if (premoved &&  (currMove == tables.size() - 1) && !premove.end.equals(new Field(x,y))) {
					premoved = false;
					premove = null;
					draggedStart = null;
					repaint();
					return;
				}
				
				if (getColor(pieces[y][x]).equals(PieceColor.WHITE) && currMove == tables.size() - 1 && !gameOver && !clicked && pieces[y][x] != '.' ) {
					clicked = true;
					tempCurrent.x = x;
					tempCurrent.y = y;
					attacked = getAttackedFields(tempCurrent, pieces, pieceContext);
					blocked = getBlockedFields(tempCurrent, attacked, pieces, pieceContext);
				}
				else if (turn.equals(PieceColor.WHITE) &&  (currMove == tables.size() - 1) && clicked && attacked.contains(new Field(x, y)) && !blocked.contains(new Field(x, y))) {
					clicked = false;
					
					current.x = tempCurrent.x;
					current.y = tempCurrent.y;
					doMove(x,y, pieces, pieceContext, true);
					
					if (!gameOver) {
						computerMove = new Thread(Board.this);
						computerMove.start();
						
					}
					
				}
				else if (clicked) {
					clicked = false;
				}
				repaint();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				
				
				if (!SwingUtilities.isLeftMouseButton(e)) return;
				
				if (!((currMove == tables.size() - 1)))
					return;
				if (attacked == null)
					return;
				if (!dragging)
					return;
				
				
				
				dragging = false;
				xoff = e.getX() % fieldSize;
				yoff = e.getY() % fieldSize;
				
				dragged = new Field(e.getX(), e.getY());
				
				int x,y;
				x = e.getX()/fieldSize;
				y = 7 - e.getY()/fieldSize;
				
				draggedEnd = new Field(x,y);
				
				if (turn.equals(PieceColor.BLACK)) {
					
					Field startingField = new Field(draggedStart.x, draggedStart.y);
					Field endingField = new Field(x,y);
					
					attacked = getPremovableFields(startingField, pieceContext);
					draggedStart = null;
					if (!attacked.contains(endingField)) {
						return;
					}
					
					premoved = true;
					premove = new Move(startingField, endingField);
					repaint();
					return;
				}
				
				if (!(attacked.contains(new Field(x, y)) && !blocked.contains(new Field(x, y)))) {
					draggedStart = null;
					repaint();
					return;
				}
				
				current.x = draggedStart.x;
				current.y = draggedStart.y;
				
				doMove(draggedEnd.x, draggedEnd.y, pieces, pieceContext, false);
				draggedStart = null;
				
				if (!gameOver) {
					computerMove = new Thread(Board.this);
					computerMove.start();
					
				}
				
				
			}

		});
		
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (!SwingUtilities.isLeftMouseButton(e)) return;
				clicked = false;
				
				if (!dragging) {
					int x,y;
					x = e.getX()/fieldSize;
					y = 7 - e.getY()/fieldSize;
					
					if (!(getColor(pieces[y][x]).equals(PieceColor.WHITE) && currMove == tables.size() - 1 && !gameOver))
						return;
					
					/*if (getColor(pieces[y][x]).equals(PieceColor.BLACK)) {
						premoved = true;
						premove = new Move(new Field(current.x, current.y), new Field(x, y));
						
					}*/
					
					draggedStart = new Field(x, y);
					xoff = e.getX() % fieldSize;
					yoff = e.getY() % fieldSize;
					
					dragging = true;
					
					tempCurrent.x = x;
					tempCurrent.y = y;
					attacked = getAttackedFields(tempCurrent, pieces, pieceContext);
					blocked = getBlockedFields(tempCurrent, attacked, pieces, pieceContext);
				}
				dragged = new Field(e.getX(), e.getY());
				repaint();
			}
			
		
		});
		
	}
	
	boolean started = false;
	
	private Move findMove(int depth, PieceColor pc, int[] score, int alpha, int beta) {
		
		if (depth == 0) {
			score[0] = boardValue();
			return null;
		}
		
		Move res = null;
		
		score[0] = (pc.equals(PieceColor.WHITE) ? INFINITY : -INFINITY);
		boolean retFlag = false;
		
		
		for(int i = 0; i<8; i++)
			for(int j = 0; j<8; j++) {
				if (!retFlag && tempBoard[i][j] != '.' && getColor(tempBoard[i][j]).equals(pc)) {
					ArrayList<Field> at = getAttackedFields(new Field(j,i), tempBoard, tempContext);
					ArrayList<Field> blk = getBlockedFields(new Field(j,i), at, tempBoard, tempContext);
					
					for(Field f:at) {
						if (!blk.contains(f)) {
							current.x = j;
							current.y = i;
							char pieceStart = tempBoard[i][j];
							char pieceEnd = tempBoard[f.y][f.x];
							PieceColor currTempTurn = tempTurn.equals(PieceColor.BLACK) ? PieceColor.BLACK: PieceColor.WHITE;
							
							Context s = saveContext(tempContext);
							doMove(f.x, f.y, tempBoard, tempContext, true);
							
							int[] sc = new int[1];
							
							/*if (depth == 5  && j == 7 && i == 3 && f.x == 7 && f.y == 1) {
								System.out.println("STARTING");
								started = true;
							}*/
								
							
							findMove(depth-1, (pc.equals(PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE), sc, alpha, beta);
							
							//if (depth == 6)
								//System.out.println(sc[0] + " " + j + " " + i + " " + f.x + " " + f.y + " " + depth);
							
							restoreContext(tempContext, s);
							if (res == null)
								res = new Move(new Field(j,i),f);
							
							if (pc.equals(PieceColor.BLACK)) {
								
								if (sc[0] > score[0]) {
									score[0] = sc[0];
									res = new Move(new Field(j,i), new Field(f.x, f.y));
									/*if (depth == 5)
										System.out.println(sc[0] + " " + j + " " + i + " " + f.x + " " + f.y + " " + depth);*/
								}
								alpha = Math.max(alpha, score[0]);
								if (beta <= alpha) {
									retFlag = true;
								}
							}
							else {
								if (sc[0] < score[0]) {
									score[0] = sc[0];
									res = new Move(new Field(j,i), new Field(f.x, f.y));
								}
								
								beta = Math.min(beta, score[0]);
								if (beta <= alpha) {
									retFlag = true;
								}

							}
							
							
							tempTurn = currTempTurn.equals(PieceColor.BLACK) ? PieceColor.BLACK: PieceColor.WHITE;
							
							
							if (Math.abs(f.x - j) == 2 && pieceStart == 'k') {
								if (f.x == 6 && f.y == 7) {
									tempBoard[7][5] = '.';
									tempBoard[7][7] = 'r';
								}
								
								else if (f.x == 2 && f.y == 7) {
									tempBoard[7][3] = '.';
									tempBoard[7][0] = 'r';
								}
								
							}
							if (Math.abs(f.x - j) == 2 && pieceStart == 'K') {
								if (f.x == 6 && f.y == 0) {
									tempBoard[0][5] = '.';
									tempBoard[0][7] = 'R';
								}
								
								else if (f.x == 2 && f.y == 0) {
									tempBoard[0][3] = '.';
									tempBoard[0][0] = 'R';
								}
								
							}
							
							tempBoard[i][j] = pieceStart;
							tempBoard[f.y][f.x] = pieceEnd;
							
						}
					}
				}
			}
		return res;
	}

	private boolean gameOver() {
		
		Timer playerTimer = owner.playerTimer;
		Timer computerTimer = owner.computerTimer;
		
		boolean res = true;
		
		for(int i = 0; i<8; i++)
			for(int j = 0; j<8; j++) {
				if (pieces[i][j] != '.' && getColor(pieces[i][j]).equals(turn)) {
					ArrayList<Field> attacked = getAttackedFields(new Field(j,i), pieces, pieceContext);
					ArrayList<Field> blocked = getBlockedFields(new Field(j,i), attacked, pieces, pieceContext);
					if (attacked.size() > blocked.size()) {
						res = false;
					}
				}
			}
		
		return res || threefold();
	}
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		Graphics2D g2D = (Graphics2D)g;
		fieldSize = w/8;
		g2D.setColor(color);
		
		drawTable(g);
		
		char[][] table = tables.get(currMove);
		
		if (premoved  &&  currMove == tables.size() - 1) {
			Field startField = premove.start;
			Field endField = premove.end;
			
			double opacity = 0.8;
			
			
			Color forWhite = Color.decode("#5E7594");
			Color forBlack = Color.decode("#455B77");
					
			
			Color toSet = ((startField.x + startField.y) % 2 == 0) ? forBlack: forWhite; 
			g2D.setColor(toSet);
			
			g2D.fillRect(startField.x * fieldSize, (7 - startField.y) * fieldSize, fieldSize, fieldSize);
			
			
			toSet = ((endField.x + endField.y) % 2 == 0) ? forBlack: forWhite; 
			g2D.setColor(toSet);
			
			g2D.fillRect(endField.x * fieldSize, (7 - endField.y) * fieldSize, fieldSize, fieldSize);
			
			
		}
		
		if (currMove - 1 >= 0 && currMove - 1 < moves.size()) {
			Move move = moves.get(currMove - 1);
			Field startField = move.start;
			Field endField = move.end;
			
			double opacity = (startField.x + startField.y) % 2 == 0 ? 0.5 : 0.8;
			g2D.setComposite(AlphaComposite.getInstance(
	                AlphaComposite.SRC_OVER, (float)opacity));
			g2D.setColor(new Color(252, 245, 95));
			
			if (premove == null || (!startField.equals(premove.start) && !startField.equals(premove.end)))
				g2D.fillRect(startField.x * fieldSize, (7 - startField.y) * fieldSize, fieldSize, fieldSize);
			
			opacity = (endField.x + endField.y) % 2 == 0 ? 0.5 : 0.8;
			
			g2D.setComposite(AlphaComposite.getInstance(
	                AlphaComposite.SRC_OVER, (float)opacity));
			
			if (premove == null || (!endField.equals(premove.start) && !endField.equals(premove.end)))
				g2D.fillRect(endField.x * fieldSize, (7 - endField.y) * fieldSize, fieldSize, fieldSize);
			
			g2D.setComposite(AlphaComposite.getInstance(
	                AlphaComposite.SRC_OVER));
		}
		
		
		for(int i = 0; i<8; i++)
			for(int j = 0; j<8; j++) {
				if (table[i][j] != '.' && (draggedStart == null || !draggedStart.equals(new Field(j,i))) && (animatedPiece == null || !animatedPiece.equals(new Field(j,i)))) {
					Image img = getImage(table[i][j]);
					//System.out.println(i + " " + j);
					g2D.drawImage(img, j*w/8, (7-i)*h/8, null);
				}
			}
		
	
		if (animatedPiece != null && currMove == tables.size() - 1) {
			Image img = getImage(table[animatedPiece.y][animatedPiece.x]);
			g2D.drawImage(img, animated.x, getHeight() - fieldSize - animated.y, null);
		}
		
		
		
		if (draggedStart != null &&  currMove == tables.size() - 1) {
			Image img = getImage(table[draggedStart.y][draggedStart.x]);
			g2D.drawImage(img, dragged.x - xoff, dragged.y - yoff, null);
		}
		/*if (premove != null &&  currMove == tables.size() - 1) {
			Image img = getImage(table[premove.end.y][premove.end.x]);
			g2D.drawImage(img, premove.end.x * fieldSize, premove.end.y * fieldSize, null);
		}
		*/
		if (clicked && currMove == tables.size() - 1 && !premoved) {
			g2D.setComposite(AlphaComposite.getInstance(
	                AlphaComposite.SRC_OVER, (float)0.5));
			g2D.setColor(new Color(27,18,18));
			
			for(Field f:attacked) {
				if (!blocked.contains(f))
					g2D.fillOval(f.x * fieldSize + 20, (7-f.y) * fieldSize + 20, fieldSize - 40, fieldSize - 40);
			}
			
			g2D.setComposite(AlphaComposite.getInstance(
	                AlphaComposite.SRC_OVER));
		}
	}
	
	private ArrayList<Field> getPremovableFields(Field curr, Context s) {
		ArrayList<Field> res = new ArrayList<>();
		
		char p = pieces[curr.y][curr.x];
		char toUpper = (char) (pieces[curr.y][curr.x] + ((getColor(pieces[curr.y][curr.x]).equals(PieceColor.BLACK)) ? ('A' - 'a'): 0));
		//System.out.println("F" + curr.y + " " + curr.x);
		char c;
		switch (toUpper) {
		case 'R':
			for(int i = 0; i<8; i++) {
				
					res.add(new Field(i,curr.y));
				
					
			}
			for(int i = 7; i>=0; i--) {
				
				
					res.add(new Field(i,curr.y));
				
			}
			for(int i = 0; i<8; i++) {
				
					res.add(new Field(curr.x, i));
				
			}
			for(int i = 7; i>=0; i--) {
				
					res.add(new Field(curr.x, i));
				
					
			}
			break;
		
		case 'B':
			for(int i = 0; i<8; i++) {
				
				int yn = i - (curr.x - curr.y);
				if (yn < 0 || yn >= 8)
					break;
				
					res.add(new Field(i,yn));
				
				
			}
			for(int i = 7; i>=0; i--) {
				int yn = i - (curr.x - curr.y);
				if (yn < 0 || yn >= 8)
					break;
				
					res.add(new Field(i,yn));
				
			}
			for(int i = 0; i<8; i++) {
				int xn = (curr.x + curr.y) - i;
				if (xn < 0 || xn >= 8)
					break;
				
					res.add(new Field(xn, i));
				
			}
			for(int i = 7; i>=0; i--) {
				int xn = (curr.x + curr.y) - i;
				if (xn < 0 || xn >= 8)
					break;
				
					res.add(new Field(xn, i));
				
				
			}
			break;
		case 'Q':
			for(int i = 0; i<8; i++) {
				
					res.add(new Field(i,curr.y));
				
				
					
			}
			for(int i = 7; i>=0; i--) {
				
					res.add(new Field(i,curr.y));
				
			}
			for(int i = 0; i<8; i++) {
				
					res.add(new Field(curr.x, i));
				
			}
			for(int i = 7; i>=0; i--) {
				
					res.add(new Field(curr.x, i));
				
					
			}
			for(int i = 0; i<8; i++) {
				
				int yn = i - (curr.x - curr.y);
				if (yn < 0 || yn >= 8)
					break;
				
					res.add(new Field(i,yn));
				
				
				
			}
			for(int i = 7; i>=0; i--) {
				int yn = i - (curr.x - curr.y);
				if (yn < 0 || yn >= 8)
					break;
			
				
					res.add(new Field(i,yn));
				
				
			}
			for(int i = 0; i<8; i++) {
				int xn = (curr.x + curr.y) - i;
				if (xn < 0 || xn >= 8)
					break;
				
					res.add(new Field(xn, i));
				
				
			}
			for(int i = 7; i>=0; i--) {
				int xn = (curr.x + curr.y) - i;
				if (xn < 0 || xn >= 8)
					break;
				
				res.add(new Field(xn, i));
				
				
			}
			break;
		case 'N':
			int[] offs = {1, -1, 2, -2};
			for(int ox:offs) {
				for(int oy:offs) {
					if (Math.abs(ox) != Math.abs(oy) && !outOfBounds(curr.x + ox, curr.y + oy) ) {
						res.add(new Field(curr.x + ox, curr.y + oy));
					}
				}
			}
			break;
		case 'P':
			if (getColor(p).equals(PieceColor.WHITE)) {
				if (curr.y + 1 < 8 && curr.x + 1 < 8 ) {
					res.add(new Field(curr.x + 1, curr.y + 1));
				}
				if (curr.y + 1 < 8 && curr.x - 1 >= 0)
				{
					res.add(new Field(curr.x - 1, curr.y + 1));
				}
				if (curr.y + 1 < 8) {
					res.add(new Field(curr.x, curr.y + 1));
					if (curr.y == 1) {
						
						res.add(new Field(curr.x, curr.y + 2));
					}
				}
			}
			else {
				if (curr.y - 1 >= 0 && curr.x + 1 < 8 ) {
					res.add(new Field(curr.x + 1, curr.y - 1));
				}
				if (curr.y - 1 >= 0 && curr.x - 1 >= 0 ) {
					res.add(new Field(curr.x - 1, curr.y - 1));
				}
				if (curr.y - 1 >= 0) {
					res.add(new Field(curr.x, curr.y - 1));
					if (curr.y == 6) {
						if (pieces[curr.y - 2][curr.x] == '.')
							res.add(new Field(curr.x, curr.y - 2));
					}
				}
			}
			break;
		case 'K':
			int[] off = {1, -1, 0};
			for(int ox:off) {
				for(int oy:off) {
					if (Math.abs(ox) + Math.abs(oy) > 0 && !outOfBounds(curr.x + ox, curr.y + oy)) {
						res.add(new Field(curr.x + ox, curr.y + oy));
					}
				}
			}
			if ((!s.rook07Moved && !s.kingMovedWhite && getColor(p).equals(PieceColor.WHITE))){
				res.add(new Field(6,0));
			}
			if ((!s.rook70Moved && !s.kingMovedBlack && getColor(p).equals(PieceColor.BLACK))){
				res.add(new Field(2,7));
			}
			if ((!s.rook00Moved && !s.kingMovedWhite && getColor(p).equals(PieceColor.WHITE))){
				res.add(new Field(2,0));
			}
			if ((!s.rook77Moved && !s.kingMovedBlack && getColor(p).equals(PieceColor.BLACK))){
				res.add(new Field(6,7));
			}
			break;
		
			
		}
		while (res.contains(curr))
			res.remove(curr);
		return res;
	}
	
	private ArrayList<Field> getEyeballing(Field f, char[][] pieces, Context s) {
		ArrayList<Field> res = new ArrayList<>();
		char p = pieces[f.y][f.x];
		char toUpper = (char) (pieces[f.y][f.x] + ((getColor(pieces[f.y][f.x]).equals(PieceColor.BLACK)) ? ('A' - 'a'): 0));
		//System.out.println("F" + curr.y + " " + curr.x);
		char c;
		switch (toUpper) {
		case 'R':
			for(int i = f.x + 1; i<8; i++) {
				c = pieces[f.y][i];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(i,f.y));
				if (pieces[f.y][i] != '.') {
					break;
				}
					
			}
			for(int i = f.x - 1; i>=0; i--) {
				c = pieces[f.y][i];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(i,f.y));
				if (pieces[f.y][i] != '.') {
					break;
				}
			}
			for(int i = f.y + 1; i<8; i++) {
				c = pieces[i][f.x];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(f.x, i));
				if (pieces[i][f.x] != '.') {
					break;
				}
			}
			for(int i = f.y - 1; i>=0; i--) {
				c = pieces[i][f.x];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(f.x, i));
				if (pieces[i][f.x] != '.') {
					break;
				}
					
			}
			break;
		
		case 'B':
			for(int i = f.x + 1; i<8; i++) {
				
				int yn = i - (f.x - f.y);
				if (yn < 0 || yn >= 8)
					break;
				c = pieces[yn][i];
				if (c == '.' || getColor(c) != getColor(p))
					res.add(new Field(i,yn));
				if (pieces[yn][i] != '.') {
					break;
				}
				
			}
			for(int i = f.x - 1; i>=0; i--) {
				int yn = i - (f.x - f.y);
				if (yn < 0 || yn >= 8)
					break;
				c = pieces[yn][i];
				if (c == '.' || getColor(c) != getColor(p))
					res.add(new Field(i,yn));
				if (pieces[yn][i] != '.') {
					break;
				}
			}
			for(int i = f.y + 1; i<8; i++) {
				int xn = (f.x + f.y) - i;
				if (xn < 0 || xn >= 8)
					break;
				c = pieces[i][xn];
				if (c == '.' || getColor(c) != getColor(p))
					res.add(new Field(xn, i));
				if (pieces[i][xn] != '.') {
					break;
				}
			}
			for(int i = f.y - 1; i>=0; i--) {
				int xn = (f.x + f.y) - i;
				if (xn < 0 || xn >= 8)
					break;
				c = pieces[i][xn];
				if (c == '.' || getColor(c) != getColor(p))
					res.add(new Field(xn, i));
				if (pieces[i][xn] != '.') {
					break;
				}
				
			}
			break;
		case 'Q':
			for(int i = f.x + 1; i<8; i++) {
				c = pieces[f.y][i];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(i,f.y));
				if (pieces[f.y][i] != '.') {
					break;
				}
					
			}
			for(int i = f.x - 1; i>=0; i--) {
				c = pieces[f.y][i];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(i,f.y));
				if (pieces[f.y][i] != '.') {
					break;
				}
			}
			for(int i = f.y + 1; i<8; i++) {
				c = pieces[i][f.x];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(f.x, i));
				if (pieces[i][f.x] != '.') {
					break;
				}
			}
			for(int i = f.y - 1; i>=0; i--) {
				c = pieces[i][f.x];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(f.x, i));
				if (pieces[i][f.x] != '.') {
					break;
				}
					
			}
			for(int i = f.x + 1; i<8; i++) {
				
				int yn = i - (f.x - f.y);
				if (yn < 0 || yn >= 8)
					break;
				c = pieces[yn][i];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(i,yn));
				if (pieces[yn][i] != '.') {
					break;
				}
				
			}
			for(int i = f.x - 1; i>=0; i--) {
				int yn = i - (f.x - f.y);
				if (yn < 0 || yn >= 8)
					break;
				c = pieces[yn][i];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(i,yn));
				if (pieces[yn][i] != '.') {
					break;
				}
			}
			for(int i = f.y + 1; i<8; i++) {
				int xn = (f.x + f.y) - i;
				if (xn < 0 || xn >= 8)
					break;
				c = pieces[i][xn];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(xn, i));
				if (pieces[i][xn] != '.') {
					break;
				}
			}
			for(int i = f.y - 1; i>=0; i--) {
				int xn = (f.x + f.y) - i;
				if (xn < 0 || xn >= 8)
					break;
				c = pieces[i][xn];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(xn, i));
				if (pieces[i][xn] != '.') {
					break;
				}
			}
			break;
		case 'N':
			int[] offs = {1, -1, 2, -2};
			for(int ox:offs) {
				for(int oy:offs) {
					if (Math.abs(ox) != Math.abs(oy) && !outOfBounds(f.x + ox, f.y + oy) && (pieces[f.y + oy][f.x + ox] == '.' || !getColor(p).equals(getColor(pieces[f.y + oy][f.x + ox])))) {
						res.add(new Field(f.x + ox, f.y + oy));
					}
				}
			}
			break;
		case 'P':
			if (getColor(p).equals(PieceColor.WHITE)) {
				if (f.y + 1 < 8 && f.x + 1 < 8 && pieces[f.y+1][f.x + 1] != '.' && !getColor(pieces[f.y + 1][f.x + 1]).equals(getColor(p))) {
					res.add(new Field(f.x + 1, f.y + 1));
				}
				if (f.y + 1 < 8 && f.x - 1 >= 0 && pieces[f.y+1][f.x - 1] != '.' && !getColor(pieces[f.y + 1][f.x - 1]).equals(getColor(p)))
				{
					res.add(new Field(f.x - 1, f.y + 1));
				}
				if (f.y + 1 < 8 && pieces[f.y + 1][f.x] == '.') {
					res.add(new Field(f.x, f.y + 1));
					if (f.y == 1) {
						if (pieces[f.y + 2][f.x] == '.')
							res.add(new Field(f.x, f.y + 2));
					}
				}
			}
			else {
				if (f.y - 1 >= 0 && f.x + 1 < 8 && pieces[f.y - 1][f.x + 1] != '.' && !getColor(pieces[f.y - 1][f.x + 1]).equals(getColor(p))) {
					res.add(new Field(f.x + 1, f.y - 1));
				}
				if (f.y - 1 >= 0 && f.x - 1 >= 0 && pieces[f.y - 1][f.x - 1] != '.' && !getColor(pieces[f.y - 1][f.x - 1]).equals(getColor(p))) {
					res.add(new Field(f.x - 1, f.y - 1));
				}
				if (f.y - 1 >= 0 && pieces[f.y - 1][f.x] == '.') {
					res.add(new Field(f.x, f.y - 1));
					if (f.y == 6) {
						if (pieces[f.y - 2][f.x] == '.')
							res.add(new Field(f.x, f.y - 2));
					}
				}
			}
			break;
		case 'K':
			int[] off = {1, -1, 0};
			for(int ox:off) {
				for(int oy:off) {
					if (Math.abs(ox) + Math.abs(oy) > 0 && !outOfBounds(f.x + ox, f.y + oy) && (pieces[f.y + oy][f.x + ox] == '.' || !getColor(p).equals(getColor(pieces[f.y + oy][f.x + ox])))) {
						res.add(new Field(f.x + ox, f.y + oy));
					}
				}
			}
			if ((!s.rook07Moved && !s.kingMovedWhite && getColor(p).equals(PieceColor.WHITE) && pieces[0][5] == '.' && pieces[0][6] == '.' && pieces[0][7] == 'R')){
				res.add(new Field(6,0));
			}
			if ((!s.rook70Moved && !s.kingMovedBlack && getColor(p).equals(PieceColor.BLACK) && pieces[7][1] == '.' && pieces[7][2] == '.' && pieces[7][3] == '.' && pieces[7][0] == 'r')){
				res.add(new Field(2,7));
			}
			if ((!s.rook00Moved && !s.kingMovedWhite && getColor(p).equals(PieceColor.WHITE) && pieces[0][1] == '.' && pieces[0][2] == '.' && pieces[0][3] == '.' && pieces[0][0] == 'R')){
				res.add(new Field(2,0));
			}
			if ((!s.rook77Moved && !s.kingMovedBlack && getColor(p).equals(PieceColor.BLACK) && pieces[7][5] == '.' && pieces[7][6] == '.' && pieces[7][7] == 'r')){
				res.add(new Field(6,7));
			}
			break;
		
		}
		return res;
	}


	private ArrayList<Field> getAttackedFields(Field curr, char[][] pieces, Context s) {
		ArrayList<Field> res = new ArrayList<>();
		
		char p = pieces[curr.y][curr.x];
		char toUpper = (char) (pieces[curr.y][curr.x] + ((getColor(pieces[curr.y][curr.x]).equals(PieceColor.BLACK)) ? ('A' - 'a'): 0));
		//System.out.println("F" + curr.y + " " + curr.x);
		char c;
		switch (toUpper) {
		case 'R':
			for(int i = curr.x + 1; i<8; i++) {
				c = pieces[curr.y][i];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(i,curr.y));
				if (pieces[curr.y][i] != '.') {
					break;
				}
					
			}
			for(int i = curr.x - 1; i>=0; i--) {
				c = pieces[curr.y][i];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(i,curr.y));
				if (pieces[curr.y][i] != '.') {
					break;
				}
			}
			for(int i = curr.y + 1; i<8; i++) {
				c = pieces[i][curr.x];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(curr.x, i));
				if (pieces[i][curr.x] != '.') {
					break;
				}
			}
			for(int i = curr.y - 1; i>=0; i--) {
				c = pieces[i][curr.x];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(curr.x, i));
				if (pieces[i][curr.x] != '.') {
					break;
				}
					
			}
			break;
		
		case 'B':
			for(int i = curr.x + 1; i<8; i++) {
				
				int yn = i - (curr.x - curr.y);
				if (yn < 0 || yn >= 8)
					break;
				c = pieces[yn][i];
				if (c == '.' || getColor(c) != getColor(p))
					res.add(new Field(i,yn));
				if (pieces[yn][i] != '.') {
					break;
				}
				
			}
			for(int i = curr.x - 1; i>=0; i--) {
				int yn = i - (curr.x - curr.y);
				if (yn < 0 || yn >= 8)
					break;
				c = pieces[yn][i];
				if (c == '.' || getColor(c) != getColor(p))
					res.add(new Field(i,yn));
				if (pieces[yn][i] != '.') {
					break;
				}
			}
			for(int i = curr.y + 1; i<8; i++) {
				int xn = (curr.x + curr.y) - i;
				if (xn < 0 || xn >= 8)
					break;
				c = pieces[i][xn];
				if (c == '.' || getColor(c) != getColor(p))
					res.add(new Field(xn, i));
				if (pieces[i][xn] != '.') {
					break;
				}
			}
			for(int i = curr.y - 1; i>=0; i--) {
				int xn = (curr.x + curr.y) - i;
				if (xn < 0 || xn >= 8)
					break;
				c = pieces[i][xn];
				if (c == '.' || getColor(c) != getColor(p))
					res.add(new Field(xn, i));
				if (pieces[i][xn] != '.') {
					break;
				}
				
			}
			break;
		case 'Q':
			for(int i = curr.x + 1; i<8; i++) {
				c = pieces[curr.y][i];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(i,curr.y));
				if (pieces[curr.y][i] != '.') {
					break;
				}
					
			}
			for(int i = curr.x - 1; i>=0; i--) {
				c = pieces[curr.y][i];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(i,curr.y));
				if (pieces[curr.y][i] != '.') {
					break;
				}
			}
			for(int i = curr.y + 1; i<8; i++) {
				c = pieces[i][curr.x];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(curr.x, i));
				if (pieces[i][curr.x] != '.') {
					break;
				}
			}
			for(int i = curr.y - 1; i>=0; i--) {
				c = pieces[i][curr.x];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(curr.x, i));
				if (pieces[i][curr.x] != '.') {
					break;
				}
					
			}
			for(int i = curr.x + 1; i<8; i++) {
				
				int yn = i - (curr.x - curr.y);
				if (yn < 0 || yn >= 8)
					break;
				c = pieces[yn][i];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(i,yn));
				if (pieces[yn][i] != '.') {
					break;
				}
				
			}
			for(int i = curr.x - 1; i>=0; i--) {
				int yn = i - (curr.x - curr.y);
				if (yn < 0 || yn >= 8)
					break;
				c = pieces[yn][i];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(i,yn));
				if (pieces[yn][i] != '.') {
					break;
				}
			}
			for(int i = curr.y + 1; i<8; i++) {
				int xn = (curr.x + curr.y) - i;
				if (xn < 0 || xn >= 8)
					break;
				c = pieces[i][xn];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(xn, i));
				if (pieces[i][xn] != '.') {
					break;
				}
			}
			for(int i = curr.y - 1; i>=0; i--) {
				int xn = (curr.x + curr.y) - i;
				if (xn < 0 || xn >= 8)
					break;
				c = pieces[i][xn];
				if (c == '.' || !getColor(c).equals(getColor(p)))
					res.add(new Field(xn, i));
				if (pieces[i][xn] != '.') {
					break;
				}
			}
			break;
		case 'N':
			int[] offs = {1, -1, 2, -2};
			for(int ox:offs) {
				for(int oy:offs) {
					if (Math.abs(ox) != Math.abs(oy) && !outOfBounds(curr.x + ox, curr.y + oy) && (pieces[curr.y + oy][curr.x + ox] == '.' || !getColor(p).equals(getColor(pieces[curr.y + oy][curr.x + ox])))) {
						res.add(new Field(curr.x + ox, curr.y + oy));
					}
				}
			}
			break;
		case 'P':
			if (getColor(p).equals(PieceColor.WHITE)) {
				if (curr.y + 1 < 8 && curr.x + 1 < 8 && pieces[curr.y+1][curr.x + 1] != '.' && !getColor(pieces[curr.y + 1][curr.x + 1]).equals(getColor(p))) {
					res.add(new Field(curr.x + 1, curr.y + 1));
				}
				if (curr.y + 1 < 8 && curr.x - 1 >= 0 && pieces[curr.y+1][curr.x - 1] != '.' && !getColor(pieces[curr.y + 1][curr.x - 1]).equals(getColor(p)))
				{
					res.add(new Field(curr.x - 1, curr.y + 1));
				}
				if (curr.y + 1 < 8 && pieces[curr.y + 1][curr.x] == '.') {
					res.add(new Field(curr.x, curr.y + 1));
					if (curr.y == 1) {
						if (pieces[curr.y + 2][curr.x] == '.')
							res.add(new Field(curr.x, curr.y + 2));
					}
				}
			}
			else {
				if (curr.y - 1 >= 0 && curr.x + 1 < 8 && pieces[curr.y - 1][curr.x + 1] != '.' && !getColor(pieces[curr.y - 1][curr.x + 1]).equals(getColor(p))) {
					res.add(new Field(curr.x + 1, curr.y - 1));
				}
				if (curr.y - 1 >= 0 && curr.x - 1 >= 0 && pieces[curr.y - 1][curr.x - 1] != '.' && !getColor(pieces[curr.y - 1][curr.x - 1]).equals(getColor(p))) {
					res.add(new Field(curr.x - 1, curr.y - 1));
				}
				if (curr.y - 1 >= 0 && pieces[curr.y - 1][curr.x] == '.') {
					res.add(new Field(curr.x, curr.y - 1));
					if (curr.y == 6) {
						if (pieces[curr.y - 2][curr.x] == '.')
							res.add(new Field(curr.x, curr.y - 2));
					}
				}
			}
			break;
		case 'K':
			int[] off = {1, -1, 0};
			for(int ox:off) {
				for(int oy:off) {
					if (Math.abs(ox) + Math.abs(oy) > 0 && !outOfBounds(curr.x + ox, curr.y + oy) && (pieces[curr.y + oy][curr.x + ox] == '.' || !getColor(p).equals(getColor(pieces[curr.y + oy][curr.x + ox])))) {
						res.add(new Field(curr.x + ox, curr.y + oy));
					}
				}
			}
			ArrayList<Field> eyeballed = getAttackedByOpponent(getColor(p), pieces, s);
			
			if ((!s.rook07Moved && !s.kingMovedWhite && !eyeballed.contains(new Field(4,0)) && !eyeballed.contains(new Field(5,0)) && !eyeballed.contains(new Field(6,0)) && !eyeballed.contains(new Field(7,0)) &&  getColor(p).equals(PieceColor.WHITE) && pieces[0][5] == '.' && pieces[0][6] == '.' && pieces[0][7] == 'R')){
				res.add(new Field(6,0));
			}
			if ((!s.rook70Moved && !s.kingMovedBlack && !eyeballed.contains(new Field(0,7)) && !eyeballed.contains(new Field(1,7)) && !eyeballed.contains(new Field(2,7)) && !eyeballed.contains(new Field(3,7)) && getColor(p).equals(PieceColor.BLACK) && pieces[7][1] == '.' && pieces[7][2] == '.' && pieces[7][3] == '.' && pieces[7][0] == 'r')){
				res.add(new Field(2,7));
			}
			if ((!s.rook00Moved && !s.kingMovedWhite && !eyeballed.contains(new Field(0,0)) && !eyeballed.contains(new Field(1,0)) && !eyeballed.contains(new Field(2,0)) && !eyeballed.contains(new Field(3,0)) && !eyeballed.contains(new Field(4,0)) && getColor(p).equals(PieceColor.WHITE) && pieces[0][1] == '.' && pieces[0][2] == '.' && pieces[0][3] == '.' && pieces[0][0] == 'R')){
				res.add(new Field(2,0));
			}
			if ((!s.rook77Moved && !s.kingMovedBlack && !eyeballed.contains(new Field(3,7)) && !eyeballed.contains(new Field(4,7)) && !eyeballed.contains(new Field(5,7)) && !eyeballed.contains(new Field(6,7)) && !eyeballed.contains(new Field(7,7)) && getColor(p).equals(PieceColor.BLACK) && pieces[7][5] == '.' && pieces[7][6] == '.' && pieces[7][7] == 'r')){
				res.add(new Field(6,7));
			}
			break;
		
			
		}
		
		/*for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++) {
				
			}*/
		/*System.out.println("R" + res.size());
		for(Field f:res) {
			System.out.println(f.y + " " + f.x);
		}*/
		return res;
	}
	
	private ArrayList<Field> getBlockedFields(Field curr, ArrayList<Field> attacked, char[][] pieces, Context s){
		
		PieceColor currColor = getColor(pieces[curr.y][curr.x]);
		ArrayList<Field> res = new ArrayList<>();
		Field kingPos = null;
		for(int i = 0; i<8; i++)
			for(int j = 0; j<8; j++) {
				if ((currColor.equals(PieceColor.WHITE) && pieces[i][j] == 'K') || (currColor.equals(PieceColor.BLACK) && pieces[i][j] == 'k')) {
					kingPos = new Field(j,i);
				}
			}
		if (kingPos == null) {
			//System.out.println("WATAWAK");
			return (ArrayList<Field>) attacked.clone();
		}
		ArrayList<Field> attackedAfter;
		
		for(Field f:attacked) {
			
			char previous = pieces[f.y][f.x];
			Field previousKing = kingPos;
			pieces[f.y][f.x] = pieces[curr.y][curr.x];
			pieces[curr.y][curr.x] = '.';
			
			if (kingPos.equals(curr)) {
				kingPos = new Field(f.x, f.y);
			}
			
			if (Math.abs(kingPos.x - previousKing.x) == 2) {
				if (kingPos.x == 6 && kingPos.y == 7) {
					pieces[7][5] = 'r';
					pieces[7][7] = '.';
				}
				else if (kingPos.x == 6 && kingPos.y == 0) {
					pieces[0][5] = 'R';
					pieces[0][7] = '.';
				}
				else if (kingPos.x == 2 && kingPos.y == 7) {
					pieces[7][3] = 'r';
					pieces[7][0] = '.';
				}
				else {
					pieces[0][3] = 'R';
					pieces[0][0] = '.';
				}
			}
			
			boolean found = false;
			
			for(int i = 0; i < 8; i++)
				for(int j = 0; j < 8; j++) {
					if (!found && !getColor(pieces[i][j]).equals(currColor)) {
						attackedAfter = getAttackedFields(new Field(j,i), pieces, s);
						if (attackedAfter.contains(kingPos)) {
							res.add(f);
							found = true;
						}
					}
				}
			if (Math.abs(kingPos.x - previousKing.x) == 2) {
				if (kingPos.x == 6 && kingPos.y == 7) {
					pieces[7][5] = '.';
					pieces[7][7] = 'r';
				}
				else if (kingPos.x == 6 && kingPos.y == 0) {
					pieces[0][5] = '.';
					pieces[0][7] = 'R';
				}
				else if (kingPos.x == 2 && kingPos.y == 7) {
					pieces[7][3] = '.';
					pieces[7][0] = 'r';
				}
				else {
					pieces[0][3] = '.';
					pieces[0][0] = 'R';
				}
			}
			pieces[curr.y][curr.x] = pieces[f.y][f.x];
			pieces[f.y][f.x] = previous;
			kingPos = new Field(previousKing.x, previousKing.y);
			
		}
		/*for(Field f:res) {
			System.out.println(f.y + " " + f.x);
		}*/
		return res;
	}
	
	void setColor(Color c){
		color = c;
	} 

	private boolean outOfBounds(int x, int y) {
		return x < 0 || x>=8 || y < 0 || y>=8;
	}
	
	private Image getImage(char c) {
		switch (c) {
		case 'k': 
			return k;
		case 'q': 
			return q;
		case 'b': 
			return b;
		case 'n': 
			return n;
		case 'r': 
			return r;
		case 'p': 
			return p;
		case 'K': 
			return K;
		case 'Q': 
			return Q;
		case 'B': 
			return B;
		case 'N': 
			return N;
		case 'R': 
			return R;
		case 'P': 
			return P;
		}
		return null;	
			
	}

	private int pieceValue(char p) {
		if (p > 'Z') {
			p = (char)(p + 'A' - 'a');
		}
		switch(p) {
		case 'P':
			return 1; 
		case 'N':
			return 3;
		case 'B':
			return 3; 
		case 'R':
			return 5; 
		case 'Q':
			return 9; 
		case 'K':
			return 100;
			
		}
		return 0;
	}
	
	private int boardValue() {
		int res = 0;
		for(int i = 0; i<8; i++)
			for(int j = 0; j<8; j++) {
				if (tempBoard[i][j] != '.') {
					if (getColor(tempBoard[i][j]).equals(PieceColor.BLACK)) {
						res += pieceValue(tempBoard[i][j]);
					}
					else {
						res -= pieceValue(tempBoard[i][j]);
					}
				}
			}
		return res;
	}
	
	public void restart() {
		
		for(int i = 0; i <8; i++) {
			pieces[i] = init[i].clone();
		}
		tables = new ArrayList<char[][]>();
		moves = new ArrayList<Move>();
		if (owner.listOfMoves != null)
			owner.listOfMoves.removeAll();
		if (owner.scroll != null)
			owner.scroll.setPreferredSize(new Dimension(120, 23));
		owner.a = 0;
		setFocusable(true);
		addCurrentTable();
		checked = false;
		castledWhite = false;
		castledBlack = false;
		kingMovedWhite = false;
		kingMovedBlack = false;
		rook00Moved = false;
		rook07Moved = false;
		rook70Moved = false;
		rook77Moved = false;
		gameOver = false;
		pieceCount = 32;
		turn = PieceColor.WHITE;
		clicked = false;
		
		if (owner.winner != null)
			owner.winner.setText("");
		
		currLen = 0;
		currMove = 0;
		
		repaint();
		
	}
	
	private void addCurrentTable() {
		char [][] table = new char[8][8];
		for(int i = 0; i<8; i++)
			table[i] = pieces[i].clone();
		tables.add(table);
		currMove++;
	}

	public void decMove() {
		if (currMove > 0)
			currMove--;
		repaint();
	}

	public void incMove() {
		if (currMove < tables.size() - 1)
			currMove++;
		repaint();
		
	}
	
	public void setMove(int move) {
		currMove = move;
		repaint();
		
	}
	
	ArrayList<Field> getAttackedByOpponent(PieceColor currColor, char[][] pieces, Context s){
		ArrayList<Field> eyeballed = new ArrayList<>();
		for(int i = 0; i<8; i++)
			for(int j = 0; j<8; j++) {
				if (!getColor(pieces[i][j]).equals(currColor) && pieces[i][j] != '.') {
					ArrayList<Field> eb = getEyeballing(new Field(j,i), pieces, s);
					for(Field f:eb) {
						eyeballed.add(f);
					}
				}
			}
		return eyeballed;
	}


	@Override
	public void run() {
		
		boolean playAgain = true;
		
		while (playAgain) {
			
			for(int i = 0; i<8; i++)
				tempBoard[i] = pieces[i].clone();
			
			tempTurn = turn;
			
			int depth = 5 + (32 - pieceCount)/16;
			//System.out.println(depth + " " + pieceCount);
			
			Move m = null;
			if (currMove > 0)
				m = findMove(4, PieceColor.BLACK, new int[1], -INFINITY, INFINITY);
			else 
				m = (Math.random() < 0.5) ? new Move(new Field(3,6), new Field(3,4)) : new Move(new Field(4, 6), new Field(4,4));
			
			current.x = m.start.x;
			current.y = m.start.y;
			Field end = m.end;
			int x = end.x;
			int y = end.y;
			
			doMove(x,y, pieces, pieceContext, true);
			
			if (premoved) {
				
				Field startingField = premove.start;
				Field endingField = premove.end;
				
				
				attacked = getAttackedFields(startingField, pieces, pieceContext);
				blocked = getBlockedFields(startingField, attacked, pieces, pieceContext);
				
				playAgain = true;
				premoved = false;
				
				current.x = startingField.x;
				current.y = startingField.y;
				premove = null;
				
				if ((attacked.contains(endingField) && !blocked.contains(endingField))) {
					doMove(endingField.x, endingField.y, pieces, pieceContext, false);
				}
				else {
					playAgain = false;
					draggedStart = null;
					repaint();
					
				}
				
			}
			else {
				playAgain = false;
			}
		}
				
	}
}	
