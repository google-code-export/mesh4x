package org.mesh4j.ektoo.ui.settings.encryption;


/**
 * <code>EncryptionException</code> is a 
 * customize exception for <code>EncryptionUtil</code> 
 * @author raju
 * @see java.lang.Exception
 * @since prototype1
 */
public class EncryptionException extends Exception{
    
	private static final long serialVersionUID = 5453943064966994679L;
	private String message;
	
	/**
     * Constructs a new exception with the specified detail message.
     * @param   message   the detail message.
     */
    public EncryptionException(String message){
        super(message);
        this.message = message;
    }
    /**
     * Constructs a new exception with the specified detail message.
     * and cause
     * @param   message   the detail message.
     * @param cause the cause of exception
     */
    public  EncryptionException(String message,Throwable cause){
        super(message,cause);
        this.message = message;
    }
    /**
     * Constructs a new exception with the specified cause
     * @param cause the cause of exception
     */
    public  EncryptionException(Throwable cause){
        super(cause);
    }
    
    @Override
	public String getMessage(){
		if(message != null && !message.equals("")){
			return message;	
		}
		else{
			return super.getMessage();
		}
	}
}
