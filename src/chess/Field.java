package chess;

class Field implements Cloneable{
	int x,  y;
	public Field(int x, int y) {
		this.x = x;
		this.y = y;
	}
	@Override
	public boolean equals(Object obj) {
		Field f = (Field)obj;
		return (x == f.x && y == f.y);
	}
}