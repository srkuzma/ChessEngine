package chess;
import java.awt.*;

import javax.swing.JLabel;

import chess.Board.PieceColor;


public class Timer extends Thread {
	private int sec = 0;
	private int min = 5;
	private JLabel label;
	boolean tick;
	private int miniTick = 100;
	private Board board;
	private PieceColor piece;
	private int increment;
	
	public Timer(JLabel label, boolean tick, int min, int increment, Board board) {
		this.label = label;
		this.tick = tick;
		this.board = board;
		this.increment = increment;
		this.min = min;
		piece = (tick) ? PieceColor.WHITE:PieceColor.BLACK;
		
		label.setFont(new Font("Verdana", Font.BOLD, 26));
	}
	int getSec() {
		return sec;
	}
	int getMin() {
		return min;
	}
	void addIncrement() {
		sec += increment;
		if (sec >= 60) {
			sec = sec - 60;
			min++;
		}
		label.setText(String.format("%02d", min) + ":" +  String.format("%02d", sec));
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				
				if (tick) {
					Thread.sleep(10);
					miniTick--;
					if (miniTick == 0) {
						
						miniTick = 100;
						sec--;
						
						
						if (sec == -1) {
							sec = 59;
							min--;
						}
						
						label.setText(String.format("%02d", min) + ":" +  String.format("%02d", sec));
						label.revalidate();
						if (sec == 0 && min == 0) {
							
							//System.out.println("GAME OVER");
							
							Game game = board.owner;
							
							game.winner.setText(piece.equals(PieceColor.WHITE) ? "Black has won" : "White has won");
							board.gameOver = true;
							tick = false;
							
						}
						
					}
					board.owner.takenBlack.repaint();
					board.owner.takenWhite.repaint();
				}
				
				//revalidate();
			}
		}
		catch(Exception e) {}
	}
}
