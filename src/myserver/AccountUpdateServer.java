package myserver;

import java.sql.Date;

public abstract class AccountUpdateServer extends Server{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String fullname;
	protected String password;
	protected String phone;
	protected String address;
	protected String description;
	protected Date date;
	
	protected abstract String stmtUpdateUser();
}
