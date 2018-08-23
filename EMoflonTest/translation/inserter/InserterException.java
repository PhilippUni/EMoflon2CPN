package translation.inserter;

import translation.TranslationException;

public class InserterException extends TranslationException {
	public InserterException(Exception e) {
		super(e);
	}
	
	public InserterException(String msg) {
		super(msg);
	}
}
