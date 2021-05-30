package fiuba.tecnicas.msfiles.models.sharing;

public enum ShareModes {
	READ('R'), WRITE('W');
	
	private Character mode;
	
	public Character getMode() {
		return this.mode;
	}
	
	private ShareModes(Character mode) {
		this.mode = mode;
	}
}
