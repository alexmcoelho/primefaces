package com.alex.util;

public class InfoTable {

    private String nameTable;
    private String field;
    private String complemenPhrase;
    private String nameConstraint;

    public InfoTable() {
    }
    
    public InfoTable(String nameTable, String field, String nameConstraint) {
        this.nameTable = nameTable;
        this.field = field;
        this.nameConstraint = nameConstraint;
    }

    public InfoTable(String nameTable, String field, String nameConstraint, String complemenPhrase) {
        this.nameTable = nameTable;
        this.field = field;
        this.nameConstraint = nameConstraint;
        this.complemenPhrase = complemenPhrase;
    }

    public String getComplemenPhrase() {
        return complemenPhrase;
    }

    public void setComplemenPhrase(String complemenPhrase) {
        this.complemenPhrase = complemenPhrase;
    }

    public String getNameConstraint() {
        return nameConstraint;
    }

    public void setNameConstraint(String nameConstraint) {
        this.nameConstraint = nameConstraint;
    }

    public String getNameTable() {
        return nameTable;
    }

    public void setNameTable(String nameTable) {
        this.nameTable = nameTable;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nameConstraint == null) ? 0 : nameConstraint.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InfoTable other = (InfoTable) obj;
		if (nameConstraint == null) {
			if (other.nameConstraint != null)
				return false;
		} else if (!nameConstraint.equals(other.nameConstraint))
			return false;
		return true;
	}

}
