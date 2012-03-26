/*
* @author Sean Lawlor
* @date November 3, 2011
* @class ECSE 211 - Design Principle and Methods
*/
package bluetooth;

public enum StartCorner {
	BOTTOM_LEFT(1,0,0, "BL"),
	BOTTOM_RIGHT(2,360,0, "BR"),
	TOP_LEFT(3,0,360, "TL"),
	TOP_RIGHT(4,360,360, "TR"),
	NULL(0,0,0, "NULL");
	
	private int id, x, y;
	private String name;
	private StartCorner(int id, int x, int y, String name) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}
	
	public int[] getCooridinates() {
		return new int[] {this.x, this.y};
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getId() {
		return this.id;
	}
	
	public static StartCorner lookupCorner(int cornerId) {
		for (StartCorner corner : StartCorner.values())
			if (corner.id == cornerId)
				return corner;
		return NULL;
	}
}
